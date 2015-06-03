package io.macgyver.plugin.hipchat;

import io.macgyver.okrest.OkRestTarget;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public interface HipChatClient {

	public enum Color {
		YELLOW("yellow"),
		GREEN("green"),
		RED("red"),
		PURPLE("puple"),
		GRAY("gray");
		
		String val;
		private  Color(final String text) {
	        this.val = text;
	    }

	  
	    @Override
	    public String toString() {
	        return val;
	    }
	}
	
	public enum Format {
		HTML("html"),
		TEXT("text");
		
		
		String val;
		private  Format(final String text) {
	        this.val = text;
	    }

	  
	    @Override
	    public String toString() {
	        return val;
	    }
	}
	public OkRestTarget getBaseTarget();

	public JsonNode get(String path, Map<String, String> params);

	public JsonNode get(String path, String... args);

	public void post(String path, JsonNode body);

	public void put(String path, JsonNode body);

	public void delete(String path);
	
	public void sendRoomNotification(String roomId, String message);
	
	public void sendRoomNotification(String roomId, String message, String format, String color, boolean notify);
	
	public void sendRoomNotification(String roomId, String message, Format format, Color color, boolean notify);
}
