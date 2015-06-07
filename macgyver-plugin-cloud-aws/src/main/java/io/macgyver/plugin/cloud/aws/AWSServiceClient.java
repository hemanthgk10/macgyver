package io.macgyver.plugin.cloud.aws;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.s3.AmazonS3Client;

public interface AWSServiceClient {

	AWSCredentialsProvider getCredentialsProvider();
	
	AmazonS3Client newS3Client();
	
	AmazonEC2Client createEC2Client(String name);
	AmazonEC2Client createEC2Client(Regions region);
	AmazonEC2Client createEC2Client(Region region);
}
