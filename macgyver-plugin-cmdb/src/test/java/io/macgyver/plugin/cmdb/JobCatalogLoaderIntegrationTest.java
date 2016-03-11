package io.macgyver.plugin.cmdb;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cmdb.JobDefinitionLoader;
import io.macgyver.plugin.git.GitResourceProvider;
import io.macgyver.test.MacGyverIntegrationTest;

public class JobCatalogLoaderIntegrationTest extends MacGyverIntegrationTest {

	@Inject
	NeoRxClient neo4j;

	@Test
	public void testIt() {

		neo4j.execCypher("match (a:JobDefinition) where a.name=~'macgyver-test-.*' delete a");
		
		GitResourceProvider r = new GitResourceProvider("https://github.com/if6was9/macgyver-resource-test.git");

		
		JobDefinitionLoader l = new JobDefinitionLoader();
		
		l.addResourceProvider(r);
		l.neo4j = neo4j;
		l.importAll();
		
		JsonNode n = neo4j.execCypher("match (j:JobDefinition) where j.id='macgyver-test-job-1' return j").first().toBlocking().first();
		
		Assertions.assertThat(n.path("description").asText()).isEqualTo("test job");
		
		neo4j.execCypher("match (a:JobDefinition) where a.name=~'macgyver-test-.*' delete a");

	}

	@Test
	public void testBean() {
		Assertions.assertThat(applicationContext.getBean("macJobCatalogLoader")).isInstanceOf(JobDefinitionLoader.class);
	}
}
