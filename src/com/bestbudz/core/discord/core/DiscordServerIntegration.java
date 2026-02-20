package com.bestbudz.core.discord.core;

import com.bestbudz.core.discord.DiscordManager;
import com.bestbudz.core.discord.messaging.DiscordMessageManager;
import java.util.logging.Logger;

public class DiscordServerIntegration {
	private static final Logger logger = Logger.getLogger(DiscordServerIntegration.class.getSimpleName());

	public static void initializeDiscordBot() {
		logger.info("Initializing Discord integration...");

		try {
			DiscordManager discordManager = DiscordManager.getInstance();

			discordManager.initialize().thenRun(() -> {
				logger.info("Discord bot initialization completed successfully");
			}).exceptionally(throwable -> {
				logger.severe("Failed to initialize Discord bot: " + throwable.getMessage());
				throwable.printStackTrace();
				return null;
			});

		} catch (Exception e) {
			logger.severe("Error during Discord initialization: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void shutdownDiscordBot() {
		logger.info("Shutting down Discord integration...");

		try {

			DiscordMessageManager.announceServerShutdown();

			Thread.sleep(2000);

			DiscordManager discordManager = DiscordManager.getInstance();
			discordManager.shutdown();
			logger.info("Discord bot shutdown completed");

		} catch (Exception e) {
			logger.severe("Error during Discord shutdown: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void announceServerStartup() {
		try {

			Thread.sleep(5000);

			DiscordMessageManager.announceServerStartup();
			logger.info("Sent server startup message to Discord");

		} catch (Exception e) {
			logger.warning("Failed to announce server startup to Discord: " + e.getMessage());
		}
	}

	public static String getStatus() {
		try {
			DiscordManager discordManager = DiscordManager.getInstance();
			String botStatus;

			if (!discordManager.isActive()) {
				botStatus = "Discord bot: Offline";
			} else if (discordManager.getBotPlayer() == null) {
				botStatus = "Discord bot: Connected, Bot player: Not created";
			} else if (!discordManager.getBotPlayer().isInitialized()) {
				botStatus = "Discord bot: Connected, Bot player: Not initialized";
			} else {
				botStatus = "Discord bot: Online and active";
			}

			String messagingStatus = DiscordMessageManager.getStatus();

			return botStatus + "\n" + messagingStatus;

		} catch (Exception e) {
			return "Discord bot: Error - " + e.getMessage();
		}
	}
}
