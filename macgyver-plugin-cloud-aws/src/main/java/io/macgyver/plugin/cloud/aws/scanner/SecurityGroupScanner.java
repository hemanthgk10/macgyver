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
import java.util.concurrent.atomic.AtomicLong;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;

import io.macgyver.core.util.JsonNodes;
import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class SecurityGroupScanner extends AWSServiceScanner {

	public SecurityGroupScanner() {
		super();
	}
	
	public SecurityGroupScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);

	}

	@Override
	public Optional<String> computeArn(JsonNode n) {
		return Optional.empty();
	}

	@Override
	public void scan(Region region) {

		AmazonEC2Client client = getAWSServiceClient().createEC2Client(region.getName());

		DescribeSecurityGroupsResult result = client.describeSecurityGroups();

		long now = System.currentTimeMillis();
		GraphNodeGarbageCollector gc = newGarbageCollector().region(region).label("AwsSecurityGroup");
		result.getSecurityGroups().forEach(sg -> {

			ObjectNode g = convertAwsObject(sg, region);

			// it is possible for a vpc to be in no vpc
			String vpcId = Strings.nullToEmpty(sg.getGroupId()); 
			String cypher = "merge (sg:AwsSecurityGroup {aws_vpcId: {vpcId}, aws_groupId: {groupId}}) set sg+={props}, sg.updateTs={now} return sg";

			JsonNode xx = getNeoRxClient()
					.execCypher(cypher, "vpcId", vpcId, "groupId", sg.getGroupId(), "props", g,"now",now).toBlocking()
					.first();
		
			gc.updateEarliestTimestamp(xx);
			cypher = "match (v:AwsVpc {aws_vpcId: {vpcId}}), (sg:AwsSecurityGroup {aws_groupId:{groupId}, aws_vpcId: {vpcId}}) merge (sg)-[:RESIDES_IN]->(v)";
			getNeoRxClient().execCypher(cypher, "vpcId", vpcId, "groupId", sg.getGroupId());
		});

		gc.invoke();

	}

}
