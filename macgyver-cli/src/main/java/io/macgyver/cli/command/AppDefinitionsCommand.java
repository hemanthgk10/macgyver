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

import io.macgyver.okrest.OkRestResponse;
import joptsimple.OptionParser;

public class AppDefinitionsCommand extends Command {

	@Override
	public String getCommandName() {
		return "app-definitions";
	}

	@Override
	public String getDescription() {
		return "view app definitions";
	}

	@Override
	protected void configure(OptionParser p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute() throws IOException {
		
		OkRestResponse rr = getOkRestTarget().path("/api/cmdb/app-definitions").get().execute();
		
		System.out.println(rr.response().body().string());

	}

}
