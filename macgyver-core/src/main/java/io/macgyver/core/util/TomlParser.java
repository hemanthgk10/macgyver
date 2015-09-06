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

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.moandjiezana.toml.Toml;

public class TomlParser {

	private JsonNode parse(Toml t) {

		ObjectMapper m = new ObjectMapper();

		ObjectNode n = m.convertValue(extract(t), ObjectNode.class);

		return n;
	}

	public JsonNode parse(String s) {
		return parse(new Toml().parse(s));
	}
	

	public JsonNode parse(InputStream s) {
		return parse(new Toml().parse(s));
	}

	public JsonNode parse(Reader s) {
		return parse(new Toml().parse(s));
	}

	public JsonNode parse(File s) {
		return parse(new Toml().parse(s));
	}

	Map<String, Object> extract(Toml t) {
		try {
			Field f = Toml.class.getDeclaredField("values");
			f.setAccessible(true);

			return (Map<String, Object>) f.get(t);

		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}

	}
}
