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
import io.macgyver.plugin.ci.CIScanner;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

public class JenkinsScanner extends CIScanner<JenkinsClient> {

	Logger logger = LoggerFactory.getLogger(JenkinsScanner.class);
	JenkinsClient client;

	JsonNode masterNode;

	public JenkinsScanner(NeoRxClient neo4j, JenkinsClient c) {
		super(neo4j, c);
		this.client = c;
	}

	public void scan() {

		this.masterNode = updateMasterNode();

		Iterator<JsonNode> t = client.getServerInfo().path("jobs").elements();

		while (t.hasNext()) {
			try {
			JsonNode apiJob = t.next();

			scanJob(apiJob);

			String name = apiJob.path("name").asText();
			logger.info("scanning job: {}", name);
			}
			catch (Exception e) {
				logger.info("",e);;
			}
		}

	}

	public JsonNode updateMasterNode() {
		Preconditions.checkState(getNeoRxClient() != null,
				"neorx client must be set");
		String id = client.getServerId();
		String url = client.getServerUrl();

		String cypher = "merge (c:CIServer {macId: {macId}}) "
				+ "ON CREATE SET c.type='jenkins', "
				+ "c.createTs=timestamp(), c.updateTs=timestamp(),c.url={url} "
				+ "ON MATCH set c.updateTs=timestamp(),c.url={url} return c";

		JsonNode n = getNeoRxClient()
				.execCypher(cypher, "macId", id, "url", url).toBlocking()
				.first();
		return n;
	}

	public void scanJob(JsonNode n) {


	
		String cypher = "match (c:CIServer {macId: {macId}}) MERGE (c)-[r:CONTAINS]->(j:CIJob {name: {jobName}}) "
				+ "ON CREATE  SET j.createTs=timestamp(),r.createTs=timestamp(),j.updateTs=timestamp(),r.updateTs=timestamp(),j.url={url} "
				+ "ON MATCH   SET j.url={url}, j.updateTs=timestamp(),r.updateTs=timestamp() return j,ID(j) as id";

		JsonNode x = getNeoRxClient()
				.execCypher(cypher, "macId", masterNode.get("macId").asText(),
						"jobName", n.path("name").asText(), "url",
						n.path("url").asText())
				.toBlocking().first();

	
		decorate(x.get("id").asLong(),(ObjectNode) x.get("j"));

	

	}
}
