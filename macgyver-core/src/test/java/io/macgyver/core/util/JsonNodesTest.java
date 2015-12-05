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
package io.macgyver.core.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodesTest {

	@Test
	public void testNonArrayNode() {
		
		ObjectNode n = JsonNodes.mapper.createObjectNode();
		
		n.put("a", 1);
		n.put("b", 2);
		Assertions.assertThat(JsonNodes.arrayToList(n)).isEmpty();
	}
	
	@Test
	public void testArrayNode() {
		
		ArrayNode n = JsonNodes.mapper.createArrayNode();
		
		n.add("a");
		n.add("b");
		
		Assertions.assertThat(JsonNodes.arrayToList(n)).hasSize(2);
	}
}
