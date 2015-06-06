package io.macgyver.plugin.cloud.aws;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.s3.AmazonS3Client;

public interface AWSServiceClient {

	AWSCredentialsProvider getCredentialsProvider();
	
	AmazonS3Client newS3Client();
	
	AmazonEC2Client newEC2Client();
}
