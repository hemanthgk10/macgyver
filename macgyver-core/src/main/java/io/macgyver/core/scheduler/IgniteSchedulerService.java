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

import java.io.IOException;
import java.io.Serializable;
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
import com.google.common.io.LineProcessor;

import io.macgyver.core.Kernel;
import io.macgyver.core.scheduler.TaskStateManager.AgingTaskCleanup;
import io.macgyver.core.scheduler.TaskStateManager.OrphanedTaskCleanup;
import io.macgyver.core.scheduler.TaskStateManager.TaskState;
import io.macgyver.neorx.rest.NeoRxClient;
import it.sauronsoftware.cron4j.Scheduler;

public class IgniteSchedulerService implements Service, Runnable, Serializable, DirectScriptExecutor {

	static Logger logger = LoggerFactory.getLogger(IgniteSchedulerService.class);

	ScheduledFuture scheduledFuture;

	MacGyverTaskCollector taskCollector;

	AtomicReference<Scheduler> schedulerRef = new AtomicReference<>();

	volatile NeoRxClient neo4j;
	
	synchronized NeoRxClient getNeoRxClient() {
		if (neo4j==null) {
			neo4j = Kernel.getInstance().getApplicationContext().getBean(NeoRxClient.class);
		}
		return neo4j;
	}
	public static class CrontabLineProcessor implements LineProcessor<Optional<ObjectNode>> {
		int i = 0;
		String result;

		@Override
		public boolean processLine(String line) throws IOException {
			if (i++ > 50) {
				// only look through the first 50 lines
				return false;
			}
			if (line != null && line.contains(ScheduledTaskManager.SCHEDULE_TOKEN)) {
				result = line.substring(line.indexOf(ScheduledTaskManager.SCHEDULE_TOKEN) + ScheduledTaskManager.SCHEDULE_TOKEN.length()).trim();
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
		TaskStateManager tsm = Kernel.getApplicationContext().getBean(TaskStateManager.class);
		scheduledFuture = Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(this, 0, 10, TimeUnit.SECONDS);
		Scheduler schedulerInstance = new Scheduler();
		if (!schedulerInstance.isStarted()) {
			schedulerInstance.addTaskCollector(taskCollector);
			schedulerInstance.setDaemon(true);
			schedulerInstance.addSchedulerListener(new MacGyverScheduleListener());
			LoggerFactory.getLogger(IgniteSchedulerService.class).info("starting scheduler: {}", schedulerInstance);
			schedulerInstance.start();
			this.schedulerRef.set(schedulerInstance);
			
			
			schedulerInstance.schedule(OrphanedTaskCleanup.CRON, tsm.new OrphanedTaskCleanup());
			schedulerInstance.schedule(AgingTaskCleanup.CRON,tsm.new AgingTaskCleanup());
		}

	}

	@Override
	public void run() {
		try {
			Kernel.getApplicationContext().getBean(ScheduledTaskManager.class).scan();
		} catch (IOException e) {
			logger.warn("", e);
		}
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
