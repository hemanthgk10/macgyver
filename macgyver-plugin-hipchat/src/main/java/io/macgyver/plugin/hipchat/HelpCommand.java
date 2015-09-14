package io.macgyver.plugin.hipchat;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import io.macgyver.plugin.chat.ChatBotCommand;
import io.macgyver.plugin.chat.ChatBotContext;

public class HelpCommand extends ChatBotCommand {

	public HelpCommand() {
		super("help");
	}

	@Override
	public void handle(ChatBotContext ctx) {

		ctx.respond("Summary of MacGyver HipChat commands:<br/");

		StringBuffer sb = new StringBuffer();
		sb.append("<table></tr>");

		List<ChatBotCommand> tmp = Lists
				.newArrayList(ctx.getChatBot().getCommands().values());
		Collections.sort(tmp, (a, b) -> {
			return a.getCommand().compareTo(b.getCommand());
		});
		tmp
				.forEach(
						it -> {
							sb.append("<tr><td>" + it.getUsage()+
									"</td><td>-</td>"
									+ "<td>"+it.getDescription()
									+ "</td></tr>");

						});

		sb.append("</table>");

		HipChatClient c = ((HipChatBot) ctx.getChatBot()).getHipChatClient();
		c.sendRoomNotification(ctx.getRoomId(), sb.toString());

	}

}
