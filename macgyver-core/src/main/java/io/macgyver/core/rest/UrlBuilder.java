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
package io.macgyver.core.rest;

import io.macgyver.core.MacGyverException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;

@Deprecated
public class UrlBuilder {

	private String base;

	private List<String> parts = Lists.newArrayList();

	private Map<String, String> queryParameters = Maps.newLinkedHashMap();

	public UrlBuilder() {

	}

	public UrlBuilder(String baseUrl) {
		this.base = baseUrl;
	}

	public UrlBuilder url(String url) {
		return this;
	}
	public UrlBuilder base(String base) {
		this.base = base;
		return this;
	}

	public UrlBuilder path(String p) {
		if (p != null && p.length() > 0) {
			parts.add(p);
		}
		return this;
	}

	public UrlBuilder queryParam(String key, String val) {
		queryParameters.put(key, val);
		return this;
	}

	protected String encodePathPart(String p) {
		try {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < p.length(); i++) {

				if (p.charAt(i) == '/') {
					sb.append(p.charAt(i));
				} else {
					sb.append(URLEncoder.encode(
							Character.toString(p.charAt(i)), "UTF-8"));

				}
			}

			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			throw new MacGyverException(e);
		}
	}

	public UrlBuilder queryParam(Map<String, String> q) {
		UrlBuilder b = this;

		if (q != null) {
			for (Map.Entry<String, String> entry : q.entrySet()) {
				b = b.queryParam(entry.getKey(), entry.getValue());
			}
		}
		return b;
	}

	public String build() {

		Preconditions.checkState(base != null, "base url not set");
		String url = base;
		try {
			for (String append : parts) {
				append = encodePathPart(append);
				while (url.endsWith("/") && url.length() > 0) {
					url = url.substring(0, url.length() - 1);
				}
				while (append.startsWith("/") && append.length() > 0) {
					append = append.substring(1);
				}
				url = url + "/" + append;

			}

			if (queryParameters != null && !queryParameters.isEmpty()) {
				StringBuffer sb = new StringBuffer();
				int count = 0;
				for (Map.Entry<String, String> entry : queryParameters
						.entrySet()) {
					String key = entry.getKey();
					String val = entry.getValue();
					if (count++ > 0) {
						sb.append("&");
					}

					sb.append(URLEncoder.encode(key, "UTF-8"));
					sb.append("=");
					sb.append(URLEncoder.encode(val, "UTF-8"));

				}

				if (url.contains("?")) {
					url = url + "&" + sb.toString();
				} else {
					url = url + "?" + sb.toString();
				}
			}

			return url;
		} catch (UnsupportedEncodingException e) {
			throw new MacGyverException(e);
		}

	}

}
