package io.macgyver.plugin.cloud.aws;

import java.util.Optional;
import java.util.Properties;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import io.macgyver.core.ServiceNotFoundException;
import io.macgyver.core.service.ServiceDefinition;
import io.macgyver.core.service.ServiceFactory;
import io.macgyver.core.service.ServiceRegistry;

public class AccountLookupTest {

	@Test
	public void testIt() {

	
		ServiceRegistry registry = new ServiceRegistry();
		
		
		Properties p = new Properties();
		p.put("accountId", "1234567");
	
		ServiceFactory<AWSServiceClient> sf = new AWSServiceFactory();
		sf.setServiceRegistry(registry);
		Assertions.assertThat(sf.getServiceType()).isEqualTo("aws");
		ServiceDefinition def = new ServiceDefinition("myaws", "myaws","aws", p,sf);	
		registry.addServiceDefinition(def);
		
		
		// End of mock setup
		
		Assertions.assertThat(registry.getServiceDefinitions().get("myaws")).isSameAs(def);
		
		AWSServiceClient xdef = registry.get("myaws", AWSServiceClient.class);
		
		
		Assertions.assertThat(xdef.getAccountId()).isEqualTo("1234567");
		
		
		AWSServiceClient x1 = registry.getServiceByProperty("aws", "accountId", "1234567");
		
		Assertions.assertThat(x1).isSameAs(xdef);
		
	
		try {
			registry.getServiceByProperty("aws", "accountId", "1234567");
		}
		catch (ServiceNotFoundException e) {
			Assertions.assertThat(e).isInstanceOf(ServiceNotFoundException.class).hasMessageContaining("could not locat service");
		}
	
		
	}

}
