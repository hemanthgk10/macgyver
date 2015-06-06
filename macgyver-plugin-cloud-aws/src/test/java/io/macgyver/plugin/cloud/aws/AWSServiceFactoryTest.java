package io.macgyver.plugin.cloud.aws;

import io.macgyver.core.test.StandaloneServiceBuilder;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class AWSServiceFactoryTest {
	
	public static final String DUMMY_ACCESS="dummyaccess";
	public static final String DUMMY_SECRET="dummysecret";

	@Test
	public void testX() {
		AWSServiceClient sf = StandaloneServiceBuilder.forServiceFactory(AWSServiceFactory.class).property("accessKey", DUMMY_ACCESS).property("secretKey", DUMMY_SECRET).build(AWSServiceClient.class);
	
		
		Assertions.assertThat(sf).isNotNull();
		Assertions.assertThat(sf.getCredentialsProvider().getCredentials().getAWSAccessKeyId()).isEqualTo(DUMMY_ACCESS);
		Assertions.assertThat(sf.getCredentialsProvider().getCredentials().getAWSSecretKey()).isEqualTo(DUMMY_SECRET);
		
		
		Assertions.assertThat(sf.newEC2Client()).isNotNull();
	}
}
