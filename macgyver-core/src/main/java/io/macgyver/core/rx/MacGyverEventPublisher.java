package io.macgyver.core.rx;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import reactor.bus.Event;
import reactor.bus.EventBus;

public class MacGyverEventPublisher  {

	Logger logger = LoggerFactory.getLogger(MacGyverEventPublisher.class);

	@Autowired
	EventBus eventBus;

	static ObjectMapper mapper = new ObjectMapper();

	public class EventBuilder {

		ObjectNode json = mapper.createObjectNode();

		public EventBuilder() {
			json.put("ts", System.currentTimeMillis());
			json.put("source", getDefaultSource());
		}

		public EventBuilder payload(JsonNode n) {
			json.set("data", n);
			return this;
		}

		public EventBuilder sourceHost(String source) {
			json.put("sourceHost", source);
			return this;
		}

		public Event<JsonNode> toReactorEvent() {
			Event<JsonNode> x = Event.wrap(getJson());
			return x;
		}

		public EventBuilder topic(String topic) {
			json.put("topic", topic);
			return this;
		}

		public EventBuilder reference(String ref) {
			json.put("reference", ref);
			return this;
		}

		public ObjectNode getJson() {
			// maybe we want an immutable/deep-copy
			return json;
		}

		public long getTimestamp() {
			return json.path("ts").asLong();
		}

		public String getTopic() {
			return json.path("topic").asText();
		}
		public void publish() {
			MacGyverEventPublisher.this.publish(this);
		}
	}

	public MacGyverEventPublisher() {

	}

	public MacGyverEventPublisher(EventBus bus) {
		this.eventBus = eventBus;
	}

	public void publishObject(Object object) {
		eventBus.notify(object,Event.wrap(object));
	}


	protected void publish(EventBuilder event) {
		eventBus.notify(event.getJson(), Event.wrap(event.json));
	}



	public EventBuilder createEvent() {
		return new EventBuilder();
	}
	private static String getDefaultSource() {

		try {
			return InetAddress.getLocalHost().getHostName();

		} catch (Exception e) {
		}

		try {
			return InetAddress.getLocalHost().getHostAddress();

		} catch (Exception e) {
		}

		return "127.0.0.1";
	}
}