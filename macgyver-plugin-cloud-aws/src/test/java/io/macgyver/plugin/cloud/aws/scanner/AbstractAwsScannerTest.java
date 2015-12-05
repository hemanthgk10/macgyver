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
package io.macgyver.plugin.cloud.aws.scanner;

import org.hibernate.validator.internal.util.privilegedactions.GetMethodFromPropertyName;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.macgyver.neorx.rest.MockNeoRxClient;
import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.plugin.cloud.aws.AWSServiceClient;
import io.macgyver.test.MacGyverIntegrationTest;

/**
 * Some common test scaffolding to make it easy to test scanners.  The AWS portion is mocked.  The Neo4j portion is not.
 * @author rschoening
 *
 */
public abstract class AbstractAwsScannerTest extends MacGyverIntegrationTest {

	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired 
	NeoRxClient neo4j;

	
	public String getAccountId() {
		return "123456123456";
	}

	AWSServiceClient newMockServiceClient() {
		
		AWSServiceClient c = Mockito.mock(AWSServiceClient.class);
		Mockito.when(c.getAccountId()).thenReturn(getAccountId());
		
		return c;
	}

}
