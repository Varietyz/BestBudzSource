package com.bestbudz.core.discord.stonerbot.config;

import com.bestbudz.rs2.entity.Location;

/**
 * All default constants for the Discord bot
 * SIMPLIFIED: Basic starting stats that are set only once
 */
public class DiscordBotDefaults {

	// Basic bot settings
	public static final String DEFAULT_USERNAME = "BestBud";
	public static final String DEFAULT_DISPLAY = DEFAULT_USERNAME;
	public static final int DEFAULT_RIGHTS = 1;
	public static final boolean DEFAULT_VISIBLE = true;

	// Appearance defaults - Fixed for proper female appearance
	public static final byte DEFAULT_GENDER = 1; // Female
	public static final int[] DEFAULT_APPEARANCE = {48, 57, 61, 68, 76, 79, -1}; // head, torso, arms, hands, legs, feet, jaw
	public static final byte[] DEFAULT_COLORS = {47, 6, 4, 60, 62}; // hair, torso, legs, feet, skin

	// Location defaults
	public static final com.bestbudz.rs2.entity.Location DEFAULT_LOCATION = new Location(3436, 2916, 0);

	// Threading defaults
	public static final long UPDATE_INTERVAL = 5000; // Update every 5 seconds
	public static final int MESSAGE_QUEUE_SIZE = 200;
	public static final int THREAD_PRIORITY_OFFSET = -1; // Thread.NORM_PRIORITY - 1
	public static final long THREAD_SLEEP_TIME = 500; // 2 FPS for bot operations
	public static final int MESSAGES_PER_BATCH = 5;
	public static final long SHUTDOWN_TIMEOUT = 2000;

	// Chat defaults
	public static final long DEFAULT_ANNOUNCEMENT_DELAY = 3000;
	public static final String SYSTEM_ANNOUNCEMENT = "BestBud came to smoke Best Buds in BestBudz";


}