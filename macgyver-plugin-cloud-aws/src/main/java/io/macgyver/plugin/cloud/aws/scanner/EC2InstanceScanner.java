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
package io.macgyver.plugin.cloud.aws.scanner;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;

import com.amazonaws.services.ec2.model.DescribeInstancesResult;

import com.fasterxml.jackson.databind.JsonNode;


public class EC2InstanceScanner extends AWSServiceScanner {

	Logger logger = LoggerFactory.getLogger(EC2InstanceScanner.class);

	public EC2InstanceScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	}

	/*
	 * public void writeInstance(JsonNode instanceNode) throws IOException {
	 * 
	 * 
	 * String cypher =
	 * "MERGE (ci:ComputeInstance {provider: 'ec2', ec2_regionName: {regionName}, ec2_instanceId: {instanceId}}) ON MATCH set ci += {props}, ci.updateTs={now} ON CREATE set ci += {props}, ci.createTs={now},ci.updateTs={now} return ci"
	 * ; getNeoRxClient().execCypher(cypher,
	 * "instanceId",instanceNode.get("ec2_instanceId").asText(),"regionName",
	 * instanceNode.get("ec2_regionName").asText(),"props",instanceNode, "now",
	 * System.currentTimeMillis());
	 * 
	 * }
	 */

	@Override
	public Optional<String> computeArn(JsonNode n) {
		
		return Optional.of(String.format("arn:aws:ec2:%s:%s:instance/%s", n.path("aws_region").asText(),n.path("aws_account").asText(),n.path("aws_instanceId").asText()));

	}

	@Override
	public void scan(Region region) {
		try { 
			AmazonEC2Client client = getAWSServiceClient().createEC2Client(region);
	
			DescribeInstancesResult result = client.describeInstances();
	
			result.getReservations().forEach(reservation -> {
				
				reservation.getInstances().forEach(instance -> {
					
					JsonNode n = convertAwsObject(instance, region);
	
					String cypher = "match (x:AwsSubnet {aws_subnetId:{aws_subnetId}}) "
							+ "merge (y:AwsEc2Instance {aws_arn:{arn}}) set y+={props}, y.updateTs=timestamp() "
							+ "merge (y)-[:RESIDES_IN]->(x)";
					
					NeoRxClient neoRx = getNeoRxClient();
					neoRx.execCypher(cypher, "aws_subnetId",n.path("aws_subnetId").asText(), "arn",n.path("aws_arn").asText(),"props",n);
					
	
				});
				
			});
		} catch (RuntimeException e) {
			logger.warn("problem scanning EC2 instances",e);
		}

	}

}
