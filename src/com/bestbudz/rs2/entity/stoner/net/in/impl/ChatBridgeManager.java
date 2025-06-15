package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.discord.DiscordManager;
import com.bestbudz.core.discord.messaging.DiscordMessageManager;
import static com.bestbudz.core.discord.stonerbot.config.DiscordBotDefaults.DEFAULT_USERNAME;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.stoner.Stoner;

import java.util.logging.Logger;

/**
 * UPDATED: Handles chat bridging between game and Discord using centralized messaging system
 * No command processing - pure message relay only
 */
public class ChatBridgeManager {

	private static final Logger logger = Logger.getLogger(ChatBridgeManager.class.getSimpleName());

	/**
	 * Call this method from your PublicChatPacket after successful chat processing
	 * Relays game chat to Discord using centralized messaging (no command processing)
	 */
	public static void broadcastToDiscord(Stoner stoner, byte[] chatText, int chatLength) {
		// Only broadcast if Discord messaging is available and player isn't muted
		if (!DiscordMessageManager.isAvailable() || stoner.isMuted()) {
			return;
		}

		// Don't relay messages from the Discord bot player itself to avoid loops
		if (DEFAULT_USERNAME.equals(stoner.getUsername())) {
			return;
		}

		// Use your existing chat text decoder from Utility class
		String message = Utility.textUnpack(chatText, chatLength, true);

		logger.fine("ChatBridge: Processing message from " + stoner.getUsername() + ": '" + message + "'");

		// Skip empty messages
		if (message == null || message.trim().isEmpty()) {
			logger.fine("ChatBridge: Message is empty after decoding");
			return;
		}

		// Filter out commands and system messages (don't relay these to Discord)
		if (isCommandMessage(message)) {
			logger.fine("ChatBridge: Skipping command message: " + message);
			return;
		}

		// Filter out potential spam or inappropriate content
		if (isFilteredMessage(message)) {
			logger.fine("ChatBridge: Message filtered: " + message);
			return;
		}

		// Use the centralized messaging system to send game chat to Discord
		try {
			DiscordMessageManager.sendGameChat(stoner.getUsername(), message);
			logger.fine("ChatBridge: Successfully relayed message to Discord via centralized system");
		} catch (Exception e) {
			logger.warning("Failed to send message to Discord: " + e.getMessage());
		}
	}

	/**
	 * UPDATED: Broadcast a system message to Discord using centralized messaging
	 */
	public static void broadcastSystemMessage(String message) {
		if (!DiscordMessageManager.isAvailable()) {
			return;
		}

		try {
			// Clean the message of color codes before sending to Discord
			String cleanMessage = cleanSystemMessage(message);
			DiscordMessageManager.announceSystem(cleanMessage);
			logger.info("System message sent to Discord: " + cleanMessage);
		} catch (Exception e) {
			logger.warning("Failed to broadcast system message to Discord: " + e.getMessage());
		}
	}

	/**
	 * Send a Discord message to the game through the autonomous bot player
	 * This simply relays the message, no command processing
	 */
	public static void sendDiscordMessageToGame(String discordUsername, String message) {
		try {
			DiscordManager discordManager = DiscordManager.getInstance();
			if (discordManager.isActive() && discordManager.getBotPlayer() != null) {
				// Use the autonomous method to relay Discord messages to game
				discordManager.getBotPlayer().relayDiscordMessage(discordUsername, message);
				logger.fine("Discord message relayed to game from " + discordUsername);
			}
		} catch (Exception e) {
			logger.warning("Failed to send Discord message to game: " + e.getMessage());
		}
	}

	/**
	 * UPDATED: Send player join notification using centralized messaging
	 */
	public static void notifyPlayerJoin(String username) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.announceSystem(username + " joined the game");
		}
	}

	/**
	 * UPDATED: Send player leave notification using centralized messaging
	 */
	public static void notifyPlayerLeave(String username) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.announceSystem(username + " left the game");
		}
	}

	/**
	 * UPDATED: Send player level up notification using centralized messaging
	 */
	public static void notifyLevelUp(String username, String skill, int level) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.announceLevelUp(username, skill, level);
		}
	}

	/**
	 * UPDATED: Send player death notification using centralized messaging
	 */
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

	/**
	 * NEW: Send achievement notification using centralized messaging
	 */
	public static void notifyAchievement(String username, String achievement) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.announceAchievement(username, achievement);
		}
	}

	/**
	 * NEW: Send staff alert using centralized messaging
	 */
	public static void alertStaff(String message) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.alertStaff(message);
		}
	}

	/**
	 * NEW: Clean system messages of color codes and formatting for Discord
	 */
	private static String cleanSystemMessage(String message) {
		if (message == null) {
			return "";
		}

		// Remove RuneScape color codes (@red@, @gre@, etc.)
		String cleaned = message.replaceAll("@[a-zA-Z]+@", "");

		// Remove other formatting
		cleaned = cleaned.replaceAll("\\[\\w+\\]", ""); // Remove [brackets]

		// Clean up extra whitespace
		cleaned = cleaned.trim().replaceAll("\\s+", " ");

		return cleaned;
	}

	/**
	 * Check if a message is a command that shouldn't be relayed to Discord
	 */
	private static boolean isCommandMessage(String message) {
		if (message == null || message.trim().isEmpty()) {
			return false;
		}

		String trimmed = message.trim().toLowerCase();

		// Filter out various command prefixes
		return trimmed.startsWith("::") ||    // Admin commands
			trimmed.startsWith(";;") ||    // Player commands
			trimmed.startsWith("!") ||     // Bot commands (if any exist)
			trimmed.startsWith("/") ||     // Slash commands
			trimmed.startsWith("@") ||     // @ commands
			trimmed.startsWith("#") ||     // Channel commands
			trimmed.startsWith("$");       // Special commands
	}

	/**
	 * Check if a message should be filtered (spam, inappropriate content, etc.)
	 */
	private static boolean isFilteredMessage(String message) {
		if (message == null || message.trim().isEmpty()) {
			return true;
		}

		String cleaned = message.trim().toLowerCase();

		// Filter very short messages that are likely spam
		if (cleaned.length() < 2) {
			return true;
		}

		// Filter messages with too many repeated characters (likely spam)
		if (hasExcessiveRepeats(cleaned)) {
			return true;
		}

		// Filter messages that are all caps and longer than 10 characters (shouting)
		if (cleaned.length() > 10 && cleaned.equals(cleaned.toUpperCase()) &&
			cleaned.matches(".*[A-Z].*")) {
			return true;
		}

		return false;
	}

	/**
	 * Check for excessive character repetition (spam detection)
	 */
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

		// Allow up to 3 repeated characters, more than that is likely spam
		return maxRepeats > 3;
	}

	/**
	 * UPDATED: Get chat bridge status including centralized messaging status
	 */
	public static String getBridgeStatus() {
		StringBuilder status = new StringBuilder();
		status.append("Chat Bridge Status:\n");

		// Check centralized messaging status
		status.append("Centralized Messaging: ").append(DiscordMessageManager.isAvailable()).append("\n");
		status.append("Messaging Details: ").append(DiscordMessageManager.getStatus()).append("\n");

		// Check legacy Discord manager status
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

	/**
	 * UPDATED: Test the chat bridge connection using centralized messaging
	 */
	public static void testBridge() {
		try {
			DiscordMessageManager.announceSystem("Chat bridge connection test - " + System.currentTimeMillis());
			logger.info("Chat bridge test message sent via centralized messaging");
		} catch (Exception e) {
			logger.warning("Chat bridge test failed: " + e.getMessage());
		}
	}

	/**
	 * NEW: Test all message types
	 */
	public static void testAllMessageTypes() {
		if (!DiscordMessageManager.isAvailable()) {
			logger.warning("Cannot test - Discord messaging not available");
			return;
		}

		try {
			// Test each message type
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