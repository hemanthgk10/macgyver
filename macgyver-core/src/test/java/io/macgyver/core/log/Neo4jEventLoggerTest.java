/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.macgyver.core.log;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.test.MacGyverIntegrationTest;

public class Neo4jEventLoggerTest extends MacGyverIntegrationTest {

	ObjectMapper m = new ObjectMapper();

	@Autowired
	EventLogger eventLogger;

	@Autowired
	NeoRxClient neo4j;

	@Test
	public void testLog() throws InterruptedException, IOException {

		String id = UUID.randomUUID().toString();
		eventLogger.event().withProperty("foo", id).log();

		Thread.sleep(500L);

		List<JsonNode> x = neo4j.execCypherAsList("match (x:EventLog) where x.foo={foo} return x", "foo", id);

		Assertions.assertThat(x.size() == 1);
		
		Assertions.assertThat(x.get(0).path("eventTs").asLong()).isCloseTo(System.currentTimeMillis(), Offset.offset(2000L));
		Assertions.assertThat(x.get(0).path("eventDate").asText()).startsWith("20").endsWith("Z");
		Assertions.assertThat(x.get(0).path("foo").asText()).isEqualTo(id);
	}

}
