package io.macgyver.plugin.cloud.aws.scanner;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClientImpl;

public class DefaultAWSScannerGroup extends AWSScannerGroup {

	public DefaultAWSScannerGroup(AWSServiceClient client, NeoRxClient neo4j) {
		super(client, neo4j);
	
		
		// will create a better way to compose this later
		//ORDER MATTERS
		
		scannerList.add(new AccountScanner(client, neo4j));
		scannerList.add(new RegionScanner(client, neo4j));
		scannerList.add(new AvailabilityZoneScanner(client, neo4j));
		scannerList.add(new SubnetScanner(client, neo4j));
		scannerList.add(new VPCScanner(client, neo4j));
		scannerList.add(new SecurityGroupScanner(client, neo4j));
		scannerList.add(new AMIScanner(client, neo4j));
		scannerList.add(new EC2InstanceScanner(client, neo4j));
		scannerList.add(new ELBScanner(client, neo4j));
		scannerList.add(new LaunchConfigScanner(client, neo4j));
		scannerList.add(new ASGScanner(client, neo4j));
		scannerList.add(new RDSInstanceScanner(client, neo4j));
		
		
	}

	public static  void main(String [] args) throws Exception {
		
		AWSServiceClientImpl c = new AWSServiceClientImpl(new DefaultAWSCredentialsProviderChain(),"684690320560");
	
		NeoRxClient neo4j = new NeoRxClient();
		
		AWSScannerGroup g = new DefaultAWSScannerGroup(c, neo4j);
		
		g.scan(Region.getRegion(Regions.US_WEST_2));
	}

}
