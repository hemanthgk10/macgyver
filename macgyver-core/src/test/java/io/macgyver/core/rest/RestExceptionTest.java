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
package io.macgyver.core.rest;

import io.macgyver.okrest.OkRestException;
import io.macgyver.okrest.OkRestWrapperException;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class RestExceptionTest {

	
	@Test
	public void testX() {
		RuntimeException x = new RuntimeException("abc123");
		
		OkRestException e = new OkRestWrapperException(x);
		
		Assertions.assertThat(e.getStatusCode()).isEqualTo(400);
		
		Assertions.assertThat(e).hasCauseExactlyInstanceOf(x.getClass()).hasMessageContaining("java.lang.RuntimeException: abc123");
		
	}
}
