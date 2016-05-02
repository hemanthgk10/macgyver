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
package io.macgyver.core.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.io.ByteStreams;

import io.macgyver.core.Bootstrap;
import io.macgyver.core.Kernel;
import net.lingala.zip4j.exception.ZipException;

@Controller
@PermitAll
@RequestMapping("/")
public class CLIDownloadController {

	Logger logger = LoggerFactory.getLogger(CLIDownloadController.class);

	ClientPackager clientPackager;
	
	@PostConstruct
	public void buildCli() {
		try {
			File cliDir = new File(Bootstrap.getInstance().getMacGyverHome(),ClientPackager.CLIENT_DIR_NAME);
			cliDir.mkdirs();
			File macgyverClient = new File(cliDir,"macgyver");
			macgyverClient.delete();
		
			clientPackager = new ClientPackager().withOutputDir(cliDir);
			clientPackager.rebuild();
			
		}
		catch (IOException | RuntimeException e) {
			logger.warn("could not build CLI",e);
		}
		
	}
	
	@RequestMapping("/cli")
	public ModelAndView cli() {
		return new ModelAndView("cli/index");
	}
	@RequestMapping("/cli/download")
	public void downloadClient(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		clientPackager.rebuildIfNecessary();
		response.setHeader("Content-Disposition", "attachment; filename=macgyver");
		
		OutputStream os = response.getOutputStream();
		
		try (FileInputStream is = new FileInputStream(clientPackager.getMacGyvverCLIExecutable())) {
			ByteStreams.copy(is, os);
		}
		
		os.close();
	}
}
