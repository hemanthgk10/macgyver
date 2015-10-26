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

import java.io.IOException;
import java.net.URL;

import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.fasterxml.jackson.databind.JsonNode;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClientImpl;
import io.macgyver.plugin.cloud.aws.scanner.SubnetScanner;
import io.macgyver.test.MacGyverIntegrationTest;

public class ScannerIntegrationTest extends MacGyverIntegrationTest {

	
	@Autowired
	NeoRxClient neo4j;
	

	@Test
	public void testIt() {
		
		
		/*
		 * // versioning this class is turning out to be hard
		 
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource("org/apache/http/impl/client/DefaultConnectionKeepAliveStrategy.class");
		*/
		
		AWSServiceClientImpl c = new AWSServiceClientImpl(new DefaultAWSCredentialsProviderChain());
		
		
	//	VPCScanner scanner = new VPCScanner(c, neo4j,"000000");
		
		//scanner.scan("us-west-2");
		
		SubnetScanner scanner = new SubnetScanner(c, neo4j);
		scanner.scanAllRegions();
		
	}
}
