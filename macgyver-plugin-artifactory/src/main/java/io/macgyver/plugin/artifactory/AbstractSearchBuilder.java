package io.macgyver.plugin.artifactory;

import io.macgyver.core.rest.OkRest;
import io.macgyver.core.rest.OkRest.InvocationBuilder;
import io.macgyver.okrest.OkRestTarget;

import java.io.IOException;
import java.util.List;

import org.assertj.core.util.Lists;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public abstract class AbstractSearchBuilder<T> {

	OkRestTarget target;

	List<String> repoList = Lists.newArrayList();
	boolean detailProperties = true;
	boolean detailInfo = true;
	public String path;
	
	public AbstractSearchBuilder(OkRestTarget r, String p) {
		Preconditions.checkNotNull(r);
		this.target = r;
		this.path = p;
	}



	public T inAllRepos() {
		this.repoList.clear();
		return (T) this;
	}

	public T inRepo(String name) {
		Preconditions.checkNotNull(name);
		this.repoList.add(name);
		return (T) this;
	}

	public T inRepos(String... names) {
		for (String n : names) {
			this.repoList.add(n);
		}
		return (T) this;
	}

	public T withProperties(boolean b) {
		this.detailProperties = b;
		return (T) this;
	}

	public T withInfo(boolean b) {
		this.detailInfo = b;
		return (T) this;
	}

	public void formatRequest() throws IOException {
		String xResultDetail = "";
		if (detailInfo) {
			xResultDetail = "info";
		}
		if (detailProperties) {
			if (!Strings.isNullOrEmpty(xResultDetail)) {
				xResultDetail = xResultDetail + ", ";
			}
			xResultDetail = xResultDetail + "properties";

		}
		if (!com.google.common.base.Strings.isNullOrEmpty(xResultDetail)) {
			target = target.header("X-Result-Detail", xResultDetail);
		}
		if (!repoList.isEmpty()) {
			target = target.queryParameter("repos", Joiner.on(",").skipNulls()
					.join(repoList));
		}	
	}
	
	public JsonNode execute() throws IOException {
		
		formatRequest();
		OkRestTarget t = target.path(path);

		return t.get().execute(JsonNode.class);
	}
}
