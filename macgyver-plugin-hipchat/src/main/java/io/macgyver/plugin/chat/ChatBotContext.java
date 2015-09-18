package io.macgyver.plugin.chat;

import java.util.List;

import org.joda.time.DateTime;

public abstract class ChatBotContext {

	private ChatBot chatbot;
	
	public ChatBotContext(ChatBot cb) {
		this.chatbot = cb;
	}
	
	
	public abstract String getRawMessage();
	public abstract String getCommand();
	public abstract List<String> getCommandArgs();
	public abstract String getRoomId();
	public abstract String getSenderName();	
	public abstract String getSenderId();
	public abstract void respond(String response);
	public ChatBot getChatBot() {
		return chatbot;
	}
}
