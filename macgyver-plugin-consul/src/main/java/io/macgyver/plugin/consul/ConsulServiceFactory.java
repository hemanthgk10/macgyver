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
package io.macgyver.plugin.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.google.common.base.Strings;

import io.macgyver.core.service.ServiceDefinition;
import io.macgyver.core.service.ServiceFactory;

public class ConsulServiceFactory extends ServiceFactory<ConsulClient> {
	public ConsulServiceFactory() {
		super("consul");

	}

	@Override
	protected ConsulClient doCreateInstance(ServiceDefinition def) {

		
		String host = Strings.nullToEmpty(def.getProperty("host")).trim();
		String port = Strings.nullToEmpty(def.getProperty("port")).trim();
		
		if (!Strings.isNullOrEmpty(host)) {
			if (!Strings.isNullOrEmpty(port)) {
				return new ConsulClient(host,Integer.parseInt(port));
			}
			else {
				return new ConsulClient(host);
			}
		}
		
		return new ConsulClient();
	}
}
