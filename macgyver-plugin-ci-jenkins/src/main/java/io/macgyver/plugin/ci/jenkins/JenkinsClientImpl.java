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
package io.macgyver.plugin.ci.jenkins;

import io.macgyver.okrest.BasicAuthInterceptor;
import io.macgyver.okrest.OkRestLoggingInterceptor;
import io.macgyver.okrest.OkRestClient;
import io.macgyver.okrest.OkRestException;
import io.macgyver.okrest.OkRestResponse;
import io.macgyver.okrest.OkRestTarget;
import io.macgyver.okrest.OkRestWrapperException;
import io.macgyver.okrest.compat.OkUriBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class JenkinsClientImpl implements JenkinsClient {

	Logger logger = LoggerFactory.getLogger(JenkinsClientImpl.class);

	OkRestTarget target;

	private String urlBase;


	protected JenkinsClientImpl(String urlBase, String username, String password) {
		this.urlBase = urlBase;


		target = new OkRestClient().uri(urlBase);

		if (!Strings.isNullOrEmpty(username)) {
			target.getOkHttpClient().interceptors()
					.add(new BasicAuthInterceptor(username, password));
		}
	}

	@Override
	public JsonNode getJson(String path) {

		return target.path(path).get().execute(JsonNode.class);

	}

	@Override
	public JsonNode getServerInfo() {
		return getJson("api/json");
	}

	@Override
	public JsonNode getJob(String jobName) {

		return target.path("job").path(jobName).path("api").path("json").get()
				.execute(JsonNode.class);

	}

	@Override
	public JsonNode getBuild(String jobName, int buildNumber) {

		// this is wrong
		return target.path("job").path(jobName).path("api").path("json").get()
				.execute(JsonNode.class);

	}

	@Override
	public org.jdom2.Document getJobConfig(String jobName) {

		try {
			OkRestResponse rr = target.path("job").path(jobName)
					.path("config.xml").get().execute();

			return new SAXBuilder().build(rr.response().body().byteStream());
		} catch (IOException | JDOMException e) {
			throw new OkRestWrapperException(e);
		}

	}

	@Override
	public List<String> getJobNames() {
		List<String> tmp = new ArrayList<String>();
		for (JsonNode n : Lists.newArrayList(getServerInfo().path("jobs")
				.elements())) {
			tmp.add(n.path("name").asText());
		}
		return tmp;
	}

	@Override
	public String executeGroovyScript(String groovy) {
		try {

			String url = new OkUriBuilder().uri(urlBase).path("scriptText").build().toString();

			RequestBody formBody = new FormEncodingBuilder().add("script",
					groovy).build();

			Request request = new Request.Builder().url(url).post(formBody)
					.build();

			OkHttpClient client = target.getOkHttpClient();
			Response response = client.newCall(request).execute();

			throwRestExceptionOnError(response);

			return response.body().string();

		} catch (IOException e) {
			throw new OkRestWrapperException(e);
		}
	}

	protected void throwRestExceptionOnError(Response r) {
		int sc = r.code();

		if (sc > 299) {
	
			throw new OkRestException(sc);
		}
	}

	@Override
	public JsonNode build(String jobName) {
		try {

			String url = new OkUriBuilder().uri(urlBase).path("job").path(jobName)
					.path("build").build().toString();

			RequestBody formBody = new FormEncodingBuilder().add("__dummy__",
					"").build();

			Request request = new Request.Builder()
					.addHeader("Accept", "application/json").url(url)
					.post(formBody).build();

			OkHttpClient client = target.getOkHttpClient();

			Response response = client.newCall(request).execute();
			
			String locationHeader = response.header("Location");
			
			if (Strings.isNullOrEmpty(locationHeader)) { 
			
				JsonNode responseAsJson = new ObjectMapper().readTree(response.body().string());
				
				return responseAsJson;
			} else {
				
				Optional<String> qp = extractQueuePath(locationHeader);

				if (qp.isPresent()) {
					return getJson(new OkUriBuilder().uri(qp.get()).path("api/json")
							.build().toString());
				} else {
					throw new IllegalStateException(
							"jenkins should have returned a Locaton header");
				}

			}

		} catch (IOException e) {
			throw new OkRestWrapperException(e);
		}
	}


	Optional<String> extractQueuePath(String location) {
		Pattern p = Pattern.compile(".*(\\/queue\\/item\\/\\d+)[$\\/]*.*");
		Matcher m = p.matcher(location);
		if (m.matches()) {
			return Optional.fromNullable(m.group(1));
		}
		return Optional.absent();

	}

	@Override
	public JsonNode buildWithParameters(String jobName, Map<String, String> m) {
		try {

			String url = new OkUriBuilder().uri(urlBase).path("job").path(jobName)
					.path("buildWithParameters").build().toString();

			FormEncodingBuilder builder = new FormEncodingBuilder();

			if (m == null || m.isEmpty()) {
				builder = builder.add("__dummy__", "__dummy__");
			} else {
				for (Map.Entry<String, String> entry : m.entrySet()) {
					builder = builder.add(entry.getKey(), entry.getValue());
				}
			}

			RequestBody formBody = builder.build();

			OkHttpClient client = target.getOkHttpClient();
			Request request = new Request.Builder()
					.addHeader("Accept", "application/json").url(url)
					.post(formBody).build();
			

			Response response = client.newCall(request).execute();
			
			throwRestExceptionOnError(response);
			
			String locationHeader = response.header("Location");
			
			if (Strings.isNullOrEmpty(locationHeader)) { 
			
				JsonNode responseAsJson = new ObjectMapper().readTree(response.body().string());
				return responseAsJson;
				
			} else {
				Optional<String> qp = extractQueuePath(locationHeader);

				if (qp.isPresent()) {
					return target.path(qp.get()).path("api").path("json").get().execute(JsonNode.class);
					
				} else {
					throw new IllegalStateException(
							"jenkins should have returned a Locaton header");
				}
			}
		

		} catch (IOException e) {
			throw new OkRestWrapperException(e);
		}
	}

	@Override
	public JsonNode buildWithParameters(String name, String... args) {

		com.google.common.base.Preconditions.checkArgument(
				!Strings.isNullOrEmpty(name),
				"job name cannot be null or empty");

		Map<String, String> m = new HashMap<>();

		if (args != null) {
			Preconditions.checkArgument(args.length % 2 == 0,
					"parameters must be in key value pairs");
			for (int i = 0; i < args.length - 1; i += 2) {
				String key = args[i];
				String val = args[i + 1];
				m.put(key, val);

			}
		}
		return buildWithParameters(name, m);
	}

	@Override
	public JsonNode getBuildQueue() {

		return getJson(new OkUriBuilder().path("queue/api/json").build().toString());
	}

	@Override
	public JsonNode getLoadStats() {
		return getJson(new OkUriBuilder().path("overallLoad/api/json").build().toString());
	}

	@Override
	public void restart() {
		try {
			postWithoutResult("restart");
		} catch (OkRestException e) {
			if (e.getStatusCode() == 503) {
				return;
			}
			throw e;
		}
	}

	@Override
	public void restartAfterJobsComplete() {
		try {
			postWithoutResult("safeRestart");
		} catch (OkRestException e) {
			if (e.getStatusCode() == 503) {
				return;
			}
			throw e;
		}

	}

	@Override
	public void quietDown() {
		postWithoutResult("quietDown");

	}

	protected void postWithoutResult(String path) {
		try {

			String url = new OkUriBuilder().uri(urlBase).path(path).build().toString();

			Request request = new Request.Builder()
					.addHeader("Accept", "application/json")
					.url(url)
					.post(RequestBody.create(
							MediaType.parse("application/json"), "{}")).build();

			OkHttpClient client = target.getOkHttpClient();
			Response response = client.newCall(request).execute();

			throwRestExceptionOnError(response);

		} catch (IOException e) {
			throw new OkRestWrapperException(e);
		}
	}

	@Override
	public void cancelQuietDown() {
		postWithoutResult("cancelQuietDown");
	}

	public String getServerId() {
		return Hashing.sha1().hashString(getServerUrl(), Charsets.UTF_8)
				.toString();
	}

	@Override
	public String getServerUrl() {
		return urlBase;
	}

}
