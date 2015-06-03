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
package io.macgyver.plugin.hipchat;



import io.macgyver.okrest.OkRestClient;
import io.macgyver.okrest.OkRestException;
import io.macgyver.okrest.OkRestResponse;
import io.macgyver.okrest.OkRestTarget;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.ning.http.client.AsyncHttpClient;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;


public class HipChatClientImpl implements HipChatClient {

	public static final String HOSTED_URL = "https://api.hipchat.com";

	Logger logger = LoggerFactory.getLogger(HipChatClientImpl.class);
	String token;
	OkRestClient okRestClient;
	OkRestTarget base;
	String url;
	ObjectMapper mapper = new ObjectMapper();
	String version = "v2";

	public static class BearerTokenInterceptor implements Interceptor {

		private String token;

		public BearerTokenInterceptor(String token) {
			this.token = token;
		}

		@Override
		public Response intercept(Chain chain) throws IOException {

			return chain.proceed(chain.request().newBuilder()
					.addHeader("Authorization", "Bearer " + token).build());

		}

	}

	public HipChatClientImpl(String token) {
		this(HOSTED_URL, token);
	}

	public HipChatClientImpl(String url, String token) {
		Preconditions.checkNotNull(url);
		Preconditions.checkNotNull(token);
		this.url = url;
		this.token = token;
		
		okRestClient = new OkRestClient();
		
		okRestClient.getOkHttpClient().interceptors().add(new BearerTokenInterceptor(token));
		base = okRestClient.uri(url);
	}

	public OkRestTarget getBaseTarget() {
		return base.path(version);
	}

	public String getToken() {
		return token;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public JsonNode get(String path, Map<String, String> params) {
		try { 
			OkRestTarget target = getBaseTarget().path(path);
			if (params != null) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					target = target
							.queryParameter(entry.getKey(), entry.getValue());
				}
			}
			
			OkRestResponse response = target.get().execute();
			return response.getBody(JsonNode.class);
		} catch (IOException e) { 
			//not sure what to do here
			return mapper.createObjectNode().put("error", "get failed: " + e.getMessage());
		
		}
			

	}

	@Override
	public JsonNode get(String path, String... args) {
		Map<String, String> m = Maps.newHashMap();
		for (int i = 0; i < args.length; i += 2) {
			m.put(args[i], args[i + 1]);
		}
		return get(path, m);
	}

	@Override
	public void post(String path, JsonNode body) {
		try {
			getBaseTarget()
					.path(path)
					.post(body)
					.execute();
		} catch (IOException e) { 
			//IDK what to do here
		}

	}

	@Override
	public void put(String path, JsonNode body) {
		try { 
			getBaseTarget()	
				.path(path)
				.put(body)
				.execute();
		} catch (IOException e) { 
			//do something here
		}

	}

	@Override
	public void delete(String path) {
		try {
			getBaseTarget().path(path).delete().execute();
		} catch (IOException e) { 
			//idk 
		}

	}

	@Override
	public void sendRoomNotification(String roomId, String message) {
		try {
			ObjectNode n = mapper.createObjectNode();
			n.put("message", message);
			getBaseTarget().path("/room").path(roomId).path("notification")
					.post(n).execute();
		} catch (IOException e) {
			//IDK
		}

	}

	@Override
	public void sendRoomNotification(String roomId, String message, Format format,
			Color color, boolean notify) {
		sendRoomNotification(roomId, message, format.toString(),color.toString(),notify);
		
	}

	@Override
	public void sendRoomNotification(String roomId, String message,
			String format, String color, boolean notify) {
		try {
			ObjectNode n = mapper.createObjectNode();
			n.put("message", message);
			n.put("notify", notify);
			n.put("message_format", format);
			n.put("color", color);
			
			getBaseTarget().path("/room").path(roomId).path("notification")
					.post(n).execute();
		} catch (IOException e) {
			//IDK
		}
		
	}

}
