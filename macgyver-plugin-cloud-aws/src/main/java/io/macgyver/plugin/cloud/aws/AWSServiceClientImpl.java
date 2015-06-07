package io.macgyver.plugin.cloud.aws;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.s3.AmazonS3Client;

public class AWSServiceClientImpl implements AWSServiceClient {

	AWSCredentialsProvider credentialsProvider;
	@Override
	public AWSCredentialsProvider getCredentialsProvider() {
		return credentialsProvider;
	}
	@Override
	public AmazonS3Client newS3Client() {
		
		return new AmazonS3Client(getCredentialsProvider());
	}
	@Override
	public AmazonEC2Client createEC2Client(Region region) {
		AmazonEC2Client client = new AmazonEC2Client(getCredentialsProvider());
		client.setRegion(region);
		return client;
	}
	@Override
	public AmazonEC2Client createEC2Client(Regions region) {
		return createEC2Client(Region.getRegion(region));
	}
	@Override
	public AmazonEC2Client createEC2Client(String name) {
		return createEC2Client(Regions.fromName(name));
	}

}
