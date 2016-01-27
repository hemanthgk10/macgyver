package io.macgyver.core.auth;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.core.convert.support.GenericConversionService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.BaseEncoding;

import io.macgyver.core.MacGyverException;

public class ApiToken {

	static ObjectMapper mapper = new ObjectMapper();

	ObjectNode data = mapper.createObjectNode();

	protected ApiToken() {
		// TODO Auto-generated constructor stub
	}

	public String getAccessKey() {
		return data.get("a").asText();
	}

	public String getSecretKey() {
		return data.get("s").asText();
	}

	public String getArmoredString() {
		return BaseEncoding.base64Url().encode(data.toString().getBytes());
	}

	public static ApiToken parse(String armored) {

		try {
			ApiToken t = new ApiToken();
			t.data = (ObjectNode) mapper.readTree(new String(BaseEncoding.base64Url().decode(armored), "UTF8"));

			return t;
		} catch (IOException | RuntimeException e) {
			// the exception tends to leak information about the token, so don't include it
			throw new MacGyverException("could not parse token");
		}

	}

	public static ApiToken createRandom() {
		try {
			ApiToken t = new ApiToken();

			byte[] accessKey = new byte[8];
			byte[] secretKey = new byte[16];
			SecureRandom.getInstance("SHA1PRNG").nextBytes(accessKey);
			SecureRandom.getInstance("SHA1PRNG").nextBytes(secretKey);

			t.data.put("a", BaseEncoding.base64().encode(accessKey));
			t.data.put("s", BaseEncoding.base64().encode(secretKey));

			return t;
		} catch (NoSuchAlgorithmException e) {
			throw new MacGyverException(e);
		}
	}
}
