package io.macgyver.core.reactor;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.macgyver.core.log.EventLogger;
import io.macgyver.core.log.EventLogger.LogMessage;
import io.macgyver.test.MacGyverIntegrationTest;
import reactor.Environment;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.bus.selector.ClassSelector;
import reactor.bus.selector.Selector;
import reactor.bus.selector.Selectors;
import reactor.fn.Predicate;

import static reactor.bus.selector.Selectors.$;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ReactorIntegrationTest extends MacGyverIntegrationTest {

	@Autowired
	EventBus eventBus;

	@Autowired
	EventLogger eventLogger;

	@Test
	public void testIt() throws InterruptedException {
	
		Assertions.assertThat(eventBus).isNotNull();
		
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<Event<LogMessage>> ref = new AtomicReference<Event<LogMessage>>(null);
		eventBus.on(Selectors.T(LogMessage.class),(Event<LogMessage> x)->{
			ref.set(x);
			latch.countDown();
		});
		
		eventLogger.event().withProperty("foo", "bar").log();

		Assertions.assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
		
		Assertions.assertThat(ref.get().getData().getJsonNode().path("foo").asText()).isEqualTo("bar");
	}

	@Test
	public void testMe() {

		Selector<JsonNode> s = Selectors.predicate(x -> {
			System.out.println(">>> " + x);
			return true;
		});

		eventBus.on(s, (Event<String> ev) -> {

			System.out.printf("Got %s on thread %s%n", ev.getData(), Thread.currentThread());
		});

		ObjectNode n = new ObjectMapper().createObjectNode().put("name", "foo");
		eventBus.notify("topic", Event.wrap(n));

		System.out.println("!!!");
		try {
			Thread.sleep(5000L);
		} catch (Exception e) {
		}

	}

}
