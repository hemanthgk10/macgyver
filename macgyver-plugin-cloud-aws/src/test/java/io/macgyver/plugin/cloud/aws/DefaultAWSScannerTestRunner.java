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

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;

import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClientImpl;
import io.macgyver.plugin.cloud.aws.scanner.DefaultAWSScannerGroup;

public class DefaultAWSScannerTestRunner {


	
	public static void main(String [] args) {
		
		NeoRxClient neo4j = new NeoRxClient();
		
		AWSServiceClientImpl aws = new AWSServiceClientImpl(new DefaultAWSCredentialsProviderChain());
		aws.setAccountId("000000000000"); // 12-digit
		
		DefaultAWSScannerGroup sg = new DefaultAWSScannerGroup(aws,neo4j);
	
		sg.scan("us-west-2");
//		sg.scanAllRegions();
		
	}

}
