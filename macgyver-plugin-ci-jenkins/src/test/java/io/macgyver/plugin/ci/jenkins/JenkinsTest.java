/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.macgyver.plugin.ci.jenkins;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.ci.jenkins.decorators.GitHubDecorator;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import com.squareup.okhttp.mockwebserver.rule.MockWebServerRule;

public class JenkinsTest {

	@Rule
	public MockWebServerRule mockServer = new MockWebServerRule();

	JenkinsClient client;

	@Before
	public void setupClient() {
		client = new JenkinsClientBuilder()
				.url(mockServer.getUrl("/jenkins").toString())
				.credentials("username", "password").build();

	}

	@Test
	public void testGetBuildQueue() throws InterruptedException {

		mockServer.enqueue(new MockResponse().setBody("{\"items\":[]}"));
		client.getBuildQueue();

		RecordedRequest rr = mockServer.takeRequest();

		Assertions.assertThat(rr.getPath())
				.isEqualTo("/jenkins/queue/api/json");
		Assertions.assertThat(rr.getMethod()).isEqualTo("GET");
	}

	@Test
	public void testGetLoadStats() throws InterruptedException {

		mockServer
				.enqueue(new MockResponse()
						.setBody("{\"busyExecutors\":{},\"queueLength\":{},\"totalExecutors\":{},\"totalQueueLength\":{}}"));
		client.getLoadStats();

		RecordedRequest rr = mockServer.takeRequest();

		Assertions.assertThat(rr.getPath()).isEqualTo(
				"/jenkins/overallLoad/api/json");
		Assertions.assertThat(rr.getMethod()).isEqualTo("GET");

	}

	@Test
	public void testGetServerInfo() throws InterruptedException {

		String x = "{\n" + "  \"assignedLabels\" : [ { } ],\n"
				+ "  \"mode\" : \"NORMAL\",\n"
				+ "  \"nodeDescription\" : \"the master Jenkins node\",\n"
				+ "  \"nodeName\" : \"\",\n" + "  \"numExecutors\" : 2,\n"
				+ "  \"description\" : null,\n" + "  \"jobs\" : [ {\n"
				+ "    \"name\" : \" myjob\",\n"
				+ "    \"url\" : \"https://jenkins.example.com/job/myjob/\",\n"
				+ "    \"color\" : \"red\"\n" + "  }]}";
		mockServer.enqueue(new MockResponse().setBody(x));
		client.getServerInfo();

		RecordedRequest rr = mockServer.takeRequest();

		Assertions.assertThat(rr.getPath()).isEqualTo("/jenkins/api/json");
		Assertions.assertThat(rr.getMethod()).isEqualTo("GET");

	}

	@Test
	@Ignore
	public void testReal() throws InterruptedException,
			JsonProcessingException, IOException {

		NeoRxClient neo4j = new NeoRxClient();

		JenkinsClient c = new JenkinsClientBuilder()
				.url("https://jenkins.example.com/")
				.credentials("username", "password").build();
	

		JenkinsScanner s = new JenkinsScanner(neo4j, c);	
	
		s.addDecorationAction(new GitHubDecorator());
		
		s.scan();
	}
}
	



