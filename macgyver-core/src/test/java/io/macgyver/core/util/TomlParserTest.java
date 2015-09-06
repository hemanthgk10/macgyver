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

import com.fasterxml.jackson.databind.JsonNode;
import com.moandjiezana.toml.Toml;

public class TomlParserTest {

	
	@Test
	public void testSimple() {
		
		
		
		JsonNode n = new TomlParser().parse("[foo]\na=1\nb=2\nc=true");
		
		Assertions.assertThat(n.path("foo").path("a").asInt()).isEqualTo(1);
		Assertions.assertThat(n.path("foo").path("c").asBoolean()).isTrue();
		
	
	}
	
	@Test
	public void testMulti() {
		
		
		
		JsonNode n = new TomlParser().parse("[foo.bar]\na=1");
		
		
		Assertions.assertThat(n.path("foo").path("bar").path("a").asInt()).isEqualTo(1);
		
	
	}
	
	@Test
	public void testArray() {
		
		
		
		JsonNode n = new TomlParser().parse("[[foo]]\na=1\n[[foo]]\nb=2");
		
		
		System.out.println(n);
		Assertions.assertThat(n.path("foo").path(0).path("a").asInt()).isEqualTo(1);
		Assertions.assertThat(n.path("foo").path(1).path("b").asInt()).isEqualTo(2);
		
	
	}
	
	@Test
	public void testExample() {
		JsonNode n = new TomlParser().parse(getClass().getClassLoader().getResourceAsStream("example-v0.4.0.toml"));
		
		
		Assertions.assertThat(n.path("array").path("key4").get(0).get(0).asInt()).isEqualTo(1);
		Assertions.assertThat(n.path("array").path("key4").get(0).get(1).asInt()).isEqualTo(2);
		
		Assertions.assertThat(n.path("array").path("key4").get(1).get(0).asText()).isEqualTo("a");
		Assertions.assertThat(n.path("array").path("key4").get(1).get(1).asText()).isEqualTo("b");
		Assertions.assertThat(n.path("array").path("key4").get(1).get(2).asText()).isEqualTo("c");
	
		
		
	}
}
