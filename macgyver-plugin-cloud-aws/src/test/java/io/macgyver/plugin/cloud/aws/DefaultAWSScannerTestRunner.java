package io.macgyver.plugin.cloud.aws;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClientImpl;
import io.macgyver.plugin.cloud.aws.scanner.DefaultAWSScannerGroup;

public class DefaultAWSScannerTestRunner {


	
	public static void main(String [] args) {
		
		NeoRxClient neo4j = new NeoRxClient();
		
		AWSServiceClientImpl aws = new AWSServiceClientImpl(new DefaultAWSCredentialsProviderChain());
		aws.setAccountId("000000000000"); // 12-digit
		
		DefaultAWSScannerGroup sg = new DefaultAWSScannerGroup(aws,neo4j);
	
		sg.scan("us-west-2");
//		sg.scanAllRegions();
		
	}

}
