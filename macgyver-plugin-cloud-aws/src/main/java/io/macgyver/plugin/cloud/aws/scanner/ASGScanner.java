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

import com.amazonaws.regions.Region;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class ASGScanner extends AWSServiceScanner {
	NeoRxClient neoRx = new NeoRxClient();
	ObjectMapper mapper = new ObjectMapper();

	public ASGScanner() {
		super();
	}
	public ASGScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	}

	@Override
	public Optional<String> computeArn(JsonNode n) {
		return Optional.of(n.path("aws_autoScalingGroupARN").asText());
	}

	@Override
	public void scan(Region region) {

		GraphNodeGarbageCollector gc = newGarbageCollector().label("AwsAsg").region(region);
			AmazonAutoScalingClient client = new AmazonAutoScalingClient(getAWSServiceClient().getCredentialsProvider()).withRegion(region); 
			DescribeAutoScalingGroupsResult results = client.describeAutoScalingGroups();
			results.getAutoScalingGroups().forEach(asg -> {
				ObjectNode n = convertAwsObject(asg, region);
				String asgArn = n.path("aws_arn").asText();
				
				String cypher = "merge (x:AwsAsg {aws_arn:{aws_arn}}) set x+={props}, x.updateTs=timestamp() return x";
				
				Preconditions.checkNotNull(neoRx);
				neoRx.execCypher(cypher, "aws_arn",asgArn, "props",n).forEach(gc.MERGE_ACTION);

				mapAsgRelationships(asg, asgArn, region.getName());				
				
			});
			gc.invoke();
	}

	protected void mapAsgRelationships(AutoScalingGroup asg, String asgArn, String region) { 
		JsonNode n = mapper.valueToTree(asg);
 
		String subnets = n.path("vpczoneIdentifier").asText().trim();
		String launchConfig = n.path("launchConfigurationName").asText().trim();
		JsonNode instances = n.path("instances");
		JsonNode elbs = n.path("loadBalancerNames");
		
		mapAsgToSubnet(subnets, asgArn, region);
		mapAsgToLaunchConfig(launchConfig, asgArn, region);
		mapAsgToInstance(instances, asgArn, region);
		mapAsgToElb(elbs, asgArn, region);
		
	}
	
	protected void mapAsgToLaunchConfig(String launchConfig, String asgArn, String region) { 
		String cypher = "match (x:AwsAsg {aws_arn:{asgArn}}), (y:AwsLaunchConfig {aws_launchConfigurationName:{lcn}, aws_region:{region}, aws_account:{account}}) "
				+ "merge (x)-[r:HAS]->(y) set r.updateTs=timestamp()";
		neoRx.execCypher(cypher, "asgArn",asgArn, "lcn",launchConfig, "region",region, "account",getAccountId());
	}
	
	protected void mapAsgToSubnet(String subnets, String asgArn, String region) {
		String[] arr = subnets.split(",");
		for (String s : arr) { 
			String subnetArn = String.format("arn:aws:ec2:%s:%s:subnet/%s",region, getAccountId() ,s.trim());

			String cypher = "match (x:AwsAsg {aws_arn:{asgArn}}), (y:AwsSubnet {aws_arn:{subnetArn}}) "
					+ "merge (x)-[r:LAUNCHES_INSTANCES_IN]->(y) set r.updateTs=timestamp()";
			neoRx.execCypher(cypher, "asgArn",asgArn, "subnetArn",subnetArn);
		}
	}
	
	protected void mapAsgToInstance(JsonNode instances, String asgArn, String region) {
		for (JsonNode i : instances) { 
			String instanceId = i.path("instanceId").asText();
			String instanceArn = String.format("arn:aws:ec2:%s:%s:instance/%s", region, getAccountId(), instanceId);

			String cypher = "match (x:AwsEc2Instance {aws_arn:{instanceArn}}), (y:AwsAsg {aws_arn:{asgArn}}) "
					+ "merge (y)-[r:CONTAINS]->(x) set r.updateTs=timestamp()";
			neoRx.execCypher(cypher, "instanceArn",instanceArn, "asgArn",asgArn);
		}
	}

	protected void mapAsgToElb(JsonNode elbs, String asgArn, String region) {
		for (JsonNode e : elbs) { 
			String elbName = e.asText();
			String elbArn = String.format("arn:aws:elasticloadbalancing:%s:%s:loadbalancer/%s", region, getAccountId(), elbName);

			String cypher = "match (x:AwsElb {aws_arn:{elbArn}}), (y:AwsAsg {aws_arn:{asgArn}}) "
					+ "merge (y)-[r:ATTACHED_TO]-(x) set r.updateTs=timestamp()";
			neoRx.execCypher(cypher, "elbArn",elbArn, "asgArn",asgArn);
		}
	}
		
	
	
}
