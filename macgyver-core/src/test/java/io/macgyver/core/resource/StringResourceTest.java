package io.macgyver.core.resource;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class StringResourceTest {

	@Test
	public void testX() throws IOException {
		
		Assertions.assertThat(new StringResource("bar").getPath()).isNull();
		Assertions.assertThat(new StringResource("bar","foo").getPath()).isEqualTo("foo");
		Assertions.assertThat(new StringResource("bar","foo").exists()).isTrue();
		Assertions.assertThat(new StringResource("bar","foo").getContentAsString()).isEqualTo("bar");
		
		Assertions.assertThat(new StringResource("bar","foo").getResourceProvider()).isNull(); // this may change
	}

}
