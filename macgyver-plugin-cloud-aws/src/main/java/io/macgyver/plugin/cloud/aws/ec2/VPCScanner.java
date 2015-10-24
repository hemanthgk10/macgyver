package io.macgyver.plugin.cloud.aws.ec2;

import java.util.Optional;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;
import io.macgyver.plugin.cloud.aws.AWSServiceScanner;

public class VPCScanner extends AWSServiceScanner {

	Logger logger = LoggerFactory.getLogger(VPCScanner.class);
	ObjectMapper mapper = new ObjectMapper();

	public VPCScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);

	}



	
	

	
	@Override
	public void scan(Region region) {

		
			AmazonEC2Client c = getAWSServiceClient().createEC2Client(region);

			DescribeVpcsResult result = c.describeVpcs();

			
			result.getVpcs().forEach(it -> {
				try {
					ObjectNode n = convertAwsObject(it, region);
					
					
					String cypher = "MERGE (v:AwsVpc {aws_arn:{aws_arn}}) set v+={props}, v.updateTs=timestamp()";
					
					NeoRxClient client = getNeoRxClient();
					Preconditions.checkNotNull(client);
					client.execCypher(cypher, "aws_arn",n.get("aws_arn").asText(),"props",n);

				} catch (RuntimeException e) {
					logger.warn("problem scanning vpc",e);
				}
			});

			
	

	}




	@Override
	public Optional<String> computeArn(JsonNode n){
		
		String region = n.get("aws_region").asText();
		
		return Optional.of(String.format("arn:aws:ec2:%s:%s:vpc/%s",region,n.get("aws_account").asText(),n.get("aws_vpcId").asText()));
	}
}
