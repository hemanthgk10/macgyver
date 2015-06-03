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


import io.macgyver.core.service.ServiceDefinition;


public class ArtifactoryServiceFactory extends io.macgyver.core.service.ServiceFactory<ArtifactoryClient>{

	public ArtifactoryServiceFactory() {
		super("artifactory");

	}

	@Override
	protected ArtifactoryClient doCreateInstance(ServiceDefinition def) {
		
		String username = def.getProperties().getProperty("username", "");
		String url = def.getProperties().getProperty("url");
		String password = def.getProperties().getProperty("password","");
		ArtifactoryClientImpl c = new ArtifactoryClientImpl(url, username,password);
		
		return c;
	}
}
