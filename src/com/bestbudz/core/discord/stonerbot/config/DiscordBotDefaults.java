package com.bestbudz.core.discord.stonerbot.config;

import com.bestbudz.rs2.entity.Location;

public class DiscordBotDefaults {

	public static final String DEFAULT_USERNAME = "BestBud";
	public static final String DEFAULT_DISPLAY = DEFAULT_USERNAME;
	public static final int DEFAULT_RIGHTS = 1;
	public static final boolean DEFAULT_VISIBLE = true;

	public static final byte DEFAULT_GENDER = 1;
	public static final int[] DEFAULT_APPEARANCE = {48, 57, 61, 68, 76, 79, -1};
	public static final byte[] DEFAULT_COLORS = {47, 6, 4, 60, 62};

	public static final com.bestbudz.rs2.entity.Location DEFAULT_LOCATION = new Location(3436, 2916, 0);

	public static final long UPDATE_INTERVAL = 5000;
	public static final int MESSAGE_QUEUE_SIZE = 200;
	public static final int THREAD_PRIORITY_OFFSET = -1;
	public static final long THREAD_SLEEP_TIME = 500;
	public static final int MESSAGES_PER_BATCH = 5;
	public static final long SHUTDOWN_TIMEOUT = 2000;

	public static final long DEFAULT_ANNOUNCEMENT_DELAY = 3000;
	public static final String SYSTEM_ANNOUNCEMENT = "BestBud came to smoke Best Buds in BestBudz";

}
