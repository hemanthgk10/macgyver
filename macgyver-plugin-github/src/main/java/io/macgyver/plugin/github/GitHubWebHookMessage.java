/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.macgyver.plugin.github;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.google.common.io.ByteStreams;

import io.macgyver.core.reactor.MacGyverMessage;

public class GitHubWebHookMessage extends MacGyverMessage {
	static ObjectMapper mapper = new ObjectMapper();

	HttpServletRequest request;
	byte[] rawData;

	String signature;
	String deliveryId;
	String eventType;
	
	
	public GitHubWebHookMessage(HttpServletRequest request) throws IOException {
		this.request = request;
		this.rawData = ByteStreams.toByteArray(request.getInputStream());
		
		// https://developer.github.com/webhooks/#delivery-headers
		
		signature = request.getHeader("X-Hub-Signature");
		deliveryId = request.getHeader("X-GitHub-Delivery");
		eventType = request.getHeader("X-GitHub-Event");
		
		withData(mapper.readTree(this.rawData));
		
		
	
	}
	
	public Optional<HttpServletRequest> getServletRequest() {
		return Optional.ofNullable(request);
	}
	public Optional<String> getWebHookDeliveryId() {
		return Optional.ofNullable(deliveryId);
	}
	
	public Optional<String> getWebHookEventType() {
		return Optional.ofNullable(eventType);
	}
	
	public Optional<String> getWebHookSignature() {
		return Optional.ofNullable(signature);
	}
	
	public Optional<byte []> getWebHookRawData() {
		return Optional.ofNullable(rawData);
	}
	
	public String toString() {
		return MoreObjects.toStringHelper(this).add("webHookDeliveryId", getWebHookDeliveryId().orElse(null)).add("webHookEventType", getWebHookEventType().orElse(null)).toString();
	}
}
