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

import org.assertj.core.api.Assertions;
import org.junit.Test;

import io.macgyver.cli.command.LoginCommand;

public class LoginCommandTest {

	
	@Test
	public void testIt() {
		
		LoginCommand c = new LoginCommand();
		c.init(new String [] {"--user","foo"});		
		Assertions.assertThat(c.getUsername().get()).isEqualTo("foo");
		
		
		c = new LoginCommand();
		c.init(new String [] {});		
		
		

	}
}
