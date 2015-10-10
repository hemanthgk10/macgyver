package io.macgyver.core.event.provider.local;

import org.junit.After;
import org.junit.Test;

import io.macgyver.core.event.DistributedEvent;
import io.macgyver.core.event.DistributedEventProvider;
import io.macgyver.core.event.DistributedEventProviderProxy;
import io.macgyver.core.event.provider.AbstractEventProvider;

public class LocalEventProviderTest {

	
	DistributedEventProviderProxy provider;
	LocalEventProvider localProvider;
	@org.junit.Before
	public void startProvider() {
		provider = new DistributedEventProviderProxy();
		
		localProvider = new LocalEventProvider(provider);
		localProvider.start();
	}
	
	@After
	public void stopProvider() {
		localProvider.stop();
	}
	@Test
	public void testIt() {
		
		
		provider.getObservableDistributedEvent().subscribe(it -> System.out.println(it));

		DistributedEvent evt = new DistributedEvent().topic("test");
		provider.publish(evt);
		
		
		
		
	}

}
