package io.macgyver.core.reactor;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.macgyver.core.reactor.MacGyverEventPublisher.MessageBuilder;

public class MacGyverMessageTest {

	ObjectMapper mapper = new ObjectMapper();

	
	public static class TestMessage extends MacGyverMessage {

	}

	@Test

	public void testTestMessage() {
		MacGyverEventPublisher p = new MacGyverEventPublisher();
		MacGyverMessage m = p.createMessage(TestMessage.class).publish();
		
		Assertions.assertThat(m).isInstanceOf(TestMessage.class);
		
		Assertions.assertThat(m.getJsonNode().isObject()).isTrue();
	}


	
}
