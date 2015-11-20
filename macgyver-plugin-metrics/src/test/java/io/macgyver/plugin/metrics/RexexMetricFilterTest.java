package io.macgyver.plugin.metrics;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class RexexMetricFilterTest {

	@Test
	public void testIt() {
		RegexMetricFilter f = new RegexMetricFilter().excludes(".*counter\\..*");
		
		
		Assertions.assertThat(f.matches("foo", null)).isTrue();
		Assertions.assertThat(f.matches("abc.counter.test", null)).isFalse();
	}
	
	
	@Test
	public void testDefaults() {
	
		Assertions.assertThat(new RegexMetricFilter().matches("foo", null)).isTrue();
		Assertions.assertThat(new RegexMetricFilter().excludes("  ").matches("foo", null)).isTrue();
		Assertions.assertThat(new RegexMetricFilter().excludes(null).matches("foo", null)).isTrue();
		
	}

}
