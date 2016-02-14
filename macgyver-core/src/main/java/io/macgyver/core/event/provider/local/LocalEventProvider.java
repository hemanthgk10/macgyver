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
package io.macgyver.core.event.provider.local;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.macgyver.core.Kernel;
import io.macgyver.core.event.DistributedEvent;
import io.macgyver.core.event.DistributedEventProviderProxy;
import io.macgyver.core.event.provider.AbstractEventProvider;
import io.macgyver.core.util.BackoffThrottle;

public class LocalEventProvider extends AbstractEventProvider {
	ArrayBlockingQueue<DistributedEvent> localEventQueue = new ArrayBlockingQueue<>(1000);
	Logger logger = LoggerFactory.getLogger(LocalEventProvider.class);

	BackoffThrottle throttle = new BackoffThrottle();

	public LocalEventProvider() {
		super();
	}

	public LocalEventProvider(DistributedEventProviderProxy proxy) {
		super(proxy);
	}

	@Override
	public DistributedEvent fetchNextEvent() {
		DistributedEvent event = null;
		while (event == null && isRunning()) {
			try {
				event = localEventQueue.poll(10, TimeUnit.SECONDS);
				throttle.markSuccess();
			} 
			catch (InterruptedException e) {
				// not a problem...we just timed out
			}
			catch (RuntimeException  e) {
				logger.warn("problem taking request from queue", e);
				throttle.markFailureAndSleep();

			}
		}
		return event;
	}

	public boolean publish(DistributedEvent event) {

		if (!isRunning()) {
			logger.warn("{} is not running...events will not be dispatched.  Did you forget to start()?", this);
		}
		boolean result = localEventQueue.offer(event);

		if (result) {
			logger.debug("published event: {}", event);
		} else {
			logger.warn("event queue full: {}", event);
		}
		return result;
	}

	@Override
	public void doStart() {
		// nothing to do

	}

}
