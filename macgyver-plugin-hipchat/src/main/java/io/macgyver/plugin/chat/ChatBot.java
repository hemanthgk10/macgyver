package io.macgyver.plugin.chat;

import io.macgyver.plugin.hipchat.HelpCommand;
import io.macgyver.plugin.hipchat.HipChatBotContext;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public abstract class ChatBot {

	private Map<String,ChatBotCommand> commandMap = Maps.newConcurrentMap();
	
	
	public ChatBot() {
		register(new HelpCommand());
	}
	
	public void register(ChatBotCommand cmd) {
		this.commandMap.put(cmd.getCommand(), cmd);
	}
	
	
	public abstract ChatBotContext createContext(ChatBot bot, JsonNode n);
	
	public Map<String,ChatBotCommand> getCommands() {
		return ImmutableMap.copyOf(commandMap);
	}
	public final void dispatch(JsonNode n) {
		
		
		ChatBotContext ctx = createContext(this,n);
		
		Optional<ChatBotCommand> cmd = findCommand(ctx);
		
		if (cmd.isPresent()) {
			cmd.get().handle(ctx);
		}
		else {
			String command = ctx.getCommand();
			handleUnknownCommand(ctx);
			
		}
	}
	
	public Optional<ChatBotCommand> findCommand(String cmd) {
		if (cmd == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(commandMap.get(cmd));
	}
	public Optional<ChatBotCommand> findCommand(ChatBotContext ctx) {
		if (ctx==null) {
			return Optional.empty();
		}
	
		return findCommand(ctx.getCommand());
		
		
	}
	
	protected void handleUnknownCommand(ChatBotContext ctx) {
		ctx.respond("unknown command: "+ctx.getCommand()+". Type /help for help with commands");
	}
}
