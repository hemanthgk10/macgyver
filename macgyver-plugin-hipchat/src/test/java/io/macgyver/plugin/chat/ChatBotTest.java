package io.macgyver.plugin.chat;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

public class ChatBotTest {

	public class TestChatBot extends ChatBot {

		@Override
		public ChatBotContext createContext(ChatBot bot, JsonNode n) {
			ChatBotContext c = new ChatBotContext(this) {
				
				@Override
				public void respond(String response) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public String getRoomId() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public String getRawMessage() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public String getCommand() {
					return "foo";
				}
				
				@Override
				public List<String> getCommandArgs() {
					// TODO Auto-generated method stub
					return null;
				}
			};
			return c;
		}

		
		
	}
 	@Test
 	public void testIt() {
 		
 		
 		TestChatBot b = new TestChatBot();
 		
 		ChatBotCommand cmd = new ChatBotCommand("foo") {

			@Override
			public void handle(ChatBotContext ctx) {
				// TODO Auto-generated method stub
				
			}
 			
 		};
 		
 		b.register(cmd);
 		
 		Optional<ChatBotCommand> x = b.findCommand("foo");
 		
 		Assertions.assertThat(x.isPresent()).isTrue();
 		
 		Assertions.assertThat(x.get()).isSameAs(cmd);
 		
 	}
	
}
