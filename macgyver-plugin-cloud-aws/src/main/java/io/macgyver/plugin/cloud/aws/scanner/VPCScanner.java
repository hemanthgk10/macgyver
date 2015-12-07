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
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class VPCScanner extends AWSServiceScanner {

	Logger logger = LoggerFactory.getLogger(VPCScanner.class);

	public VPCScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);

	}

	@Override
	public void scan(Region region) {

		AmazonEC2Client c = getAWSServiceClient().createEC2Client(region);

		DescribeVpcsResult result = c.describeVpcs();

		GraphNodeGarbageCollector gc = newGarbageCollector().region(region).label("AwsVpc");
		NeoRxClient neoRx = getNeoRxClient();
		Preconditions.checkNotNull(neoRx);

		result.getVpcs().forEach(it -> {
			try {					
				ObjectNode n = convertAwsObject(it, region);
									
				String cypher = "merge (x:AwsVpc {aws_arn:{aws_arn}}) set x+={props} set x.updateTs=timestamp() return x";
				
				String mapToSubnetCypher = "match (y:AwsSubnet {aws_vpcId:{aws_vpcId}}), "
						+ "(x:AwsVpc {aws_arn:{aws_arn}}) "
						+ "merge (x)-[r:CONTAINS]->(y) set r.updateTs=timestamp()";
				
				neoRx.execCypher(cypher, "aws_arn",n.path("aws_arn").asText(), "props",n).forEach(gc.MERGE_ACTION);
				neoRx.execCypher(mapToSubnetCypher, "aws_arn",n.path("aws_arn").asText(), "aws_vpcId",n.path("aws_vpcId").asText());	
			} catch (RuntimeException e) { 
				logger.warn("problem scanning VPC", e);
			}
		});
	
		String mapAccountCypher = "match (x:AwsAccount {aws_account:{aws_account}}), (y:AwsVpc {aws_account:{aws_account}}) "
				+ "merge (x)-[r:OWNS]->(y) set r.updateTs=timestamp()";
		String mapRegionCypher = "match (x:AwsVpc {aws_region:{aws_region}}), (y:AwsRegion {aws_regionName:{aws_region}, aws_account:{aws_account}}) "
				+ "merge (x)-[r:RESIDES_IN]->(y) set r.updateTs=timestamp()";
		
		neoRx.execCypher(mapAccountCypher, "aws_account",getAccountId());
		neoRx.execCypher(mapRegionCypher, "aws_region", region.getName(), "aws_account",getAccountId());
		gc.invoke();
	}
	

	@Override
	public Optional<String> computeArn(JsonNode n) {

		String region = n.get("aws_region").asText();

		return Optional.of(String.format("arn:aws:ec2:%s:%s:vpc/%s", region, n.get("aws_account").asText(),
				n.get("aws_vpcId").asText()));
	}
}
