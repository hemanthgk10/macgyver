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
package io.macgyver.core.web.handlebars;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.samskivert.mustache.Mustache;

public class HandlebarsTest {

	
	@Test
	public void testIt() throws IOException {
		com.samskivert.mustache.Template t = Mustache.compiler().compile("Hello, {{name}}!");
		
		Map<String,String> m = Maps.newHashMap();
		m.put("name","world");
		StringWriter sw = new StringWriter();
		
		t.execute(m, sw);
		
		Assertions.assertThat(sw.toString()).isEqualTo("Hello, world!");
		
		
	}
	
	public static class TestBean {
		String name;
		
		public String getName() {
			return name;
		}
	}

	
	
}
