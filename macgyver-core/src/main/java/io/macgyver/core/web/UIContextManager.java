package io.macgyver.core.web;

import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class UIContextManager {

	Logger logger = LoggerFactory.getLogger(UIContextManager.class);
	@Autowired
	ApplicationContext applicationContext;

	public UIContext forCurrentUser() {

		
		
		MacGyverWebContext ctx = MacGyverWebContext.get();
		
		UIContext nc = (UIContext) ctx.getServletRequest().getSession(true).getAttribute(UIContext.class.getName());
		
		if (nc==null) {
			
			Map<String, UIContextDecorator> beans = applicationContext
					.getBeansOfType(UIContextDecorator.class);

			final UIContext ncf  = new UIContext();
			beans.entrySet().stream().forEach(kv -> {
				logger.info(kv.getKey()+ " ==> "+kv.getValue());
				
				kv.getValue().call(ncf);
			});
			ctx.getServletRequest().getSession(true).setAttribute(UIContext.class.getName(), ncf);
			nc = ncf;
		}
		
		return nc;
	}

}
