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
