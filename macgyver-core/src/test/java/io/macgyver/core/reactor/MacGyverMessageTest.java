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
package io.macgyver.core.reactor;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.macgyver.core.reactor.MacGyverEventPublisher.MessageBuilder;

public class MacGyverMessageTest {

	ObjectMapper mapper = new ObjectMapper();

	
	public static class TestMessage extends MacGyverMessage {

	}

	@Test

	public void testTestMessage() {
		MacGyverEventPublisher p = new MacGyverEventPublisher();
		MacGyverMessage m = p.createMessage(TestMessage.class).publish();
		
		Assertions.assertThat(m).isInstanceOf(TestMessage.class);
		
		Assertions.assertThat(m.getData().isObject()).isTrue();
	}


	
}
