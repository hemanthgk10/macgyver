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

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class EC2InstanceScanner extends AWSServiceScanner {

	Logger logger = LoggerFactory.getLogger(EC2InstanceScanner.class);

	public EC2InstanceScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	}

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
		GraphNodeGarbageCollector gc = new GraphNodeGarbageCollector().neo4j(getNeoRxClient()).account(getAccountId()).label("AwsEc2Instance").region(region.getName());
		
		forEachInstance(region, instance -> {

			try {
				
				if (instance.getState().getName().equals("terminated")) {
					// instance is terminated
					// we may want to take the opportunity to delete it right here
				} else {
					JsonNode n = convertAwsObject(instance, region);
					NeoRxClient neoRx = getNeoRxClient();

					String subnetId = n.path("aws_subnetId").asText(null);
					String instanceArn = n.path("aws_arn").asText(null);
					String account = n.path("aws_account").asText(null);
					String imageId = n.path("aws_imageId").asText(null);

					Preconditions.checkNotNull(neoRx);

					Preconditions.checkState(!Strings.isNullOrEmpty(instanceArn), "aws_arn must not be null");
					Preconditions.checkState(!Strings.isNullOrEmpty(account), "aws_account must not be null");

					
					
					String createInstanceCypher = "merge (x:AwsEc2Instance {aws_arn:{instanceArn}}) set x+={props}, x.updateTs=timestamp() return x";
					neoRx.execCypher(createInstanceCypher, "instanceArn", instanceArn, "props", n).forEach(gc.MERGE_ACTION);
					
					if (!Strings.isNullOrEmpty(imageId)) {
						String amiArn = String.format("arn:aws:ec2:%s::image/%s", region.getName(), imageId);

					String mapToImageCypher = "match (x:AwsAmi {aws_arn:{amiArn}}), "
							+ "(y:AwsEc2Instance {aws_arn:{instanceArn}}) "
							+ "merge (y)-[r:USES]-(x) set r.updateTs=timestamp()";
					neoRx.execCypher(mapToImageCypher, "amiArn", amiArn, "instanceArn", instanceArn);
					}
					
					if (!Strings.isNullOrEmpty(subnetId)) {
						String subnetArn = String.format("arn:aws:ec2:%s:%s:subnet/%s", region.getName(), account,
								subnetId);
						String mapToSubnetCypher = "match (x:AwsSubnet {aws_arn:{subnetArn}}), "
								+ "(y:AwsEc2Instance {aws_arn:{instanceArn}}) "
								+ "merge (y)-[r:RESIDES_IN]->(x) set r.updateTs=timestamp()";
						neoRx.execCypher(mapToSubnetCypher, "subnetArn", subnetArn, "instanceArn", instanceArn);
						
					}
				}

			} catch (RuntimeException e) {
				logger.warn("problem scanning EC2 instance", e);
			}

		});
		
		gc.invoke();
		
	}
	
	private void forEachInstance(Region region, Consumer<Instance> consumer) { 		
		AmazonEC2Client client = getAWSServiceClient().createEC2Client(region);

		DescribeInstancesResult results = client.describeInstances(new DescribeInstancesRequest());
		String token = results.getNextToken();
		results.getReservations().forEach(reservation -> { 
			reservation.getInstances().forEach(consumer);
		});
		
		while (!Strings.isNullOrEmpty(token) && !token.equals("null")) { 
			results = client.describeInstances(new DescribeInstancesRequest().withNextToken(token));
			token = results.getNextToken();
			results.getReservations().forEach(reservation -> {
				reservation.getInstances().forEach(consumer);
			});
		}	
	}
}
