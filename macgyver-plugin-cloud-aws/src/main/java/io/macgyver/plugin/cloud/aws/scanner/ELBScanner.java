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
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class ELBScanner extends AWSServiceScanner {
	ObjectMapper mapper = new ObjectMapper();
	NeoRxClient neoRx = getNeoRxClient();

	public ELBScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	}

	@Override
	public Optional<String> computeArn(JsonNode n) {
		return Optional
				.of(String.format("arn:aws:elasticloadbalancing:%s:%s:loadbalancer/%s", n.path("aws_region").asText(),
						n.path("aws_account").asText(), n.path("aws_loadBalancerName").asText()));
	}

	@Override
	public void scan(Region region) {

		AmazonElasticLoadBalancingClient client = new AmazonElasticLoadBalancingClient(
				getAWSServiceClient().getCredentialsProvider()).withRegion(region);
		DescribeLoadBalancersResult results = client.describeLoadBalancers();
		GraphNodeGarbageCollector gc = new GraphNodeGarbageCollector().neo4j(getNeoRxClient()).region(region.getName()).account(getAccountId()).label("AwsElb");
		results.getLoadBalancerDescriptions().forEach(lb -> {
			try {
				ObjectNode n = convertAwsObject(lb, region);

				String elbArn = n.path("aws_arn").asText();

				String cypher = "merge (x:AwsElb {aws_arn:{aws_arn}}) set x+={props} set x.updateTs=timestamp() return x";

				Preconditions.checkNotNull(neoRx);

				neoRx.execCypher(cypher, "aws_arn", elbArn, "props", n).forEach(gc.MERGE_ACTION);

				mapElbRelationships(lb, elbArn, region.getName());
			} catch (RuntimeException e) {
				logger.warn("problem scanning ELBs", e);
			}

		});

		gc.invoke();
	}

	protected void mapElbRelationships(LoadBalancerDescription lb, String elbArn, String region) {
		JsonNode n = mapper.valueToTree(lb);
		JsonNode subnets = n.path("subnets");
		JsonNode instances = n.path("instances");
		JsonNode securityGroups = n.path("securityGroups");

		mapElbToSubnet(subnets, elbArn, region);
		mapElbToInstance(instances, elbArn, region);
		addSecurityGroups(securityGroups, elbArn);

	}

	protected void addSecurityGroups(JsonNode securityGroups, String elbArn) {
		List<String> l = new ArrayList<>();
		for (JsonNode s : securityGroups) {
			l.add(s.asText());
		}

		String cypher = "match (x:AwsElb {aws_arn:{aws_arn}}) set x.aws_securityGroups={sg}";
		neoRx.execCypher(cypher, "aws_arn", elbArn, "sg", l);
	}

	protected void mapElbToSubnet(JsonNode subnets, String elbArn, String region) {

		for (JsonNode s : subnets) {
			String subnetName = s.asText();
			String subnetArn = String.format("arn:aws:subnet:%s:%s:subnet/%s", region, getAccountId(), subnetName);
			String cypher = "match (x:AwsElb {aws_arn:{elbArn}}), (y:AwsSubnet {aws_arn:{subnetArn}}) "
					+ "merge (x)-[r:AVAILABLE_IN]->(y) set r.updateTs=timestamp()";
			neoRx.execCypher(cypher, "elbArn",elbArn, "subnetArn",subnetArn);					
		}
	}

	protected void mapElbToInstance(JsonNode instances, String elbArn, String region) {

		for (JsonNode i : instances) {
			String instanceName = i.path("instanceId").asText();
			String instanceArn = String.format("arn:aws:ec2:%s:%s:instance/%s", region, getAccountId(), instanceName);
			String cypher = "match (x:AwsElb {aws_arn:{elbArn}}), (y:AwsEc2Instance {aws_arn:{instanceArn}}) "
					+ "merge (x)-[r:DISTRIBUTES_TRAFFIC_TO]->(y) set r.updateTs=timestamp()";
			neoRx.execCypher(cypher, "elbArn",elbArn, "instanceArn",instanceArn);

		}
	}
}
