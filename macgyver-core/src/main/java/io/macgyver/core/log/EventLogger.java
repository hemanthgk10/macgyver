package io.macgyver.core.log;

import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.macgyver.core.event.DistributedEvent;
import io.macgyver.core.event.DistributedEventSystem;

public abstract class EventLogger {

	Logger logger = LoggerFactory.getLogger(EventLogger.class);

	@Autowired
	DistributedEventSystem distributedEventSystem;

	ObjectMapper mapper = new ObjectMapper();

	public class Event {
		ObjectNode data = mapper.createObjectNode();
		String label = null;
		Instant instant = null;

		public Event withProperty(String key, String val) {
			data.put(key, val);
			return this;
		}

		public Event withMessage(String message) {
			return withProperty("message", message);
		}

		public Event withLabel(String label) {
			this.label = label;
			return this;
		}

		public Event withTimestamp(Instant instant) {
			this.instant = instant;
			return this;
		}

		public Event withTimestamp(long timestamp) {
			this.instant = Instant.ofEpochMilli(timestamp);
			return this;
		}

		public Event withTimestamp(Date d) {
			this.instant = d.toInstant();
			return this;
		}

		public void log() {
			logEvent(this);
		}
	}

	public Event event() {
		return new Event();
	}

	DistributedEvent convert(Event e) {

		return DistributedEvent.create().payload(e.data);

	}

	protected final void logEvent(Event event) {
		
		try {
			// log the event to the local logging system first
			doLogEvent(event);
		} catch (Exception e) {
			logger.warn("could not log event", e);
		}
		
		
		try {
			// now log to the distributed system
			distributedEventSystem.getDistributedEventProvider().publish(convert(event));
		} catch (Exception e) {
			logger.warn("could not log event to distributed event system", e);
		}
	}

	protected abstract void doLogEvent(Event e);
}
