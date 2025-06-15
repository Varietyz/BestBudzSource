package com.bestbudz.core.discord.stonerbot.config;

import com.bestbudz.rs2.entity.Location;

/**
 * Configuration class for Discord Bot Stoner
 * Centralizes all configurable parameters for easy modification
 */
public class DiscordBotStonerConfig {

	// === THREADING CONFIGURATION ===
	public static final int MESSAGE_QUEUE_SIZE = 100;
	public static final int MESSAGES_PER_BATCH = 5;
	public static final int THREAD_SLEEP_TIME = 600; // milliseconds
	public static final int THREAD_PRIORITY_OFFSET = 1;
	public static final long SHUTDOWN_TIMEOUT = 5000; // milliseconds

	// === UPDATE INTERVALS ===
	public static final long UPDATE_INTERVAL = 1000; // milliseconds
	public static final long ACTIVITY_INTERVAL_MIN = 8000; // milliseconds
	public static final long ACTIVITY_INTERVAL_MAX = 20000; // milliseconds
	public static final long EMOTE_INTERVAL_MIN = 15000; // milliseconds
	public static final long EMOTE_INTERVAL_MAX = 45000; // milliseconds
	public static final long DEBUG_INTERVAL = 30000; // milliseconds

	// === STATIONARY PERIOD CONFIGURATION ===
	public static final double STATIONARY_START_CHANCE = 0.001; // 0.1% per update
	public static final long STATIONARY_DURATION_MIN = 20000; // milliseconds
	public static final long STATIONARY_DURATION_MAX = 60000; // milliseconds
	public static final long STATIONARY_EMOTE_INTERVAL = 30000; // milliseconds
	public static final double STATIONARY_EMOTE_CHANCE = 0.3; // 30%

	// === MOVEMENT CONFIGURATION ===
	public static final int MOVEMENT_RANGE_MAX = 30; // tiles
	public static final int COMBAT_AREA_RANGE = 8; // tiles

	// === BANKING CONFIGURATION ===
	public static final int MAX_ITEMS_PER_BANKING = 20;
	public static final int BANKING_EXPERIENCE_REWARD = 10;
	public static final int[] ESSENTIAL_ITEMS = {}; // Items never to bank

	// === BOT IDENTITY CONFIGURATION ===
	public static final String DEFAULT_USERNAME = "BestBud";
	public static final String DEFAULT_DISPLAY = "BestBud";
	public static final int DEFAULT_RIGHTS = 3;
	public static final boolean DEFAULT_VISIBLE = true;

	// === MESSAGING CONFIGURATION ===
	public static final String SYSTEM_ANNOUNCEMENT = "BestBud came to smoke best buds in BestBudz with best buds";
	public static final long DEFAULT_ANNOUNCEMENT_DELAY = 5000; // milliseconds
	public static final long STARTUP_SKILL_DELAY = 15000; // milliseconds

	// === LOCATION CONFIGURATION ===
	public static final Location COMBAT_AREA_LOCATION = new Location(3415, 2934, 0);
	public static final Location DEFAULT_SPAWN_LOCATION = new Location(3200, 3200, 0);

	// === EMOTE CONFIGURATION ===
	public static final int[] STATIONARY_EMOTES = {863, 861, 2339, 857};
	public static final int[] IDLE_EMOTES = {863, 861, 2339, 857, 856, 858};

	// === CLIENT CONFIGURATION ===
	public static final long NPC_UPDATE_INTERVAL = 1000; // milliseconds
	public static final long PACKET_TIMEOUT = 5000; // milliseconds

	// === SKILL CONFIGURATION ===
	public static final boolean AUTO_BANKING_ENABLED = true;
	public static final boolean DIRECT_BANK_ACCESS = true;
	public static final boolean SKIP_INVENTORY = true;

	// === COMBAT CONFIGURATION ===
	public static final boolean AUTO_COMBAT_ENABLED = true;
	public static final boolean COMBAT_PRIORITY = true; // Combat processing has highest priority

	// === LOGGING CONFIGURATION ===
	public static final boolean DEBUG_ENABLED = true;
	public static final boolean VERBOSE_LOGGING = false;
	public static final String LOG_PREFIX = "[DiscordBot]";

	// === FEATURE FLAGS ===
	public static final boolean ENABLE_QUARRYING = true;
	public static final boolean ENABLE_LUMBERING = true;
	public static final boolean ENABLE_MOVEMENT_MANAGER = true;
	public static final boolean ENABLE_DECISION_MANAGER = true;
	public static final boolean ENABLE_EMOTES = true;
	public static final boolean ENABLE_STATIONARY_PERIODS = true;

	// === PERFORMANCE CONFIGURATION ===
	public static final int MAX_CONCURRENT_ACTIONS = 3;
	public static final long ACTION_COOLDOWN = 1000; // milliseconds
	public static final int MAX_RETRIES = 3;

	// === UTILITY METHODS ===

	/**
	 * Get random activity interval
	 */
	public static long getRandomActivityInterval() {
		return ACTIVITY_INTERVAL_MIN + (long)(Math.random() * (ACTIVITY_INTERVAL_MAX - ACTIVITY_INTERVAL_MIN));
	}

	/**
	 * Get random emote interval
	 */
	public static long getRandomEmoteInterval() {
		return EMOTE_INTERVAL_MIN + (long)(Math.random() * (EMOTE_INTERVAL_MAX - EMOTE_INTERVAL_MIN));
	}

	/**
	 * Get random stationary duration
	 */
	public static long getRandomStationaryDuration() {
		return STATIONARY_DURATION_MIN + (long)(Math.random() * (STATIONARY_DURATION_MAX - STATIONARY_DURATION_MIN));
	}

	/**
	 * Get random stationary emote
	 */
	public static int getRandomStationaryEmote() {
		return STATIONARY_EMOTES[(int)(Math.random() * STATIONARY_EMOTES.length)];
	}

	/**
	 * Get random idle emote
	 */
	public static int getRandomIdleEmote() {
		return IDLE_EMOTES[(int)(Math.random() * IDLE_EMOTES.length)];
	}

	/**
	 * Check if item is essential
	 */
	public static boolean isEssentialItem(int itemId) {
		for (int essential : ESSENTIAL_ITEMS) {
			if (itemId == essential) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calculate distance between two locations
	 */
	public static int calculateDistance(Location loc1, Location loc2) {
		return Math.max(
			Math.abs(loc1.getX() - loc2.getX()),
			Math.abs(loc1.getY() - loc2.getY())
		);
	}

	/**
	 * Check if location is within combat area
	 */
	public static boolean isInCombatArea(Location location) {
		return calculateDistance(location, COMBAT_AREA_LOCATION) <= COMBAT_AREA_RANGE;
	}
}