package io.macgyver.plugin.atlassian.confluence;

import groovy.text.GStringTemplateEngine;
import io.macgyver.okrest.OkRestException;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

public class BasicConfluenceClientTest {

	@Rule
	public MockWebServer mockServer = new MockWebServer();

	
	BasicConfluenceClient client;
	
	@Before
	public void setupClient() {
		
		client = new BasicConfluenceClient(mockServer.url("/rest/api/")
				.toString(), "scott", "tiger");
	}
	@Test
	public void testRequestAuth() throws InterruptedException {

		
		mockServer.enqueue(new MockResponse().setBody("{}"));
		client.getBaseTarget().get().execute();
		
		RecordedRequest rr = mockServer.takeRequest();
		

		Assertions.assertThat(rr.getHeader("authorization")).isEqualTo("Basic c2NvdHQ6dGlnZXI=");
	}

	
	
	
	
}
