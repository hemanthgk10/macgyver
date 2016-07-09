package io.macgyver.jdbc.event;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.davidmoten.rx.jdbc.Database;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.macgyver.core.event.EventLogger;
import io.macgyver.core.event.MacGyverEventPublisher;
import io.macgyver.core.event.MacGyverMessage;
import io.macgyver.core.util.JsonNodes;
import io.macgyver.jdbc.JacksonRxJdbcResultSetMapper;
import io.macgyver.jdbc.event.JdbcEventWriter;
import io.macgyver.test.MacGyverIntegrationTest;
import reactor.bus.Event;
import reactor.bus.selector.Selectors;

public class JdbcEventWriterTest extends MacGyverIntegrationTest {

	static Database db;

	@Autowired
	MacGyverEventPublisher publisher;

	@Autowired
	JdbcEventWriter eventWriter;

	@Autowired
	EventLogger eventLogger;
	
	@Before
	public void setupTempDB() {

		if (db == null) {
			db = Database.builder().url("jdbc:h2:mem:event_test").pool(1, 1).build();

			try {
				db.update(JdbcEventWriter.GENERIC_DDL).execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		eventWriter.withDatabase(db);

	}

	// @Test
	public void test() throws Exception {

		for (int i = 0; i < 100; i++) {
			MacGyverMessage m = new MacGyverMessage();
			m.withAttribute("test", "hello " + i);

			new JdbcEventWriter().withDatabase(db).withPrivateEventBus().writeAsync(m);
		}

		Thread.sleep(1000);

		db.select("select * from event").get(new JacksonRxJdbcResultSetMapper()).forEach(it -> {
			System.out.println(it);
		});
	}

	public static class TestMessageType extends MacGyverMessage {
		
	}
	@Test
	public void testX() throws InterruptedException, IOException, JsonProcessingException{
		
		CountDownLatch latch = new CountDownLatch(1);
		eventWriter.getEventBus().on(Selectors.T(MacGyverMessage.class), (Event<MacGyverMessage> x) -> {
			
			if (x.getData().getPayload().path("hello").asText().equals("world")) {
				latch.countDown();
			}
			
		});
		
	
		
		
		//MacGyverMessage m = eventLogger.event().withMessage("hello");
		MacGyverMessage m = publisher.createMessage().withMessageType(TestMessageType.class).withAttribute("hello", "world").publish();

		JsonNode envelope = m.getEnvelope();
		
		System.out.println(JsonNodes.pretty(envelope));
		Assertions.assertThat(envelope.has("eventId")).isTrue();
		Assertions.assertThat(envelope.get("eventTs").asLong()).isCloseTo(System.currentTimeMillis(), Offset.offset(5000L));
		Assertions.assertThat(envelope.path("data").path("hello").asText()).isEqualTo("world");
		Assertions.assertThat(envelope.get("data")).isSameAs(m.getPayload());
		Assertions.assertThat(m.getPayload().size()).isEqualTo(1);
		Assertions.assertThat(envelope.path("eventType").asText()).isEqualTo(TestMessageType.class.getName());
		latch.await(5, TimeUnit.SECONDS);
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		
		JsonNode x = db.select("select * from event where EVENT_ID=?").parameter(envelope.get("eventId").asText()).get(new JacksonRxJdbcResultSetMapper()).toBlocking().first();
		Assertions.assertThat(x.path("EVENT_ID").asText()).isEqualTo(m.getEventId()).isEqualTo(envelope.get("eventId").asText());
		
		JsonNode jsonFromDb = JsonNodes.mapper.readTree(x.path("JSON_DATA").asText());
		logger.info("EVENT row: \n{}",JsonNodes.pretty(x));
		
		logger.info("JSON_DATA from db: \n{}",JsonNodes.pretty(jsonFromDb));
		
		Assertions.assertThat(jsonFromDb.path("data").path("hello").asText()).isEqualTo("world");
		Assertions.assertThat(jsonFromDb.path("eventId").asText()).isEqualTo(envelope.get("eventId").asText());
		Assertions.assertThat(jsonFromDb.path("eventTs").asLong()).isEqualTo(envelope.get("eventTs").asLong());

	}

}
