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
package io.macgyver.core.log;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.neorx.rest.NeoRxClientBuilder;

public class Neo4jEventLoggerTest {

	ObjectMapper m = new ObjectMapper();
	
	@Rule
	public  MockWebServer mockServer = new MockWebServer();
	
	NeoRxClient neo4j;
	
	Neo4jEventLogger eventLogger;
	
	@Before
	public void setup() {
		neo4j = new NeoRxClientBuilder().url(mockServer.url("/").toString()).build();
		eventLogger = new Neo4jEventLogger();
		eventLogger.neo4j = neo4j;
	}
	

	
	@Test
	public void testLog() throws InterruptedException, IOException {
		
		

		mockServer.enqueue(new MockResponse().setBody("{\"results\":[{\"columns\":[]}], \"errors\": []}").setHeader("Content-type" , "application/json"));
		eventLogger.event().withProperty("foo", "bar").log();
		
		RecordedRequest rr = mockServer.takeRequest();		
		ObjectNode request = (ObjectNode) m.readTree(rr.getBody().readUtf8());
		
		Assertions.assertThat(request.path("statements").get(0).path("statement").asText()).isEqualTo("create (x:EventLog) set x={props}");
		
		long eventTs = request.path("statements").get(0).path("parameters").path("props").path("eventTs").asLong();
		
		String eventString = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        .withZone(ZoneOffset.UTC)
        .format(Instant.ofEpochMilli(eventTs));
		
		Assertions.assertThat(eventTs).isCloseTo(System.currentTimeMillis(), Offset.offset(5000L));
		Assertions.assertThat(request.path("statements").get(0).path("parameters").path("props").path("eventDate").asText()).isEqualTo(eventString);
	}
	
	@Test
	public void testLogWithLabel() throws InterruptedException, IOException {
		
		

		mockServer.enqueue(new MockResponse().setBody("{\"results\":[{\"columns\":[]}], \"errors\": []}").setHeader("Content-type" , "application/json"));
		eventLogger.event().withLabel("FooLog").withProperty("foo", "bar").log();		
		RecordedRequest rr = mockServer.takeRequest();		
		ObjectNode request = (ObjectNode) m.readTree(rr.getBody().readUtf8());
		
		Assertions.assertThat(request.path("statements").get(0).path("statement").asText()).isEqualTo("create (x:EventLog:FooLog) set x={props}");
		
		long eventTs = request.path("statements").get(0).path("parameters").path("props").path("eventTs").asLong();
		
		String eventString = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        .withZone(ZoneOffset.UTC)
        .format(Instant.ofEpochMilli(eventTs));
		
		Assertions.assertThat(eventTs).isCloseTo(System.currentTimeMillis(), Offset.offset(5000L));
		Assertions.assertThat(request.path("statements").get(0).path("parameters").path("props").path("eventDate").asText()).isEqualTo(eventString);
	}
	
	
	@Test
	public void testTimestamp() throws InterruptedException, IOException {
		
		
		long testTimestamp = 1455134000000L;
		
		mockServer.enqueue(new MockResponse().setBody("{\"results\":[{\"columns\":[]}], \"errors\": []}").setHeader("Content-type" , "application/json"));
		eventLogger.event().withLabel("FooLog").withTimestamp(testTimestamp).withProperty("foo", "bar").log();		
		RecordedRequest rr = mockServer.takeRequest();		
		ObjectNode request = (ObjectNode) m.readTree(rr.getBody().readUtf8());
		
		Assertions.assertThat(request.path("statements").get(0).path("statement").asText()).isEqualTo("create (x:EventLog:FooLog) set x={props}");
		
		long eventTs = request.path("statements").get(0).path("parameters").path("props").path("eventTs").asLong();
		
		String eventString = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        .withZone(ZoneOffset.UTC)
        .format(Instant.ofEpochMilli(eventTs));
		
		Assertions.assertThat(eventTs).isEqualTo(testTimestamp);
		Assertions.assertThat(request.path("statements").get(0).path("parameters").path("props").path("eventDate").asText()).isEqualTo(eventString);
		
		Assertions.assertThat(request.path("statements").get(0).path("parameters").path("props").path("eventDate").asText()).isEqualTo("2016-02-10T19:53:20.000Z");
	}
}
