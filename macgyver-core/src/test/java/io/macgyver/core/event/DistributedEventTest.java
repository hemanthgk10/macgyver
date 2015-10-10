package io.macgyver.core.event;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DistributedEventTest {

	@Test
	public void testIt() {
		
		DistributedEvent x = new DistributedEvent();
		
		Assertions.assertThat(x.getTimestamp()).isEqualTo(x.getJson().path("ts").asLong());
		Assertions.assertThat(Math.abs(x.getJson().path("ts").asLong()-System.currentTimeMillis())).isLessThan(500);
		
	}

}
