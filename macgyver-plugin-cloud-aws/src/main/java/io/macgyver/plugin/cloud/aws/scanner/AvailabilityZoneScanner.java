package io.macgyver.plugin.cloud.aws.scanner;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

import java.util.Optional;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

public class AvailabilityZoneScanner extends AWSServiceScanner {

	public AvailabilityZoneScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	}

	@Override
	public Optional<String> computeArn(JsonNode n) {
		return Optional.empty();
	}

	@Override
	public void scan(Region region) {
		AmazonEC2Client c = getAWSServiceClient().createEC2Client(region);

		GraphNodeGarbageCollector gc = newGarbageCollector().region(region).label("AwsAvailabilityZone");
		DescribeAvailabilityZonesResult result = c.describeAvailabilityZones();
		result.getAvailabilityZones().forEach(it -> {
			
			try {
				ObjectNode n = convertAwsObject(it, region);
				
				String cypher = "merge (y:AwsAvailabilityZone {aws_zoneName:{aws_zoneName}, aws_region:{aws_region}, aws_account:{aws_account}}) set y+={props} set y.updateTs=timestamp() return y";
				String mapToSubnetCypher = "match (x:AwsSubnet {aws_availabilityZone:{aws_zoneName}, aws_region:{aws_region}, aws_account:{aws_account}}), "
						+ "(y:AwsAvailabilityZone {aws_zoneName:{aws_zoneName}, aws_region:{aws_region}, aws_account:{aws_account}}) "
						+ "merge (x)-[r:RESIDES_IN]->(y) set r.updateTs=timestamp()";
				String mapToRegionCypher = "match (x:AwsRegion {aws_regionName:{aws_region}, aws_account:{aws_account}}), "
						+ "(y:AwsAvailabilityZone {aws_zoneName:{aws_zoneName}, aws_regionName:{aws_region}, aws_account:{aws_account}}) "
						+ "merge (x)-[r:CONTAINS]->(y) set r.updateTs=timestamp()";
				
				NeoRxClient neoRx = getNeoRxClient();	
				Preconditions.checkNotNull(neoRx);
			
				neoRx.execCypher(cypher, "aws_zoneName",n.path("aws_zoneName").asText(), "aws_region",n.path("aws_region").asText(), "aws_account",getAccountId(), "props",n).forEach(gc.MERGE_ACTION);
				neoRx.execCypher(mapToSubnetCypher, "aws_zoneName",n.path("aws_zoneName").asText(), "aws_region",n.path("aws_region").asText(), "aws_account",getAccountId());
				neoRx.execCypher(mapToRegionCypher, "aws_zoneName",n.path("aws_zoneName").asText(), "aws_region",n.path("aws_region").asText(), "aws_account",getAccountId());

			} catch (RuntimeException e) { 
				logger.warn("problem scanning availability zones",e);
			}		
		});		
		gc.invoke();
	}

}
