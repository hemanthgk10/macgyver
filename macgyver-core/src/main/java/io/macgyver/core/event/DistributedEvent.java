/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

	public static DistributedEvent create() {
		return new DistributedEvent();
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
}
