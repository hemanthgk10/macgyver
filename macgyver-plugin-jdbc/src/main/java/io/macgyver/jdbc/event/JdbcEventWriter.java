package io.macgyver.jdbc.event;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.github.davidmoten.rx.jdbc.Database;
import com.google.common.base.Preconditions;

import io.macgyver.core.ServiceNotFoundException;
import io.macgyver.core.event.EventLogger;
import io.macgyver.core.event.MacGyverEventPublisher;
import io.macgyver.core.event.MacGyverMessage;
import io.macgyver.core.service.ServiceRegistry;
import io.macgyver.core.util.JsonNodes;
import reactor.Environment;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.bus.registry.Registration;
import reactor.bus.selector.Selectors;
import reactor.fn.Consumer;

public class JdbcEventWriter implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	ServiceRegistry serviceRegistry;

	@Autowired
	MacGyverEventPublisher publisher;

	@Autowired
	EventLogger eventLogger;

	TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());

	AtomicReference<Database> database = new AtomicReference<Database>(null);

	AtomicReference<EventBus> localBus = new AtomicReference<EventBus>(null);

	Logger logger = LoggerFactory.getLogger(JdbcEventWriter.class);

	ObjectMapper mapper = new ObjectMapper();

	AtomicBoolean enabled = new AtomicBoolean(true);

	public JdbcEventWriter() {
		Environment.initializeIfEmpty();

	}

	class LocalConsumer implements Consumer<Event<MacGyverMessage>> {

		@Override
		public void accept(Event<MacGyverMessage> t) {
			write(t.getData());
		}

	}

	public EventBus getEventBus() {
		return localBus.get();
	}

	public Database getDatabase() {
		return database.get();
	}

	public JdbcEventWriter withPrivateEventBus() {
		return withEventBus(EventBus.create(Environment.newDispatcher("jdbc-event-log", 2048)));
	}

	public JdbcEventWriter withEventBus(EventBus bus) {
		localBus.set(bus);
		bus.on(Selectors.matchAll(), new LocalConsumer());
		return this;
	}

	public JdbcEventWriter withDatabase(Database db) {
		database.set(db);
		return this;
	}

	public static final String GENERIC_DDL = "create table event (event_id varchar(38) not null, event_type varchar(150), json_data clob, event_ts timestamp)";
	public static final String MYSQL_DDL = "create table event (event_id varchar(38) not null, event_type varchar(150), json_data json, event_ts timestamp)";

	public void writeAsync(MacGyverMessage m) {
		Preconditions.checkState(localBus != null, "event bus not set");
		getEventBus().notify(m, Event.wrap(m));
	}

	public void write(MacGyverMessage m) {
		write(m.getEnvelope());
	}

	protected void write(JsonNode data) {
		try {
			if (!isEnabled()) {
				return;
			}
			if (data == null || !data.isObject()) {
				return;
			}
			String jsonString = mapper.writeValueAsString(data);

			if (Strings.isNullOrEmpty(jsonString)) {
				return;
			}

			String id = data.path("eventId").asText(uuidGenerator.generate().toString());

			String eventType = data.path("eventType").asText();

			long ts = data.path("eventTs").longValue();
			if (ts <= 0) {
				ts = System.currentTimeMillis();
			}
			Timestamp eventTimestamp = new Timestamp(ts);

			int count = getDatabase()
					.update("insert into event(event_id, event_type, json_data,event_ts) values (?,?,?,?)")
					.parameter(id).parameter(eventType).parameter(jsonString).parameter(eventTimestamp).execute();
			logger.debug("inserted event id={} eventType={}", id, eventType);
		} catch (JsonProcessingException | RuntimeException e) {
			logger.warn("could not log event", e);
		}
	}

	public Registration<Object, Consumer<? extends Event<?>>> subscribe(EventBus bus) {
		return bus.on(Selectors.type(MacGyverMessage.class), x -> {
			getEventBus().notify(x.getKey(), Event.wrap(x.getKey()));
		});

	}

	public boolean isEnabled() {
		return enabled.get() && database.get() != null;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		try {
			logger.info("configuring JdbcEventWriter");
			Database db = serviceRegistry.get("jdbcEventLogRxJdbc", Database.class);
			this.database.set(db);

			logger.info("configured JdbcEventLogWriter with db: {}", db);

		} catch (ServiceNotFoundException e) {
			logger.warn("service id={} not defined....events will not be logged to RDBMS", e.getMessage());
		} catch (RuntimeException e) {
			logger.error("could not load service id=jdbcEventLog...events will not be logger to RDBMS", e);
		}
	}
}
