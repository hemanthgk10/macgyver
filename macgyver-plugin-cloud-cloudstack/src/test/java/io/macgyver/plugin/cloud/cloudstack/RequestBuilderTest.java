package io.macgyver.plugin.cloud.cloudstack;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class RequestBuilderTest {

	
	@Test
	public void testGenerateCommandString() {
		
		
		RequestBuilder b = new RequestBuilder().command("dUMMy").param("foo", "bar");
		
		Assertions.assertThat(b.generateCommandStringForHmac()).contains("foo=bar","command=dummy");
		
//		Assertions.assertThat(b.computeSignature()).isEqualTo("G47boWEyjAZVlpoudlbrNKiaNV4=");
	}
}
