package io.macgyver.plugin.elb.a10;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class RequestBuilderTest {

	@Test
	public void testIt() {
		A10ClientImpl c = Mockito.mock(A10ClientImpl.class);

		RequestBuilder b = new RequestBuilder(c, "test");

		Assertions.assertThat(b.getParams()).containsEntry("method", "test")
				.hasSize(1);

		Assertions.assertThat(b.method("foo").getParams())
				.containsEntry("method", "foo").hasSize(1);

		Assertions.assertThat(b.param("a", "1").getParams())
				.containsEntry("method", "foo").containsEntry("a", "1")
				.hasSize(2);
		Assertions.assertThat(b.param("a", "2").getParams())
				.containsEntry("method", "foo").containsEntry("a", "2")
				.hasSize(2);

	}

	@Test
	public void testFormat() {
		A10ClientImpl c = Mockito.mock(A10ClientImpl.class);

		RequestBuilder b = new RequestBuilder(c, "test");

		Assertions.assertThat(b.getParams()).doesNotContainKey("format");

		assertThat(b.withXmlRequest().getParams()).containsEntry("format", "xml");
		assertThat(b.withJsonRequest().getParams()).containsEntry("format", "json");

	}

	@Test
	public void testImmuableParams() {
		A10ClientImpl c = Mockito.mock(A10ClientImpl.class);

		RequestBuilder b = new RequestBuilder(c, "test");

		try {
			b.getParams().put("foo", "bar");
			Assert.fail();
		} catch (UnsupportedOperationException e) {
			
		}

	}
}
