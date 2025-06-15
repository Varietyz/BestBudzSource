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

	// NEW: Bot player state management
	private final AtomicBoolean botPlayerActive = new AtomicBoolean(false);
	private final AtomicBoolean shouldBotPlayerBeActive = new AtomicBoolean(false);
	private long lastPlayerCountCheck = 0;
	private static final long PLAYER_COUNT_CHECK_INTERVAL = 10000; // Check every 10 seconds

	// Single executor optimized for Discord operations
	private ExecutorService discordExecutor;

	private DiscordManager() {
		this.config = new DiscordConfig();
		// Use a fixed thread pool instead of cached to prevent thread explosion
		this.discordExecutor = Executors.newFixedThreadPool(2, r -> {
			Thread t = new Thread(r, "Discord-Operations");
			t.setDaemon(true);
			t.setPriority(Thread.NORM_PRIORITY - 1); // Lower priority than game threads
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
				bot.connect().join(); // Wait for bot to connect

				active = true;
				logger.info("Discord bot initialized (game bot will spawn when players are online)");

				// Start the player monitoring task
				startPlayerMonitoring();

			} catch (Exception e) {
				logger.severe("Failed to initialize Discord bot: " + e.getMessage());
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}, discordExecutor);
	}

	/**
	 * NEW: Start monitoring player count and manage bot player accordingly
	 */
	private void startPlayerMonitoring() {
		CompletableFuture.runAsync(() -> {
			while (active && !Thread.currentThread().isInterrupted()) {
				try {
					long currentTime = System.currentTimeMillis();

					if (currentTime - lastPlayerCountCheck >= PLAYER_COUNT_CHECK_INTERVAL) {
						checkAndManageBotPlayer();
						lastPlayerCountCheck = currentTime;
					}

					Thread.sleep(5000); // Check every 5 seconds

				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				} catch (Exception e) {
					logger.warning("Error in player monitoring: " + e.getMessage());
				}
			}
		}, discordExecutor);
	}

	/**
	 * NEW: Check player count and manage bot player lifecycle
	 */
	private void checkAndManageBotPlayer() {
		int realPlayerCount = World.getRealStonerCount(); // Uses existing World method
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

	/**
	 * NEW: Create bot player asynchronously when needed
	 */
	private void createBotPlayerAsync() {
		if (botPlayerActive.get()) {
			return; // Already active
		}

		CompletableFuture.runAsync(() -> {
			try {
				// Short delay to ensure world state is stable
				Thread.sleep(2000);

				// Double-check that we still need the bot player
				if (!shouldBotPlayerBeActive.get()) {
					logger.info("Bot player no longer needed, canceling creation");
					return;
				}

				botPlayer = new DiscordBotStoner(bot);
				botPlayer.initialize();

				// Initialize item handler after bot player is created
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

	/**
	 * NEW: Shutdown bot player asynchronously when no longer needed
	 */
	private void shutdownBotPlayerAsync() {
		if (!botPlayerActive.get()) {
			return; // Already inactive
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

	/**
	 * NEW: Force check player count (can be called externally when players join/leave)
	 */
	public void onPlayerCountChanged() {
		if (active) {
			// Reset the timer to trigger immediate check
			lastPlayerCountCheck = 0;
		}
	}

	public void shutdown() {
		logger.info("Shutting down Discord manager...");

		// Shutdown bot player first
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

		// Shutdown Discord bot
		if (bot != null) {
			try {
				bot.disconnect();
				active = false;
				logger.info("Discord bot disconnected");
			} catch (Exception e) {
				logger.warning("Error disconnecting Discord bot: " + e.getMessage());
			}
		}

		// Shutdown executor with shorter timeout
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

	/**
	 * NEW: Check if Discord is active (bot connected) but game bot may or may not be active
	 */
	public boolean isDiscordActive() {
		return active && bot != null && bot.isConnected();
	}

	/**
	 * MODIFIED: Check if both Discord and game bot are active
	 */
	public boolean isActive() {
		return active && bot != null && bot.isConnected() &&
			botPlayerActive.get() && botPlayer != null && botPlayer.isInitialized();
	}

	/**
	 * NEW: Check if game bot player is currently active
	 */
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

	/**
	 * MODIFIED: Send game message to Discord (with null check for bot player)
	 */
	public void sendGameMessageToDiscord(String gameUsername, String message) {
		if (isBotPlayerActive()) {
			// Use the bot's own thread for Discord relay
			botPlayer.relayGameChatToDiscord(gameUsername, message);
		} else {
			// Discord is active but game bot is not - could log this to Discord directly
			logger.fine("Game message received but bot player is not active: " + gameUsername + ": " + message);
		}
	}

	/**
	 * MODIFIED: Handle item used on Discord bot (with null check)
	 */
	public void handleItemUsedOnBot(com.bestbudz.rs2.entity.stoner.Stoner player, int itemId, int itemSlot) {
		if (isBotPlayerActive() && itemHandler != null) {
			itemHandler.handleItemUsedOnBot(player, itemId, itemSlot);
		}
	}

	/**
	 * MODIFIED: Check if a stoner is the Discord bot (use World's method)
	 */
	public boolean isDiscordBot(com.bestbudz.rs2.entity.stoner.Stoner stoner) {
		return World.isDiscordBot(stoner);
	}
}