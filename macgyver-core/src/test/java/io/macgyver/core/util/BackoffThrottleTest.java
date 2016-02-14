package io.macgyver.core.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class BackoffThrottleTest {

	
	@Test
	public void testIt() {
		
		long t0 = System.currentTimeMillis();
		BackoffThrottle.sleep(500L);
		long t1 = System.currentTimeMillis();
		
		Assertions.assertThat(t1-t0).isGreaterThanOrEqualTo(500L);
	}
	
	
	@Test
	public void testX() {
		BackoffThrottle b = new BackoffThrottle();
		
		Assertions.assertThat(b.getDelay()).isEqualTo(0);
		b.markFailure();
	
		Assertions.assertThat(b.getDelay()).isEqualTo(100);
		b.markFailure();
		Assertions.assertThat(b.getDelay()).isEqualTo(400);
		b.markSuccess();
		Assertions.assertThat(b.getDelay()).isEqualTo(0);
	}

}
