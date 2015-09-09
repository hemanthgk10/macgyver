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
package io.macgyver.plugin.cloud.aws.ec2;

import io.macgyver.core.test.StandaloneServiceBuilder;
import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;
import io.macgyver.plugin.cloud.aws.AWSServiceFactory;
import io.macgyver.test.MacGyverIntegrationTest;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Strings;

public class EC2InstanceScannerIntegrationTest extends MacGyverIntegrationTest {
	@Autowired
	NeoRxClient neo4j;

	Logger logger = LoggerFactory.getLogger(EC2InstanceScannerTest.class);

	String accessKey;
	String secretKey;
	
	@Before
	public void setupCredentials() {
		accessKey = getPrivateProperty("aws.accessKey");
		secretKey = getPrivateProperty("aws.secretKey");
		Assume.assumeFalse(Strings.isNullOrEmpty(accessKey));
		Assume.assumeFalse(Strings.isNullOrEmpty(secretKey));
	}
	@Test
	public void testIt() {
		AWSServiceClient sf = StandaloneServiceBuilder
				.forServiceFactory(AWSServiceFactory.class)
				.property("accessKey", getPrivateProperty("aws.accessKey"))
				.property("secretKey", getPrivateProperty("aws.secretKey"))
				.build(AWSServiceClient.class);

		EC2InstanceScanner s = new EC2InstanceScanner(sf, neo4j);

		s.scan();
	}
}
