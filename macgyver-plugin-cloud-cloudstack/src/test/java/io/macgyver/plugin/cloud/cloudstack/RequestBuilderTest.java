package io.macgyver.plugin.cloud.cloudstack;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class RequestBuilderTest {

	
	@Test
	public void testGenerateCommandString() {
		
		
		RequestBuilder b = new RequestBuilder().param("command", "dUMMy").param("apiKey", "key").secretKey("secret");
		
		Assertions.assertThat(b.generateCommandStringForHmac()).isEqualTo("apikey=key&command=dummy");
		
		Assertions.assertThat(b.computeSignature()).isEqualTo("G47boWEyjAZVlpoudlbrNKiaNV4=");
	}
}
