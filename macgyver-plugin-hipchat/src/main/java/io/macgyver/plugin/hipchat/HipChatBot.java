package io.macgyver.plugin.hipchat;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.macgyver.core.Kernel;
import io.macgyver.core.service.ServiceDefinition;
import io.macgyver.core.service.ServiceRegistry;
import io.macgyver.plugin.chat.ChatBot;
import io.macgyver.plugin.chat.ChatBotCommand;
import io.macgyver.plugin.chat.ChatBotContext;

public class HipChatBot extends ChatBot {

	

	@Override
	public ChatBotContext createContext(ChatBot bot, JsonNode n) {
		HipChatBotContext ctx = new HipChatBotContext(this, n);
		
		return ctx;
	}

	public HipChatClient getHipChatClient() {
		ServiceRegistry registry = Kernel.getApplicationContext().getBean(ServiceRegistry.class);
		
		for (Map.Entry<String,ServiceDefinition> entry: Kernel.getApplicationContext().getBean(ServiceRegistry.class).getServiceDefinitions().entrySet()) {
			
			ServiceDefinition def = entry.getValue();
			if (def.getServiceFactory() instanceof HipChatServiceFactory) {
				return (HipChatClient) registry.get(def.getPrimaryName());
			}
		}
		
		throw new IllegalStateException("could not resolve a hipchat service");
	}
	
}
