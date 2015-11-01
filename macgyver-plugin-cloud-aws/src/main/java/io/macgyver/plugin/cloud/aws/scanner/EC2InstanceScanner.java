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

import io.macgyver.core.util.JsonNodes;
import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;
import joptsimple.internal.Strings;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2Client;

import com.amazonaws.services.ec2.model.DescribeInstancesResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gwt.thirdparty.guava.common.base.Preconditions;

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

		String region = n.path("aws_region").asText(null);
		String account = n.path("aws_account").asText(null);
		String instanceId = n.path("aws_instanceId").asText(null);

		Preconditions.checkState(!Strings.isNullOrEmpty(region), "aws_region not set");
		Preconditions.checkState(!Strings.isNullOrEmpty(account), "aws_account not set");
		Preconditions.checkState(!Strings.isNullOrEmpty(instanceId), "aws_instanceId not set");

		return Optional.of(String.format("arn:aws:ec2:%s:%s:instance/%s", region, account, instanceId));

	}

	@Override
	public void scan(Region region) {

		AmazonEC2Client client = getAWSServiceClient().createEC2Client(region);

		DescribeInstancesResult result = client.describeInstances();

		result.getReservations().forEach(reservation -> {

			reservation.getInstances().forEach(instance -> {

				JsonNode n = convertAwsObject(instance, region);

				String subnetId = n.path("aws_subnetId").asText(null);
				String arn = n.path("aws_arn").asText(null);
				String account = n.path("aws_account").asText(null);
				Preconditions.checkState(!Strings.isNullOrEmpty(subnetId),"aws_subnetId must not be null");
				Preconditions.checkState(!Strings.isNullOrEmpty(arn),"aws_arn must not be null");
				Preconditions.checkState(!Strings.isNullOrEmpty(account),"aws_account must not be null");
				// This is technically ambiguous since subnetId's are not guaranteed to be unique across accounts
				// need to qualify with account.  Or more properly, the computed arn of the subnet
				
				String cypher = "match (x:AwsSubnet {aws_subnetId:{aws_subnetId}, aws_account:{aws_account}}) "
						+ "merge (y:AwsEc2Instance {aws_arn:{arn}}) set y+={props}, y.updateTs=timestamp() "
						+ "merge (y)-[:RESIDES_IN]->(x)";

				NeoRxClient neoRx = getNeoRxClient();
				neoRx.execCypher(cypher, "aws_subnetId", subnetId, "aws_account",account,"arn",
						arn, "props", n);

			});

		});

	}

}
