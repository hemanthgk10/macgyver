package io.macgyver.core.event;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.macgyver.test.MacGyverIntegrationTest;

public class MacGyverEventPublisherIntegrationTest extends MacGyverIntegrationTest {

	public static class TestType extends MacGyverMessage {

	}

	@Autowired
	MacGyverEventPublisher publisher;

	@Test
	public void testIllegalState() {

		try {
			publisher.createMessage().withAttribute("foo", "bar").withMessageType(TestType.class).publish();
			Assertions.failBecauseExceptionWasNotThrown(IllegalStateException.class);
		} catch (Exception e) {
			Assertions.assertThat(e).isInstanceOf(IllegalStateException.class);
		}
	}
}
