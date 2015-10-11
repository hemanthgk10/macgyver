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
