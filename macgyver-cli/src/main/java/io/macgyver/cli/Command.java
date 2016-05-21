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
package io.macgyver.cli;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import io.macgyver.okrest3.LoggingInterceptor;
import io.macgyver.okrest3.LoggingInterceptor.Level;
import io.macgyver.okrest3.OkRestClient;
import io.macgyver.okrest3.OkRestTarget;

public abstract class Command {

	public final ObjectMapper mapper = new ObjectMapper();
	
	Logger logger = LoggerFactory.getLogger(getClass());

	List<String> args;

	Properties props = new Properties();

	CLI cli;

	@Parameter(names = "--debug", hidden = true)
	boolean debugEnabled = false;

	@Parameter(names = "--help", help = true, hidden = true)
	private boolean help = false;

	public boolean isHelpRequested() {
		return help;
	}

	public String getCommandName() {
		List<String> list = Splitter.on(".").splitToList(getClass().getName());
		String name = list.get(list.size() - 1);

		if (name.endsWith("Command")) {
			name = name.substring(0, name.length() - "Command".length());
		}

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);

			if (i > 0 && Character.isUpperCase(c)) {
				sb.append("-");
			}
			sb.append(Character.toLowerCase(c));
		}

		return sb.toString();

	}

	public final Command init(String[] args) {

		return doInit();

	}

	public List<String> args() {
		return args;
	}

	public Command doInit() {
		return this;
	}

	protected Optional<String> getToken() {

		return Optional.ofNullable(Strings.emptyToNull(getConfig().path("token").asText(null)));
	}

	public abstract void execute() throws IOException;

	OkRestTarget target;

	public OkRestTarget getOkRestTarget() {
		if (target == null) {

			String token = getToken().orElse(null);

			target = new OkRestClient.Builder().withOkHttpClientConfig(cfg -> {
				cfg.readTimeout(60, TimeUnit.SECONDS);
				if (isDebugEnabled()) {
					cfg.addInterceptor(new LoggingInterceptor().withLevel(Level.BODY).withLogger(getClass()));
				}
			}).build().uri(getServerUrl());

			if (!Strings.isNullOrEmpty(token)) {

				target = target.addHeader("Authorization", "Token " + token);
			}

		}
		return target;
	}

	public ObjectNode getConfig() {
		CLI cli = getCLI();
		ConfigManager cm = cli.getConfigManager();
		ObjectNode n = cm.getData();
		return n;
	}

	public String getServerUrl() {

		String url = getConfig().path("url").asText().trim();
		if (Strings.isNullOrEmpty(url)) {
			throw new CLIException("url must be set");
		} else if ((!url.startsWith("http://")) && (!url.startsWith("https://"))) {
			throw new CLIException("url must be http(s)");
		}
		return url;
	}

	public CLI getCLI() {
		return cli;
	}

	public void exitWithError(String message) {
		throw new CLIException(message);
	}

	public void exitWithUsage() {
		throw new CLIUsageException();
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("name", getCommandName()).toString();
	}
	public void statusOutput(String s) {
		System.err.println(s);
	}
	public boolean isDebugEnabled() {
		return debugEnabled;
	}
	
	public ConfigManager getConfigManager() {
		return getCLI().getConfigManager();
	}
}
