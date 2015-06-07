package io.macgyver.plugin.artifactory;

import com.fasterxml.jackson.databind.JsonNode;

import io.macgyver.okrest.OkRestTarget;

public class AQLSearchBuilder {

	OkRestTarget target;
	String aql;
	
	AQLSearchBuilder(OkRestTarget t) {
		this.target = t;
	}
	public AQLSearchBuilder aql(String aql) {
		this.aql = aql;
		return this;
	}
	
	public JsonNode execute() {
		return target.path("api/search/aql").addHeader("Content-type", "text/plain").post(aql).execute(JsonNode.class);
	}
}
