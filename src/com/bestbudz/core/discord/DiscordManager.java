package com.bestbudz.core.discord;

import com.bestbudz.core.discord.core.DiscordBot;
import com.bestbudz.core.discord.core.DiscordConfig;
import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.core.discord.stonerbot.handling.DiscordBotItemHandler;
import com.bestbudz.rs2.entity.World;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class DiscordManager {
	private static final Logger logger = Logger.getLogger(DiscordManager.class.getSimpleName());
	private static DiscordManager instance;

	private DiscordBot bot;
	private DiscordConfig config;
	private DiscordBotStoner botPlayer;
	private DiscordBotItemHandler itemHandler;
	private boolean active = false;

	private final AtomicBoolean botPlayerActive = new AtomicBoolean(false);
	private final AtomicBoolean shouldBotPlayerBeActive = new AtomicBoolean(false);
	private long lastPlayerCountCheck = 0;
	private static final long PLAYER_COUNT_CHECK_INTERVAL = 10000;

	private ExecutorService discordExecutor;

	private DiscordManager() {
		this.config = new DiscordConfig();

		this.discordExecutor = Executors.newFixedThreadPool(2, r -> {
			Thread t = new Thread(r, "Discord-Operations");
			t.setDaemon(true);
			t.setPriority(Thread.NORM_PRIORITY - 1);
			return t;
		});
	}

	public static DiscordManager getInstance() {
		if (instance == null) {
			instance = new DiscordManager();
		}
		return instance;
	}

	public CompletableFuture<Void> initialize() {
		if (!config.isEnabled()) {
			logger.info("Discord integration disabled");
			return CompletableFuture.completedFuture(null);
		}

		return CompletableFuture.runAsync(() -> {
			try {
				bot = new DiscordBot(config);
				bot.connect().join();

				active = true;
				logger.info("Discord bot initialized (game bot will spawn when players are online)");

				startPlayerMonitoring();

			} catch (Exception e) {
				logger.severe("Failed to initialize Discord bot: " + e.getMessage());
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}, discordExecutor);
	}

	private void startPlayerMonitoring() {
		CompletableFuture.runAsync(() -> {
			while (active && !Thread.currentThread().isInterrupted()) {
				try {
					long currentTime = System.currentTimeMillis();

					if (currentTime - lastPlayerCountCheck >= PLAYER_COUNT_CHECK_INTERVAL) {
						checkAndManageBotPlayer();
						lastPlayerCountCheck = currentTime;
					}

					Thread.sleep(5000);

				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				} catch (Exception e) {
					logger.warning("Error in player monitoring: " + e.getMessage());
				}
			}
		}, discordExecutor);
	}

	private void checkAndManageBotPlayer() {
		int realPlayerCount = World.getRealStonerCount();
		boolean shouldBeActive = realPlayerCount > 0;

		if (shouldBeActive != shouldBotPlayerBeActive.get()) {
			shouldBotPlayerBeActive.set(shouldBeActive);

			if (shouldBeActive && !botPlayerActive.get()) {
				logger.info("Real players detected (" + realPlayerCount + "), spawning Discord bot player");
				createBotPlayerAsync();
			} else if (!shouldBeActive && botPlayerActive.get()) {
				logger.info("No real players online, shutting down Discord bot player");
				shutdownBotPlayerAsync();
			}
		}
	}

	private void createBotPlayerAsync() {
		if (botPlayerActive.get()) {
			return;
		}

		CompletableFuture.runAsync(() -> {
			try {

				Thread.sleep(2000);

				if (!shouldBotPlayerBeActive.get()) {
					logger.info("Bot player no longer needed, canceling creation");
					return;
				}

				botPlayer = new DiscordBotStoner(bot);
				botPlayer.initialize();

				itemHandler = new DiscordBotItemHandler(botPlayer);
				botPlayerActive.set(true);

				logger.info("Discord bot player created and initialized successfully");

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.warning("Bot player initialization interrupted");
			} catch (Exception e) {
				logger.severe("Failed to initialize bot player: " + e.getMessage());
				e.printStackTrace();
				botPlayerActive.set(false);
			}
		}, discordExecutor);
	}

	private void shutdownBotPlayerAsync() {
		if (!botPlayerActive.get()) {
			return;
		}

		CompletableFuture.runAsync(() -> {
			try {
				if (botPlayer != null) {
					botPlayer.shutdown();
					botPlayer = null;
				}

				if (itemHandler != null) {
					itemHandler = null;
				}

				botPlayerActive.set(false);
				logger.info("Discord bot player shut down successfully");

			} catch (Exception e) {
				logger.warning("Error shutting down bot player: " + e.getMessage());
			}
		}, discordExecutor);
	}

	public void onPlayerCountChanged() {
		if (active) {

			lastPlayerCountCheck = 0;
		}
	}

	public void shutdown() {
		logger.info("Shutting down Discord manager...");

		if (botPlayer != null) {
			try {
				botPlayer.shutdown();
				botPlayer = null;
				itemHandler = null;
				botPlayerActive.set(false);
				logger.info("Bot player shut down");
			} catch (Exception e) {
				logger.warning("Error shutting down bot player: " + e.getMessage());
			}
		}

		if (bot != null) {
			try {
				bot.disconnect();
				active = false;
				logger.info("Discord bot disconnected");
			} catch (Exception e) {
				logger.warning("Error disconnecting Discord bot: " + e.getMessage());
			}
		}

		if (discordExecutor != null && !discordExecutor.isShutdown()) {
			discordExecutor.shutdown();
			try {
				if (!discordExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
					discordExecutor.shutdownNow();
				}
				logger.info("Discord executor shut down");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				discordExecutor.shutdownNow();
			}
		}
	}

	public boolean isDiscordActive() {
		return active && bot != null && bot.isConnected();
	}

	public boolean isActive() {
		return active && bot != null && bot.isConnected() &&
			botPlayerActive.get() && botPlayer != null && botPlayer.isInitialized();
	}

	public boolean isBotPlayerActive() {
		return botPlayerActive.get() && botPlayer != null && botPlayer.isInitialized();
	}

	public DiscordBot getBot() {
		return bot;
	}

	public DiscordConfig getConfig() {
		return config;
	}

	public DiscordBotStoner getBotPlayer() {
		return botPlayer;
	}

	public void setBotPlayer(DiscordBotStoner botPlayer) {
		this.botPlayer = botPlayer;
	}

	public DiscordBotItemHandler getItemHandler() {
		return itemHandler;
	}

	public void sendGameMessageToDiscord(String gameUsername, String message) {
		if (isBotPlayerActive()) {

			botPlayer.relayGameChatToDiscord(gameUsername, message);
		} else {

			logger.fine("Game message received but bot player is not active: " + gameUsername + ": " + message);
		}
	}

	public void handleItemUsedOnBot(com.bestbudz.rs2.entity.stoner.Stoner player, int itemId, int itemSlot) {
		if (isBotPlayerActive() && itemHandler != null) {
			itemHandler.handleItemUsedOnBot(player, itemId, itemSlot);
		}
	}

	public boolean isDiscordBot(com.bestbudz.rs2.entity.stoner.Stoner stoner) {
		return World.isDiscordBot(stoner);
	}
}
