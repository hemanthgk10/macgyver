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
package io.macgyver.plugin.elb.f5;

import java.util.Map;
import java.util.Properties;

import org.jdom2.Element;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.okhttp.Response;

import io.macgyver.core.service.ServiceFactory;
import io.macgyver.okrest3.OkRestClient;
import io.macgyver.okrest3.OkRestTarget;
import joptsimple.internal.Strings;
import okhttp3.OkHttpClient;

public class F5Client {

	OkRestTarget target;
	
	public static class Builder {
		
		
		String url;
		String username;
		String password;
		boolean validateCerts=true;
		
		public Builder withUrl(String url) {
			this.url = url;
			return this;
		}
		
		public Builder withCredentials(String username, String password) {
			this.username = username;
			this.password = password;
			return this;
		}
		
		public Builder withProperties(Properties p) {
			
			String username = p.getProperty("username");
			String password = p.getProperty("password");
			String url = p.getProperty("url");
			
			if (username!=null && password!=null) {
				withCredentials(username,password);
			}
			
			if (url!=null) {
				withUrl(url);
			}
			boolean certVerificationEnabled = Boolean.parseBoolean(p.getProperty(ServiceFactory.CERTIFICATE_VERIFICATION_ENABLED, "true"));
			withCertificateVerificationEnabled(certVerificationEnabled);
			return this;
		}
		public Builder withCertificateVerificationEnabled(boolean b) {
			validateCerts = b;
			return this;
		}
		
		public F5Client build() {
			
			F5Client client = new F5Client();
			
			io.macgyver.okrest3.OkRestClient.Builder b = new OkRestClient.Builder();
			
			if (!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(password)) {
				b = b.withBasicAuth(username, password);
			}
			if (!validateCerts) {
				b = b.disableCertificateVerification();
			}
			client.target = b.build().uri(url);
			
			return client;
		}
	}
	
	public OkRestTarget getTarget() {
		return target;
	}
	
}
