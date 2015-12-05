package io.macgyver.plugin.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.google.common.base.Strings;

import io.macgyver.core.service.ServiceDefinition;
import io.macgyver.core.service.ServiceFactory;

public class ConsulServiceFactory extends ServiceFactory<ConsulClient> {
	public ConsulServiceFactory() {
		super("consul");

	}

	@Override
	protected ConsulClient doCreateInstance(ServiceDefinition def) {

		
		String host = Strings.nullToEmpty(def.getProperty("host")).trim();
		String port = Strings.nullToEmpty(def.getProperty("port")).trim();
		
		if (!Strings.isNullOrEmpty(host)) {
			if (!Strings.isNullOrEmpty(port)) {
				return new ConsulClient(host,Integer.parseInt(port));
			}
			else {
				return new ConsulClient(host);
			}
		}
		
		return new ConsulClient();
	}
}
