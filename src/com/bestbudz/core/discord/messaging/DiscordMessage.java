// DiscordMessage.java
package com.bestbudz.core.discord.messaging;

/**
 * Represents a message to be sent to Discord with type and formatting information
 */
public class DiscordMessage {
	public enum Type {
		CHAT,               // Player chat messages
		SERVER_STATUS,      // Server online/offline status
		SYSTEM_ANNOUNCEMENT,// General system announcements
		LEVEL_UP,          // Player level up notifications
		SKILL_PROGRESS,    // Skill progress updates
		ADVANCEMENT,       // Quest/achievement completions
		ACHIEVEMENT,       // Special achievements
		STAFF_ALERT,       // Staff notifications
		ADMIN_MESSAGE      // Administrative messages
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