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
package io.macgyver.core;

import io.macgyver.core.eventbus.MacGyverEventBus;

import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.ServletRequestHandledEvent;

/**
 * Simple wrapper to start server.
 *
 * @author rschoening
 *
 */

@Configuration
@ComponentScan(basePackages = { "io.macgyver.config",
		"io.macgyver.plugin.config", "io.macgyver.core.config" })
@EnableAutoConfiguration
public class ServerMain {

	static Logger logger = org.slf4j.LoggerFactory.getLogger(ServerMain.class);

	static boolean daemonized = false;

	public static void main(String[] args) throws Exception {

		daemonizeIfRequired();

		Bootstrap.printBanner();

		if (!daemonized) {
			logger.info("process not daemonized; set -Dmacgyver.daemon=true to daemonize");
		}

		ApplicationListener<ApplicationEvent> x = new ApplicationListener<ApplicationEvent>() {

			@Override
			public void onApplicationEvent(ApplicationEvent event) {
				if (event instanceof ServletRequestHandledEvent) {
					// this will generate crazy logging if we log all servlet requests
					logger.debug("onApplicationEvent({})", event);
				} else {
					// but it is very nice to have this logging output, so log it at info
					logger.info("onApplicationEvent({})", event);
				}

			}
		};
		ConfigurableApplicationContext ctx = new SpringApplicationBuilder()
				.sources(ServerMain.class)
				.initializers(new SpringContextInitializer()).listeners(x)
				.run(args);

		Environment env = ctx.getEnvironment();

		logger.info("spring environment: {}", env);
		Kernel.getInstance().getApplicationContext()
				.getBean(MacGyverEventBus.class)
				.post(new Kernel.ServerStartedEvent(Kernel.getInstance()));
	}

	public static void daemonizeIfRequired() throws Exception {
		String val = System.getProperty("macgyver.daemon", "false");

		boolean daemonize = val.trim().equalsIgnoreCase("true");

		if (daemonize) {
			com.sun.akuma.Daemon d = new com.sun.akuma.Daemon();

			if (d.isDaemonized()) {
				daemonized = true;
				d.init();
			} else {
				if (daemonize) {
					d.daemonize();
					System.exit(0);
				}
			}
		}
	}

}
