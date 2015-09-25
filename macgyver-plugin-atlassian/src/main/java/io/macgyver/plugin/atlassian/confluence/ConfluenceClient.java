package io.macgyver.plugin.atlassian.confluence;

import io.macgyver.okrest.OkRestClient;
import io.macgyver.okrest.OkRestTarget;

public interface ConfluenceClient {

	OkRestTarget getBaseTarget();
	ContentRequestBuilder contentRequest();
}
