package io.macgyver.plugin.config;

import io.macgyver.plugin.etcd.EtcdClientServiceFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtcdConfig {

	@Bean
	public EtcdClientServiceFactory macEtcdClientServiceFactory() {
		return new EtcdClientServiceFactory();
	}
}
