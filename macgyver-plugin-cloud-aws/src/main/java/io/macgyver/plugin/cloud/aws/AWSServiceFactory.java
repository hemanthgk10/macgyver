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
package io.macgyver.plugin.cloud.aws;

import io.macgyver.core.service.ServiceDefinition;
import io.macgyver.core.service.ServiceFactory;

import java.io.IOException;

import org.apache.http.auth.AuthScope;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.google.common.base.Strings;

public class AWSServiceFactory extends ServiceFactory<AWSServiceClient> {

	public AWSServiceFactory() {
		super("aws");

	}

	@Override
	protected AWSServiceClient doCreateInstance(ServiceDefinition def) {

		AWSServiceClientImpl ci = new AWSServiceClientImpl();
		ci.setAccountId(def.getProperty("accountId"));
		ci.credentialsProvider = newProviderChain(def);
		String regionName = Strings.emptyToNull(Strings.nullToEmpty(
				def.getProperties().getProperty("region")).trim());
		if (regionName != null) {
			logger.info("setting region: {}", regionName);
			ci.defaultRegion = Region.getRegion(Regions.fromName(regionName));
		}

		return ci;
	}

	AWSCredentialsProvider newProviderChain(ServiceDefinition def) {

		final String accessKey = def.getProperties().getProperty("accessKey");
		final String secretKey = def.getProperties().getProperty("secretKey");
	
		AWSCredentialsProvider cp = new AWSCredentialsProvider() {

			@Override
			public void refresh() {

			}

			@Override
			public AWSCredentials getCredentials() {
				if (Strings.isNullOrEmpty(accessKey)
						|| Strings.isNullOrEmpty(secretKey)) {
					logger.info("accessKey or secretKey not specified.  Falling back to DefaultAWSCredentialsProviderChain.");
					return null;
				} else {
					final BasicAWSCredentials c = new BasicAWSCredentials(def
							.getProperties().getProperty("accessKey"), def
							.getProperties().getProperty("secretKey"));
					return c;
				}
			}
		};

		AWSCredentialsProviderChain chain = new AWSCredentialsProviderChain(cp,
				new DefaultAWSCredentialsProviderChain());
		return chain;
	}

}
