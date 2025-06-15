package com.bestbudz.core.discord.stonerbot;

import com.bestbudz.core.discord.core.DiscordBot;
import com.bestbudz.core.discord.stonerbot.automations.banking.DiscordBotBankingManager;
import com.bestbudz.core.discord.stonerbot.config.DiscordBotStonerConfig;
import com.bestbudz.core.discord.stonerbot.threading.DiscordBotThreadManager;
import com.bestbudz.core.discord.stonerbot.state.DiscordBotStationaryManager;
import com.bestbudz.core.discord.stonerbot.client.DiscordBotIsolatedClient;

// Import existing components
import com.bestbudz.core.discord.stonerbot.handling.DiscordBotDecisionManager;
import com.bestbudz.core.discord.stonerbot.automations.DiscordBotEmotes;
import com.bestbudz.core.discord.stonerbot.automations.professions.DiscordBotLumbering;
import com.bestbudz.core.discord.stonerbot.handling.DiscordBotMovementManager;
import com.bestbudz.core.discord.stonerbot.automations.professions.DiscordBotQuarrying;
import com.bestbudz.core.discord.stonerbot.config.DiscordBotActions;
import com.bestbudz.core.discord.stonerbot.automations.DiscordBotChat;
import com.bestbudz.core.discord.stonerbot.state.DiscordBotGrades;
import com.bestbudz.core.discord.stonerbot.handling.DiscordBotObjectHandler;
import com.bestbudz.core.discord.stonerbot.state.DiscordBotPersistence;
import com.bestbudz.core.discord.stonerbot.state.DiscordBotAppearance;
import com.bestbudz.core.discord.stonerbot.state.DiscordBotLocation;

import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.Client;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * REFACTORED: Modular Discord Bot with separated concerns and configuration-driven design
 * - All parameters moved to DiscordBotStonerConfig
 * - Threading handled by DiscordBotThreadManager
 * - Banking handled by DiscordBotBankingManager
 * - Stationary periods handled by DiscordBotStationaryManager
 * - Client isolation handled by DiscordBotIsolatedClient
 */
public class DiscordBotStoner extends Stoner implements Runnable {
	private static final Logger logger = Logger.getLogger(DiscordBotStoner.class.getSimpleName());

	// Core state management
	private final AtomicBoolean isInitialized = new AtomicBoolean(false);
	private final AtomicLong lastActivityTime = new AtomicLong(0);

	// Update flags
	private volatile boolean needsVisualUpdate = false;
	private volatile boolean pendingMovement = false;

	// Current activity
	private volatile String currentActivity = "idle";

	// === NEW MODULAR MANAGERS ===
	private final DiscordBotThreadManager threadManager;
	private final DiscordBotStationaryManager stationaryManager;
	private final DiscordBotBankingManager bankingManager;

	// === EXISTING COMPONENTS (unchanged interface) ===
	private final DiscordBotAppearance appearance;
	private final DiscordBotChat chat;
	private final DiscordBotLocation location;
	private final DiscordBotGrades grades;
	private final DiscordBotPersistence persistence;
	private final DiscordBotActions actions;
	private final DiscordBotEmotes emotes;
	private final DiscordBotQuarrying quarrying;
	private final DiscordBotLumbering lumbering;
	private final DiscordBotObjectHandler objectHandler;
	private final DiscordBotDecisionManager decisionManager;
	private final DiscordBotMovementManager movementManager;

	public DiscordBotStoner(DiscordBot discordBot) {
		// CRITICAL FIX: Call super first with new isolated client
		super(new DiscordBotIsolatedClient());

		// CRITICAL FIX: Set the bot reference in the client AFTER super() call
		if (getClient() instanceof DiscordBotIsolatedClient) {
			((DiscordBotIsolatedClient) getClient()).setBotStoner(this);
		}

		// Initialize NEW modular managers
		this.threadManager = new DiscordBotThreadManager(this);
		this.stationaryManager = new DiscordBotStationaryManager(this);
		this.bankingManager = new DiscordBotBankingManager(this);

		// Initialize existing components (unchanged)
		this.appearance = new DiscordBotAppearance(this);
		this.chat = new DiscordBotChat(this, discordBot);
		this.location = new DiscordBotLocation(this);
		this.grades = new DiscordBotGrades(this);
		this.persistence = new DiscordBotPersistence(this);
		this.emotes = new DiscordBotEmotes(this);
		this.actions = new DiscordBotActions(this, chat, location, appearance);
		this.objectHandler = new DiscordBotObjectHandler(this);
		this.quarrying = new DiscordBotQuarrying(this);
		this.lumbering = new DiscordBotLumbering(this);
		this.decisionManager = new DiscordBotDecisionManager(this);
		this.movementManager = new DiscordBotMovementManager(this);

		// Bot setup using configuration - only if not already set
		if (getUsername() == null || getUsername().isEmpty()) {
			setUsername(DiscordBotStonerConfig.DEFAULT_USERNAME);
		}
		if (getDisplay() == null || getDisplay().isEmpty()) {
			setDisplay(DiscordBotStonerConfig.DEFAULT_DISPLAY);
		}
		// Only set these if they're default values
		if (getRights() == 0) {
			setRights(DiscordBotStonerConfig.DEFAULT_RIGHTS);
		}
		setVisible(DiscordBotStonerConfig.DEFAULT_VISIBLE);
		setActive(false);

		logger.info("Modular Discord bot created with fixed client reference management");
	}

	public void initialize() {
		// FIXED: Single atomic check prevents race condition
		if (!isInitialized.compareAndSet(false, true)) {
			logger.warning("Discord bot already initialized, ignoring duplicate call");
			return;
		}

		try {
			int index = World.register(this);
			if (index == -1) {
				logger.severe("Failed to register Discord bot - world full");
				isInitialized.set(false);
				return;
			}

			setActive(true);
			getClient().setStage(Client.Stages.LOGGED_IN);

			// Initialize components in order
			initializeComponents();

			// Initialize isolated client
			if (getClient() instanceof DiscordBotIsolatedClient) {
				((DiscordBotIsolatedClient) getClient()).simulateLogin();
			}

			// Start thread manager
			threadManager.startThread();
			lastActivityTime.set(System.currentTimeMillis());

			// Initialize combat system
			initializeCombatSystem();

			logger.info("Modular Discord bot initialized - index: " + index + " at location: " + getLocation());

			// Schedule initial messages using thread manager
			scheduleInitialMessages();

		} catch (Exception e) {
			logger.severe("Failed to initialize modular Discord bot: " + e.getMessage());
			e.printStackTrace();
			isInitialized.set(false);
			setActive(false);
		}
	}

	/**
	 * Initialize all components
	 */
	private void initializeComponents() {
		// Set initial location
		location.setInitialLocation();

		// Initialize appearance
		appearance.setupMinimalAppearance();

		// Load saved state
		persistence.loadBotState();
		grades.updateAllProfessions();

		// Initialize movement handler
		getMovementHandler().reset();

		// Initialize update flags
		getUpdateFlags().setUpdateRequired(true);
		setNeedsPlacement(true);
		setAppearanceUpdateRequired(true);
	}

	/**
	 * Initialize combat system
	 */
	private void initializeCombatSystem() {
		if (DiscordBotStonerConfig.AUTO_COMBAT_ENABLED) {
			if (getAutoCombat() != null) {
				getAutoCombat().setEnabled(true);
			} else {
				logger.warning("Discord bot AutoCombat is null during initialization!");
			}
		}
	}

	/**
	 * Schedule initial system messages
	 */
	private void scheduleInitialMessages() {
		// Send initial system announcement
		threadManager.scheduleMessage(new BotMessage(BotMessage.Type.SYSTEM_BROADCAST,
			DiscordBotStonerConfig.SYSTEM_ANNOUNCEMENT,
			DiscordBotStonerConfig.DEFAULT_ANNOUNCEMENT_DELAY));

		// Begin autonomous behavior after startup delay
		threadManager.scheduleMessage(new BotMessage(BotMessage.Type.START_SKILLING,
			"begin", DiscordBotStonerConfig.STARTUP_SKILL_DELAY));
	}

	/**
	 * DELEGATED: Main run method now delegates to ThreadManager
	 */
	@Override
	public void run() {
		// The actual run logic is now handled by ThreadManager
		threadManager.run();
	}

	/**
	 * DELEGATED: Update method now uses modular managers
	 */
	public void performBotUpdate() {
		if (!isInitialized.get() || !isActive()) {
			return;
		}

		try {
			if (needsVisualUpdate) {
				appearance.setNeedsVisualUpdate();
				needsVisualUpdate = false;
			}

			if (pendingMovement) {
				location.setNeedsPlacement();
				pendingMovement = false;
			}

			grades.restoreGrades();

			if (DiscordBotStonerConfig.ENABLE_EMOTES) {
				emotes.update();
			}

		} catch (Exception e) {
			logger.warning("Bot update error: " + e.getMessage());
		}
	}

	public void shutdown() {
		if (!isInitialized.get()) {
			return;
		}

		try {
			// Save state
			persistence.forceSaveBotState();

			// Shutdown components
			if (DiscordBotStonerConfig.ENABLE_EMOTES) {
				emotes.shutdown();
			}

			if (DiscordBotStonerConfig.ENABLE_QUARRYING) {
				quarrying.stopQuarrying();
			}

			if (DiscordBotStonerConfig.ENABLE_LUMBERING) {
				lumbering.stopLumbering();
			}

			// Shutdown thread manager
			threadManager.stopThread();

			// Shutdown client
			if (getClient() instanceof DiscordBotIsolatedClient) {
				((DiscordBotIsolatedClient) getClient()).simulateLogout();
			}

			// Cleanup
			World.unregister(this);
			setActive(false);
			isInitialized.set(false);

			logger.info("Modular Discord bot shut down gracefully");

		} catch (Exception e) {
			logger.warning("Error during bot shutdown: " + e.getMessage());
		}
	}

	/**
	 * DELEGATED: Message scheduling now handled by ThreadManager
	 */
	public void scheduleMessage(BotMessage message) {
		threadManager.scheduleMessage(message);
	}

	/**
	 * DELEGATED: Banking operations now handled by BankingManager
	 */
	public void performAutoBanking() {
		bankingManager.performAutoBanking();
	}

	/**
	 * DELEGATED: Banking operations
	 */
	public void addItemToBank(int itemId, int amount) {
		bankingManager.addItemToBank(itemId, amount);
	}

	/**
	 * DELEGATED: Direct banking
	 */
	public void addItemDirectlyToBank(int itemId, int amount) {
		bankingManager.addItemDirectlyToBank(itemId, amount);
	}

	/**
	 * DELEGATED: Inventory space check
	 */
	public boolean hasInventorySpace(int requiredSlots) {
		return bankingManager.hasInventorySpace(requiredSlots);
	}

	@Override
	public void process() throws Exception {
		if (isInitialized.get() && getClient() != null) {
			getClient().resetLastPacketReceived();

			// ALWAYS process AutoCombat first for Discord bot
			if (getAutoCombat() != null) {
				try {
					getAutoCombat().process();
				} catch (Exception e) {
					System.out.println("AutoCombat error: " + e.getMessage());
				}
			}

			// Process regular combat (but AutoCombat should take precedence)
			if (getCombat() != null) {
				getCombat().process();
			}
		}
	}

	@Override
	public boolean login(boolean starter) throws Exception {
		return true;
	}

	@Override
	public void logout(boolean force) {
		shutdown();
	}

	// === PUBLIC API METHODS ===

	public void relayDiscordMessage(String discordUsername, String message) {
		actions.relayDiscordMessage(discordUsername, message);
	}

	public void relayGameChatToDiscord(String username, String message) {
		actions.relayGameChatToDiscord(username, message);
	}

	// === COMPONENT ACCESSORS (unchanged) ===
	public DiscordBotPersistence getBotPersistence() { return persistence; }
	public DiscordBotGrades getBotGrades() { return grades; }
	public DiscordBotAppearance getBotAppearance() { return appearance; }
	public DiscordBotLocation getBotLocation() { return location; }
	public DiscordBotEmotes getBotEmotes() { return emotes; }
	public DiscordBotActions getActions() { return actions; }
	public DiscordBotQuarrying getBotQuarrying() { return quarrying; }
	public DiscordBotLumbering getBotLumbering() { return lumbering; }
	public DiscordBotObjectHandler getBotObjectHandler() { return objectHandler; }
	public DiscordBotDecisionManager getDecisionManager() { return decisionManager; }
	public DiscordBotMovementManager getMovementManager() { return movementManager; }

	// === NEW MANAGER ACCESSORS ===
	public DiscordBotThreadManager getThreadManager() { return threadManager; }
	public DiscordBotStationaryManager getStationaryManager() { return stationaryManager; }
	public DiscordBotBankingManager getBankingManager() { return bankingManager; }

	// === STATUS ACCESSORS ===
	public boolean isInitialized() { return isInitialized.get(); }
	public String getCurrentActivity() { return currentActivity; }
	public void setCurrentActivity(String activity) { this.currentActivity = activity; }
	public long getLastActivityTime() { return lastActivityTime.get(); }
	public void setLastActivityTime(long time) { lastActivityTime.set(time); }

	// === STATIONARY PERIOD ACCESSORS (delegated) ===
	public boolean isInStationaryPeriod() { return stationaryManager.isInStationaryPeriod(); }
	public long getStationaryTimeRemaining() { return stationaryManager.getStationaryTimeRemaining(); }

	// === UPDATE FLAGS ===
	public void setNeedsVisualUpdate() { this.needsVisualUpdate = true; }
	public void setPendingMovement() { this.pendingMovement = true; }

	/**
	 * UNCHANGED: Message class for internal bot communications
	 */
	public static class BotMessage {
		public enum Type {
			DISCORD_MESSAGE, MOVE, SYSTEM_BROADCAST, START_SKILLING, STOP_SKILLING, PERFORM_EMOTE
		}

		private final Type type;
		private final String content;
		private final String username;
		private final Location location;
		private final long delay;

		public BotMessage(Type type, String content) {
			this(type, content, null, null, 0);
		}

		public BotMessage(Type type, String content, String username) {
			this(type, content, username, null, 0);
		}

		public BotMessage(Type type, String content, long delay) {
			this(type, content, null, null, delay);
		}

		public BotMessage(Type type, String content, String username, Location location) {
			this(type, content, username, location, 0);
		}

		public BotMessage(Type type, String content, String username, Location location, long delay) {
			this.type = type;
			this.content = content;
			this.username = username;
			this.location = location;
			this.delay = delay;
		}

		public Type getType() { return type; }
		public String getContent() { return content; }
		public String getUsername() { return username; }
		public Location getLocation() { return location; }
		public long getDelay() { return delay; }
	}

}