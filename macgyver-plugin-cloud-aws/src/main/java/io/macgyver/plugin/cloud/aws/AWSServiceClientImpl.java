package io.macgyver.plugin.cloud.aws;

import com.amazonaws.auth.AWSCredentialsProvider;
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
	public AmazonEC2Client newEC2Client() {
		return new AmazonEC2Client(getCredentialsProvider());
	}

}
