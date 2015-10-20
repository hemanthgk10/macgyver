package io.macgyver.plugin.cloud.aws.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeVpcAttributeRequest;
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.GetUserRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;
import io.macgyver.plugin.cloud.aws.AWSServiceScanner;

public class VPCServiceScanner extends AWSServiceScanner {

	ObjectMapper mapper = new ObjectMapper();

	public VPCServiceScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);

	}

	ObjectNode flatten(ObjectNode n) {
		ObjectNode r = mapper.createObjectNode();

		n.fields().forEachRemaining(it -> {

			if (!it.getValue().isContainerNode()) {
				r.set(it.getKey(), it.getValue());
			}

		});

		n.path("tags").iterator().forEachRemaining(it -> {
			String tagKey = "tag_"+it.path("key").asText();
			r.put(tagKey, it.path("value").asText());
		});

				
		System.out.println(r);
		return r;
	}

	public void scan(String region) {

		try {
			
			
		
		
			AmazonEC2Client c = getAWSServiceClient().createEC2Client(region);

		
			
			System.out.println(c.describeAccountAttributes().getAccountAttributes());

			DescribeVpcsResult result = c.describeVpcs();

			
			result.getVpcs().forEach(it -> {
				try {
					ObjectNode n = flatten(mapper.valueToTree(it));
					n.put("region", region);
					
					String cypher = "MERGE (v:AwsVpc {region:{region}, vpcId:{vpcId}) set v={props}";

				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			});

			System.out.println(result);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

	}
}
