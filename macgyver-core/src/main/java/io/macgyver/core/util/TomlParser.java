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
