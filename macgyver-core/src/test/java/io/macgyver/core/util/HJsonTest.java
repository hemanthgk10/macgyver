package io.macgyver.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class HJsonTest {

	Logger logger = LoggerFactory.getLogger(HJsonTest.class);

	@Test
	public void testIt() throws IOException {

		String input = " {\nbool:  true\nint:123\nfloat:123.45\nstring:Hello, world!\n/*blah blah\nblah blah */\narray: [ 1,2,3]\n}";

		JsonNode n = HJson.parse(input);

		logger.info("\n" + JsonNodes.pretty(n));
		Assertions.assertThat(n.path("bool").asBoolean()).isTrue();
		Assertions.assertThat(n.path("int").asInt()).isEqualTo(123);
		Assertions.assertThat(n.path("float").asDouble()).isEqualTo(123.45);
		Assertions.assertThat(n.path("string").asText()).isEqualTo(
				"Hello, world!");
		Assertions.assertThat(n.path("array").get(1).asInt()).isEqualTo(2);
	}

	@Test
	public void testNull()  {

		try {
			File a = null;
			HJson.parse(a);
			Assertions.fail("should throw");
		} catch (Exception e) {
			Assertions.assertThat(e).isInstanceOf(NullPointerException.class);
		}
		
		try {
			InputStream a = null;
			HJson.parse(a);
			Assertions.fail("should throw");
		} catch (Exception e) {
			Assertions.assertThat(e).isInstanceOf(NullPointerException.class);
		}
		
		try {
			Reader a = null;
			HJson.parse(a);
			Assertions.fail("should throw");
		} catch (Exception e) {
			Assertions.assertThat(e).isInstanceOf(NullPointerException.class);
		}
		
		try {
			String a = null;
			HJson.parse(a);
			Assertions.fail("should throw");
		} catch (Exception e) {
			Assertions.assertThat(e).isInstanceOf(NullPointerException.class);
		}
	}
}
