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
package io.macgyver.cli;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import io.macgyver.cli.command.Command;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.internal.Strings;

public class Main {

	static Logger logger = null;

	public Main() {

	}

	protected Command command(String[] args) {
		try {
			OptionParser parser = new OptionParser();
			parser.allowsUnrecognizedOptions();
			OptionSet set = parser.parse(args);

			List<String> list = (List<String>) set.nonOptionArguments();
			AtomicReference<String> commandName = new AtomicReference<String>(null);
			list.forEach(it -> {
				if (commandName.get() == null) {

					String x = com.google.common.base.Strings.nullToEmpty(it).trim();
					if (x.startsWith("http:") || x.startsWith("https:")) {
						// not a command
					} else {
						if (Character.isAlphabetic(it.charAt(0))) {
							commandName.set(it);
						}
					}
				}
			});

			logger.info("command: {}", commandName);

			if (Strings.isNullOrEmpty(commandName.get())) {
				throw new CLIParseException("no command specified");
			}
			String className = toClassName(commandName.get());

			logger.info("command class: {}", className);

			try {
				Command command = (Command) Class.forName(className).newInstance();
				command.init(args);

				return command;
			} catch (ClassNotFoundException e) {

				throw new CLIParseException("command not found: " + commandName);
			}

		} catch (CLIParseException e) {
			throw e;
		} catch (RuntimeException | InstantiationException | IllegalAccessException e) {
			logger.warn("",e);
			throw new CLIParseException(e);
		}
	}

	String toClassName(String commandName) {
		boolean upper = true;
		StringBuffer sb = new StringBuffer();
		for (char c : commandName.toCharArray()) {
			if (upper) {
				c = Character.toUpperCase(c);
				upper = false;
			}
			if (Character.isAlphabetic(c)) {
			
				sb.append(c);
			} else {
				upper = true;
			}

		}
		
		return Command.class.getPackage().getName()+"."+sb.toString()+"Command";
	}

	

	public static void main(String[] args) throws IOException {

		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		parser.accepts("debug");
		OptionSet os = parser.parse(args);

		if (os.has("debug")) {
			logger = LoggerFactory.getLogger(Main.class);
		} else {

			LoggerContext logCtx = (LoggerContext) LoggerFactory.getILoggerFactory();
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			ch.qos.logback.classic.Logger myLogger = loggerContext
					.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
			myLogger.detachAndStopAllAppenders();

			logger = myLogger;
			PatternLayoutEncoder encoder = new PatternLayoutEncoder();
			encoder.setContext(loggerContext);
			encoder.setPattern("%d{HH:mm:ss.SSS} [%-5level] %msg %n");
			encoder.start();

			FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
			fileAppender.setContext(loggerContext);
			fileAppender.setName("MY_FILE_LOGGER");
			fileAppender.setAppend(true);
			fileAppender.setEncoder(encoder);

			File f = new File(System.getProperty("user.home", "."), ".macgyver/macgyver.log");

			fileAppender.setFile(f.getAbsolutePath());
			fileAppender.start();
			myLogger.addAppender(fileAppender);
		}
		try {
			new Main().command(args).execute();

		} catch (CLIParseException e) {

			System.err.println("");
			System.err.println("error: " + e.getMessage());

		}

	}

}
