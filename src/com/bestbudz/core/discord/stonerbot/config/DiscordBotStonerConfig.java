package com.bestbudz.core.discord.stonerbot.config;

import com.bestbudz.rs2.entity.Location;

public class DiscordBotStonerConfig {

	public static final int MESSAGE_QUEUE_SIZE = 100;
	public static final int MESSAGES_PER_BATCH = 5;
	public static final int THREAD_SLEEP_TIME = 600;
	public static final int THREAD_PRIORITY_OFFSET = 1;
	public static final long SHUTDOWN_TIMEOUT = 5000;

	public static final long UPDATE_INTERVAL = 1000;
	public static final long ACTIVITY_INTERVAL_MIN = 8000;
	public static final long ACTIVITY_INTERVAL_MAX = 20000;
	public static final long EMOTE_INTERVAL_MIN = 15000;
	public static final long EMOTE_INTERVAL_MAX = 45000;
	public static final long DEBUG_INTERVAL = 30000;

	public static final double STATIONARY_START_CHANCE = 0.001;
	public static final long STATIONARY_DURATION_MIN = 20000;
	public static final long STATIONARY_DURATION_MAX = 60000;
	public static final long STATIONARY_EMOTE_INTERVAL = 30000;
	public static final double STATIONARY_EMOTE_CHANCE = 0.3;

	public static final int MOVEMENT_RANGE_MAX = 30;
	public static final int COMBAT_AREA_RANGE = 8;

	public static final int MAX_ITEMS_PER_BANKING = 20;
	public static final int BANKING_EXPERIENCE_REWARD = 10;
	public static final int[] ESSENTIAL_ITEMS = {};

	public static final String DEFAULT_USERNAME = "BestBud";
	public static final String DEFAULT_DISPLAY = "BestBud";
	public static final int DEFAULT_RIGHTS = 3;
	public static final boolean DEFAULT_VISIBLE = true;

	public static final String SYSTEM_ANNOUNCEMENT = "BestBud came to smoke best buds in BestBudz with best buds";
	public static final long DEFAULT_ANNOUNCEMENT_DELAY = 5000;
	public static final long STARTUP_SKILL_DELAY = 15000;

	public static final Location COMBAT_AREA_LOCATION = new Location(3415, 2934, 0);
	public static final Location DEFAULT_SPAWN_LOCATION = new Location(3200, 3200, 0);

	public static final int[] STATIONARY_EMOTES = {863, 861, 2339, 857};
	public static final int[] IDLE_EMOTES = {863, 861, 2339, 857, 856, 858};

	public static final long NPC_UPDATE_INTERVAL = 1000;
	public static final long PACKET_TIMEOUT = 5000;

	public static final boolean AUTO_BANKING_ENABLED = true;
	public static final boolean DIRECT_BANK_ACCESS = true;
	public static final boolean SKIP_INVENTORY = true;

	public static final boolean AUTO_COMBAT_ENABLED = true;
	public static final boolean COMBAT_PRIORITY = true;

	public static final boolean DEBUG_ENABLED = true;
	public static final boolean VERBOSE_LOGGING = false;
	public static final String LOG_PREFIX = "[DiscordBot]";

	public static final boolean ENABLE_QUARRYING = true;
	public static final boolean ENABLE_LUMBERING = true;
	public static final boolean ENABLE_MOVEMENT_MANAGER = true;
	public static final boolean ENABLE_DECISION_MANAGER = true;
	public static final boolean ENABLE_EMOTES = true;
	public static final boolean ENABLE_STATIONARY_PERIODS = true;

	public static final int MAX_CONCURRENT_ACTIONS = 3;
	public static final long ACTION_COOLDOWN = 1000;
	public static final int MAX_RETRIES = 3;

	public static long getRandomActivityInterval() {
		return ACTIVITY_INTERVAL_MIN + (long)(Math.random() * (ACTIVITY_INTERVAL_MAX - ACTIVITY_INTERVAL_MIN));
	}

	public static long getRandomEmoteInterval() {
		return EMOTE_INTERVAL_MIN + (long)(Math.random() * (EMOTE_INTERVAL_MAX - EMOTE_INTERVAL_MIN));
	}

	public static long getRandomStationaryDuration() {
		return STATIONARY_DURATION_MIN + (long)(Math.random() * (STATIONARY_DURATION_MAX - STATIONARY_DURATION_MIN));
	}

	public static int getRandomStationaryEmote() {
		return STATIONARY_EMOTES[(int)(Math.random() * STATIONARY_EMOTES.length)];
	}

	public static int getRandomIdleEmote() {
		return IDLE_EMOTES[(int)(Math.random() * IDLE_EMOTES.length)];
	}

	public static boolean isEssentialItem(int itemId) {
		for (int essential : ESSENTIAL_ITEMS) {
			if (itemId == essential) {
				return true;
			}
		}
		return false;
	}

	public static int calculateDistance(Location loc1, Location loc2) {
		return Math.max(
			Math.abs(loc1.getX() - loc2.getX()),
			Math.abs(loc1.getY() - loc2.getY())
		);
	}

	public static boolean isInCombatArea(Location location) {
		return calculateDistance(location, COMBAT_AREA_LOCATION) <= COMBAT_AREA_RANGE;
	}
}
