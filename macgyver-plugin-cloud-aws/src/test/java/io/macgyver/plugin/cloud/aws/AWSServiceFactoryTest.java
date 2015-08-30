package io.macgyver.plugin.cloud.aws;

import io.macgyver.core.test.StandaloneServiceBuilder;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.codedeploy.AmazonCodeDeployClient;

public class AWSServiceFactoryTest {

	public static final String DUMMY_ACCESS = "dummyaccess";
	public static final String DUMMY_SECRET = "dummysecret";

	@Test
	public void testX() {
		AWSServiceClient sf = StandaloneServiceBuilder
				.forServiceFactory(AWSServiceFactory.class)
				.property("accessKey", DUMMY_ACCESS)
				.property("secretKey", DUMMY_SECRET)
				.build(AWSServiceClient.class);

		Assertions.assertThat(sf).isNotNull();
		Assertions.assertThat(
				sf.getCredentialsProvider().getCredentials()
						.getAWSAccessKeyId()).isEqualTo(DUMMY_ACCESS);
		Assertions.assertThat(
				sf.getCredentialsProvider().getCredentials().getAWSSecretKey())
				.isEqualTo(DUMMY_SECRET);

		Assertions.assertThat(
				sf.createEC2Client(Region.getRegion(Regions.US_EAST_1)))
				.isNotNull();
	}

	@Test
	public void testAdHocService() {
		AWSServiceClient sf = StandaloneServiceBuilder
				.forServiceFactory(AWSServiceFactory.class)
				.property("accessKey", DUMMY_ACCESS)
				.property("secretKey", DUMMY_SECRET)
				.build(AWSServiceClient.class);

		Assertions.assertThat(sf).isNotNull();
		Assertions.assertThat(
				sf.getCredentialsProvider().getCredentials()
						.getAWSAccessKeyId()).isEqualTo(DUMMY_ACCESS);
		Assertions.assertThat(
				sf.getCredentialsProvider().getCredentials().getAWSSecretKey())
				.isEqualTo(DUMMY_SECRET);

		Assertions.assertThat(sf.createClient(AmazonCodeDeployClient.class))
				.isNotNull().isInstanceOf(AmazonCodeDeployClient.class);

	}

	@Test
	public void testRegion() {
		AWSServiceClient sf = StandaloneServiceBuilder
				.forServiceFactory(AWSServiceFactory.class)
				.property("accessKey", DUMMY_ACCESS)
				.property("secretKey", DUMMY_SECRET)
				.property("region", "us-west-1").build(AWSServiceClient.class);

	}

	@Test
	public void testInvalidRegion() {
		try {
			AWSServiceClient sf = StandaloneServiceBuilder
					.forServiceFactory(AWSServiceFactory.class)
					.property("accessKey", DUMMY_ACCESS)
					.property("secretKey", DUMMY_SECRET)
					.property("region", "us-foo-1")
					.build(AWSServiceClient.class);
		} catch (IllegalArgumentException e) {
			Assertions.assertThat(e).isInstanceOf(
					IllegalArgumentException.class);
		}

	}

}
