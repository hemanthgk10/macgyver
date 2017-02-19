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

import java.util.List;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.util.StringUtils;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import io.macgyver.core.service.ServiceDefinition;
import io.macgyver.core.service.ServiceFactory;

public class AWSServiceFactory extends ServiceFactory<AWSServiceClient> {

	public AWSServiceFactory() {
		super("aws");

	}

	List<String> splitToList(String input) {
		return Splitter.onPattern("(,|\\s|;)").omitEmptyStrings().trimResults()
		.splitToList(Strings.nullToEmpty(input));
	}
	@Override
	protected AWSServiceClient doCreateInstance(ServiceDefinition def) {

		AWSServiceClientImpl ci = new AWSServiceClientImpl();
		ci.setAccountId(def.getProperty("accountId"));

		ci.credentialsProvider = getCredentialsProvider(def);
		
		List<Regions> regionList = Lists.newArrayList();
	
		splitToList(def.getProperties().getProperty("regions"))
				.forEach(it -> {
					try {
						regionList.add(Regions.fromName(it));
					} catch (RuntimeException e) {
						logger.warn("could not configure region: " + it, e);
					}

				});
		if (regionList.isEmpty()) {
			// Add the two "primary" US regions by default if not specified
			regionList.add(Regions.US_EAST_1);
			regionList.add(Regions.US_WEST_2);
		}
		ci.regionList = ImmutableList.copyOf(regionList);
		return ci;
	}

	private AWSCredentialsProvider getCredentialsProvider(ServiceDefinition def) {

		String accessKey = def.getProperty("accessKey");
		String secretKey = def.getProperty("secretKey");

		if (!StringUtils.isNullOrEmpty(accessKey) && !StringUtils.isNullOrEmpty(secretKey)) {
			logger.info("using static credentials " + accessKey + " for AWS service " + def.getName());
			return new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
		}

		String sourceService = def.getProperty("sourceService");
		String assumeRoleName = def.getProperty("assumeRoleName");
		if (!StringUtils.isNullOrEmpty(sourceService) && !StringUtils.isNullOrEmpty(assumeRoleName)) {
			String roleArn = "arn:aws:iam::" + def.getProperty("accountId") + ":role/" + assumeRoleName;
			logger.info("using assume-role credentials for " + roleArn + " from " + sourceService + " for AWS service "
					+ def.getName());
			return new AWSServiceClientAssumeRoleCredentialsProvider(registry, sourceService, roleArn);
		}

		logger.info("using default credentials provider for AWS service " + def.getName());
		return new DefaultAWSCredentialsProviderChain();

	}

}
