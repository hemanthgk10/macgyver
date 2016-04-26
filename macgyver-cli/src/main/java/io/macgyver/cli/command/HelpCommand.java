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

import java.io.IOException;

import com.google.common.reflect.ClassPath;

import joptsimple.OptionParser;

public class HelpCommand extends Command {

	@Override
	protected void configure(OptionParser p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() throws IOException {
		ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClasses(getClass().getPackage().getName()).forEach(
				it -> {
					
				
						try {
						Command tc = (Command) (Class.forName(it.getName())).newInstance();
						System.out.println(tc.getCommandName()+" - "+tc.getDescription());
						
						}
						catch (ClassNotFoundException | IllegalAccessException | InstantiationException | RuntimeException e) {
							logger.debug("could not load: {}",it.getName());
					
						}
						
					
				});
		
	}

	public String getCommandName() {
		return "help";
	}
	public String getDescription() {
		return "summary of commands";
	}
}
