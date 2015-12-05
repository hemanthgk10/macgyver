package io.macgyver.plugin.config;

import io.macgyver.plugin.consul.ConsulServiceFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsulConfig {

	@Bean
	public ConsulServiceFactory consulServiceFactory() {
		return new ConsulServiceFactory();
	}
}
