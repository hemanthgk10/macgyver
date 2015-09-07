package io.macgyver.plugin.rabbitmq;

import io.macgyver.core.service.ServiceDefinition;
import io.macgyver.core.service.ServiceFactory;

import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqServiceFactory extends ServiceFactory<ConnectionFactory> {

	public RabbitMqServiceFactory() {
		super("rabbitmq");

	}

	@Override
	protected ConnectionFactory doCreateInstance(ServiceDefinition def) {

	
			ConnectionFactory cf = new ConnectionFactory();

			assignProperties(cf, def.getProperties(), false);

			return cf;
		
	}

}
