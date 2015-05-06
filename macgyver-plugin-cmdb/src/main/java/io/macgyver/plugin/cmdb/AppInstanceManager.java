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
package io.macgyver.plugin.cmdb;

import io.macgyver.core.MacGyverException;

import io.macgyver.neorx.rest.NeoRxClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;

public class AppInstanceManager {
	Logger logger = LoggerFactory.getLogger(AppInstanceManager.class);

	@Autowired
	NeoRxClient neo4j;

	

	
	CheckInProcessor processor = new BasicCheckInProcessor();


	public ObjectNode processCheckIn(ObjectNode data) {

		String host = data.path("host").asText();
		String group = data.path("groupId").asText();
		String app = data.path("appId").asText();

		
		if (host.toLowerCase().equals("unknown") || host.toLowerCase().equals("localhost")) {
			return new ObjectMapper().createObjectNode();
		} else {
			if (Strings.isNullOrEmpty(group)) {
				group = "";
			}
			logger.debug("host:{} group:{} app:{}", host, group, app);
	
			if (!Strings.isNullOrEmpty(host) && !Strings.isNullOrEmpty(app)) {	
				ObjectNode set = new ObjectMapper().createObjectNode();
				set.setAll(data);
				set.put("lastContactTs", System.currentTimeMillis());
								
				ObjectNode p = new ObjectMapper().createObjectNode();
				p.put("h", host);
				p.put("gi", group);
				p.put("ai", app);
				p.set("props", set);
							
				String cypher = "merge (x:AppInstance {host:{h}, groupId:{gi}, appId:{ai}}) set x={props} return x";
	
				JsonNode r = neo4j.execCypher(cypher, p).toBlocking().firstOrDefault(null);
				if (r!=null) {
					return (ObjectNode) r;
				}
			}
			return new ObjectMapper().createObjectNode();
		}
	}
	public CheckInProcessor getCheckInProcessor() {
		return processor;
	}
	public void setCheckInProcessor(CheckInProcessor p) {
		this.processor = p;
	}

}
