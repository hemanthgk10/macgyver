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
package io.macgyver.plugin.artifactory;

import io.macgyver.okrest.OkRestLoggingInterceptor;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import com.squareup.okhttp.mockwebserver.rule.MockWebServerRule;

public class ArtifactoryClientImplTest {

	@Rule
	public MockWebServerRule mockServer = new MockWebServerRule();



	@Test
	public void testMockGavcSearch() throws IOException, InterruptedException {

		String response = "{\n"
				+ "  \"results\" : [ {\n"
				+ "    \"repo\" : \"lib-release\",\n"
				+ "    \"path\" : \"/mygroup/myartifact/1.0.1/myartifact-1.0.1.jar\",\n"
				+ "    \"created\" : \"2015-01-06T18:50:11.270-08:00\",\n"
				+ "    \"createdBy\" : \"deployuser\",\n"
				+ "    \"lastModified\" : \"2015-01-06T18:50:11.262-08:00\",\n"
				+ "    \"modifiedBy\" : \"deployuser\",\n"
				+ "    \"lastUpdated\" : \"2015-01-06T18:50:11.262-08:00\",\n"
				+ "    \"properties\" : { },\n"
				+ "    \"downloadUri\" : \"https://artifactory.example.com/artifactory/lib-release/mygroup/myartifact/1.0.1/myartifact-1.0.1.jar\",\n"
				+ "    \"mimeType\" : \"application/java-archive\",\n"
				+ "    \"size\" : \"42429\",\n"
				+ "    \"checksums\" : {\n"
				+ "      \"sha1\" : \"b8b79fcae449ad6b300c26957ebc98ab362b2e6c\",\n"
				+ "      \"md5\" : \"dc0bf7c2bfde4ac73a2c9561e8b11901\"\n"
				+ "    },\n"
				+ "    \"originalChecksums\" : {\n"
				+ "      \"sha1\" : \"b8b79fcae449ad6b300c26957ebc98ab362b2e6c\",\n"
				+ "      \"md5\" : \"dc0bf7c2bfde4ac73a2c9561e8b11901\"\n"
				+ "    },\n"
				+ "    \"uri\" : \"https://artifactory.example.com/artifactory/api/storage/lib-release/mygroup/myartifact/1.0.1/myartifact-1.0.1.jar\"\n"
				+ "  }, {\n"
				+ "    \"repo\" : \"lib-release\",\n"
				+ "    \"path\" : \"/mygroup/myartifact/1.0.1/myartifact-1.0.1.pom\",\n"
				+ "    \"created\" : \"2015-01-06T18:50:11.294-08:00\",\n"
				+ "    \"createdBy\" : \"deployuser\",\n"
				+ "    \"lastModified\" : \"2015-01-06T18:50:11.294-08:00\",\n"
				+ "    \"modifiedBy\" : \"deployuser\",\n"
				+ "    \"lastUpdated\" : \"2015-01-06T18:50:11.294-08:00\",\n"
				+ "    \"properties\" : { },\n"
				+ "    \"downloadUri\" : \"https://artifactory.example.com/artifactory/lib-release/mygroup/myartifact/1.0.1/myartifact-1.0.1.pom\",\n"
				+ "    \"mimeType\" : \"application/x-maven-pom+xml\",\n"
				+ "    \"size\" : \"6395\",\n"
				+ "    \"checksums\" : {\n"
				+ "      \"sha1\" : \"b0b25aeb7671ecfafe9e1bb6ee496bd3f72ce414\",\n"
				+ "      \"md5\" : \"009095fb9116b284949be888686955aa\"\n"
				+ "    },\n"
				+ "    \"originalChecksums\" : {\n"
				+ "      \"sha1\" : \"b0b25aeb7671ecfafe9e1bb6ee496bd3f72ce414\",\n"
				+ "      \"md5\" : \"009095fb9116b284949be888686955aa\"\n"
				+ "    },\n"
				+ "    \"uri\" : \"https://artifactory.example.com/artifactory/api/storage/lib-release/mygroup/myartifact/1.0.1/myartifact-1.0.1.pom\"\n"
				+ "  } ]\n" + "}";
		mockServer
				.enqueue(new MockResponse()
						.setBody(response)
						.addHeader("Content-type",
								"application/vnd.org.jfrog.artifactory.search.GavcSearchResult+json")
						.setResponseCode(200));

		ArtifactoryClientImpl c = new ArtifactoryClientImpl(mockServer.getUrl(
				"/artifactory").toExternalForm(), "scott", "tiger");
		c.getBaseTarget().getOkHttpClient().interceptors()
				.add(new OkRestLoggingInterceptor());

		JsonNode n = c.gavcSearch().artifact("myartifact").version("1.0.1")
				.withInfo(true).withProperties(true).inRepo("lib-release")
				.execute();
		RecordedRequest rr = mockServer.takeRequest();
		System.out.println(rr.getHeaders());
		Assertions
				.assertThat(rr.getPath())
				.isEqualTo(
						"/artifactory/api/search/gavc?a=myartifact&v=1.0.1&repos=lib-release");
		Assertions.assertThat(rr.getHeader("authorization")).isEqualTo(
				"Basic c2NvdHQ6dGlnZXI=");
		Assertions.assertThat(rr.getHeader("X-Result-Detail")).isEqualTo(
				"info, properties");

		mockServer
				.enqueue(new MockResponse()
						.setBody(response)
						.addHeader("Content-type",
								"application/vnd.org.jfrog.artifactory.search.GavcSearchResult+json")
						.setResponseCode(200));

		c = new ArtifactoryClientImpl(mockServer.getUrl("/artifactory")
				.toExternalForm(), "scott", "tiger");
		c.getBaseTarget().getOkHttpClient().interceptors()
				.add(new OkRestLoggingInterceptor());

		n = c.gavcSearch().artifact("myartifact").version("1.0.1")
				.withInfo(true).withProperties(true).inRepo("lib-release")
				.execute();
		rr = mockServer.takeRequest();
		System.out.println(rr.getHeaders());
		Assertions
				.assertThat(rr.getPath())
				.isEqualTo(
						"/artifactory/api/search/gavc?a=myartifact&v=1.0.1&repos=lib-release");
		Assertions.assertThat(rr.getHeader("authorization")).isEqualTo(
				"Basic c2NvdHQ6dGlnZXI=");
		Assertions.assertThat(rr.getHeader("X-Result-Detail")).isEqualTo(
				"info, properties");

		
		
		// *************
		
		mockServer
				.enqueue(new MockResponse()
						.setBody(response)
						.addHeader("Content-type",
								"application/vnd.org.jfrog.artifactory.search.GavcSearchResult+json")
						.setResponseCode(200));

		c = new ArtifactoryClientImpl(mockServer.getUrl("/artifactory")
				.toExternalForm(), "scott", "tiger");
		c.getBaseTarget().getOkHttpClient().interceptors()
				.add(new OkRestLoggingInterceptor());

		n = c.gavcSearch().artifact("myartifact").version("1.0.1")
				.withInfo(true).withProperties(false).inRepo("lib-release")
				.execute();
		rr = mockServer.takeRequest();
		System.out.println(rr.getHeaders());
		Assertions
				.assertThat(rr.getPath())
				.isEqualTo(
						"/artifactory/api/search/gavc?a=myartifact&v=1.0.1&repos=lib-release");
		Assertions.assertThat(rr.getHeader("authorization")).isEqualTo(
				"Basic c2NvdHQ6dGlnZXI=");
		Assertions.assertThat(rr.getHeader("X-Result-Detail")).isEqualTo(
				"info");
		
	// *************
		
		mockServer
				.enqueue(new MockResponse()
						.setBody(response)
						.addHeader("Content-type",
								"application/vnd.org.jfrog.artifactory.search.GavcSearchResult+json")
						.setResponseCode(200));

		c = new ArtifactoryClientImpl(mockServer.getUrl("/artifactory")
				.toExternalForm(), "scott", "tiger");
		c.getBaseTarget().getOkHttpClient().interceptors()
				.add(new OkRestLoggingInterceptor());

		n = c.gavcSearch().artifact("myartifact").version("1.0.1")
				.withInfo(false).withProperties(false).inRepo("lib-release")
				.execute();
		rr = mockServer.takeRequest();
		System.out.println(rr.getHeaders());
		Assertions
				.assertThat(rr.getPath())
				.isEqualTo(
						"/artifactory/api/search/gavc?a=myartifact&v=1.0.1&repos=lib-release");
		Assertions.assertThat(rr.getHeader("authorization")).isEqualTo(
				"Basic c2NvdHQ6dGlnZXI=");
		Assertions.assertThat(rr.getHeader("X-Result-Detail")).isNull();

	}
	
	@Test
	public void testPropertySearch() throws IOException, InterruptedException {

		String response = "{\n"
				+ "  \"results\" : [ {\n"
				+ "    \"repo\" : \"lib-release\",\n"
				+ "    \"path\" : \"/mygroup/myartifact/1.0.1/myartifact-1.0.1.jar\",\n"
				+ "    \"created\" : \"2015-01-06T18:50:11.270-08:00\",\n"
				+ "    \"createdBy\" : \"deployuser\",\n"
				+ "    \"lastModified\" : \"2015-01-06T18:50:11.262-08:00\",\n"
				+ "    \"modifiedBy\" : \"deployuser\",\n"
				+ "    \"lastUpdated\" : \"2015-01-06T18:50:11.262-08:00\",\n"
				+ "    \"properties\" : { },\n"
				+ "    \"downloadUri\" : \"https://artifactory.example.com/artifactory/lib-release/mygroup/myartifact/1.0.1/myartifact-1.0.1.jar\",\n"
				+ "    \"mimeType\" : \"application/java-archive\",\n"
				+ "    \"size\" : \"42429\",\n"
				+ "    \"checksums\" : {\n"
				+ "      \"sha1\" : \"b8b79fcae449ad6b300c26957ebc98ab362b2e6c\",\n"
				+ "      \"md5\" : \"dc0bf7c2bfde4ac73a2c9561e8b11901\"\n"
				+ "    },\n"
				+ "    \"originalChecksums\" : {\n"
				+ "      \"sha1\" : \"b8b79fcae449ad6b300c26957ebc98ab362b2e6c\",\n"
				+ "      \"md5\" : \"dc0bf7c2bfde4ac73a2c9561e8b11901\"\n"
				+ "    },\n"
				+ "    \"uri\" : \"https://artifactory.example.com/artifactory/api/storage/lib-release/mygroup/myartifact/1.0.1/myartifact-1.0.1.jar\"\n"
				+ "  }, {\n"
				+ "    \"repo\" : \"lib-release\",\n"
				+ "    \"path\" : \"/mygroup/myartifact/1.0.1/myartifact-1.0.1.pom\",\n"
				+ "    \"created\" : \"2015-01-06T18:50:11.294-08:00\",\n"
				+ "    \"createdBy\" : \"deployuser\",\n"
				+ "    \"lastModified\" : \"2015-01-06T18:50:11.294-08:00\",\n"
				+ "    \"modifiedBy\" : \"deployuser\",\n"
				+ "    \"lastUpdated\" : \"2015-01-06T18:50:11.294-08:00\",\n"
				+ "    \"properties\" : { },\n"
				+ "    \"downloadUri\" : \"https://artifactory.example.com/artifactory/lib-release/mygroup/myartifact/1.0.1/myartifact-1.0.1.pom\",\n"
				+ "    \"mimeType\" : \"application/x-maven-pom+xml\",\n"
				+ "    \"size\" : \"6395\",\n"
				+ "    \"checksums\" : {\n"
				+ "      \"sha1\" : \"b0b25aeb7671ecfafe9e1bb6ee496bd3f72ce414\",\n"
				+ "      \"md5\" : \"009095fb9116b284949be888686955aa\"\n"
				+ "    },\n"
				+ "    \"originalChecksums\" : {\n"
				+ "      \"sha1\" : \"b0b25aeb7671ecfafe9e1bb6ee496bd3f72ce414\",\n"
				+ "      \"md5\" : \"009095fb9116b284949be888686955aa\"\n"
				+ "    },\n"
				+ "    \"uri\" : \"https://artifactory.example.com/artifactory/api/storage/lib-release/mygroup/myartifact/1.0.1/myartifact-1.0.1.pom\"\n"
				+ "  } ]\n" + "}";
		
		mockServer
				.enqueue(new MockResponse()
						.setBody(response)
						.addHeader("Content-type",
								"application/vnd.org.jfrog.artifactory.search.MetadataSearchResult+json")
						.setResponseCode(200));

		ArtifactoryClientImpl c = new ArtifactoryClientImpl(mockServer.getUrl(
				"/artifactory").toExternalForm(), "scott", "tiger");
		c.getBaseTarget().getOkHttpClient().interceptors()
				.add(new OkRestLoggingInterceptor());

		JsonNode n = c.propertySearch().property("artifactId","myartifact")
				.withInfo(true).withProperties(true).inRepo("lib-release")
				.execute();
		RecordedRequest rr = mockServer.takeRequest();
		System.out.println(rr.getHeaders());
		Assertions
				.assertThat(rr.getPath())
				.isEqualTo(
						"/artifactory/api/search/prop?artifactId=myartifact&repos=lib-release");
		Assertions.assertThat(rr.getHeader("authorization")).isEqualTo(
				"Basic c2NvdHQ6dGlnZXI=");
		Assertions.assertThat(rr.getHeader("X-Result-Detail")).isEqualTo(
				"info, properties");

		
	}
}
