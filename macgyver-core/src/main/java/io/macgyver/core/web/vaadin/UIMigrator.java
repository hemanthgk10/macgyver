package io.macgyver.core.web.vaadin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.macgyver.core.web.UIContext;
import io.macgyver.core.web.UIContextDecorator;

public class UIMigrator implements UIContextDecorator {

	Logger logger = LoggerFactory.getLogger(UIMigrator.class);
	Map<String,String> map = new ConcurrentHashMap<>();
	
	
	public UIMigrator() {
		map.put("home", "/home");
		map.put("admin/cluster", "/core/admin/cluster-info");
		map.put("admin/beans", "/core/admin/spring-beans");
		map.put("admin/scripts", "/core/admin/scripts");
		map.put("admin/services", "/core/admin/services");
		map.put("admin/propertyEncryption", "/core/admin/encrypt-string");
	}
	
	public void addMigratedViewx(String viewName, String newPath) {
		map.put(viewName, newPath);
	}
	
	public Optional<String> getMigratedPath(String vaadinViewName) {
		String val = map.get(vaadinViewName);
		return Optional.ofNullable(val);
	}

	@Override
	public void call(UIContext ctx) {
		
		
	}

}
