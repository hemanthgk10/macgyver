package io.macgyver.core.event;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import com.google.common.collect.Lists;

import io.macgyver.core.event.provider.AbstractEventProvider;
import io.macgyver.core.event.provider.local.LocalEventProvider;
import rx.Observable;

public class DistributedEventSystem implements ApplicationListener<ApplicationReadyEvent> {

	Logger logger = LoggerFactory.getLogger(DistributedEventSystem.class);

	@Autowired
	DistributedEventProviderProxy proxy;

	public DistributedEventSystem() {

	}

	public DistributedEventProvider getDistributedEventProvider() {

		return proxy;
	}

	public Observable<DistributedEvent> getObservableDistributedEvent() {
		return proxy.getObservableDistributedEvent();
	}

	public void installDistributedEventProvider(DistributedEventProvider provider) {

		((AbstractEventProvider) provider).internalInstall(this);
		provider.start();
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		logger.info("onApplicationEvent(" + event + ")");

		// If there are no spring-registered DistributedEventProviders
		// registered, install LocalEventProvider
		List<DistributedEventProvider> list = Lists.newArrayList();
		event.getApplicationContext().getBeansOfType(DistributedEventProvider.class).values().forEach(it -> {
			if (!(it instanceof DistributedEventProviderProxy)) {
				list.add(it);
			}
		});
		if (list.isEmpty()) {
			logger.info("no spring-registered DistributedEventProvider found -- adding LocalEventProvider");
			list.add(new LocalEventProvider());
		}

		if (list.size() > 1) {
			logger.warn("multiple providers found: {}", list);
		}
		DistributedEventProvider provider = list.get(0);
		logger.info("installing {}", provider);
		installDistributedEventProvider(list.get(0));

	}
}
