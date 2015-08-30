package io.macgyver.plugin.cloud.aws;

import java.util.Optional;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * Client factory for creating instances of the AWS Java SDK.
 * @author rschoening
 *
 */
public interface AWSServiceClient {

	/**
	 * Returns a credentials provider that returns credentials through the MacGyver service configuration
	 * system.
	 */
	AWSCredentialsProvider getCredentialsProvider();
	
	
	/**
	 * Convenience for creating an AWS service specific client.  This provides an alternative to manually instantiating
	 * the AWS-provided client and passing the AWSCredentialsProvider.
	 */
	<T extends AmazonWebServiceClient> T createClient(Class<? extends T> t);
	<T extends AmazonWebServiceClient> T createClient(Class<? extends T> t, Region region);
	
	/**
	 * If the region parameter is set on the service, initialized clients will be initialized with this as the selected region.
	 * @return
	 */
	Optional<Region> getDefaultRegion();
	
	AmazonS3Client newS3Client();
	
	AmazonEC2Client createEC2Client();
	AmazonEC2Client createEC2Client(String name);
	AmazonEC2Client createEC2Client(Regions region);
	AmazonEC2Client createEC2Client(Region region);
	

}
