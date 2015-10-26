package io.macgyver.plugin.cloud.aws.scanner;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;

public class DefaultAWSScannerGroup extends AWSScannerGroup {

	public DefaultAWSScannerGroup(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	
		
		// will create a better way to compose this later
		
		scannerList.add(new AccountScanner(client, neo4j));
		scannerList.add(new RegionScanner(client, neo4j));
		scannerList.add(new AvailabilityZoneScanner(client, neo4j));
		scannerList.add(new SubnetScanner(client, neo4j));
		scannerList.add(new VPCScanner(client, neo4j));
		scannerList.add(new EC2InstanceScanner(client, neo4j));
	}



}