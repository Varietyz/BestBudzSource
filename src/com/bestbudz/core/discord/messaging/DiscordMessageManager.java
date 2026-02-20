
package com.bestbudz.core.discord.messaging;

public class DiscordMessageManager {

	public static void sendGameChat(String username, String message) {
		DiscordMessageService.getInstance().sendGameChat(username, message);
	}

	public static void announceServerStartup() {
		DiscordMessageService.getInstance().sendServerStatus("Server is now online! Players can connect.");
	}

	public static void announceServerShutdown() {
		DiscordMessageService.getInstance().sendServerStatus("Server is shutting down... Please reconnect in a few minutes.");
	}

	public static void announceSystem(String message) {
		DiscordMessageService.getInstance().sendSystemAnnouncement(message);
	}

	public static void announceLevelUp(String username, String skill, int level) {
		DiscordMessageService.getInstance().sendLevelUp(username, skill, level);
	}

	public static void announceAchievement(String username, String achievement) {
		DiscordMessageService.getInstance().sendAchievement(username, achievement);
	}

	public static void alertStaff(String message) {
		DiscordMessageService.getInstance().sendStaffAlert(message);
	}

	public static boolean isAvailable() {
		return DiscordMessageService.getInstance().isConnected();
	}

	public static String getStatus() {
		return DiscordMessageService.getInstance().getStatus();
	}

	public static void announceGameMessage (String message){
		DiscordMessageService.getInstance().sendGameMessage(message);
	}
}
