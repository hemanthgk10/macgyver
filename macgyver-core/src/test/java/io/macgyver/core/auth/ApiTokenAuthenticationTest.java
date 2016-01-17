package io.macgyver.core.auth;

import org.junit.Test;

import com.lambdaworks.crypto.SCryptUtil;

public class ApiTokenAuthenticationTest {

	@Test
	public void testIt() {
		System.out.println(SCryptUtil.scrypt("test", 4096, 8, 1));
		System.out.println(SCryptUtil.scrypt("test", 4096, 8, 1));
	}

}
