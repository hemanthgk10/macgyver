package io.macgyver.plugin.chat;

import java.util.Optional;

import com.google.common.base.Strings;

public abstract class ChatBotCommand {

	String command=null;
	String description=null;
	String usage=null;
	
	public ChatBotCommand(String cmd) {
		this.command = cmd;
		this.usage="/"+command;
		this.description = "no description for /"+command;
	}
	public final String getCommand() {
		return command;
	}
	
	public String getDescription() {
		return Strings.nullToEmpty(description);
	}
	
	public void setDescription(String description) { 
		this.description = description;
	}
	
	public String getUsage() {
		return usage;
		
	}
	public abstract void handle(ChatBotContext ctx);
}
