package io.macgyver.plugin.cloud.cloudstack;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import com.squareup.okhttp.mockwebserver.rule.MockWebServerRule;

public class CloudStackClientImplTest {

	@Rule
	public MockWebServerRule mockServer = new MockWebServerRule();
	
	

	@Test
	public void testIt() throws InterruptedException {
		
		mockServer.enqueue(new MockResponse().setBody("{}"));
		CloudStackClientImpl c = new CloudStackClientImpl(mockServer.getUrl("/client/api").toExternalForm(), "key", "secret");
		
		RequestBuilder b = c.newRequest();
		
		Assertions.assertThat(b.paramMap.get("apiKey")).isEqualTo("key");
		Assertions.assertThat(b.secretKey).isEqualTo("secret");
		JsonNode n = b.param("a", "b").executeJson();
		
		
		RecordedRequest rr = mockServer.takeRequest();
		
		//Assertions.assertThat(rr.getPath()).isEqualTo("xx");
		
	}
}
