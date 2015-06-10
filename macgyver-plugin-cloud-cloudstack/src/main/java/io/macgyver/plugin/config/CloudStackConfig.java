package io.macgyver.plugin.config;

import io.macgyver.plugin.cloud.cloudstack.CloudStackClientServiceFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudStackConfig {

	@Bean
	public  CloudStackClientServiceFactory macCloudStackClientServiceFactory() {
		return new CloudStackClientServiceFactory();
	}
}
