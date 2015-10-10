package io.macgyver.core.event.provider.kinesis;

import io.macgyver.core.event.DistributedEvent;
import io.macgyver.core.event.DistributedEventProvider;
import rx.Observable;

public class KinesisDistributedEventProvider implements DistributedEventProvider {


	@Override
	public boolean publish(DistributedEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Observable<DistributedEvent> getObservableDistributedEvent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
