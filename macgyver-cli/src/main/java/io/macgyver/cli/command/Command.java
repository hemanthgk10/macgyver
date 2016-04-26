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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import io.macgyver.okrest.OkRestClient;
import io.macgyver.okrest.OkRestTarget;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public abstract class Command {

	Logger logger = LoggerFactory.getLogger(getClass());
	
	List<String> args;

	Properties props = new Properties();

	OptionSet optionSetx;
	
	OptionParser optionParser = new OptionParser();
	
	
	public abstract String getCommandName();
	
	public abstract String getDescription();
	
	public final Command init(String[] args) {
		this.args = Collections.unmodifiableList(Lists.newArrayList(args));
		
		OptionParser p = optionParser;
		//p.allowsUnrecognizedOptions();
		p.accepts("endpoint-url").withRequiredArg();
		p.accepts("user").withRequiredArg();
		p.accepts("config").withRequiredArg();
		p.accepts("token").withRequiredArg();
		p.accepts("debug");
		configure(optionParser);
		
		optionSetx = optionParser.parse(args);
		parseConfig();
		return doInit();
	}

	
	public List<String> args() {
		return args;
	}

	public Command doInit() {
		return this;
	}
	Optional<String> getToken() {
		OptionParser p = new OptionParser();
		p.allowsUnrecognizedOptions();
		p.accepts("token").withRequiredArg();	
		
		OptionSet os = p.parse(args.toArray(new String[0]));
		if (os.has("token")) {
			String x = java.util.Objects.toString(os.valueOf("token"),null);
			if (x!=null) {
				return Optional.of(x);
			}
		}
		
		
		return Optional.ofNullable(props.getProperty("token"));
	}

	protected abstract void configure(OptionParser p);
	
	public abstract void execute() throws IOException ;

	OkRestTarget target;
	
	public OkRestTarget getOkRestTarget() {
		if (target == null) {
			
	
			target = new OkRestClient().uri(getEndpointUrl().get()).addHeader("Authorization", "Token "+getToken().orElse(""));
		}
		return target;
	}

	public void parseConfig() {
		Properties p = new Properties();

		File f = getConfigFile();

		try (FileInputStream fis = new FileInputStream(f)) {
			p.load(fis);
			props.putAll(p);
		} catch (IOException e) {

		}

	
	}

	public Optional<String> getOptionalArg(String name) {
		if (optionSetx.has(name)) {
			String x = Objects.toString(optionSetx.valueOf(name),null);
			if (x!=null) {
				return Optional.ofNullable(Strings.emptyToNull(x));
			}	
		}
		return Optional.empty();
	}
	
	public Optional<String> getEndpointUrl() {
		
		
		Optional<String> val = getOptionalArg("endpoint-url");
		if (!val.isPresent()) {
	
			String x = Strings.emptyToNull(props.getProperty("endpoint-url","").trim());
			val = Optional.ofNullable(x);
		}
		
		logger.info("endpoint-url: "+val);
		
		return val;
	}
	
	public File getConfigFile() {
		File f = new File(System.getProperty("user.home", "."), ".macgyver/config");
		return f;
	}
}
