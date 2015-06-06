package io.macgyver.plugin.cloud.aws;

import io.macgyver.core.service.ServiceDefinition;
import io.macgyver.core.service.ServiceFactory;

import java.io.IOException;

import org.apache.http.auth.AuthScope;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;


public class AWSServiceFactory extends ServiceFactory<AWSServiceClient> {

	public AWSServiceFactory() {
		super("aws");
		
	}

	@Override
	protected AWSServiceClient doCreateInstance(ServiceDefinition def) {
		
		final BasicAWSCredentials c = new BasicAWSCredentials(def.getProperties().getProperty("accessKey"), def.getProperties().getProperty("secretKey"));
		
		AWSCredentialsProvider cp = new AWSCredentialsProvider() {
			
			@Override
			public void refresh() {
				
			}
			
			@Override
			public AWSCredentials getCredentials() {
				return c;
			}
		};
		
		AWSServiceClientImpl ci = new AWSServiceClientImpl();
		ci.credentialsProvider = cp;
		return ci;
	}

}
