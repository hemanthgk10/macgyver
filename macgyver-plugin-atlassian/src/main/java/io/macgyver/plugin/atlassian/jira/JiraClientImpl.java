package io.macgyver.plugin.atlassian.jira;

import java.io.IOException;

import io.macgyver.core.rest.BasicAuthInterceptor;
import io.macgyver.okrest.OkRestClient;
import io.macgyver.okrest.OkRestTarget;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.OkHttpClient;

public class JiraClientImpl implements JiraClient {

	private OkRestTarget target;
	
	public JiraClientImpl(String url, String username, String password) {
		
		OkHttpClient c = new OkHttpClient();
		c.interceptors().add(new BasicAuthInterceptor(username, password));
		
		OkRestClient client = new OkRestClient(c);
		target = client.url(url);
		
	}
	@Override
	public JsonNode getIssue(String issue) throws IOException {
		return target.path("issue").path(issue).get().execute(JsonNode.class);
	}

	@Override
	public JsonNode getJson(String path) throws IOException {
		return target.path(path).get().execute(JsonNode.class);
	}

	@Override
	public JsonNode postJson(String path, JsonNode body) throws IOException{
		return target.path(path).post(body).execute(JsonNode.class);
	}
	
	
	@Override
	public OkRestTarget getOkRestTarget() {
		return target;
	}

}
