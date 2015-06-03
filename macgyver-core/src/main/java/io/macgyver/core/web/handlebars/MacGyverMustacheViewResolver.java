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

import java.io.Reader;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mustache.web.MustacheView;
import org.springframework.boot.autoconfigure.mustache.web.MustacheViewResolver;
import org.springframework.web.servlet.View;

import com.samskivert.mustache.Mustache.Compiler;

public class MacGyverMustacheViewResolver extends MustacheViewResolver {

	@Autowired
	MacGyverMustacheTemplateLoader loader;
	
	@Autowired
	Compiler compiler;
	
	@Override
	protected View loadView(String viewName, Locale locale) throws Exception {
		Reader r = loader.getTemplate(viewName);
		
		if (r == null) {
			return null;
		}
		MustacheView view = new MustacheView(compiler.compile(r));
		view.setApplicationContext(getApplicationContext());
		view.setServletContext(getServletContext());
		return view;
	}
	
	

}
