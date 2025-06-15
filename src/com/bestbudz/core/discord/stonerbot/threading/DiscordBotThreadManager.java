package com.bestbudz.core.discord.stonerbot.threading;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.core.discord.stonerbot.config.DiscordBotStonerConfig;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Manages threading and message processing for Discord Bot
 */
public class DiscordBotThreadManager implements Runnable {
	private static final Logger logger = Logger.getLogger(DiscordBotThreadManager.class.getSimpleName());

	private final DiscordBotStoner bot;
	private Thread botThread;
	private final AtomicBoolean running = new AtomicBoolean(false);
	private final AtomicLong lastUpdateTime = new AtomicLong(0);

	// Message queue management
	private final BlockingQueue<DiscordBotStoner.BotMessage> messageQueue =
		new LinkedBlockingQueue<>(DiscordBotStonerConfig.MESSAGE_QUEUE_SIZE);

	public DiscordBotThreadManager(DiscordBotStoner bot) {
		this.bot = bot;
	}

	/**
	 * Start the bot thread
	 */
	public void startThread() {
		if (running.compareAndSet(false, true)) {
			botThread = new Thread(this, "DiscordBot-Threading");
			botThread.setDaemon(true);
			botThread.setPriority(Thread.NORM_PRIORITY + DiscordBotStonerConfig.THREAD_PRIORITY_OFFSET);
			botThread.start();
			logger.info("Discord bot thread started");
		}
	}

	/**
	 * Stop the bot thread
	 */
	public void stopThread() {
		if (running.compareAndSet(true, false)) {
			if (botThread != null && botThread.isAlive()) {
				botThread.interrupt();
				try {
					botThread.join(DiscordBotStonerConfig.SHUTDOWN_TIMEOUT);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			messageQueue.clear();
			logger.info("Discord bot thread stopped");
		}
	}

	/**
	 * Main thread execution loop
	 */
	@Override
	public void run() {
		lastUpdateTime.set(System.currentTimeMillis());

		while (running.get()) {
			try {
				processMessageBatch();
				long currentTime = System.currentTimeMillis();

				// Process core bot systems
				processCoreSystemsLoop(currentTime);

				// Regular updates
				if (currentTime - lastUpdateTime.get() >= DiscordBotStonerConfig.UPDATE_INTERVAL) {
					bot.performBotUpdate();
					lastUpdateTime.set(currentTime);
				}

				Thread.sleep(DiscordBotStonerConfig.THREAD_SLEEP_TIME);

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			} catch (Exception e) {
				logger.warning("Bot thread error: " + e.getMessage());
			}
		}
	}

	/**
	 * Process core systems in priority order
	 */
	private void processCoreSystemsLoop(long currentTime) {
		try {
			// CRITICAL: Combat processing has highest priority
			processCombatSystems();

			// Movement validation and management
			processMovementSystems(currentTime);

			// Stationary period management
			bot.getStationaryManager().manageStationaryPeriods(currentTime);

			// Skill system updates
			processSkillSystems();

			// Decision making (lowest priority)
			processDecisionSystems(currentTime);

		} catch (Exception e) {
			logger.warning("Error in core systems loop: " + e.getMessage());
		}
	}

	/**
	 * Process combat systems with highest priority
	 */
	private void processCombatSystems() {
		try {
			if (bot.getCombat() != null) {
				bot.getCombat().process();
			}

			if (bot.getAutoCombat() != null) {
				bot.getAutoCombat().process();
			}
		} catch (Exception e) {
			logger.warning("Combat processing error: " + e.getMessage());
		}
	}

	/**
	 * Process movement systems
	 */
	private void processMovementSystems(long currentTime) {
		try {
			// Validate movement
			bot.getBotLocation().validateMovement();

			// Movement management (but don't interfere with combat)
			if (!bot.getBotQuarrying().isCurrentlyMining() &&
				!bot.getStationaryManager().isInStationaryPeriod() &&
				!bot.getCombat().inCombat()) {

				bot.getMovementManager().checkMovementIdle(currentTime);

				if (bot.getBotLocation().isMoving()) {
					bot.getMovementManager().onMovement();
				}
			}
		} catch (Exception e) {
			logger.warning("Movement processing error: " + e.getMessage());
		}
	}

	/**
	 * Process skill systems
	 */
	private void processSkillSystems() {
		try {
			if (DiscordBotStonerConfig.ENABLE_QUARRYING) {
				bot.getBotQuarrying().update();
			}

			if (DiscordBotStonerConfig.ENABLE_LUMBERING) {
				bot.getBotLumbering().update();
			}
		} catch (Exception e) {
			logger.warning("Skill processing error: " + e.getMessage());
		}
	}


	/**
	 * Process decision systems (lowest priority)
	 */
	private void processDecisionSystems(long currentTime) {
		try {
			if (!bot.getBotQuarrying().isQuarrying() &&
				!bot.getBotLumbering().isLumbering() &&
				!bot.getStationaryManager().isInStationaryPeriod() &&
				!bot.getBotLocation().isMoving() &&
				!bot.getCombat().inCombat() &&
				currentTime - bot.getLastActivityTime() >= DiscordBotStonerConfig.getRandomActivityInterval()) {

				if (DiscordBotStonerConfig.ENABLE_DECISION_MANAGER) {
					bot.getDecisionManager().makeDecision();
					bot.setLastActivityTime(currentTime);
				}
			}
		} catch (Exception e) {
			logger.warning("Decision processing error: " + e.getMessage());
		}
	}

	/**
	 * Process batch of messages from queue
	 */
	private void processMessageBatch() {
		for (int i = 0; i < DiscordBotStonerConfig.MESSAGES_PER_BATCH; i++) {
			DiscordBotStoner.BotMessage message = messageQueue.poll();
			if (message == null) break;
			processMessage(message);
		}
	}

	/**
	 * Process individual message
	 */
	private void processMessage(DiscordBotStoner.BotMessage message) {
		try {
			bot.getActions().processMessage(message);
		} catch (Exception e) {
			logger.warning("Error processing bot message: " + e.getMessage());
		}
	}

	/**
	 * Schedule a message with optional delay
	 */
	public void scheduleMessage(DiscordBotStoner.BotMessage message) {
		if (message.getDelay() > 0) {
			// Schedule for later execution
			new Thread(() -> {
				try {
					Thread.sleep(message.getDelay());
					if (!messageQueue.offer(message)) {
						logger.warning("Bot message queue full, dropping delayed message");
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}, "BotDelayedMessage").start();
		} else {
			// Add immediately
			if (!messageQueue.offer(message)) {
				logger.warning("Bot message queue full, dropping message");
			}
		}
	}

	/**
	 * Get queue size for monitoring
	 */
	public int getQueueSize() {
		return messageQueue.size();
	}

	/**
	 * Check if thread is running
	 */
	public boolean isRunning() {
		return running.get();
	}
}