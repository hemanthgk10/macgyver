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

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class SubnetScanner extends AWSServiceScanner {

	Logger logger = LoggerFactory.getLogger(SubnetScanner.class);
	public SubnetScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
		
	}

	
	@Override
	public Optional<String> computeArn(JsonNode n){
		
		String region = n.get("aws_region").asText();
		
		return Optional.of(String.format("arn:aws:ec2:%s:%s:subnet/%s",region,n.get("aws_account").asText(),n.get("aws_subnetId").asText()));
		
	}
	


	@Override
	public void scan(Region region) {
		AmazonEC2Client c = getAWSServiceClient().createEC2Client(region);

		DescribeSubnetsResult result = c.describeSubnets();

		GraphNodeGarbageCollector gc = newGarbageCollector().label("AwsSubnet").region(region);
		
		result.getSubnets().forEach(it -> {
			try {
				ObjectNode n = convertAwsObject(it, region);
				
				
				String cypher = "MERGE (v:AwsSubnet {aws_arn:{aws_arn}}) set v+={props}, v.updateTs=timestamp() return v";
				
				NeoRxClient client = getNeoRxClient();
				Preconditions.checkNotNull(client);
				client.execCypher(cypher, "aws_arn",n.get("aws_arn").asText(),"props",n).forEach(gc.MERGE_ACTION);

			} catch (RuntimeException e) {
				logger.warn("problem scanning subnets",e);
			}
		});
		
		gc.invoke();
	}

	

}
