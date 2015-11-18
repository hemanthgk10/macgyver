/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.macgyver.core.scheduler;

import io.macgyver.core.Kernel;
import io.macgyver.core.resource.Resource;
import io.macgyver.core.script.ExtensionResourceProvider;
import io.macgyver.core.script.ScriptExecutor;
import io.macgyver.neorx.rest.NeoRxClient;
import it.sauronsoftware.cron4j.Scheduler;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;
import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

public class IgniteSchedulerService implements Service, Runnable, Serializable, DirectScriptExecutor {

	static Logger logger = LoggerFactory.getLogger(IgniteSchedulerService.class);

	ScheduledFuture scheduledFuture;

	MacGyverTaskCollector taskCollector;

	AtomicReference<Scheduler> schedulerRef = new AtomicReference<>();

	public static class CrontabLineProcessor implements LineProcessor<Optional<ObjectNode>> {
		int i = 0;
		String result;

		@Override
		public boolean processLine(String line) throws IOException {
			if (i++ > 50) {
				// only look through the first 50 lines
				return false;
			}
			if (line != null && line.contains(SCHEDULE_TOKEN)) {
				result = line.substring(line.indexOf(SCHEDULE_TOKEN) + SCHEDULE_TOKEN.length()).trim();
				return false;
			}
			return true;
		}

		@Override
		public Optional<ObjectNode> getResult() {
			if (result == null || result.trim().length() == 0) {
				return Optional.absent();
			}
			try {
				ObjectNode n = (ObjectNode) new ObjectMapper().readTree(result);

				return Optional.fromNullable(n);
			} catch (IOException e) {
				logger.warn("problem parsing: {}", result);
				return Optional.absent();
			}
		}

	}

	@Override
	public synchronized void cancel(ServiceContext ctx) {

		Scheduler s = schedulerRef.get();
			if (s!=null) {
				s.stop();
			}

			scheduledFuture.cancel(true);
		

	}

	@Override
	public void init(ServiceContext ctx) throws Exception {
		// we have a race/deadlock if we try to access spring from here
	}

	@Override
	public synchronized void execute(ServiceContext ctx) throws Exception {
		logger.info("execute...");
		NeoRxClient client = Kernel.getApplicationContext().getBean(NeoRxClient.class);

		taskCollector = new MacGyverTaskCollector(client);

		scheduledFuture = Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(this, 0, 10, TimeUnit.SECONDS);
		Scheduler schedulerInstance = new Scheduler();
		if (!schedulerInstance.isStarted()) {
			schedulerInstance.addTaskCollector(taskCollector);
			schedulerInstance.setDaemon(true);
			schedulerInstance.addSchedulerListener(new MacGyverScheduleListener());
			LoggerFactory.getLogger(IgniteSchedulerService.class).info("starting scheduler: {}", schedulerInstance);
			schedulerInstance.start();
			this.schedulerRef.set(schedulerInstance);
			Runnable r = new Runnable() {

				@Override
				public void run() {
					LoggerFactory.getLogger(it.sauronsoftware.cron4j.Scheduler.class).info("heartbeat");

				}

			};
			String key = schedulerInstance.schedule("* * * * *", r);
		}

	}

	@Override
	public void run() {
		try {
			scan();
		} catch (IOException e) {
			logger.warn("", e);
		}
	}

	public void scan() throws IOException {

		ExtensionResourceProvider extensionLoader = Kernel.getInstance().getApplicationContext()
				.getBean(ExtensionResourceProvider.class);
		long scanTime = System.currentTimeMillis();
		NeoRxClient client = Kernel.getApplicationContext().getBean(NeoRxClient.class);

		ScriptExecutor se = new ScriptExecutor();
		for (Resource r : extensionLoader.findResources()) {
			if (logger.isDebugEnabled()) {
				logger.debug("evaluating {} to see if it can be scheduled", r);
			}
			if (r.getPath().startsWith("scripts/scheduler/")) {

				Optional<ObjectNode> schedule = extractCronExpression(r);
				if (!schedule.isPresent()) {
					schedule = Optional.of(new ObjectMapper().createObjectNode());
				}
				boolean b = schedule.get().path("enabled").asBoolean(true);

				ObjectNode descriptor = schedule.get();

				String cypher = "merge (s:ScheduledTask {script:{script}}) set s.scheduledBy='script', s.enabled={enabled}, s.cron={cron}, s.lastUpdateTs={ts} return s;";

				client.execCypher(cypher, "script", r.getPath(), "enabled", b, "cron", descriptor.path("cron").asText(),
						"ts", scanTime);

			}
		}

		// now remove all entries scheduled via script that were not just
		// updated

		if (logger.isDebugEnabled()) {
			logger.debug("removing old scheduled entries...");
		}
		String cypher = "match (s:ScheduledTask) where s.scheduledBy='script' and (s.lastUpdateTs is null or s.lastUpdateTs<{ts}) delete s";
		client.execCypher(cypher, "ts", scanTime);

	}

	public static final String SCHEDULE_TOKEN = "#@Schedule";

	public static Optional<ObjectNode> extractCronExpression(Resource r) {

		try (StringReader sr = new StringReader(r.getContentAsString())) {
			return CharStreams.readLines(sr, new CrontabLineProcessor());
		} catch (IOException | RuntimeException e) {
			try {
				logger.warn("unable to extract cron expression: ", r.getContentAsString());
			} catch (Exception IGNORE) {
				logger.warn("unable to extract cron expression");
			}
		}

		return Optional.absent();

	}

	/**
	 * This should only be invoked via ignite.
	 * 
	 * @param scriptName
	 */
	public void executeScriptImmediately(String scriptName) {
		ObjectNode n = new ObjectMapper().createObjectNode();
		n.put("script", scriptName);
		MacGyverTask task = new MacGyverTask(n);
		
		Scheduler s = schedulerRef.get();
		if (s!=null) {
			s.launch(task);
		}
		
	}

}
