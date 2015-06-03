package io.macgyver.core.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.ning.http.client.RequestBuilder;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.thoughtworks.proxy.toys.decorate.Decorator;

@Deprecated
public class OkRest {

	public static final MediaType APPLICATION_JSON = MediaType.parse("application/json");
	private OkHttpClient client;
	private String url;

	static class KVPair {
		String key;
		String val;
	}

	private Multimap<String, String> queryParameters = ArrayListMultimap
			.create();

	public class InvocationBuilder {
		Request.Builder okBuilder = new Request.Builder();

		InvocationBuilder(Request.Builder okBuilder) {
			this.okBuilder = okBuilder;
		}

		public Response execute() throws IOException {
			Call c = getOkHttpClient().newCall(okBuilder.build());
			return c.execute();
		}

		public Request.Builder okBuilder() {
			return okBuilder;
		}

		public InvocationBuilder with(Request.Builder b) {
			return new InvocationBuilder(b);
		}

		public InvocationBuilder post(JsonNode body) {
			return post(RequestBody.create(APPLICATION_JSON, body.toString()));
		}
		public InvocationBuilder post(RequestBody body) {
			return new InvocationBuilder(okBuilder.post(body));
		}

		public InvocationBuilder get() {
			return new InvocationBuilder(okBuilder.get());
		}

		public InvocationBuilder delete() {
			return new InvocationBuilder(okBuilder.delete());
		}
		
		public InvocationBuilder put(RequestBody body) {
			return new InvocationBuilder(okBuilder.put(body));
		}
		public InvocationBuilder put(JsonNode body) {
			return put(RequestBody.create(APPLICATION_JSON, body.toString()));
		}
		public InvocationBuilder delete(RequestBody body) {
			return new InvocationBuilder(okBuilder.delete());
		}
		public InvocationBuilder patch(RequestBody body) {
			return new InvocationBuilder(okBuilder.patch(body));
		}
		public InvocationBuilder patch(JsonNode body) {
			return patch(RequestBody.create(APPLICATION_JSON, body.toString()));
		}
		public InvocationBuilder head(RequestBody body) {
			return new InvocationBuilder(okBuilder.head());
		}
		public InvocationBuilder head(JsonNode body) {
			return post(RequestBody.create(APPLICATION_JSON, body.toString()));
		}
		
		public InvocationBuilder method(String method, RequestBody body) {
			return new InvocationBuilder(okBuilder.method(method,body));
		}
	}

	public OkRest() {
		this(new OkHttpClient(), null);
	}

	public OkRest(OkHttpClient c) {
		this(c, null);
	}

	public OkRest(OkHttpClient c, String url) {
		this(c, url, null);
	}

	OkRest(OkHttpClient c, String url, Multimap<String, String> qp) {

		this.client = c;
		this.url = url;
		
		if (qp != null) {
			this.queryParameters.putAll(qp);
		}


	}
	
	public OkRest(OkRest from) {
		this(from.client, from.url,from.queryParameters);
		Preconditions.checkNotNull(from);
	}
	

	public OkRest queryParameter(String key, Object... vals) {

		ArrayListMultimap<String, String> mm = ArrayListMultimap.create();
		mm.putAll(queryParameters);
		for (Object x : vals) {
			if (x != null) {
				mm.put(key, x.toString());
			}
		}
		OkRest copy = new OkRest(this);
		copy.queryParameters = mm;
	
		return copy;

	}

	public OkRest path(String path) {

		OkRest copy = new OkRest(this);
		copy.url = new UrlBuilder(url).path(path).build();
		return copy;
	}

	public OkRest url(String url) {
	
		OkRest copy = new OkRest(this);
		copy.url = url;
		return copy;
	}

	public InvocationBuilder request() {
		String urlWithQueryString = toUrlWithQueryString();
		Request.Builder rb = new Request.Builder();
		rb = rb.url(urlWithQueryString);
		InvocationBuilder b = new InvocationBuilder(rb);

		return b;
	}

	public String getUrl() {
		return url;
	}
	public OkHttpClient getOkHttpClient() {
		return client;
	}

	public Call newCall(Request.Builder b) {
		return client.newCall(b.build());
	}

	public String toUrlWithQueryString() {
		try {
			if (url == null) {
				throw new IllegalStateException("invalid url: " + url);
			}
			StringBuilder sb = new StringBuilder();
			sb.append(url);

			if (!queryParameters.isEmpty()) {
				int count = 0;

				for (Entry<String, String> x : queryParameters.entries()) {
					if (count == 0) {
						sb.append("?");
					} else {
						sb.append("&");
					}
					sb.append(URLEncoder.encode(x.getKey(), "UTF8"));
					sb.append("=");
					sb.append(URLEncoder.encode(x.getValue(), "UTF8"));
					
					count++;
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	

}
