package io.macgyver.core.event;

import rx.Observable;

public interface DistributedEventProvider {


	public boolean publish(DistributedEvent event);
	 Observable<DistributedEvent> getObservableDistributedEvent();
	 
	 public void shutdown();
}
