package io.macgyver.plugin.config;

import io.macgyver.plugin.artifactory.ArtifactoryServiceFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArtifactoryConfig {

	@Bean
	public ArtifactoryServiceFactory arifactoryServiceFactory() {
		return new ArtifactoryServiceFactory();
	}
}
