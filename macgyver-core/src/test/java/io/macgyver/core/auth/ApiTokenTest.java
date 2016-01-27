package io.macgyver.core.auth;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ApiTokenTest {

	@Test
	public void testIt() {
		ApiToken t = ApiToken.createRandom();
		
		String armored = t.getArmoredString();
		
		
		ApiToken t2 = ApiToken.parse(armored);
		
		Assertions.assertThat(t.getSecretKey()).isEqualTo(t2.getSecretKey());
		Assertions.assertThat(t.getAccessKey()).isEqualTo(t2.getAccessKey());
		Assertions.assertThat(t.getArmoredString()).isEqualTo(t2.getArmoredString());
		
		System.out.println(t.getArmoredString());
	}

}
