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
package io.macgyver.core.reactor;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class MacGyverMessage {

	static ObjectMapper mapper = new ObjectMapper();
	
	private JsonNode data = mapper.createObjectNode();
	
	private Instant timestamp = Instant.now();
	
	public MacGyverMessage() {
		
	}
	
	public MacGyverMessage withData(JsonNode n) {
	
		this.data = n;
		return this;
	}

	public JsonNode getData() {
		return data;
	}
	public MacGyverMessage withTimestamp(long instant) {
	
		return withTimestamp( Instant.ofEpochMilli(instant));
	
	}
	
	public Instant getTimestamp() {
		return timestamp;
	}
	public MacGyverMessage withTimestamp(Instant instant) {
		Preconditions.checkNotNull(instant);
		this.timestamp = instant;
		return this;
	}
	public MacGyverMessage withTimestamp(Date instant) {
		Preconditions.checkNotNull(instant);
		return withTimestamp(instant.getTime());
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
