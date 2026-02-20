package com.bestbudz.core.discord.stonerbot.handling;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;

public class DiscordBotMovementManager {

	private final DiscordBotStoner bot;

	private volatile long lastMovementTime = System.currentTimeMillis();
	private volatile long lastIdleCheck = System.currentTimeMillis();

	private static final long IDLE_TIMEOUT = 20000;
	private static final long IDLE_CHECK_INTERVAL = 5000;

	public DiscordBotMovementManager(DiscordBotStoner bot) {
		this.bot = bot;
	}

	public void checkMovementIdle(long currentTime) {

		if (currentTime - lastIdleCheck < IDLE_CHECK_INTERVAL) {
			return;
		}
		lastIdleCheck = currentTime;

		if (bot.isInStationaryPeriod() ||
			bot.getBotQuarrying().isCurrentlyMining() ||
			bot.getBotLocation().isMoving()) {
			lastMovementTime = currentTime;
			return;
		}

		if (currentTime - lastMovementTime > IDLE_TIMEOUT) {
			makeIdleMovement();
			lastMovementTime = currentTime;
		}
	}

	private void makeIdleMovement() {
		try {

			if (bot.isInStationaryPeriod() ||
				bot.getBotQuarrying().isCurrentlyMining() ||
				bot.getBotLocation().isMoving()) {
				return;
			}

			if (bot.getBotLocation().isTooFarFromHome()) {
				bot.getBotLocation().moveTowardsHome();
				bot.setCurrentActivity("returning home (idle)");
			} else {

				bot.getBotLocation().performRandomWalk(4, 1);
				bot.setCurrentActivity("idle movement");
			}
		} catch (Exception e) {
			System.out.println("Error in idle movement: " + e.getMessage());
		}
	}

	public void resetMovementTimers() {
		lastMovementTime = System.currentTimeMillis();
		lastIdleCheck = System.currentTimeMillis();
	}

	public void onMovement() {
		lastMovementTime = System.currentTimeMillis();
	}

	public boolean isMoving() {
		return bot.getBotLocation().isMoving();
	}

	public long getLastMovementTime() {
		return lastMovementTime;
	}
}
