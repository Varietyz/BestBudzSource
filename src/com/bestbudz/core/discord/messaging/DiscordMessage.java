
package com.bestbudz.core.discord.messaging;

public class DiscordMessage {
	public enum Type {
		CHAT,
		SERVER_STATUS,
		SYSTEM_ANNOUNCEMENT,
		LEVEL_UP,
		SKILL_PROGRESS,
		ADVANCEMENT,
		ACHIEVEMENT,
		STAFF_ALERT,
		ADMIN_MESSAGE,
		GAME_MESSAGE
	}

	private final Type type;
	private final String content;
	private final String username;
	private final long timestamp;

	public DiscordMessage(Type type, String content) {
		this(type, content, null);
	}

	public DiscordMessage(Type type, String content, String username) {
		this.type = type;
		this.content = content;
		this.username = username;
		this.timestamp = System.currentTimeMillis();
	}

	public Type getType() { return type; }
	public String getContent() { return content; }
	public String getUsername() { return username; }
	public long getTimestamp() { return timestamp; }
}
