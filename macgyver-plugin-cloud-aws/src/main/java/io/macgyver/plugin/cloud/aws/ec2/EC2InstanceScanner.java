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
package io.macgyver.plugin.cloud.aws.ec2;

import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

public class EC2InstanceScanner {

	AWSServiceClient client;
	Logger logger = LoggerFactory.getLogger(EC2InstanceScanner.class);
	ObjectMapper m = new ObjectMapper();
	NeoRxClient neo4j;
	public EC2InstanceScanner(AWSServiceClient client, NeoRxClient neo4j) {
		this.client = client;
		this.neo4j = neo4j;
	}
	
	public void scan() {
		
		AmazonEC2Client x = client.createEC2Client(Regions.DEFAULT_REGION);
		
		DescribeRegionsResult drr = x.describeRegions();
		
		for (com.amazonaws.services.ec2.model.Region r : drr.getRegions()) {
			scanRegion(r);
		}
	
	}
	

	public void writeInstance(JsonNode instanceNode) throws IOException {

	
		String cypher = "MERGE (ci:ComputeInstance {provider: 'ec2', ec2_regionName: {regionName}, ec2_instanceId: {instanceId}}) ON MATCH set ci += {props}, ci.updateTs={now} ON CREATE set ci += {props}, ci.createTs={now},ci.updateTs={now} return ci";
		neo4j.execCypher(cypher, "instanceId",instanceNode.get("ec2_instanceId").asText(),"regionName",instanceNode.get("ec2_regionName").asText(),"props",instanceNode, "now", System.currentTimeMillis());
	
	}
	
	public void scanRegion(com.amazonaws.services.ec2.model.Region r) {
		logger.info("scanning region {} for instances",r.getRegionName());
	
		AmazonEC2Client x = client.createEC2Client(r.getRegionName());
		DescribeInstancesResult dir = x.describeInstances();
		for (Reservation reservation: dir.getReservations()) {
		
			String reservationId = reservation.getReservationId();
			for (Instance instance: reservation.getInstances()) {
				logger.info("instance: {}",instance);
				try {
					ObjectNode instanceNode = (ObjectNode) flattenInstance(m.readTree(m.writeValueAsString(instance)));
					instanceNode.put("ec2_reservationId", reservationId);
					instanceNode.put("ec2_regionName", r.getRegionName());
					
					writeInstance(instanceNode);
				
				}
				catch ( IOException e) {
					logger.warn("",e);
				}
				
			}
			
		}
	}
	
	public JsonNode flattenInstance(JsonNode n) throws IOException {
		ObjectNode target = m.createObjectNode();
		Iterator<String> fields = n.fieldNames();
		while (fields.hasNext()) {
			String fieldName = fields.next();
	
			JsonNode val = n.get(fieldName);
			if (val.isContainerNode()) {
				logger.debug("skipping nested property: "+fieldName);
			}
			else {
				target.set(mangleName(fieldName), val);
			}
		}
	
		for (JsonNode tag : Lists.newArrayList(n.get("tags").iterator())) {
			if (tag.path("key").asText().toLowerCase().equals("name")) {
				target.put("name", tag.path("value").asText());
			}
			else if (tag.path("key").asText().toLowerCase().equals("description")) {
				target.put("description", tag.path("value").asText());
			}
			
		}
		target.put("privateIpAddress", Strings.emptyToNull(n.path("privateIpAddress").asText(null)));
		target.put("privateDnsName", Strings.emptyToNull(n.path("privateDnsName").asText(null)));
		target.put("state", Strings.emptyToNull(n.path("state").path("name").asText(null)));
		target.put("launchTs", n.path("launchTime").asLong(0));

		return target;
	}
	String mangleName(String f) {
		return "ec2_"+f;
	}
	
	public String calculateMacId(JsonNode n) {
		String macId = "ec2-"+n.get("ec2_regionName").asText()+"-"+n.get("ec2_instanceId").asText();
		return macId;
	}
}
