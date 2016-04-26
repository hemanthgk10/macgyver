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
package io.macgyver.cli.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;

import io.macgyver.okrest.OkRestClient;
import io.macgyver.okrest.OkRestResponse;
import io.macgyver.okrest.OkRestTarget;
import joptsimple.OptionParser;

public class LoginCommand extends Command {

	public String getCommandName() {
		return "login";
	}

	public String getDescription() {
		return "obtains an api token";
	}

	public Optional<String> getUsername() {

		Optional<String> optUser = Optional.empty();

		String username = java.util.Objects.toString(optionSetx.valueOf("user"), "").trim();

		if (!Strings.isNullOrEmpty(username)) {
			logger.debug("username from command line is: {}", username);
			return Optional.ofNullable(username);
		}

		username = props.getProperty("username");
		if (!Strings.isNullOrEmpty(username)) {
			logger.debug("username from config file is: {}", username);
			return Optional.of(username.trim());
		}

		return Optional.of(System.getProperty("user.name"));
	}

	char[] readPassword() {
		char[] p = System.console().readPassword("%s", "Password: ");
		return p;
	}

	


	public String readUrl() {
		String url = super.getEndpointUrl().orElse("");
		url = System.console().readLine("url      [%s]: ", url);
		if (Strings.isNullOrEmpty(url)) {
			url = super.getEndpointUrl().orElse("");
		
			
		}	
		props.put("endpoint-url", url);
		return url;
	}
	public String readUsername() {
		String username = getUsername().orElse("");
		username = Strings.nullToEmpty(System.console().readLine("username [%s]: ", username)).trim();		
		if (Strings.isNullOrEmpty(username)) {
			username = getUsername().orElse("");
		}
		logger.info("username: " + username);
		props.put("username", username);
		return username;
	}
	
	String localUrl;
	@Override
	public Optional<String> getEndpointUrl() {
		return Optional.ofNullable(localUrl);
	}

	@Override
	public void execute() throws IOException {

		
		localUrl = readUrl();
		
	
		String username = readUsername();
		
		char[] password = readPassword();

		String header = username + ":" + new String(password);

		
		header = "Basic " + BaseEncoding.base64().encode(header.getBytes());
		
	
		OkRestResponse rr = new OkRestClient().uri(getEndpointUrl().get()).path("/api/core/token/create").addHeader("Authorization", header).get()
				.execute();

		JsonNode n = rr.getBody(JsonNode.class);
	

		Properties p = new Properties();
		p.putAll(props);
		p.put("username", n.path("username").asText());
		p.put("token", n.path("token").asText());

		File d = new File(System.getProperty("user.home"), ".macgyver");
		if (!d.exists()) {
			d.mkdirs();
		}
		File config = new File(d, "config");
		try (FileOutputStream fos = new FileOutputStream(config)) {
			p.store(fos, "written by login command");
		}
	}

	@Override
	protected void configure(OptionParser p) {
		// TODO Auto-generated method stub

	}

}
