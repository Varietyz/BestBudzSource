package com.bestbudz.core.discord.stonerbot.handling;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;

/**
 * FIXED: Simple movement manager using proper pathfinding
 */
public class DiscordBotMovementManager {

	private final DiscordBotStoner bot;

	// Simple timing
	private volatile long lastMovementTime = System.currentTimeMillis();
	private volatile long lastIdleCheck = System.currentTimeMillis();

	// Constants
	private static final long IDLE_TIMEOUT = 20000; // 20 seconds before idle movement
	private static final long IDLE_CHECK_INTERVAL = 5000; // 5 seconds

	public DiscordBotMovementManager(DiscordBotStoner bot) {
		this.bot = bot;
	}

	/**
	 * FIXED: Check for idle movement using proper pathfinding
	 */
	public void checkMovementIdle(long currentTime) {
		// Only check every few seconds
		if (currentTime - lastIdleCheck < IDLE_CHECK_INTERVAL) {
			return;
		}
		lastIdleCheck = currentTime;

		// Skip if bot is busy
		if (bot.isInStationaryPeriod() ||
			bot.getBotQuarrying().isCurrentlyMining() ||
			bot.getBotLocation().isMoving()) {
			lastMovementTime = currentTime;
			return;
		}

		// Check if idle too long
		if (currentTime - lastMovementTime > IDLE_TIMEOUT) {
			makeIdleMovement();
			lastMovementTime = currentTime;
		}
	}

	/**
	 * FIXED: Make proper idle movement
	 */
	private void makeIdleMovement() {
		try {
			// Don't move if busy
			if (bot.isInStationaryPeriod() ||
				bot.getBotQuarrying().isCurrentlyMining() ||
				bot.getBotLocation().isMoving()) {
				return;
			}

			// FIXED: Use proper movement methods
			if (bot.getBotLocation().isTooFarFromHome()) {
				bot.getBotLocation().moveTowardsHome();
				bot.setCurrentActivity("returning home (idle)");
			} else {
				// Small random movement to prevent appearing AFK
				bot.getBotLocation().performRandomWalk(4, 1);
				bot.setCurrentActivity("idle movement");
			}
		} catch (Exception e) {
			System.out.println("Error in idle movement: " + e.getMessage());
		}
	}

	/**
	 * Reset movement timers
	 */
	public void resetMovementTimers() {
		lastMovementTime = System.currentTimeMillis();
		lastIdleCheck = System.currentTimeMillis();
	}

	/**
	 * Update movement time when bot moves
	 */
	public void onMovement() {
		lastMovementTime = System.currentTimeMillis();
	}

	// Simple getters
	public boolean isMoving() {
		return bot.getBotLocation().isMoving();
	}

	public long getLastMovementTime() {
		return lastMovementTime;
	}
}