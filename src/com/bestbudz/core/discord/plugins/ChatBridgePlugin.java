package com.bestbudz.core.discord.plugins;

import com.bestbudz.core.discord.core.DiscordBot;
import static com.bestbudz.core.discord.core.DiscordConfig.CHAT_CHANNEL_ID;
import com.bestbudz.core.discord.core.DiscordPlugin;
import com.bestbudz.core.discord.messaging.DiscordMessageService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.regex.Pattern;

public class ChatBridgePlugin extends DiscordPlugin {

	private static final int MAX_GAME_MESSAGE_LENGTH = 80;

	private static final Pattern DISCORD_FORMATTING_PATTERN = Pattern.compile(
		"<[@!#&:]\\d+>|<a?:[\\w~]+:\\d+>|\\*\\*|\\*|__|_|~~|`|```|\\|\\||spoiler\\|\\|"
	);

	private static final Pattern EMOJI_PATTERN = Pattern.compile(
		"[\\x{1F600}-\\x{1F64F}]|" +
			"[\\x{1F300}-\\x{1F5FF}]|" +
			"[\\x{1F680}-\\x{1F6FF}]|" +
			"[\\x{1F1E0}-\\x{1F1FF}]|" +
			"[\\x{2600}-\\x{26FF}]|" +
			"[\\x{2700}-\\x{27BF}]"
	);

	@Override
	public String getName() {
		return "ChatBridgePlugin";
	}

	@Override
	public String getDescription() {
		return "Bridges game chat with Discord channel";
	}

	@Override
	public void onEnable(DiscordBot bot) {
		bot.getJDA().addEventListener(new ChatBridgeListener());

		DiscordMessageService.getInstance().initialize(bot);

		System.out.println("ChatBridgePlugin: Connected to centralized messaging service");
	}

	@Override
	public void onDisable() {

		DiscordMessageService.getInstance().shutdown();
	}

	private String sanitizeDiscordMessage(String content) {
		String sanitized = DISCORD_FORMATTING_PATTERN.matcher(content).replaceAll("");
		sanitized = EMOJI_PATTERN.matcher(sanitized).replaceAll("");
		sanitized = sanitized.trim().replaceAll("\\s+", " ");
		sanitized = sanitized.replaceAll("[<>\\[\\]{}|\\\\]", "");
		return sanitized;
	}

	private void sendDiscordMessageToGame(String username, String content) {
		String sanitized = sanitizeDiscordMessage(content);

		if (sanitized.isEmpty()) {
			return;
		}

		com.bestbudz.rs2.entity.stoner.net.in.impl.ChatBridgeManager
			.sendDiscordMessageToGame(username, sanitized);
	}

	private class ChatBridgeListener extends ListenerAdapter {
		@Override
		public void onMessageReceived(MessageReceivedEvent event) {
			if (event.getAuthor().isBot()) {
				return;
			}

			if (!event.getChannel().getId().equals(CHAT_CHANNEL_ID)) {
				return;
			}

			Message message = event.getMessage();

			if (!message.getAttachments().isEmpty()) {
				event.getChannel().sendMessage("❌ Messages with attachments cannot be sent to game chat.").queue();
				return;
			}

			String content = message.getContentRaw();
			String username = event.getAuthor().getName();

			if (content.trim().isEmpty()) {
				return;
			}

			if (content.length() > MAX_GAME_MESSAGE_LENGTH * 5) {
				event.getChannel().sendMessage("❌ Message too long! Please keep messages shorter.").queue();
				return;
			}

			sendDiscordMessageToGame(username, content);
		}
	}
}
