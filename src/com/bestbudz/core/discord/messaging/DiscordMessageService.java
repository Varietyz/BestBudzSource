// DiscordMessageService.java
package com.bestbudz.core.discord.messaging;

import com.bestbudz.core.discord.core.DiscordBot;
import com.bestbudz.core.discord.core.DiscordConfig;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Centralized Discord messaging service that handles all Discord communications
 * Independent of bot player status - works even when bot is offline
 */
public class DiscordMessageService implements Runnable {
	private static final Logger logger = Logger.getLogger(DiscordMessageService.class.getSimpleName());

	// Singleton instance
	private static volatile DiscordMessageService instance;
	private static final Object instanceLock = new Object();

	// Discord connection
	private volatile DiscordBot discordBot;
	private volatile boolean isConnected = false;

	// Message processing
	private final BlockingQueue<DiscordMessage> messageQueue = new LinkedBlockingQueue<>(1000);
	private final AtomicBoolean running = new AtomicBoolean(false);
	private Thread processingThread;

	// Channel cache
	private volatile TextChannel chatChannel;
	private volatile TextChannel statusChannel;
	private volatile TextChannel levelsChannel;
	private volatile TextChannel advancementsChannel;
	private volatile TextChannel staffChannel;

	private DiscordMessageService() {
		// Private constructor for singleton
	}

	public static DiscordMessageService getInstance() {
		if (instance == null) {
			synchronized (instanceLock) {
				if (instance == null) {
					instance = new DiscordMessageService();
				}
			}
		}
		return instance;
	}

	/**
	 * Initialize the messaging service with Discord bot
	 */
	public void initialize(DiscordBot discordBot) {
		this.discordBot = discordBot;
		initializeChannels();
		startProcessingThread();
		logger.info("Discord messaging service initialized");
	}

	/**
	 * Initialize Discord channel references
	 */
	private void initializeChannels() {
		if (discordBot == null || discordBot.getJDA() == null) {
			logger.warning("Cannot initialize channels - Discord bot not available");
			return;
		}

		try {
			chatChannel = discordBot.getJDA().getTextChannelById(DiscordConfig.CHAT_CHANNEL_ID);
			statusChannel = discordBot.getJDA().getTextChannelById(DiscordConfig.STATUS_CHANNEL_ID);
			levelsChannel = discordBot.getJDA().getTextChannelById(DiscordConfig.LEVELS_CHANNEL_ID);
			advancementsChannel = discordBot.getJDA().getTextChannelById(DiscordConfig.ADVANCEMENTS_CHANNEL_ID);
			staffChannel = discordBot.getJDA().getTextChannelById(DiscordConfig.STAFF_CHANNEL_ID);

			isConnected = (chatChannel != null || statusChannel != null || levelsChannel != null ||
				advancementsChannel != null || staffChannel != null);

			if (isConnected) {
				logger.info("Discord channels initialized successfully");
			} else {
				logger.warning("No Discord channels found - check channel IDs in config");
			}

		} catch (Exception e) {
			logger.severe("Error initializing Discord channels: " + e.getMessage());
			isConnected = false;
		}
	}

	/**
	 * Start the message processing thread
	 */
	private void startProcessingThread() {
		if (running.get()) {
			return;
		}

		running.set(true);
		processingThread = new Thread(this, "DiscordMessageProcessor");
		processingThread.setDaemon(true);
		processingThread.start();
	}

	@Override
	public void run() {
		while (running.get()) {
			try {
				DiscordMessage message = messageQueue.take();
				processMessage(message);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			} catch (Exception e) {
				logger.warning("Error processing Discord message: " + e.getMessage());
			}
		}
	}

	/**
	 * Process individual Discord message
	 */
	private void processMessage(DiscordMessage message) {
		if (!isConnected || discordBot == null) {
			logger.warning("Cannot send Discord message - not connected");
			return;
		}

		TextChannel targetChannel = getChannelForType(message.getType());
		if (targetChannel == null) {
			logger.warning("No channel available for message type: " + message.getType());
			return;
		}

		try {
			String formattedMessage = formatMessage(message);

			targetChannel.sendMessage(formattedMessage)
				.queue(
					success -> logger.fine("Message sent to " + message.getType() + " channel"),
					new ErrorHandler()
						.handle(ErrorResponse.MISSING_PERMISSIONS,
							e -> logger.warning("Missing permissions for " + message.getType() + " channel"))
						.handle(ErrorResponse.UNKNOWN_CHANNEL,
							e -> logger.warning("Channel not found for " + message.getType()))
						.handle(ErrorResponse.CANNOT_SEND_TO_USER,
							e -> logger.warning("Cannot send to " + message.getType() + " channel"))
				);

		} catch (Exception e) {
			logger.warning("Error sending message to Discord: " + e.getMessage());
		}
	}

	/**
	 * Get appropriate channel for message type
	 */
	private TextChannel getChannelForType(DiscordMessage.Type type) {
		switch (type) {
			case CHAT:
				return chatChannel;
			case SERVER_STATUS:
			case SYSTEM_ANNOUNCEMENT:
				return statusChannel;
			case LEVEL_UP:
			case SKILL_PROGRESS:
				return levelsChannel;
			case ADVANCEMENT:
			case ACHIEVEMENT:
				return advancementsChannel;
			case STAFF_ALERT:
			case ADMIN_MESSAGE:
				return staffChannel;
			default:
				return statusChannel; // Default fallback
		}
	}

	/**
	 * Format message based on type
	 */
	private String formatMessage(DiscordMessage message) {
		switch (message.getType()) {
			case CHAT:
				return formatChatMessage(message);
			case SERVER_STATUS:
				return formatServerStatusMessage(message);
			case SYSTEM_ANNOUNCEMENT:
				return formatSystemMessage(message);
			case LEVEL_UP:
			case SKILL_PROGRESS:
				return formatSkillMessage(message);
			case ADVANCEMENT:
			case ACHIEVEMENT:
				return formatAchievementMessage(message);
			case STAFF_ALERT:
			case ADMIN_MESSAGE:
				return formatStaffMessage(message);
			default:
				return message.getContent();
		}
	}

	private String formatChatMessage(DiscordMessage message) {
		if (message.getUsername() != null) {
			return "**" + message.getUsername() + "**: " + message.getContent();
		}
		return message.getContent();
	}

	private String formatServerStatusMessage(DiscordMessage message) {
		String emoji = message.getContent().contains("online") ? "🟢" :
			message.getContent().contains("shutdown") ? "🔴" : "🔄";
		return emoji + " **SERVER STATUS**: " + message.getContent();
	}

	private String formatSystemMessage(DiscordMessage message) {
		return "📢 **SYSTEM**: " + message.getContent();
	}

	private String formatSkillMessage(DiscordMessage message) {
		return "⚡ **SKILL UPDATE**: " + message.getContent();
	}

	private String formatAchievementMessage(DiscordMessage message) {
		return "🏆 **ACHIEVEMENT**: " + message.getContent();
	}

	private String formatStaffMessage(DiscordMessage message) {
		return "🚨 **STAFF ALERT**: " + message.getContent();
	}

	// Public API methods for sending different types of messages

	/**
	 * Send game chat message to Discord
	 */
	public void sendGameChat(String username, String message) {
		queueMessage(new DiscordMessage(DiscordMessage.Type.CHAT, message, username));
	}

	/**
	 * Send server status update
	 */
	public void sendServerStatus(String status) {
		queueMessage(new DiscordMessage(DiscordMessage.Type.SERVER_STATUS, status));
	}

	/**
	 * Send system announcement
	 */
	public void sendSystemAnnouncement(String announcement) {
		queueMessage(new DiscordMessage(DiscordMessage.Type.SYSTEM_ANNOUNCEMENT, announcement));
	}

	/**
	 * Send level up notification
	 */
	public void sendLevelUp(String username, String skill, int level) {
		String message = username + " reached level " + level + " in " + skill + "!";
		queueMessage(new DiscordMessage(DiscordMessage.Type.LEVEL_UP, message, username));
	}

	/**
	 * Send skill progress update
	 */
	public void sendSkillProgress(String username, String skill, String progress) {
		String message = username + " - " + skill + ": " + progress;
		queueMessage(new DiscordMessage(DiscordMessage.Type.SKILL_PROGRESS, message, username));
	}

	/**
	 * Send achievement notification
	 */
	public void sendAchievement(String username, String achievement) {
		String message = username + " unlocked: " + achievement;
		queueMessage(new DiscordMessage(DiscordMessage.Type.ACHIEVEMENT, message, username));
	}

	/**
	 * Send staff alert
	 */
	public void sendStaffAlert(String alert) {
		queueMessage(new DiscordMessage(DiscordMessage.Type.STAFF_ALERT, alert));
	}

	/**
	 * Send admin message
	 */
	public void sendAdminMessage(String message) {
		queueMessage(new DiscordMessage(DiscordMessage.Type.ADMIN_MESSAGE, message));
	}

	/**
	 * Queue message for processing
	 */
	private void queueMessage(DiscordMessage message) {
		if (!messageQueue.offer(message)) {
			logger.warning("Discord message queue full - dropping message: " + message.getContent());
		}
	}

	/**
	 * Send immediate message (bypasses queue - use sparingly)
	 */
	public void sendImmediate(DiscordMessage.Type type, String content) {
		sendImmediate(type, content, null);
	}

	public void sendImmediate(DiscordMessage.Type type, String content, String username) {
		DiscordMessage message = new DiscordMessage(type, content, username);
		processMessage(message);
	}

	/**
	 * Check if service is connected and functional
	 */
	public boolean isConnected() {
		return isConnected && discordBot != null && discordBot.getJDA() != null;
	}

	/**
	 * Get connection status information
	 */
	public String getStatus() {
		if (!isConnected()) {
			return "Discord messaging: Disconnected";
		}

		int availableChannels = 0;
		if (chatChannel != null) availableChannels++;
		if (statusChannel != null) availableChannels++;
		if (levelsChannel != null) availableChannels++;
		if (advancementsChannel != null) availableChannels++;
		if (staffChannel != null) availableChannels++;

		return "Discord messaging: Connected (" + availableChannels + "/5 channels available, " +
			messageQueue.size() + " messages queued)";
	}

	/**
	 * Shutdown the messaging service
	 */
	public void shutdown() {
		running.set(false);

		if (processingThread != null && processingThread.isAlive()) {
			processingThread.interrupt();
			try {
				processingThread.join(5000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		// Process remaining messages
		while (!messageQueue.isEmpty()) {
			try {
				DiscordMessage message = messageQueue.poll();
				if (message != null) {
					processMessage(message);
				}
			} catch (Exception e) {
				// Ignore errors during shutdown
			}
		}

		logger.info("Discord messaging service shut down");
	}

	/**
	 * Reconnect to Discord (if connection was lost)
	 */
	public void reconnect() {
		if (discordBot != null) {
			initializeChannels();
			if (!running.get()) {
				startProcessingThread();
			}
		}
	}
}



