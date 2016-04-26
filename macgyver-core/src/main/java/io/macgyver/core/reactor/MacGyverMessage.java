package io.macgyver.core.reactor;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class MacGyverMessage {

	static ObjectMapper mapper = new ObjectMapper();
	
	private JsonNode data = mapper.createObjectNode();
	
	public MacGyverMessage() {
		
	}
	
	public MacGyverMessage withData(JsonNode n) {
	
		this.data = n;
		return this;
	}

	public JsonNode getJsonNode() {
		return data;
	}
	
	public MacGyverMessage withAttribute(String key, String val) {
		((ObjectNode)data).put(key,val);
		return this;
	}
	public MacGyverMessage withAttribute(String key, JsonNode val) {
		((ObjectNode)data).set(key,val);
		return this;
	}
}
