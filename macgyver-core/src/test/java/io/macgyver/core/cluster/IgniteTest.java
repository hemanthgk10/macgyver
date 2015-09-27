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
package io.macgyver.core.cluster;

import io.macgyver.test.MacGyverIntegrationTest;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class IgniteTest extends MacGyverIntegrationTest {

	@Autowired
	Ignite ignite;

	@Test
	public void testName() throws Exception {

		Assertions.assertThat(ignite.name()).contains("macgyver");

	}

	@Test
	public void testMessage() {
		IgniteMessaging m = ignite.message().withAsync();

		String message = UUID.randomUUID().toString();
		
		AtomicReference x = new AtomicReference<>();

		m.localListen("junit.message", (nodeId, msg) -> {
			logger.info("recv: {}" , msg);
			x.set(msg);
			return true;
		});

		Assertions.assertThat(x.get()).isNull();
		m.send("junit.message", message);
		long startTime = System.currentTimeMillis();
		while (x.get() != null && System.currentTimeMillis()-startTime<3000) {
			
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			}
		}
		Assertions.assertThat(x.get()).isNotNull().isEqualTo(message);
	}
}
