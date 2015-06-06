package io.macgyver.plugin.cloud.cloudstack;

import io.macgyver.okrest.OkRestTarget;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;




import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;

public class RequestBuilder {

	
	String secretKey;
	Map<String, String> paramMap = Maps.newHashMap();

	CloudStackClientImpl client;
	
	public RequestBuilder param(String key, String val) {
		paramMap.put(key, val);
		return this;
	}

	public String generateCommandStringForHmac() {

		try {
			Map<String, String> copy = Maps.newHashMap();
			for (Map.Entry<String, String> entry : paramMap.entrySet()) {
				copy.put(entry.getKey().toLowerCase(), entry.getValue()
						.toLowerCase());
			}
			List<String> sortedKeys = Lists.newArrayList(copy.keySet());
			Collections.sort(sortedKeys);

			List<String> kvPairs = Lists.newArrayList();
			for (String x : sortedKeys) {
				String kv = URLEncoder.encode(x, "UTF-8") + "="
						+ URLEncoder.encode(copy.get(x), "UTF-8");
				kvPairs.add(kv);
			}
			
			return Joiner.on("&").join(kvPairs);
		
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String computeSignature() {
		
		String cs = generateCommandStringForHmac();
		System.out.println("cs: "+cs);
		System.out.println("sk: "+secretKey);
		String m = hmacSha1(cs, secretKey);
		
		System.out.println("m: "+m);
		return m;
		
	}
	public RequestBuilder apiKey(String s) {
		param("apiKey",s);
		return this;
	}
	public RequestBuilder secretKey(String val) {
		this.secretKey = val;
		return this;
	}
	public  String hmacSha1(String value, String signingKeyString) {
        try {
            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = signingKeyString.getBytes();           
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes());

            // Convert raw bytes to Hex
           return BaseEncoding.base64Url().encode(rawHmac);

           
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public JsonNode executeJson() {
		
		OkRestTarget t = client.target;
		param("request","json");
		for (Map.Entry<String, String> entry: paramMap.entrySet()) {
			t = t.queryParameter(entry.getKey(), entry.getValue());
		}
		t = t.queryParameter("signature", computeSignature());
		
		return t.get().execute(JsonNode.class);
	}
}
