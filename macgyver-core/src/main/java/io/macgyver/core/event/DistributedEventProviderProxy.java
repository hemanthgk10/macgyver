package io.macgyver.core.event;

import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.observers.Subscribers;

public class DistributedEventProviderProxy implements DistributedEventProvider {

	AtomicReference<DistributedEventProvider> proxy = new AtomicReference<DistributedEventProvider>();

	public DistributedEventProviderProxy() {

		setupReactive();
	}

	@Override
	public Observable<DistributedEvent> getObservableDistributedEvent() {
		// the Observable is NOT delegated
		return observable;
	}

	@Override
	public boolean publish(DistributedEvent event) {
	
		return proxy.get().publish(event);

	}

	ConnectableObservable<DistributedEvent> observable;
	private Subscriber<? super DistributedEvent> subscriber;

	private void setupReactive() {
		Observable.OnSubscribe<DistributedEvent> xx = new Observable.OnSubscribe<DistributedEvent>() {

			public void call(final Subscriber<? super DistributedEvent> t1) {

				Action1<DistributedEvent> onNextAction = new Action1<DistributedEvent>() {

					@Override
					public void call(DistributedEvent dp1) {
						t1.onNext(dp1);

					}

				};

				Action1<Throwable> onThrowable = new Action1<Throwable>() {
					public void call(Throwable e) {
						e.printStackTrace();
					}
				};

				subscriber = Subscribers.create(onNextAction, onThrowable);

			}
		};

		this.observable = Observable.create(xx).publish();

		observable.connect();
	}

	public void dispatch(DistributedEvent event) {
		subscriber.onNext(event);
	}

	public void setDelegate(DistributedEventProvider p) {
		DistributedEventProvider oldProvider = proxy.getAndSet(p);
		if (oldProvider != null) {
			oldProvider.shutdown();
		}
	}

	@Override
	public void shutdown() {
		DistributedEventProvider p = proxy.get();
		if (p != null) {
			p.shutdown();
		}

	}
}
