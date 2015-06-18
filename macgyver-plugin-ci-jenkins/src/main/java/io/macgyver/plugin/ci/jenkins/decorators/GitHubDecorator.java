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
package io.macgyver.plugin.ci.jenkins.decorators;

import java.util.List;

import org.jdom2.Document;
import org.jdom2.Text;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import io.macgyver.core.graph.NodeInfo;
import io.macgyver.plugin.ci.jenkins.JenkinsScanner;

public class GitHubDecorator implements io.macgyver.core.graph.NodeInfo.Action {

	@Override
	public void call(NodeInfo t1) {
		final XPathExpression<Text> githubProjectXPath = XPathFactory
				.instance()
				.compile(
						"//com.coravy.hudson.plugins.github.GithubProjectProperty/projectUrl/text()",
						Filters.text());

		JenkinsScanner c = ((JenkinsScanner) t1.getUserData());

		Document d = c.getServiceClient().getJobConfig(
				t1.getNode().get("name").asText());

		Optional<String> cloneUrl = extractText(d,
				"//hudson.plugins.git.UserRemoteConfig/url/text()");

		Optional<String> projectUrl = extractText(d,
				"//com.coravy.hudson.plugins.github.GithubProjectProperty/projectUrl/text()");

		List<String> tmp = Lists.newArrayList();

		ObjectNode params = new ObjectMapper().createObjectNode();
		if (projectUrl.isPresent()) {
			tmp.add("j.githubProjectUrl={projectUrl}");
			params.put("projectUrl", stripTrailingSlash(projectUrl.get()));
		}
		if (cloneUrl.isPresent()) {
			tmp.add("j.githubCloneUrl={cloneUrl}");
			params.put("cloneUrl", stripTrailingSlash(cloneUrl.get()));
		}

		if (!tmp.isEmpty()) {

			String clause = Joiner.on(", ").join(tmp);
			
			String cypher = "match (j:CIJob) where ID(j)={nodeId} SET "
					+ clause + " return j";
			params.put("nodeId", t1.getNodeId());
			t1.getNeoRxClient().execCypher(cypher, params);

		}

		if (projectUrl.isPresent()) {
			String cypher = "match (j:CIJob) where ID(j)={nodeId} MERGE (s:SCMRepo {url:{projectUrl}, type:'github'}) MERGE (j)-[r:BUILDS]->(s)"
					+ " ON CREATE set r.createTs=timestamp(), s.createTs=timestamp(), r.updateTs=timestamp(), s.updateTs=timestamp() "
					+ " ON MATCH  set r.updateTs=timestamp(), s.updateTs=timestamp()";
			t1.getNeoRxClient().execCypher(cypher, params);
		} else if (cloneUrl.isPresent()) {
			String url = cloneUrl.get();
		}

	}

	public Optional<String> extractText(Document d, String xpath) {
		XPathExpression<Text> expression = XPathFactory.instance().compile(
				xpath, Filters.text());
		return getText(expression.evaluate(d));
	}

	Optional<String> getText(List<Text> list) {
		if (list == null || list.isEmpty()) {
			return Optional.absent();
		}
		Text text = list.get(0);
		if (text == null) {
			return Optional.absent();
		}
		return Optional.fromNullable(text.getText());
	}

	String stripTrailingSlash(String input) {
		if (input != null) {
			while (!input.isEmpty() && input.charAt(input.length() - 1) == '/') {
				input = input.substring(0, input.length() - 1);
			}
		}
		return input;
	}

}
