package io.macgyver.core.okhttp;

import java.io.IOException;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import io.macgyver.core.okhttp.LoggingInterceptor.Level;

public class LoggingInterceptorTest {

	@Rule
	public MockWebServer x = new MockWebServer();
	
	@Test
	public void testIt() throws IOException {
		
		x.enqueue(new MockResponse().setBody("{\"hello\":\"world\"}").addHeader("Content-type","application/json"));
		OkHttpClient c = new OkHttpClient();
		c.interceptors().add(LoggingInterceptor.create(LoggerFactory.getLogger(LoggingInterceptorTest.class)));
		
		c.newCall(new Request.Builder().url(x.url("/foo")).addHeader("Authorization", "foo").build()).execute();
	}
	

	public static class BlowingLoggingInterceptor extends LoggingInterceptor {

		protected BlowingLoggingInterceptor(Logger logger, Level level) {
			super(logger, level);
			
		}

		@Override
		protected boolean isResponseBodySizeWithinLimit(Response response) {
			throw new RuntimeException("simulated error");
		}
		
	}
	
	
	@Test
	public void testResponseException() throws IOException {
		
		x.enqueue(new MockResponse().setBody("{\"hello\":\"world\"}").addHeader("Content-type","application/json"));
		OkHttpClient c = new OkHttpClient();
		
		c.interceptors().add(new BlowingLoggingInterceptor(LoggerFactory.getLogger(BlowingLoggingInterceptor.class),Level.BODY));
		
		c.newCall(new Request.Builder().url(x.url("/foo")).addHeader("Authorization", "foo").build()).execute();
	}

}
