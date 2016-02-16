package io.macgyver.core.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackoffThrottle {

	Logger logger = LoggerFactory.getLogger(BackoffThrottle.class);
	AtomicInteger failures = new AtomicInteger(0);

	public void markSuccess() {
		failures.set(0);
	}

	public void markFailure() {
		failures.incrementAndGet();
	}

	public void markFailureAndSleep() {
		markFailure();
		sleepIfNecessary();
	}

	public long getDelay() {
		return Math.min(30000, 100 * Math.round(Math.pow(failures.get(), 2)));
	}

	/**
	 * Guaranteed not throw any exception.
	 */
	public void sleepIfNecessary() {

		long pauseDuration = getDelay();
		if (pauseDuration < 1) {
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("throttling for {} ms", pauseDuration);
		}
		sleep(pauseDuration);

	}

	/**
	 * Like Thread.sleep() but guaranteed not to throw an exception or return
	 * before the number of milliseconds have elapsed.
	 * 
	 * @param delay
	 */
	public static void sleep(long delay) {

		long expire = System.currentTimeMillis() + Math.max(0, delay);
		while (System.currentTimeMillis() < expire) {
			try {
				Thread.sleep(expire - System.currentTimeMillis());
			} catch (Exception e) {
				// ignore
			}
		}
	}
}
