package io.macgyver.plugin.cmdb;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cmdb.AppDefinitionLoader;
import io.macgyver.plugin.git.GitResourceProvider;
import io.macgyver.test.MacGyverIntegrationTest;

public class AppCatalogLoaderIntegrationTest extends MacGyverIntegrationTest {

	@Inject
	NeoRxClient neo4j;

	@Inject
	org.springframework.context.ApplicationContext applicationContext;
	
	@Test
	public void testIt() {

		neo4j.execCypher("match (a:AppDefinition) where a.appId=~'macgyver-test.*' delete a");
		
		GitResourceProvider r = new GitResourceProvider("https://github.com/if6was9/macgyver-resource-test.git");

		
		AppDefinitionLoader l = new AppDefinitionLoader();

		l.addResourceProvider(r);
		l.neo4j = neo4j;
		l.importAll();
		
		JsonNode n = neo4j.execCypher("match (a:AppDefinition) where a.appId='macgyver-test-app-1' return a").toBlocking().first();
		
		Assertions.assertThat(n.path("foo").asText()).isEqualTo("bar");
	}

	@Test
	public void testBean() {
		Assertions.assertThat(applicationContext.getBean("macServiceCatalogLoader")).isInstanceOf(AppDefinitionLoader.class);
	}
	

}
