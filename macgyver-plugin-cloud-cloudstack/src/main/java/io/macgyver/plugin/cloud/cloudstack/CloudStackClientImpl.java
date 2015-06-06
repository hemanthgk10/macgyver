package io.macgyver.plugin.cloud.cloudstack;

import io.macgyver.okrest.OkRestClient;
import io.macgyver.okrest.OkRestTarget;



public class CloudStackClientImpl implements CloudStackClient {

	
	String apiKey;
	String secretKey;
	
	OkRestTarget target;
	
	public CloudStackClientImpl(String url, String apiKey, String secretKey) {
		target = new OkRestClient().uri(url);
		this.apiKey = apiKey;
		this.secretKey = secretKey;
	}
	@Override
	public RequestBuilder newRequest() {
		RequestBuilder b = new RequestBuilder().apiKey(apiKey).secretKey(secretKey);
		b.client = this;
		
		return b;
	}

}
