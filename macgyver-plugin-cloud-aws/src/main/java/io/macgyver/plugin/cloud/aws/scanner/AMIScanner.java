package io.macgyver.plugin.cloud.aws.scanner;

import java.util.Optional;

import joptsimple.internal.Strings;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class AMIScanner extends AWSServiceScanner {

	public AMIScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	}

	@Override
	public Optional<String> computeArn(JsonNode n) {
		
		String region = n.path("aws_region").asText(null);
		String imageId = n.path("aws_imageId").asText(null);
		
		Preconditions.checkState(!Strings.isNullOrEmpty(region), "aws_region not set");
		Preconditions.checkState(!Strings.isNullOrEmpty(imageId), "aws_imageId not set");

		return Optional.of(String.format("arn:aws:ec2:%s::image/%s", region, imageId));
	}

	@Override
	public void scan(Region region) {
		AmazonEC2Client c = getAWSServiceClient().createEC2Client(region);
		
		NeoRxClient neoRx = getNeoRxClient();
		Preconditions.checkNotNull(neoRx);
		
		DescribeImagesRequest req = new DescribeImagesRequest().withOwners("self");
		DescribeImagesResult result = c.describeImages(req);
		
		result.getImages().forEach(i -> { 
			try { 
				ObjectNode n = convertAwsObject(i, region);
				
				String cypher = "merge (x:AwsAmi {aws_arn:{aws_arn}}) set x+={props} set x.updateTs=timestamp()";
				neoRx.execCypher(cypher, "aws_arn", n.path("aws_arn").asText(), "props",n);		
				
			} catch (RuntimeException e) { 
				logger.warn("problem scanning AMI", e);
			}
		});
	}

}
