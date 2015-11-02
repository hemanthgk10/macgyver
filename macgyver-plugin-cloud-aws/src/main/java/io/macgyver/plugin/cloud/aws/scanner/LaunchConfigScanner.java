package io.macgyver.plugin.cloud.aws.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.amazonaws.regions.Region;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.DescribeLaunchConfigurationsResult;
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class LaunchConfigScanner extends AWSServiceScanner {

	public LaunchConfigScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	}

	@Override
	public Optional<String> computeArn(JsonNode n) {
		return Optional.of(n.path("aws_launchConfigurationARN").asText());
	}

	@Override
	public void scan(Region region) {

		AmazonAutoScalingClient client = new AmazonAutoScalingClient(getAWSServiceClient().getCredentialsProvider())
				.withRegion(region);
		DescribeLaunchConfigurationsResult results = client.describeLaunchConfigurations();
		results.getLaunchConfigurations().forEach(config -> {
			try {
				ObjectNode n = convertAwsObject(config, region);
				List<String> securityGroups = getSecurityGroups(config);

				String cypher = "merge (x:AwsLaunchConfig {aws_arn:{aws_arn}}) set x+={props}, x.aws_securityGroups={sg}, x.updateTimestamp=timestamp()";

				NeoRxClient neoRx = new NeoRxClient();
				Preconditions.checkNotNull(neoRx);

				neoRx.execCypher(cypher, "aws_arn", n.path("aws_arn").asText(), "props", n, "sg", securityGroups);
			} catch (RuntimeException e) {
				logger.warn("problem scanning launch configs", e);
			}
		});

	}

	protected List<String> getSecurityGroups(LaunchConfiguration c) {
		List<String> toReturnList = new ArrayList<>();
		JsonNode n = new ObjectMapper().valueToTree(c);

		JsonNode securityGroups = n.path("securityGroups");
		for (JsonNode sg : securityGroups) {
			toReturnList.add(sg.asText());
		}

		return toReturnList;
	}

}
