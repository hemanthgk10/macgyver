package io.macgyver.plugin.cloud.cloudstack;

import java.util.Properties;

import com.google.common.base.Strings;

import io.macgyver.core.service.ServiceDefinition;
import io.macgyver.core.service.ServiceFactory;

public class CloudStackClientServiceFactory extends
		ServiceFactory<CloudStackClient> {

	public CloudStackClientServiceFactory() {
		super("cloudstack");

	}

	@Override
	protected CloudStackClient doCreateInstance(ServiceDefinition def) {
		Properties p = def.getProperties();

		String url = p.getProperty("url");
		String username = p.getProperty("username");
		String password = p.getProperty("password");

		String accessKey = p.getProperty("accessKey");
		String secretKey = p.getProperty("secretKey");
		CloudStackClientImpl c = new CloudStackClientImpl(url);

		if (!Strings.isNullOrEmpty(username)) {
			c = c.usernamePasswordAuth(username, password);
		} else if (!Strings.isNullOrEmpty("accessKey")) {
			c = c.apiKeyAuth(accessKey, secretKey);
		}

		return c;
	}

}
