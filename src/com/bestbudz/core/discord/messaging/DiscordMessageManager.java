// DiscordMessageManager.java (Static utility class for easy access)
package com.bestbudz.core.discord.messaging;

/**
 * Static utility class for easy access to Discord messaging from anywhere in the codebase
 */
public class DiscordMessageManager {

	/**
	 * Send game chat to Discord
	 */
	public static void sendGameChat(String username, String message) {
		DiscordMessageService.getInstance().sendGameChat(username, message);
	}

	/**
	 * Send server startup notification
	 */
	public static void announceServerStartup() {
		DiscordMessageService.getInstance().sendServerStatus("Server is now online! Players can connect.");
	}

	/**
	 * Send server shutdown notification
	 */
	public static void announceServerShutdown() {
		DiscordMessageService.getInstance().sendServerStatus("Server is shutting down... Please reconnect in a few minutes.");
	}

	/**
	 * Send system announcement
	 */
	public static void announceSystem(String message) {
		DiscordMessageService.getInstance().sendSystemAnnouncement(message);
	}

	/**
	 * Send level up notification
	 */
	public static void announceLevelUp(String username, String skill, int level) {
		DiscordMessageService.getInstance().sendLevelUp(username, skill, level);
	}

	/**
	 * Send achievement notification
	 */
	public static void announceAchievement(String username, String achievement) {
		DiscordMessageService.getInstance().sendAchievement(username, achievement);
	}

	/**
	 * Send staff alert
	 */
	public static void alertStaff(String message) {
		DiscordMessageService.getInstance().sendStaffAlert(message);
	}

	/**
	 * Check if Discord messaging is available
	 */
	public static boolean isAvailable() {
		return DiscordMessageService.getInstance().isConnected();
	}

	/**
	 * Get status of Discord messaging
	 */
	public static String getStatus() {
		return DiscordMessageService.getInstance().getStatus();
	}

	public static void announceGameMessage (String message){
		DiscordMessageService.getInstance().sendGameMessage(message);
	}
}