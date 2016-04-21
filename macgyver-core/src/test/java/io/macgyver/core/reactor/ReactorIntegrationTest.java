package io.macgyver.core.reactor;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.macgyver.test.MacGyverIntegrationTest;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.bus.selector.ClassSelector;
import reactor.bus.selector.Selector;
import reactor.bus.selector.Selectors;
import reactor.fn.Predicate;

import static reactor.bus.selector.Selectors.$;

public class ReactorIntegrationTest  extends MacGyverIntegrationTest{

	@Autowired
	EventBus eventBus;
	
	@Test
	public void testIt() {
		Assertions.assertThat(eventBus).isNotNull();
	}
	
	@Test
	public void testMe() {
		
		
		Selector<JsonNode> s = Selectors.predicate(x-> { System.out.println(">>> "+x);
		return true;});
	
		
		eventBus.on(s,(Event<String> ev) -> {
		
			  System.out.printf("Got %s on thread %s%n", ev.getData(), Thread.currentThread());
			});
		
		ObjectNode n = new ObjectMapper().createObjectNode().put("name", "foo");
		eventBus.notify("topic",Event.wrap(n));
		
		System.out.println("!!!");
		try {
			Thread.sleep(5000L);
		}
		catch (Exception e ) {}
		
	}
	
}
