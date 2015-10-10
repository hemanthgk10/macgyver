package io.macgyver.core.event;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Lists;

import io.macgyver.test.MacGyverIntegrationTest;

public class DistributedEventIntegrationTest extends MacGyverIntegrationTest {

	@Autowired
	DistributedEventProvider provider;

	@Test
	public void testIt() throws InterruptedException {
		Assertions.assertThat(provider).isNotNull();
		
		DistributedEvent evt = DistributedEvent.create();
		List<DistributedEvent> list = Lists.newArrayList();
		provider.getObservableDistributedEvent().subscribe(it -> {list.add(it);} );
		
		provider.publish(evt);
		
		Thread.sleep(500);
		
		new DistributedEvent().topic("test");
		Assertions.assertThat(list.contains(evt));
		
		
	}
}
