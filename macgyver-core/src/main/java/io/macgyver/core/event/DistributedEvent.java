package io.macgyver.core.event;

import java.net.InetAddress;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DistributedEvent {

	static ObjectMapper mapper = new ObjectMapper();
	ObjectNode json = mapper.createObjectNode();

	public DistributedEvent() {
		json.put("ts", System.currentTimeMillis());
		json.put("source", getDefaultSource());
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

	public DistributedEvent payload(JsonNode n) {
		json.set("data", n);
		return this;
	}

	public DistributedEvent sourceHost(String source) {
		json.put("sourceHost", source);
		return this;
	}

	public DistributedEvent topic(String topic) {
		json.put("topic", topic);
		return this;
	}

	public DistributedEvent reference(String ref) {
		json.put("reference", ref);
		return this;
	}

	public JsonNode toJsonNode() {
		// maybe we want an immutable/deep-copy
		return json;
	}
}
