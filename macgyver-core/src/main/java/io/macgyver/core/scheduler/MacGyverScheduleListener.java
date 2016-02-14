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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.macgyver.core.Kernel;
import io.macgyver.core.log.EventLogger;
import io.macgyver.core.log.Neo4jEventLogger;
import it.sauronsoftware.cron4j.SchedulerListener;
import it.sauronsoftware.cron4j.TaskExecutor;

public class MacGyverScheduleListener implements SchedulerListener {

	Logger logger = LoggerFactory.getLogger(MacGyverScheduleListener.class);
	
	@Override
	public void taskFailed(TaskExecutor taskExecutor, Throwable exception) {
		logger.warn("taskFailed - "+taskExecutor.getTask(),exception);
		Kernel.getApplicationContext().getBean(TaskStateManager.class).endTask(taskExecutor.getGuid(),TaskStateManager.TaskState.FAILED);
	}

	@Override
	public void taskLaunching(TaskExecutor taskExecutor) {
		logger.info("taskLaunching: {}",taskExecutor.getTask());
		Kernel.getApplicationContext().getBean(TaskStateManager.class).startTask(taskExecutor.getGuid());
	}

	@Override
	public void taskSucceeded(TaskExecutor taskExecutor) {
		logger.info("taskSucceeded: {}",taskExecutor.getTask());
		Kernel.getApplicationContext().getBean(TaskStateManager.class).endTask(taskExecutor.getGuid(),TaskStateManager.TaskState.COMPLETED);
	}

}
