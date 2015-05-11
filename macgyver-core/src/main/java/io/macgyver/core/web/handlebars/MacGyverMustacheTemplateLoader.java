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
package io.macgyver.core.web.handlebars;

import io.macgyver.core.Bootstrap;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mustache.MustacheResourceTemplateLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.samskivert.mustache.Template;


public class MacGyverMustacheTemplateLoader extends MustacheResourceTemplateLoader {

	@Autowired
	ApplicationContext applicationContext;

	Logger logger = LoggerFactory.getLogger(MacGyverMustacheTemplateLoader.class);

	public MacGyverMustacheTemplateLoader( ) {
		super();
		setCharset("UTF-8");
	}


	
	@Override
	public Reader getTemplate(String name) throws IOException {

		
		logger.debug("getTemplate({})",name);
		Reader reader = null;
		String location = name+".html";
		try {
		
			File resourceLocation = new File(Bootstrap.getInstance()
					.getWebDir(), location);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Searching for {} on filesystem: {}",location,resourceLocation);
			}
			if (resourceLocation != null && resourceLocation.exists()) {

				reader = new FileReader(resourceLocation);
				
		
				if (logger.isDebugEnabled()) {
					logger.debug("resolved template for {} to {}", resourceLocation );
				}
			}

			if (reader==null) {
				
				// we couldn't find a file in web dir...fall back to looking in the classpath...
				
				
				String resourceName = "classpath:"
						+ appendPath("web", location);
				
				if (logger.isDebugEnabled()) {
					logger.debug("searching for {} in classpath: {}",location, resourceName);
				}
				Resource resource = applicationContext
						.getResource(resourceName);
				if (resource.exists()) {
					
					if (logger.isDebugEnabled()) {
						logger.debug("resolved template for {} to {}", location, resource);
					}
					
					reader = new InputStreamReader(resource.getInputStream());
						
				}

			}

		} catch (RuntimeException e) {
			logger.warn("problem loading template", e);
		}

		return reader;

	}

	String appendPath(String a, String b) {
		StringBuilder sb = new StringBuilder();

		sb.append(a);
		if (!a.endsWith("/") && !b.startsWith("/")) {
			sb.append("/");
		}
		sb.append(b);
		String path = sb.toString();
		while (path.contains("//")) {
			path = path.replace("//", "/");
		}
		return path;
	}

}
