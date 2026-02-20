package com.bestbudz.core.discord.stonerbot.state;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.core.discord.stonerbot.config.DiscordBotStonerConfig;

import java.util.Random;
import java.util.logging.Logger;

public class DiscordBotStationaryManager {
	private static final Logger logger = Logger.getLogger(DiscordBotStationaryManager.class.getSimpleName());

	private final DiscordBotStoner bot;
	private final Random random = new Random();

	private volatile boolean inStationaryPeriod = false;
	private volatile long stationaryPeriodStartTime = 0;
	private volatile long stationaryPeriodDuration = 0;
	private volatile long lastEmoteTime = 0;

	public DiscordBotStationaryManager(DiscordBotStoner bot) {
		this.bot = bot;
		this.lastEmoteTime = System.currentTimeMillis();
	}

	public void manageStationaryPeriods(long currentTime) {
		if (!DiscordBotStonerConfig.ENABLE_STATIONARY_PERIODS) {
			return;
		}

		if (inStationaryPeriod) {
			handleActiveStationaryPeriod(currentTime);
		} else {
			checkForStationaryPeriodStart();
		}
	}

	private void handleActiveStationaryPeriod(long currentTime) {

		if (currentTime - stationaryPeriodStartTime >= stationaryPeriodDuration) {
			endStationaryPeriod();
			return;
		}

		if (shouldPerformStationaryEmote(currentTime)) {
			performStationaryEmote();
			lastEmoteTime = currentTime;
		}
	}

	private void checkForStationaryPeriodStart() {
		if (Math.random() < DiscordBotStonerConfig.STATIONARY_START_CHANCE) {
			startStationaryPeriod();
		}
	}

	public void startStationaryPeriod() {
		inStationaryPeriod = true;
		stationaryPeriodStartTime = System.currentTimeMillis();
		stationaryPeriodDuration = DiscordBotStonerConfig.getRandomStationaryDuration();
		lastEmoteTime = System.currentTimeMillis();

		bot.setCurrentActivity("starting rest period");
		bot.getBotLocation().stopMovement();

		if (DiscordBotStonerConfig.VERBOSE_LOGGING) {
			logger.info("Bot started stationary period for " + (stationaryPeriodDuration / 1000) + " seconds");
		}
	}

	public void endStationaryPeriod() {
		inStationaryPeriod = false;
		bot.setCurrentActivity("ending rest period");

		if (DiscordBotStonerConfig.VERBOSE_LOGGING) {
			logger.info("Bot ended stationary period");
		}
	}

	public void forceEndStationaryPeriod() {
		if (inStationaryPeriod) {
			inStationaryPeriod = false;
			bot.setCurrentActivity("interrupted rest");
			logger.info("Bot stationary period interrupted");
		}
	}

	private boolean shouldPerformStationaryEmote(long currentTime) {
		return currentTime - lastEmoteTime >= DiscordBotStonerConfig.STATIONARY_EMOTE_INTERVAL &&
			Math.random() < DiscordBotStonerConfig.STATIONARY_EMOTE_CHANCE;
	}

	private void performStationaryEmote() {
		try {
			int randomEmote = DiscordBotStonerConfig.getRandomStationaryEmote();
			bot.getUpdateFlags().sendAnimation(new com.bestbudz.rs2.entity.Animation(randomEmote));
			bot.setCurrentActivity("resting emote");

			if (DiscordBotStonerConfig.VERBOSE_LOGGING) {
				logger.info("Bot performed stationary emote: " + randomEmote);
			}
		} catch (Exception e) {
			logger.warning("Error performing stationary emote: " + e.getMessage());
		}
	}

	public void performIdleEmote() {
		if (inStationaryPeriod) {
			return;
		}

		try {
			int randomEmote = DiscordBotStonerConfig.getRandomIdleEmote();
			bot.getUpdateFlags().sendAnimation(new com.bestbudz.rs2.entity.Animation(randomEmote));
			bot.setCurrentActivity("idle emote");

			if (DiscordBotStonerConfig.VERBOSE_LOGGING) {
				logger.info("Bot performed idle emote: " + randomEmote);
			}
		} catch (Exception e) {
			logger.warning("Error performing idle emote: " + e.getMessage());
		}
	}

	public boolean isInStationaryPeriod() {
		return inStationaryPeriod;
	}

	public long getStationaryTimeRemaining() {
		if (!inStationaryPeriod) {
			return 0;
		}
		long elapsed = System.currentTimeMillis() - stationaryPeriodStartTime;
		return Math.max(0, stationaryPeriodDuration - elapsed);
	}

	public double getStationaryProgress() {
		if (!inStationaryPeriod) {
			return 0.0;
		}
		long elapsed = System.currentTimeMillis() - stationaryPeriodStartTime;
		return Math.min(1.0, (double) elapsed / stationaryPeriodDuration);
	}

	public boolean canPerformActions() {
		return !inStationaryPeriod;
	}

	public String getStationaryStatus() {
		if (!inStationaryPeriod) {
			return "Active";
		}

		long remaining = getStationaryTimeRemaining();
		return "Resting (" + (remaining / 1000) + "s remaining)";
	}

	public void reset() {
		inStationaryPeriod = false;
		stationaryPeriodStartTime = 0;
		stationaryPeriodDuration = 0;
		lastEmoteTime = System.currentTimeMillis();

		if (DiscordBotStonerConfig.VERBOSE_LOGGING) {
			logger.info("Stationary manager reset");
		}
	}
}
