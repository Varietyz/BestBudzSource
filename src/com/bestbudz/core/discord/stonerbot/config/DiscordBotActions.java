package com.bestbudz.core.discord.stonerbot.config;

import com.bestbudz.core.discord.stonerbot.automations.DiscordBotChat;
import com.bestbudz.core.discord.stonerbot.state.DiscordBotLocation;
import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.core.discord.stonerbot.state.DiscordBotAppearance;
import com.bestbudz.core.discord.messaging.DiscordMessageManager;
import com.bestbudz.rs2.entity.Location;

import java.util.logging.Logger;

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

	public void processMessage(DiscordBotStoner.BotMessage message) {
		try {
			switch (message.getType()) {
				case DISCORD_MESSAGE:

					chat.performDiscordMessage(message.getUsername(), message.getContent());
					chat.showOverheadText(message.getContent());
					break;

				case MOVE:

					performAutonomousMove(message.getLocation());
					break;

				case SYSTEM_BROADCAST:

					if (DiscordMessageManager.isAvailable()) {

						String cleanMessage = message.getContent().replaceAll("@[a-zA-Z]+@", "");
						DiscordMessageManager.announceSystem(cleanMessage);
						chat.showOverheadText(cleanMessage);
						logger.fine("System broadcast sent via centralized messaging: " + cleanMessage);
					}
					break;

				case PERFORM_EMOTE:

					logger.fine("Emote message received but ignored - handled directly now");
					break;
			}
		} catch (Exception e) {
			logger.warning("Error processing autonomous bot action: " + e.getMessage());
			throw e;
		}
	}

	private void performAutonomousMove(Location targetLocation) {
		if (targetLocation != null) {
			location.performMove(targetLocation);
			location.setNeedsPlacement();

			String locationName = getLocationName(targetLocation);

			if (DiscordMessageManager.isAvailable()) {
				DiscordMessageManager.sendGameChat(bot.getUsername(), "Moved to: " + locationName);
			}

			logger.info("Bot autonomously moved to: " + targetLocation);
		}
	}

	public void relayDiscordMessage(String discordUsername, String message) {
		if (bot.isInitialized()) {
			bot.scheduleMessage(new DiscordBotStoner.BotMessage(
				DiscordBotStoner.BotMessage.Type.DISCORD_MESSAGE, message, discordUsername));
		}
	}

	public void makeAutonomousMove(Location location) {
		if (bot.isInitialized()) {
			bot.scheduleMessage(new DiscordBotStoner.BotMessage(
				DiscordBotStoner.BotMessage.Type.MOVE, null, null, location));
		}
	}

	public void relayGameChatToDiscord(String username, String message) {

		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.sendGameChat(username, message);
			logger.fine("Game chat relayed to Discord via centralized messaging: " + username + ": " + message);
		} else {

			chat.relayGameChatToDiscord(username, message);
		}
	}

	private String getLocationName(Location loc) {

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

	public void notifySkillProgress(String skillName, int newLevel) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.announceLevelUp(bot.getUsername(), skillName, newLevel);
			logger.info("Skill progress notification sent: " + bot.getUsername() + " reached grade " + newLevel + " in " + skillName);
		} else {

			chat.sendSkillProgressUpdate(skillName, newLevel);
		}

	}

	public void notifyEquipmentChange(String itemName) {
		if (DiscordMessageManager.isAvailable()) {

			DiscordMessageManager.sendGameChat(bot.getUsername(), "Equipped: " + itemName);
			logger.fine("Equipment change notification sent: " + itemName);
		} else {

			chat.sendEquipmentUpdate(itemName);
		}

		chat.showOverheadText("@gre@Equipped: @whi@" + itemName);
	}

	public void sendAutonomousStatusUpdate(String status) {
		if (DiscordMessageManager.isAvailable()) {

			String cleanStatus = status.replaceAll("@[a-zA-Z]+@", "");
			DiscordMessageManager.sendGameChat(bot.getUsername(), cleanStatus);
			logger.fine("Autonomous status update sent: " + cleanStatus);
		}

	}

	public void notifyAchievement(String achievement) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.announceAchievement(bot.getUsername(), achievement);
			logger.info("Achievement notification sent: " + achievement);
		}

		chat.showOverheadText("Achievement: " + achievement);
	}

	public void notifyBotStateChange(String stateDescription) {
		if (DiscordMessageManager.isAvailable()) {
			DiscordMessageManager.announceSystem("Discord Bot: " + stateDescription);
			logger.info("Bot state change notification sent: " + stateDescription);
		}
	}

	public String getMessagingStatus() {
		if (DiscordMessageManager.isAvailable()) {
			return "Centralized messaging: Available - " + DiscordMessageManager.getStatus();
		} else {
			return "Centralized messaging: Not available, using fallback methods";
		}
	}

	public DiscordBotChat getChat() {
		return chat;
	}
}
