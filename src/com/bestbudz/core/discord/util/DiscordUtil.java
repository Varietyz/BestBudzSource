package com.bestbudz.core.discord.util;

public final class DiscordUtil {

	private DiscordUtil() {}

	public static String escapeMarkdown(String text) {
		return text.replace("*", "\\*")
			.replace("_", "\\_")
			.replace("`", "\\`")
			.replace("~", "\\~");
	}

	public static String formatUsername(String username) {
		return "**" + escapeMarkdown(username) + "**";
	}

	public static boolean isValidChannelId(String channelId) {
		return channelId != null && channelId.matches("\\d{17,19}");
	}
}