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
package io.macgyver.core.log;

import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import io.macgyver.core.reactor.MacGyverEventPublisher;
import io.macgyver.core.reactor.MacGyverMessage;

public final class EventLogger {

	Logger logger = LoggerFactory.getLogger(EventLogger.class);

	@Autowired
	MacGyverEventPublisher publisher;
	
	ObjectMapper mapper = new ObjectMapper();
	public static class LogMessage extends MacGyverMessage {

	}

	public class LogEventBuilder  {
		ObjectNode data = mapper.createObjectNode();
		String label = null;
		Instant instant = null;

		public LogEventBuilder withProperty(String key, String val) {
			data.put(key, val);
			return this;
		}

		public LogEventBuilder withMessage(String message) {
			return withProperty("message", message);
		}

		public LogEventBuilder withLabel(String label) {
			this.label = label;
			return this;
		}

		public LogEventBuilder withTimestamp(Instant instant) {
			this.instant = instant;
			return this;
		}

		public LogEventBuilder withTimestamp(long timestamp) {
			this.instant = Instant.ofEpochMilli(timestamp);
			return this;
		}

		public LogEventBuilder withTimestamp(Date d) {
			this.instant = d.toInstant();
			return this;
		}

		public void log() {
			logEvent(this);
		}
	}

	public LogEventBuilder event() {
		return new LogEventBuilder();
	}


	protected final void logEvent(LogEventBuilder event) {
		
		
		try {
		
			// now log to the distributed system
			Preconditions.checkNotNull(publisher);
			publisher.createMessage(LogMessage.class).withMessageBody(event.data).publish();
		} catch (Exception e) {
			logger.warn("could not log event to distributed event system", e);
		}
	}


}
