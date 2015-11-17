package io.macgyver.plugin.cloud.aws.scanner;

import java.util.Optional;

import com.amazonaws.regions.Region;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class AccountScanner extends AWSServiceScanner {

	public AccountScanner(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	}

	@Override
	public Optional<String> computeArn(JsonNode n) {
		return Optional.empty();
	}

	@Override
	public void scan(Region region) {
	
			String cypher = "merge (x:AwsAccount {aws_account:{aws_account}}) set x.updateTs=timestamp()";
		
			NeoRxClient neoRx = getNeoRxClient();
			Preconditions.checkNotNull(neoRx);
			Preconditions.checkNotNull(getAccountId(),"accountId must be set on AWSServiceClient");
			neoRx.execCypher(cypher, "aws_account", getAccountId());
		
	}

}
