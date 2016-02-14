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
package io.macgyver.core.event.provider;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import io.macgyver.core.event.DistributedEvent;
import io.macgyver.core.event.DistributedEventProvider;
import io.macgyver.core.event.DistributedEventProviderProxy;
import io.macgyver.core.event.DistributedEventSystem;
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


	public AbstractEventProvider() {
		
	}

	public void internalInstall(DistributedEventSystem system) {
		Preconditions.checkState(proxy==null,"proxy already set");
		this.proxy = (DistributedEventProviderProxy) system.getDistributedEventProvider();
		proxy.setDelegate(this);
	}
	public AbstractEventProvider(DistributedEventProviderProxy proxy) {
		this.proxy = proxy;
		proxy.setDelegate(this);
	}

	public abstract void doStart();

	protected DistributedEventProviderProxy getProxy() {
		return proxy;
	}
	public void start() {
		logger.info("start()");
		doStart();
		dispatcherThread = new Thread(this, "distributed-event-dispatcher");
		dispatcherThread.setDaemon(true);
		logger.info("starting {}",dispatcherThread);
		dispatcherThread.start();
	}

	public void stop() {
		running.set(false);
		// need to add an event to wake up the receiver
	}
	public boolean isRunning() {
		return running.get();
	}
	public Observable<DistributedEvent> getObservableDistributedEvent() {
		Preconditions.checkState(proxy != null, "proxy must be set");
		return proxy.getObservableDistributedEvent();
	}

	@Override
	public void run() {
		Preconditions.checkState(proxy != null, "proxy must be set");
		running.set(true);
		while (running.get()) {
			try {
				DistributedEvent event = fetchNextEvent();
				proxy.internalDispatch(event);
			} 
			catch (UnsupportedOperationException e) {
				try {
					Thread.sleep(30000);
				} catch (Exception ex) {
				}	
			}
			catch (RuntimeException e) {
				logger.warn("", e);
				try {
					Thread.sleep(500);
				} catch (Exception ex) {
				}
			}
			
		}
		logger.info("thread exiting event loop");
	}

	public abstract DistributedEvent fetchNextEvent();
}
