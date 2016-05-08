package io.macgyver.core.metrics;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.codahale.metrics.SharedMetricRegistries;

import io.macgyver.test.MacGyverIntegrationTest;
import reactor.core.Dispatcher;

public class MacGyverMetricRegistryTest extends MacGyverIntegrationTest{

	@Autowired
	MacGyverMetricRegistry registry;
	
	@Test
	public void testIt() {
		Assertions.assertThat(registry).isNotNull();
		Assertions.assertThat(SharedMetricRegistries.names().contains("macMetricRegistry"));
		Assertions.assertThat(SharedMetricRegistries.getOrCreate("macMetricRegistry")).isSameAs(registry);
		
		
	}
}
