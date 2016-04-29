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
package io.macgyver.core.log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import io.macgyver.core.reactor.MacGyverEventPublisher;
import io.macgyver.core.reactor.MacGyverMessage;

public final class EventLogger {

	static Logger logger = LoggerFactory.getLogger(EventLogger.class);

	@Autowired
	MacGyverEventPublisher publisher;

	ObjectMapper mapper = new ObjectMapper();

	public Supplier<String> sourceHostSupplier = Suppliers.memoize(new SourceHostSupplier());

	public static class SourceHostSupplier implements Supplier<String> {

		@Override
		public String get() {
			try {
				String host = InetAddress.getLocalHost().getHostName();
				return host;
			} catch (UnknownHostException | RuntimeException e) {
				logger.warn("could not obtain local host name", e);
			}
			return "localhost";
		}

	}

	public class LogMessage extends MacGyverMessage {
		String label = null;
		boolean sent = false;

		public LogMessage withLabel(String label) {
			this.label = label;
			return this;
		}

		public String getLabel() {
			return label;
		}

		public LogMessage withMessage(String msg) {
			return (LogMessage) withAttribute("message", msg);
		}

		public LogMessage withSourceHost(String host) {
			return (LogMessage) withAttribute("sourceHost", host);
		}

		public void log() {
			logEvent(this);
		}
	}

	public LogMessage event() {
		return new LogMessage();
	}

	protected final void logEvent(LogMessage event) {

		try {

			// now log to the distributed system
			Preconditions.checkNotNull(publisher);
			Preconditions.checkNotNull(event,"event cannot be null");
			Preconditions.checkState(event.sent == false, "event already published");
			JsonNode data = event.getData();
			logger.info("logEvent data: {}",data);
			if (!data.has("sourceHost")) {
				event.withAttribute("sourceHost", sourceHostSupplier.get());
			}
			publisher.createMessage().withMessage(event).publish();
			event.sent = true;
		} catch (RuntimeException e) {
			logger.warn("could not publish log event", e);
		}
	}

}
