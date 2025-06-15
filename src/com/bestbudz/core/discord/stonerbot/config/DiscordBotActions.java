package com.bestbudz.core.discord.stonerbot.config;

import com.bestbudz.core.discord.stonerbot.automations.DiscordBotChat;
import com.bestbudz.core.discord.stonerbot.state.DiscordBotLocation;
import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.core.discord.stonerbot.state.DiscordBotAppearance;
import com.bestbudz.core.discord.messaging.DiscordMessageManager;
import com.bestbudz.rs2.entity.Location;

import java.util.logging.Logger;

/**
 * FIXED: Handles autonomous Discord bot actions using centralized messaging
 * All actions are self-initiated by the bot's AI logic
 * SIMPLIFIED EMOTE HANDLING
 */
public class DiscordBotActions {

	private static final Logger logger = Logger.getLogger(DiscordBotActions.class.getSimpleName());

	private final DiscordBotStoner bot;
	private final DiscordBotChat chat;
	private final DiscordBotLocation location;
	private final DiscordBotAppearance appearance;

	public DiscordBotActions(DiscordBotStoner bot, DiscordBotChat chat,
							 DiscordBotLocation location, DiscordBotAppearance appearance) {
		this.bot = bot;
		this.chat = chat;
		this.location = location;
		this.appearance = appearance;
	}

	/**
	 * Process internal bot messages (not user commands) - SIMPLIFIED
	 */
	public void processMessage(DiscordBotStoner.BotMessage message) {
		try {
			switch (message.getType()) {
				case DISCORD_MESSAGE:
					// Simply relay Discord messages to game players
					chat.performDiscordMessage(message.getUsername(), message.getContent());
					chat.showOverheadText(message.getContent());
					break;

				case MOVE:
					// Autonomous movement decisions
					performAutonomousMove(message.getLocation());
					break;

				case SYSTEM_BROADCAST:
					// UPDATED: System announcements now use centralized messaging
					// No longer broadcast to game players, just send to Discord
					if (DiscordMessageManager.isAvailable()) {
						// Clean the message of color codes before sending
						String cleanMessage = message.getContent().replaceAll("@[a-zA-Z]+@", "");
						DiscordMessageManager.announceSystem(cleanMessage);
						chat.showOverheadText(cleanMessage);
						logger.fine("System broadcast sent via centralized messaging: " + cleanMessage);
					}
					break;

				case PERFORM_EMOTE:
					// REMOVED: Emote handling is now done directly in decision making
					// to avoid conflicts and timing issues
					logger.fine("Emote message received but ignored - handled directly now");
					break;
			}
		} catch (Exception e) {
			logger.warning("Error processing autonomous bot action: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Handle autonomous movement decisions
	 */
	private void performAutonomousMove(Location targetLocation) {
		if (targetLocation != null) {
			location.performMove(targetLocation);
			location.setNeedsPlacement();

			// UPDATED: Send autonomous status update using centralized messaging
			String locationName = getLocationName(targetLocation);

			// Send to Discord via centralized messaging instead of through chat relay
			if (DiscordMessageManager.isAvailable()) {
				DiscordMessageManager.sendGameChat(bot.getUsername(), "Moved to: " + locationName);
			}

			logger.info("Bot autonomously moved to: " + targetLocation);
		}
	}

	/**
	 * Send Discord message relay (not a command response)
	 */
	public void relayDiscordMessage(String discordUsername, String message) {
		if (bot.isInitialized()) {
			bot.scheduleMessage(new DiscordBotStoner.BotMessage(
				DiscordBotStoner.BotMessage.Type.DISCORD_MESSAGE, message, discordUsername));
		}
	}

	/**
	 * Autonomous movement decision (not user commanded)
	 */
	public void makeAutonomousMove(Location location) {
		if (bot.isInitialized()) {
			bot.scheduleMessage(new DiscordBotStoner.BotMessage(
				DiscordBotStoner.BotMessage.Type.MOVE, null, null, location));
		}
	}

	/**
	 * UPDATED: Relay game chat to Discord using centralized messaging
	 */
	public void relayGameChatToDiscord(String username, String message) {
		// Use centralized messaging instead of going through chat class
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.sendGameChat(username, message);
			logger.fine("Game chat relayed to Discord via centralized messaging: " + username + ": " + message);
		} else {
			// Fallback to old method if centralized messaging not available
			chat.relayGameChatToDiscord(username, message);
		}
	}

	/**
	 * Get friendly name for location
	 */
	private String getLocationName(Location loc) {
		// Convert coordinates to friendly location names
		// This could be expanded with a location database
		if (loc.getX() >= 3440 && loc.getX() <= 3450 && loc.getY() >= 2910 && loc.getY() <= 2920) {
			return "Home Area";
		} else if (loc.getX() >= 3200 && loc.getX() <= 3300 && loc.getY() >= 3200 && loc.getY() <= 3300) {
			return "Varrock";
		} else if (loc.getX() >= 3000 && loc.getX() <= 3100 && loc.getY() >= 3100 && loc.getY() <= 3200) {
			return "Lumbridge";
		} else {
			return String.format("(%d, %d, %d)", loc.getX(), loc.getY(), loc.getZ());
		}
	}

	/**
	 * UPDATED: Notify about skill progress using centralized messaging
	 */
	public void notifySkillProgress(String skillName, int newLevel) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.announceLevelUp(bot.getUsername(), skillName, newLevel);
			logger.info("Skill progress notification sent: " + bot.getUsername() + " reached level " + newLevel + " in " + skillName);
		} else {
			// Fallback to old method
			chat.sendSkillProgressUpdate(skillName, newLevel);
		}

	}

	/**
	 * UPDATED: Notify about equipment changes using centralized messaging
	 */
	public void notifyEquipmentChange(String itemName) {
		if (DiscordMessageManager.isAvailable()) {
			// Send as game chat message for equipment changes
			DiscordMessageManager.sendGameChat(bot.getUsername(), "Equipped: " + itemName);
			logger.fine("Equipment change notification sent: " + itemName);
		} else {
			// Fallback to old method
			chat.sendEquipmentUpdate(itemName);
		}

		// FIXED: Show overhead text for equipment changes
		chat.showOverheadText("@gre@Equipped: @whi@" + itemName);
	}

	/**
	 * FIXED: Send autonomous status updates directly using centralized messaging
	 */
	public void sendAutonomousStatusUpdate(String status) {
		if (DiscordMessageManager.isAvailable()) {
			// Clean the status message of color codes
			String cleanStatus = status.replaceAll("@[a-zA-Z]+@", "");
			DiscordMessageManager.sendGameChat(bot.getUsername(), cleanStatus);
			logger.fine("Autonomous status update sent: " + cleanStatus);
		}

		// FIXED: Use the chat instance to show overhead text
		//chat.showOverheadText(status);
	}

	/**
	 * NEW: Send achievement notifications using centralized messaging
	 */
	public void notifyAchievement(String achievement) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.announceAchievement(bot.getUsername(), achievement);
			logger.info("Achievement notification sent: " + achievement);
		}

		// FIXED: Show overhead text for achievements
		chat.showOverheadText("Achievement: " + achievement);
	}

	/**
	 * NEW: Send bot state changes to appropriate Discord channels
	 */
	public void notifyBotStateChange(String stateDescription) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.announceSystem("Discord Bot: " + stateDescription);
			logger.info("Bot state change notification sent: " + stateDescription);
		}
	}

	/**
	 * NEW: Get the current messaging system status
	 */
	public String getMessagingStatus() {
		if (DiscordMessageManager.isAvailable()) {
			return "Centralized messaging: Available - " + DiscordMessageManager.getStatus();
		} else {
			return "Centralized messaging: Not available, using fallback methods";
		}
	}

	/**
	 * NEW: Access to chat instance for external overhead text calls
	 */
	public DiscordBotChat getChat() {
		return chat;
	}
}