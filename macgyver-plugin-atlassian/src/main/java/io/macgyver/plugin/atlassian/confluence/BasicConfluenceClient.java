package io.macgyver.plugin.atlassian.confluence;

import java.util.concurrent.TimeUnit;

import io.macgyver.okrest.BasicAuthInterceptor;
import io.macgyver.okrest.OkRestClient;
import io.macgyver.okrest.OkRestTarget;

import com.squareup.okhttp.OkHttpClient;

public class BasicConfluenceClient implements ConfluenceClient {

	private OkRestTarget target;
	
	public BasicConfluenceClient(String url, String username, String password) {
		OkHttpClient c = new OkHttpClient();
		c.interceptors().add(new BasicAuthInterceptor(username, password));
		
		OkRestClient client = new OkRestClient(c);
		
		this.target = client.uri(url);
		
	//	client.getConverterRegistry().setDefaultResponseErrorHandler(new ConfluenceResponseErrorHandler());
		
	}

	@Override
	public OkRestTarget getBaseTarget() {
		return target;
	}
	
	public ContentRequestBuilder contentRequest() {
		return new ContentRequestBuilder(this);
	}
}
