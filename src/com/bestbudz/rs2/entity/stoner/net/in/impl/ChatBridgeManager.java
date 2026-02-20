package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.discord.DiscordManager;
import com.bestbudz.core.discord.messaging.DiscordMessageManager;
import static com.bestbudz.core.discord.stonerbot.config.DiscordBotDefaults.DEFAULT_USERNAME;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.stoner.Stoner;

import java.util.logging.Logger;

public class ChatBridgeManager {

	private static final Logger logger = Logger.getLogger(ChatBridgeManager.class.getSimpleName());

	public static void broadcastToDiscord(Stoner stoner, byte[] chatText, int chatLength) {

		if (!DiscordMessageManager.isAvailable() || stoner.isMuted()) {
			return;
		}

		if (DEFAULT_USERNAME.equals(stoner.getUsername())) {
			return;
		}

		String message = Utility.textUnpack(chatText, chatLength, true);

		logger.fine("ChatBridge: Processing message from " + stoner.getUsername() + ": '" + message + "'");

		if (message == null || message.trim().isEmpty()) {
			logger.fine("ChatBridge: Message is empty after decoding");
			return;
		}

		if (isCommandMessage(message)) {
			logger.fine("ChatBridge: Skipping command message: " + message);
			return;
		}

		if (isFilteredMessage(message)) {
			logger.fine("ChatBridge: Message filtered: " + message);
			return;
		}

		try {
			DiscordMessageManager.sendGameChat(stoner.getUsername(), message);
			logger.fine("ChatBridge: Successfully relayed message to Discord via centralized system");
		} catch (Exception e) {
			logger.warning("Failed to send message to Discord: " + e.getMessage());
		}
	}

	public static void broadcastSystemMessage(String message) {
		if (!DiscordMessageManager.isAvailable()) {
			return;
		}

		try {

			String cleanMessage = cleanSystemMessage(message);
			DiscordMessageManager.announceSystem(cleanMessage);
			logger.info("System message sent to Discord: " + cleanMessage);
		} catch (Exception e) {
			logger.warning("Failed to broadcast system message to Discord: " + e.getMessage());
		}
	}

	public static void sendDiscordMessageToGame(String discordUsername, String message) {
		try {
			DiscordManager discordManager = DiscordManager.getInstance();
			if (discordManager.isActive() && discordManager.getBotPlayer() != null) {

				discordManager.getBotPlayer().relayDiscordMessage(discordUsername, message);
				logger.fine("Discord message relayed to game from " + discordUsername);
			}
		} catch (Exception e) {
			logger.warning("Failed to send Discord message to game: " + e.getMessage());
		}
	}

	public static void notifyPlayerJoin(String username) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.announceSystem(username + " joined the game");
		}
	}

	public static void notifyPlayerLeave(String username) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.announceSystem(username + " left the game");
		}
	}

	public static void notifyLevelUp(String username, String skill, int level) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.announceLevelUp(username, skill, level);
		}
	}

	public static void notifyPlayerDeath(String username, String killer) {
		if (!DiscordMessageManager.isAvailable()) {
			return;
		}

		String message;
		if (killer != null && !killer.isEmpty()) {
			message = username + " was killed by " + killer;
		} else {
			message = username + " died";
		}
		DiscordMessageManager.announceSystem(message);
	}

	public static void notifyAchievement(String username, String achievement) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.announceAchievement(username, achievement);
		}
	}

	public static void alertStaff(String message) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.alertStaff(message);
		}
	}

	private static String cleanSystemMessage(String message) {
		if (message == null) {
			return "";
		}

		String cleaned = message.replaceAll("@[a-zA-Z]+@", "");

		cleaned = cleaned.replaceAll("\\[\\w+\\]", "");

		cleaned = cleaned.trim().replaceAll("\\s+", " ");

		return cleaned;
	}

	private static boolean isCommandMessage(String message) {
		if (message == null || message.trim().isEmpty()) {
			return false;
		}

		String trimmed = message.trim().toLowerCase();

		return trimmed.startsWith("::") ||
			trimmed.startsWith(";;") ||
			trimmed.startsWith("!") ||
			trimmed.startsWith("/") ||
			trimmed.startsWith("@") ||
			trimmed.startsWith("#") ||
			trimmed.startsWith("$");
	}

	private static boolean isFilteredMessage(String message) {
		if (message == null || message.trim().isEmpty()) {
			return true;
		}

		String cleaned = message.trim().toLowerCase();

		if (cleaned.length() < 2) {
			return true;
		}

		if (hasExcessiveRepeats(cleaned)) {
			return true;
		}

		if (cleaned.length() > 10 && cleaned.equals(cleaned.toUpperCase()) &&
			cleaned.matches(".*[A-Z].*")) {
			return true;
		}

		return false;
	}

	private static boolean hasExcessiveRepeats(String message) {
		if (message.length() < 4) {
			return false;
		}

		int maxRepeats = 0;
		int currentRepeats = 1;
		char lastChar = message.charAt(0);

		for (int i = 1; i < message.length(); i++) {
			char currentChar = message.charAt(i);
			if (currentChar == lastChar) {
				currentRepeats++;
				maxRepeats = Math.max(maxRepeats, currentRepeats);
			} else {
				currentRepeats = 1;
				lastChar = currentChar;
			}
		}

		return maxRepeats > 3;
	}

	public static String getBridgeStatus() {
		StringBuilder status = new StringBuilder();
		status.append("Chat Bridge Status:\n");

		status.append("Centralized Messaging: ").append(DiscordMessageManager.isAvailable()).append("\n");
		status.append("Messaging Details: ").append(DiscordMessageManager.getStatus()).append("\n");

		DiscordManager discordManager = DiscordManager.getInstance();
		status.append("Discord Manager Active: ").append(discordManager.isActive()).append("\n");

		if (discordManager.getBotPlayer() != null) {
			status.append("Bot Player Initialized: ").append(discordManager.getBotPlayer().isInitialized()).append("\n");
			status.append("Bot Activity: ").append(discordManager.getBotPlayer().getCurrentActivity()).append("\n");
		} else {
			status.append("Bot Player: null\n");
		}

		return status.toString();
	}

	public static void testBridge() {
		try {
			DiscordMessageManager.announceSystem("Chat bridge connection test - " + System.currentTimeMillis());
			logger.info("Chat bridge test message sent via centralized messaging");
		} catch (Exception e) {
			logger.warning("Chat bridge test failed: " + e.getMessage());
		}
	}

	public static void testAllMessageTypes() {
		if (!DiscordMessageManager.isAvailable()) {
			logger.warning("Cannot test - Discord messaging not available");
			return;
		}

		try {

			DiscordMessageManager.sendGameChat("TestUser", "Test chat message");
			DiscordMessageManager.announceSystem("Test system announcement");
			DiscordMessageManager.announceLevelUp("TestUser", "Weedsmoking", 99);
			DiscordMessageManager.announceAchievement("TestUser", "Test Achievement");
			DiscordMessageManager.alertStaff("Test staff alert");

			logger.info("All message types tested successfully");
		} catch (Exception e) {
			logger.warning("Error testing message types: " + e.getMessage());
		}
	}
}
