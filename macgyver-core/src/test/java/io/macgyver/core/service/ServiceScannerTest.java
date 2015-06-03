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
package io.macgyver.core.service;

import java.util.concurrent.atomic.AtomicInteger;

import io.macgyver.core.graph.NodeInfo;
import io.macgyver.neorx.rest.NeoRxClient;
import io.macgyver.test.MacGyverIntegrationTest;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import rx.functions.Action1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;

public class ServiceScannerTest extends MacGyverIntegrationTest {

	@Autowired
	NeoRxClient neorx;
	
	public static class TestScanner extends ServiceScanner<String> {

		public TestScanner(NeoRxClient neo4j, String service) {
			super(neo4j, service);
			
		}

		@Override
		public void scan() {
			
			
		}
		
	}
	
	
	@Test
	public void testIt() {
		
		
		

		TestScanner ts = new TestScanner(neorx, "dummy");
		
		Action1<NodeInfo> action = new Action1<NodeInfo>() {

			@Override
			public void call(NodeInfo t1) {
				//System.out.println(t1.getNeoRxClient());
				
			}};
		ts.addDecorationAction(action);
		
		ObjectNode n = new ObjectMapper().createObjectNode().put("foo", "bar");
		
		ts.decorate(0,n);
		

		
	}
}
