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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.amazonaws.regions.Region;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.gwt.thirdparty.guava.common.base.Preconditions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class RDSInstanceScanner extends AWSServiceScanner {

	public RDSInstanceScanner() {
		super();
	}
	public RDSInstanceScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	}

	@Override
	public Optional<String> computeArn(JsonNode n) {
		
		String region = n.path("aws_region").asText(null);
		String account = n.path("aws_account").asText(null);
		String dbInstanceId = n.path("aws_dbinstanceIdentifier").asText(null);
		
		Preconditions.checkState(!Strings.isNullOrEmpty(region), "aws_region not set");
		Preconditions.checkState(!Strings.isNullOrEmpty(account), "aws_account not set");
		Preconditions.checkState(!Strings.isNullOrEmpty(dbInstanceId), "aws_dbinstanceIdentifier not set");

		return Optional.of(String.format("arn:aws:rds:%s:%s:db:%s", region, account, dbInstanceId));
	}

	@Override
	public void scan(Region region) {
		AmazonRDSClient client = new AmazonRDSClient(getAWSServiceClient().getCredentialsProvider()).withRegion(region);
		DescribeDBInstancesResult result = client.describeDBInstances();
		
		GraphNodeGarbageCollector gc = new GraphNodeGarbageCollector().label("AwsRdsInstance").account(getAccountId()).region(region);
		result.getDBInstances().forEach(instance -> { 	
			try { 
				ObjectNode n = convertAwsObject(instance, region);
				NeoRxClient neoRx = getNeoRxClient();
				Preconditions.checkNotNull(neoRx);
				
				String rdsArn = n.path("aws_arn").asText();
				
				String cypher = "merge (x:AwsRdsInstance {aws_arn:{aws_arn}}) set x+={props} set x.updateTs=timestamp()";
				neoRx.execCypher(cypher, "aws_arn", rdsArn, "props",n).forEach(gc.MERGE_ACTION);
				
				List<String> subnets = getSubnets(instance);
				for (String s : subnets) { 
					String subnetArn = computeSubnetArn(s, n);
					
					String mapToSubnetCypher = "match (x:AwsRdsInstance {aws_arn:{rdsArn}}), "
							+ "(y:AwsSubnet {aws_arn:{subnetArn}}) "
							+ "merge (x)-[r:AVAILABLE_IN]->(y) set r.updateTs=timestamp()";
					neoRx.execCypher(mapToSubnetCypher, "rdsArn",rdsArn, "subnetArn",subnetArn);
				}
			} catch (RuntimeException e) { 
				logger.warn("problem scanning RDS Instance", e);
			}
		});
	
		gc.invoke();
	
	}
	
	
	protected String computeSubnetArn(String subnetId, ObjectNode n) {
		String region = n.path("aws_region").asText(null);
		String account = n.path("aws_account").asText(null);
		
		Preconditions.checkState(!Strings.isNullOrEmpty(region), "aws_region must not be null");
		Preconditions.checkState(!Strings.isNullOrEmpty(account), "aws_account must not be null");

		String subnetArn = String.format("arn:aws:ec2:%s:%s:subnet/%s", region, account, subnetId);
		
		return subnetArn;
	}
	
	
	protected List<String> getSubnets(DBInstance i) { 
		List<String> listToReturn = new ArrayList<>();
		JsonNode n = new ObjectMapper().valueToTree(i);
		
		JsonNode subnets = n.path("dbsubnetGroup").path("subnets");
		for (JsonNode s : subnets) { 
			String subnetId = s.path("subnetIdentifier").asText();
			if (!Strings.isNullOrEmpty(subnetId)) { 
				listToReturn.add(subnetId);
			}
		}		
		
		return listToReturn;
	}

}
