package io.macgyver.core.event;

import org.junit.Test;

public class DistributedEventTest {

	@Test
	public void testIt() {
		
		DistributedEvent x = new DistributedEvent();
		
		
		System.out.println(x.toJsonNode());
	}

}
