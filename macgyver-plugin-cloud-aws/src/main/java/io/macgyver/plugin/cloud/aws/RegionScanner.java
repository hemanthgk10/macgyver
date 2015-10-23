package io.macgyver.plugin.cloud.aws;

import java.util.Optional;

import com.amazonaws.regions.Region;
import com.fasterxml.jackson.databind.JsonNode;

import io.macgyver.neorx.rest.NeoRxClient;

public class RegionScanner extends AWSServiceScanner {

	public RegionScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client,neo4j);
	}

	@Override
	public Optional<String> computeArn(JsonNode n) {
		return Optional.empty();
	}

	@Override
	public void scan(Region region) {
		// TODO Auto-generated method stub
		
	}



}
