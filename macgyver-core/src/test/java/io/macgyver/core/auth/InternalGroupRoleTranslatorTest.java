package io.macgyver.core.auth;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class InternalGroupRoleTranslatorTest {

	
	@Test
	public void testTransformName() {
		Assertions.assertThat(InternalGroupRoleTranslator.normalizeUpperCase("abcDEF_GHI  123##-abc.g")).isEqualTo("ABCDEF_GHI__123___ABC_G");
	}
}
