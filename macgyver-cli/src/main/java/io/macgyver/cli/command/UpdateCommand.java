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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameters;
import com.google.common.base.Strings;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import io.macgyver.cli.CLIException;
import io.macgyver.cli.Command;
import okhttp3.Response;


@Parameters()
public class UpdateCommand  extends Command {

	Logger logger = LoggerFactory.getLogger(UpdateCommand.class);

	@Override
	public void execute() throws IOException {
		
		String exe = System.getProperty("cli.exe");
		if (Strings.isNullOrEmpty(exe)) {
			System.err.println("error: update can only be called from packaged executable");
			System.exit(1);
		}
		File exeFile = new File(exe);
		File exeBackup = new File(exe+".bak");
		
		Files.copy(exeFile, exeBackup);
		
		File tempDir = Files.createTempDir();
		File tempExe = new File(tempDir,"macgyver");
		Response response = getOkRestTarget().path("/cli/download").get().execute().response();
		if (!response.isSuccessful()) {
			throw new CLIException("could not download");
		}
		try (InputStream is = response.body().byteStream()) {
			try (FileOutputStream fos = new FileOutputStream(tempExe)) {
				ByteStreams.copy(is, fos);
				tempExe.setExecutable(true);
				Files.copy(tempExe,exeFile);
			}
		}
		
		
		
	}
	
	

}
