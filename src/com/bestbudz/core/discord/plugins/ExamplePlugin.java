package com.bestbudz.core.discord.plugins;

import com.bestbudz.core.discord.core.DiscordBot;
import com.bestbudz.core.discord.core.DiscordPlugin;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ExamplePlugin extends DiscordPlugin {

	@Override
	public String getName() {
		return "ExamplePlugin";
	}

	@Override
	public void onEnable(DiscordBot bot) {
		bot.getJDA().addEventListener(new ExampleListener());
	}

	@Override
	public void onDisable() {
		// Cleanup if needed
	}

	private static class ExampleListener extends ListenerAdapter {
		@Override
		public void onMessageReceived(MessageReceivedEvent event) {
			if (event.getAuthor().isBot()) return;

			if (event.getMessage().getContentRaw().equals("!ping")) {
				event.getChannel().sendMessage("Pong!").queue();
			}
		}
	}
}