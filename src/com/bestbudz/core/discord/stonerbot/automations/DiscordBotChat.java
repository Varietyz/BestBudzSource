package com.bestbudz.core.discord.stonerbot.automations;

import com.bestbudz.core.discord.core.DiscordBot;
import com.bestbudz.core.discord.messaging.DiscordMessageManager;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

import java.util.logging.Logger;

public class DiscordBotChat {

	private static final Logger logger = Logger.getLogger(DiscordBotChat.class.getSimpleName());

	private final Stoner stoner;

	public DiscordBotChat(Stoner stoner, DiscordBot discordBot) {
		this.stoner = stoner;

	}

	public void performDiscordMessage(String discordUsername, String message) {
		if (discordUsername == null || message == null) {
			logger.warning("Received null username or message from Discord");
			return;
		}

		String formattedMessage = "@blu@[Discord] @whi@" + discordUsername + ": " + message;
		broadcastToPlayers(formattedMessage);

		if (message.toLowerCase().contains("bestbud") || message.toLowerCase().contains("bot")) {
			showOverheadText("@blu@" + discordUsername + ": @whi@" + message);
		}

		logger.info("Discord message relayed from " + discordUsername + ": " + message);
	}

	public void showOverheadText(String text) {
		if (text == null || text.trim().isEmpty()) {
			return;
		}

		try {

			String displayText = text.length() > 60 ? text.substring(0, 57) + "..." : text;

			stoner.getUpdateFlags().sendForceMessage(displayText);

			logger.fine("Overhead text displayed: " + displayText);

		} catch (Exception e) {
			logger.warning("Error displaying overhead text: " + e.getMessage());
		}
	}

	public void broadcastToPlayers(String message) {
		try {
			if (message == null || message.trim().isEmpty()) {
				return;
			}

			int playerCount = 0;
			for (Stoner stoner : World.getStoners()) {
				if (stoner != null && stoner.isActive() && stoner != this.stoner) {
					try {
						stoner.send(new SendMessage(message));
						playerCount++;
					} catch (Exception e) {
						logger.warning("Error sending message to player " + stoner.getUsername() + ": " + e.getMessage());
					}
				}
			}

			logger.fine("Message broadcast to " + playerCount + " players");

		} catch (Exception e) {
			logger.warning("Error broadcasting message: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void relayGameChatToDiscord(String username, String message) {
		if (username != null && message != null) {
			DiscordMessageManager.sendGameChat(username, message);
			logger.fine("Game chat relayed to Discord: " + username + ": " + message);
		}
	}

	public void sendAutonomousStatusUpdate(String status) {
		if (status != null) {

			String cleanStatus = status.replaceAll("@[a-zA-Z]+@", "");
			DiscordMessageManager.sendGameChat(stoner.getUsername(), cleanStatus);
		}
	}

	public void sendSkillProgressUpdate(String skillName, int level) {
		DiscordMessageManager.announceLevelUp(stoner.getUsername(), skillName, level);
	}

	public void sendLocationUpdate(String locationName) {
		String message = "Moved to: " + locationName;
		DiscordMessageManager.sendGameChat(stoner.getUsername(), message);
	}

	public void sendEquipmentUpdate(String itemName) {
		String message = "Equipped: " + itemName;
		DiscordMessageManager.sendGameChat(stoner.getUsername(), message);
	}
}
