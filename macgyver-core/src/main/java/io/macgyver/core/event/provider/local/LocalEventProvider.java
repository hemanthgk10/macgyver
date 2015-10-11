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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.macgyver.core.event.DistributedEvent;
import io.macgyver.core.event.DistributedEventProviderProxy;
import io.macgyver.core.event.provider.AbstractEventProvider;

public class LocalEventProvider extends AbstractEventProvider {
	ArrayBlockingQueue<DistributedEvent> localEventQueue = new ArrayBlockingQueue<>(1000);
	Logger logger = LoggerFactory.getLogger(LocalEventProvider.class);

	public LocalEventProvider(DistributedEventProviderProxy proxy) {
		super(proxy);
	}

	@Override
	public DistributedEvent fetchNextEvent() {
		DistributedEvent event = null;
		while (event == null) {
			try {
				return localEventQueue.take();

			} catch (InterruptedException e) {
				try {
					Thread.sleep(100L);
				}
				catch (Exception e2) {}
			}
		}
		return event;
	}

	public boolean publish(DistributedEvent event) {
		boolean result = localEventQueue.offer(event);

		if (result) {
			logger.debug("published event: {}", event);
		} else {
			logger.warn("event queue full: {}", event);
		}
		return result;
	}
	
	public void shutdown() {
		stop();
	}
}
