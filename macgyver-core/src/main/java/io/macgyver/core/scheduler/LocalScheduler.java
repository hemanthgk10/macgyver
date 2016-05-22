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

import javax.annotation.PostConstruct;

import org.crsh.console.jline.internal.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;
import com.google.common.io.LineProcessor;
import com.google.common.util.concurrent.Service;

import io.macgyver.core.Kernel;
import io.macgyver.core.scheduler.TaskStateManager.AgingTaskCleanup;
import io.macgyver.core.scheduler.TaskStateManager.OrphanedTaskCleanup;
import io.macgyver.neorx.rest.NeoRxClient;
import it.sauronsoftware.cron4j.Scheduler;

public class LocalScheduler implements  Runnable, DirectScriptExecutor {

	static Logger logger = LoggerFactory.getLogger(LocalScheduler.class);

	ScheduledFuture scheduledFuture;

	@Autowired
	Scheduler scheduler;
	
	@Autowired
	MacGyverTaskCollector taskCollector;

	@Autowired
	NeoRxClient neo4j;
	
	@Autowired
	TaskStateManager taskStateManager;
	
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

	public boolean isMaster() {
		return true;
	}

	@PostConstruct
	public void startup() throws Exception {
		logger.info("starting scheduler...");

		scheduledFuture = Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(this, 0, 10, TimeUnit.SECONDS);
	
		Preconditions.checkNotNull(taskCollector);
			scheduler.addTaskCollector(taskCollector);
			scheduler.setDaemon(true);
			scheduler.addSchedulerListener(new MacGyverScheduleListener());
			
			scheduler.start();
				
			
			scheduler.schedule(OrphanedTaskCleanup.CRON, taskStateManager.new OrphanedTaskCleanup());
			scheduler.schedule(AgingTaskCleanup.CRON,taskStateManager.new AgingTaskCleanup());
	

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

			scheduler.launch(task);
		
		
	}


}
