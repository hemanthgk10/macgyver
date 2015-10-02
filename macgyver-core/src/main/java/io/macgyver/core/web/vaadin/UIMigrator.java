package io.macgyver.core.web.vaadin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UIMigrator {

	Map<String,String> map = new ConcurrentHashMap<>();
	
	
	public UIMigrator() {
		map.put("home", "/home");
		map.put("admin/cluster", "/admin/cluster-info");
		map.put("admin/beans", "/admin/spring-beans");
		map.put("admin/scripts", "/admin/scripts");
	}
	
	public void addMigratedView(String viewName, String newPath) {
		map.put(viewName, newPath);
	}
	
	public Optional<String> getMigratedPath(String vaadinViewName) {
		String val = map.get(vaadinViewName);
		return Optional.ofNullable(val);
	}

}
