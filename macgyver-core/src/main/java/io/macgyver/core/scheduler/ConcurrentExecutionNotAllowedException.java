package io.macgyver.core.scheduler;

import it.sauronsoftware.cron4j.TaskExecutor;

public class ConcurrentExecutionNotAllowedException extends IllegalStateException {



	public ConcurrentExecutionNotAllowedException(String message) {
		super(message);
		
	}

}
