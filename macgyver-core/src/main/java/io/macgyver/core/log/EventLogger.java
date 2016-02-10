package io.macgyver.core.log;

import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class EventLogger {

	ObjectMapper mapper = new ObjectMapper();
	public class Event {
		ObjectNode data = mapper.createObjectNode();
		String label=null;
		Instant instant=null;
		public Event withProperty(String key, String val) {
			data.put(key, val);
			return this;
		}
		public Event withMessage(String message) {
			return withProperty("message",message);
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
	
	protected abstract void logEvent(Event e);
}
