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
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class RegionScanner extends AWSServiceScanner {
	
	Logger logger = LoggerFactory.getLogger(RegionScanner.class);

	public RegionScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client,neo4j);
	}

	@Override
	public Optional<String> computeArn(JsonNode n) {
		return Optional.empty();
	}

	@Override
	public void scan(Region region) {

		AmazonEC2Client c = getAWSServiceClient().createEC2Client(region);

		DescribeRegionsResult result = c.describeRegions();
		result.getRegions().forEach(it -> {
			try {
				ObjectNode n = convertAwsObject(it, region);
				
				n.remove("aws_account");
				String cypher = "merge (x:AwsRegion {aws_regionName:{aws_regionName}}) set x+={props}  remove x.aws_region,x.aws_account set x.updateTs=timestamp()";
				
				NeoRxClient neoRx = getNeoRxClient();
				Preconditions.checkNotNull(neoRx);
				
				neoRx.execCypher(cypher, "aws_regionName",n.path("aws_regionName").asText(), "aws_region",n.path("aws_region").asText(), "props",n);
			
			} catch (RuntimeException e) { 
				logger.warn("problem scanning regions",e);
			}		
		});
	}



}
