package io.macgyver.core.web;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MacGyverMenuManager {

	ObjectMapper mapper =  new ObjectMapper();
	
	public static class MenuItem {

		String id;
		String url;
		String text;
		boolean collapsible;
	
		List<MenuItem> itemList = new CopyOnWriteArrayList<>();
		

		public MenuItem(String id, String text, String url, boolean c) {
			this.id = id;
			this.url = url;
			this.text = text;
			this.collapsible = c;
		}
		
		public String getId() {
			return id;
		}
		
		public String getUrl() {
			return url;
		}
		
		public String getText() {
			return text;
		}
		
		List<MenuItem> getItems() {
			return itemList;
		}
		
		public void addItem(MenuItem item) {
			itemList.add(item);
		}
		
		public MenuItem addItem(String id, String text, String url) {
			MenuItem child = new MenuItem(id,text,url,false);
			itemList.add(child);
			return child;
		}
		
		public boolean isCollapsible() {
			return collapsible;
		}
		
	}
	public MenuItem forCurrentUser() {
		
	
		
		MenuItem top = new MenuItem("top", null, null, false);
		
		MenuItem main = top.addItem("main","Main", "");
		
		main.addItem("home","Home","/home");
		main.addItem("legacy-ui","Legacy UI","/ui#!home");
		MenuItem admin = top.addItem("admin","Admin","");
		admin.collapsible=true;
		
		admin.addItem("neo4j", "Neo4j Browser","/browser");
		
		admin.addItem("clusterInfo", "Cluster Info","/admin/cluster-info");
		admin.addItem("encryptString", "Encrypt String","/admin/encrypt-string");
		admin.addItem("scripts", "Scripts","/admin/scripts");
		admin.addItem("springBeans", "Spring Beans","/admin/spring-beans");
		admin.addItem("services", "Services","/admin/services");
		return top;
		
	}

}
