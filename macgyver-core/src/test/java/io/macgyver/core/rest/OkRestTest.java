/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.macgyver.core.rest;

import java.io.IOException;

import javax.ws.rs.client.WebTarget;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import com.squareup.okhttp.mockwebserver.rule.MockWebServerRule;

public class OkRestTest {

	static OkHttpClient okClient = new OkHttpClient();

	@Rule
	public MockWebServerRule mockServer = new MockWebServerRule();

	public OkRest mock(MockResponse r) {
		mockServer.enqueue(r);
		OkRest okRest = new OkRest(okClient, mockServer.getUrl("/test")
				.toString());
		return okRest;
	}
	
	@Test
	public void testMultiParamPath() throws IOException, InterruptedException { 
		mockServer.enqueue(new MockResponse().setBody("{}"));

		OkRest okRest = new OkRest(okClient,mockServer.getUrl("/test").toString()); 
		okRest.queryParameter("abc", "def").queryParameter("xyz","123").request().get().execute().body();	
		RecordedRequest rr = mockServer.takeRequest();
		
		Assertions.assertThat(rr.getPath()).isEqualTo("/test?abc=def&xyz=123");
	
	}
	

	@Test
	public void testPath() {
		OkRest okRest = new OkRest(okClient, mockServer.getUrl("/test")
				.toString());
		Assertions.assertThat(okRest.path("/a/b/c").getUrl()).endsWith("/test/a/b/c");
		
	}
	@Test
	public void testNoArgConstructor() throws IOException, InterruptedException {

		OkRest r = new OkRest();
		Assertions.assertThat(r.getOkHttpClient()).isNotNull();

		mockServer.enqueue(new MockResponse().setBody("{}"));

		try {
			r.request().get().execute();
		} catch (Exception e) {
			Assertions.assertThat(e)
					.isInstanceOf(IllegalStateException.class);
		}
		
		mockServer.enqueue(new MockResponse().setBody("{}"));

		try {
			r=r.path("x");
			r.request().get().execute().body();
		} catch (Exception e) {
			Assertions.assertThat(e)
					.isInstanceOf(IllegalStateException.class);
		}
		
		
	}

	@Test
	public void testIt2() {
		OkRest r = new OkRest().url("http://localhost/test");
		
		Assertions.assertThat(r.getUrl()).isEqualTo("http://localhost/test");
		Assertions.assertThat(r.path("x").getUrl()).isEqualTo("http://localhost/test/x");
		Assertions.assertThat(r.getUrl()).isEqualTo("http://localhost/test");
		
		Assertions.assertThat(r.queryParameter("a", "b").getUrl()).isEqualTo("http://localhost/test");
	}
	
	@Test
	public void testQueryParameter() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("{}"));
		OkRest r = new OkRest().url(mockServer.getUrl("/test").toString());
		
		r.queryParameter("abc", "def").request().get().execute().body();	
		RecordedRequest rr = mockServer.takeRequest();
		
		Assertions.assertThat(rr.getPath()).isEqualTo("/test?abc=def");
		
		mockServer.enqueue(new MockResponse().setBody("{}"));
		r.request().get().execute().body();	
		rr = mockServer.takeRequest();
		Assertions.assertThat(rr.getPath()).isEqualTo("/test");
	
	}
	
	
	@Test
	public void testDecorator() throws IOException, InterruptedException {
		mockServer.enqueue(new MockResponse().setBody("{}"));
		OkRest r = new OkRest().url(mockServer.getUrl("/test").toString());
		
		Interceptor x = new Interceptor() {

			@Override
			public Response intercept(Chain chain) throws IOException {
				Request r = chain.request().newBuilder().header("X-Foo", "bar").build();
				
				return chain.proceed(r);
			}
			
		};
		r.getOkHttpClient().interceptors().add(x);
		
		
	
		
		r.request().get().execute().body();	
		RecordedRequest rr = mockServer.takeRequest();
		
		Assertions.assertThat(rr.getHeader("X-Foo")).isEqualTo("bar");
		
		
	
	}
	
	
	@Test
	public void testIt() throws IOException, InterruptedException {

		OkRest r = mock(new MockResponse().addHeader("Content-type",
				"application/json").setBody("{}"));

		r.request().get().execute().body().string();

		RecordedRequest request = mockServer.takeRequest();

		Assertions.assertThat(request.getMethod()).isEqualTo("GET");
		Assertions.assertThat(request.getPath()).isEqualTo("/test");

		// -----

		r = mock(new MockResponse().addHeader("Content-type",
				"application/json").setBody("{}"));

		r.queryParameter("hello", "world").request().get().execute().body()
				.string();

		request = mockServer.takeRequest();

		Assertions.assertThat(request.getMethod()).isEqualTo("GET");
		Assertions.assertThat(request.getPath()).isEqualTo("/test?hello=world");

	}

	@Test
	public void testPost() throws IOException, InterruptedException {

		OkRest r = mock(new MockResponse().addHeader("Content-type",
				"application/json").setBody("{}"));

		r.request()
				.post(RequestBody.create(MediaType.parse("application/json"),
						"{\"name\":\"Jerry\"")).execute();

		RecordedRequest request = mockServer.takeRequest();

		Assertions.assertThat(request.getMethod()).isEqualTo("POST");
		Assertions.assertThat(request.getPath()).isEqualTo("/test");
		Assertions.assertThat(request.getHeader("Content-type")).contains(
				"application/json");
		Assertions.assertThat(request.getUtf8Body()).isEqualTo(
				"{\"name\":\"Jerry\"");

	}
}
