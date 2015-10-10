package io.macgyver.core.event.provider;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.thirdparty.guava.common.base.Preconditions;

import io.macgyver.core.event.DistributedEvent;
import io.macgyver.core.event.DistributedEventProvider;
import io.macgyver.core.event.DistributedEventProviderProxy;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.observers.Subscribers;

public abstract class AbstractEventProvider implements DistributedEventProvider, Runnable {

	Logger logger = LoggerFactory.getLogger(AbstractEventProvider.class);

	Thread dispatcherThread;
	AtomicBoolean running = new AtomicBoolean(false);

	DistributedEventProviderProxy proxy;
	

	public AbstractEventProvider(DistributedEventProviderProxy proxy) {
		this.proxy = proxy;
		proxy.setDelegate(this);
	}

	public void start() {
		dispatcherThread = new Thread(this, "distributed-event-dispatcher");
		dispatcherThread.setDaemon(true);

		dispatcherThread.start();
	}

	public void stop() {
		running.set(false);
		// need to add an event to wake up the receiver
	}

	public Observable<DistributedEvent> getObservableDistributedEvent() {
		Preconditions.checkState(proxy!=null, "proxy must be set");
		return proxy.getObservableDistributedEvent();
	}



	@Override
	public void run() {
		Preconditions.checkState(proxy!=null, "proxy must be set");
		running.set(true);
		while (running.get()) {
			try {
				DistributedEvent event = fetchNextEvent();
				proxy.dispatch(event);
			} catch (RuntimeException e) {
				logger.warn("", e);
			}
		}
	}




	public abstract DistributedEvent fetchNextEvent();
}
