package io.macgyver.pluin.config;

import io.macgyver.plugin.cloud.aws.AWSServiceFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

	@Bean
	public AWSServiceFactory macAWSServiceClientFactory() {
		
		return new  AWSServiceFactory();
		
	}
}
