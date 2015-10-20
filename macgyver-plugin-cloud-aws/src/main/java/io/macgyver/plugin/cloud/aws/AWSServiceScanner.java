package io.macgyver.plugin.cloud.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.macgyver.core.service.ServiceScanner;
import io.macgyver.neorx.rest.NeoRxClient;

public class AWSServiceScanner {

	AWSServiceClient client;
	Logger logger = LoggerFactory.getLogger(AWSServiceScanner.class);
	static ObjectMapper mapper = new ObjectMapper();
	NeoRxClient neo4j;
	
	public AWSServiceScanner(AWSServiceClient client, NeoRxClient neo4j) {
		this.client = client;
		this.neo4j = neo4j;
	}
	
	public AWSServiceClient getAWSServiceClient() {
		return client;
	}

}
