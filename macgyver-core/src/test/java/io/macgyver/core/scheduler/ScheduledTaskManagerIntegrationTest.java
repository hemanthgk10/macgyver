package io.macgyver.core.scheduler;

import java.util.UUID;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import io.macgyver.test.MacGyverIntegrationTest;

public class ScheduledTaskManagerIntegrationTest extends MacGyverIntegrationTest {

	@Inject
	ScheduledTaskManager stm;

	@Test
	public void testSetup() {
		Assertions.assertThat(stm).isNotNull();
	}

	@Test
	public void testStateExceptions() {
		try {
			stm.disable(UUID.randomUUID().toString());
			Assertions.failBecauseExceptionWasNotThrown(IllegalStateException.class);
		} catch (IllegalStateException e) {
			Assertions.assertThat(e).hasMessageContaining("id=").hasMessageContaining("does not exist");
		}
		
		try {
			stm.enable(UUID.randomUUID().toString());
			Assertions.failBecauseExceptionWasNotThrown(IllegalStateException.class);
		} catch (IllegalStateException e) {
			Assertions.assertThat(e).hasMessageContaining("id=").hasMessageContaining("does not exist");
		}
		
		try {
			stm.enable(UUID.randomUUID().toString(),false);
			Assertions.failBecauseExceptionWasNotThrown(IllegalStateException.class);
		} catch (IllegalStateException e) {
			Assertions.assertThat(e).hasMessageContaining("id=").hasMessageContaining("does not exist");
		}
		
		try {
			stm.scheduleByScript(UUID.randomUUID().toString());
			Assertions.failBecauseExceptionWasNotThrown(IllegalStateException.class);
		} catch (IllegalStateException e) {
			Assertions.assertThat(e).hasMessageContaining("id=").hasMessageContaining("does not exist");
		}
		
	
	}

}
