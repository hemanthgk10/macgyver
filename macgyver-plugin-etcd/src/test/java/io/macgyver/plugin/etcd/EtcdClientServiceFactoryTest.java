package io.macgyver.plugin.etcd;

import io.macgyver.core.test.StandaloneServiceBuilder;
import mousio.etcd4j.EtcdClient;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import com.squareup.okhttp.mockwebserver.MockWebServer;

public class EtcdClientServiceFactoryTest {


	@Rule
	public MockWebServer mockServer = new MockWebServer();

	@Test
	public void testIt() throws Exception {


		EtcdClient c= StandaloneServiceBuilder.forServiceFactory(EtcdClientServiceFactory.class).property("uri", mockServer.getUrl("/").toExternalForm()).build(EtcdClient.class);
		Assertions.assertThat(mockServer).isNotNull();
		mockServer.enqueue(new MockResponse().setBody("{}"));

		c.getVersion();

		RecordedRequest rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getMethod()).isEqualTo("GET");
		Assertions.assertThat(rr.getPath()).isEqualTo("/version");

		c.close();
	}
}
