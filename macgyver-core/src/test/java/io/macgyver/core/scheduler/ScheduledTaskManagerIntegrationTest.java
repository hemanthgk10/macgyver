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
package io.macgyver.core.scheduler;

import java.util.UUID;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import io.macgyver.test.MacGyverIntegrationTest;

public class ScheduledTaskManagerIntegrationTest extends MacGyverIntegrationTest {

	@Inject
	ScheduledTaskManager stm;

	@Test
	public void testSetup() {
		Assertions.assertThat(stm).isNotNull();
	}

	@Test
	public void testStateExceptions() {
		try {
			stm.disable(UUID.randomUUID().toString());
			Assertions.failBecauseExceptionWasNotThrown(IllegalStateException.class);
		} catch (IllegalStateException e) {
			Assertions.assertThat(e).hasMessageContaining("id=").hasMessageContaining("does not exist");
		}
		
		try {
			stm.enable(UUID.randomUUID().toString());
			Assertions.failBecauseExceptionWasNotThrown(IllegalStateException.class);
		} catch (IllegalStateException e) {
			Assertions.assertThat(e).hasMessageContaining("id=").hasMessageContaining("does not exist");
		}
		
		try {
			stm.enable(UUID.randomUUID().toString(),false);
			Assertions.failBecauseExceptionWasNotThrown(IllegalStateException.class);
		} catch (IllegalStateException e) {
			Assertions.assertThat(e).hasMessageContaining("id=").hasMessageContaining("does not exist");
		}
		
		try {
			stm.scheduleByScript(UUID.randomUUID().toString());
			Assertions.failBecauseExceptionWasNotThrown(IllegalStateException.class);
		} catch (IllegalStateException e) {
			Assertions.assertThat(e).hasMessageContaining("id=").hasMessageContaining("does not exist");
		}
		
	
	}

}
