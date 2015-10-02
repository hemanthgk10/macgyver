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
}
