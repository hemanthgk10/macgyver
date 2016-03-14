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
package io.macgyver.plugin.cmdb;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;

import io.macgyver.core.Kernel;
import io.macgyver.core.resource.Resource;
import io.macgyver.core.util.HJson;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class JobDefinitionLoader extends AbstractCatalogLoader {


	Pattern jobPattern = Pattern.compile(".*jobs.*?\\/((\\S+)\\.[h]*json)");
	Logger logger = LoggerFactory.getLogger(JobDefinitionLoader.class);









	public class AppDefinitionWriter implements Func1<JsonNode, Observable<JsonNode>> {
		@Override
		public Observable<JsonNode> call(JsonNode n) {
			try {
				logger.info("app ({}): {}", n.get("appId"), n);

				String cypher = "merge (a:AppDefinition {appId:{appId}}) set a+={props} remove a.error return a";

				return neo4j.execCypher(cypher, "appId", n.get("appId").asText(), "props", n);
			} catch (RuntimeException e) {
				logger.warn("problem processing app definition", e);
			}
			return Observable.empty();
		}
	}



	public class JobDefinitionWriter implements Action1<JsonNode> {

		@Override
		public void call(JsonNode n) {
			logger.info("job ({}): {}", n.get("id"), n);
			Preconditions.checkState(neo4j!=null,"neo4j not set");
			try {
				String cypher = "merge (j:JobDefinition {id:{id}}) set j+={props}, j.updateTs=timestamp()";				
				neo4j.execCypher(cypher, "id", n.get("id").asText(), "props", n);
			} catch (RuntimeException e) {
				logger.warn("problem processing job definition", e);
			}
			
		}

	}

	public class HJsonJobMapper implements Func1<Resource, Observable<JsonNode>> {

		@Override
		public Observable<JsonNode> call(Resource t) {
			try {



				Matcher m = jobPattern.matcher(t.getPath());
				if (m.matches()) {
					ObjectNode n = (ObjectNode) HJson.parse(t.getContentAsString());

					n.put("id", m.group(2));
					return Observable.just(n);
				}

			} catch (RuntimeException | IOException e) {
				logger.warn("could not parse hjson resource " + t, e);
			}
			return Observable.empty();
		}

	}



	public void importAll() {
		Observable.from(providers).flatMap(new  ProviderMapper()).flatMap(new HJsonJobMapper())
				.forEach(new JobDefinitionWriter());
		
	}


	@Subscribe
	public void start(Kernel.ServerStartedEvent event) {
		discoverResourceProviders();
	}


	@Override
	public void doRecordParseError(String name, Resource resource, Throwable e) {
		String cypher = "merge (j:JobDefinition {id:{id}}) set j.error={error} return j";
		neo4j.execCypher(cypher, "id",name,"error",e.toString());
	}

}
