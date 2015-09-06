package io.macgyver.core.config;

import io.macgyver.test.MacGyverIntegrationTest;

import org.junit.Test;

public class ConfigTest extends MacGyverIntegrationTest {

	
	
	@Test
	public void testCoreBeanNameStability() {
		applicationContext.getBean("macAccessDecisionManager");
		applicationContext.getBean("macCoreApiController");
		applicationContext.getBean("macHomeController");
		applicationContext.getBean("macLoginController");
		applicationContext.getBean("macContextRefreshApplicationListener");
		applicationContext.getBean("macAsyncHttpClient");
		applicationContext.getBean("macEventBus");
		applicationContext.getBean("macAsyncEventBus");
		applicationContext.getBean("macEventBusPostProcessor");
		applicationContext.getBean("macKernel");
		applicationContext.getBean("macStartup");
		applicationContext.getBean("macBindingSupplierManager");
		applicationContext.getBean("macCoreBindingSupplier");
		applicationContext.getBean("macCrypto");
		applicationContext.getBean("macAutowiredPostProcessor");
		applicationContext.getBean("macServiceRegistry");
		applicationContext.getBean("macPropertyPlaceholderConfigurer");
		applicationContext.getBean("macHookScriptManager");
		applicationContext.getBean("macBeanFactoryPostProcessor");
		applicationContext.getBean("macCoreRevisionInfo");
		applicationContext.getBean("macUserManager");
		applicationContext.getBean("macGraphClient");
		applicationContext.getBean("macExtensionResourceProvider");
		applicationContext.getBean("macPluginManager");
		applicationContext.getBean("macCorePlugin");
		applicationContext.getBean("macClusterManager");
		applicationContext.getBean("macIgniteTcpDiscoverySpi");
		applicationContext.getBean("macIgnite");
		applicationContext.getBean("macAccessDecisionVoterList");
		applicationContext.getBean("macAccessDecisionManager");
		applicationContext.getBean("macInternalGroupRoleTranslator");
		applicationContext.getBean("macGrantedAuthoritiesTranslatorChain");
		applicationContext.getBean("macGrantedAuthoritiesTranslatorScriptHook");
		applicationContext.getBean("macInternalAuthenticationProvider");
		applicationContext.getBean("macTestBeanFactory");
		applicationContext.getBean("macWeb");
		applicationContext.getBean("macHandlerMappingPostProcessor");
		applicationContext.getBean("macWebConfig");
		applicationContext.getBean("macVaadinServlet");
		applicationContext.getBean("macViewDecorators");
		applicationContext.getBean("macAdminUIDecorator");
		applicationContext.getBean("macMustacheTemplateLoader");
		applicationContext.getBean("macMustacheCompiler");
		applicationContext.getBean("macMustacheViewResolver");
		applicationContext.getBean("macDummyHandlebarsController");
		applicationContext.getBean("macAccessLogCustomizer");
		applicationContext.getBean("macNeo4jProxyServlet");
		applicationContext.getBean("macNeo4jProxyServletRegistrationBean");
		
		/*
		 
			if (s.startsWith("mac")) {
			System.out.println("applicationContext.getBean(\""+s+"\");");
			
			}
		}
		*/
	}
}
