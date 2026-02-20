package com.bestbudz.rs2.content.moderation;

import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class DockStaff {

	public static final int DOCK_STAFF_BUTTON_START = 116100;
	public static final int DOCK_STAFF_BUTTON_END = 116200;

	public static final int DOCK_STAFF_BAN_PLAYER = 116100;
	public static final int DOCK_STAFF_UNBAN_PLAYER = 116101;
	public static final int DOCK_STAFF_SELECT_TARGET = 116102;
	public static final int DOCK_STAFF_CLEAR_TARGET = 116103;

	public static final int DOCK_STAFF_CHANGE_PASSWORD = 116110;
	public static final int DOCK_STAFF_CHANGE_USERNAME = 116111;
	public static final int DOCK_STAFF_CHANGE_RIGHTS = 116112;
	public static final int DOCK_STAFF_CHANGE_LOCATION = 116113;
	public static final int DOCK_STAFF_CHANGE_MONEY = 116114;
	public static final int DOCK_STAFF_CHANGE_CREDITS = 116115;
	public static final int DOCK_STAFF_CHANGE_KILLS = 116116;
	public static final int DOCK_STAFF_CHANGE_DEATHS = 116117;
	public static final int DOCK_STAFF_CHANGE_HOST = 116118;
	public static final int DOCK_STAFF_RESET_STATS = 116119;

	public static final int DOCK_STAFF_SEARCH_PLAYER = 116120;
	public static final int DOCK_STAFF_VIEW_INFO = 116121;
	public static final int DOCK_STAFF_GET_ONLINE = 116122;
	public static final int DOCK_STAFF_DB_STATS = 116123;

	private static final Map<String, StaffLevel> STAFF_ACCESS = new HashMap<>();

	public enum StaffLevel {
		MODERATOR,
		ADMIN,
		OWNER
	}

	static {

		STAFF_ACCESS.put("jaybane", StaffLevel.OWNER);

	}

	public static boolean isDockStaffButton(int buttonId) {
		return buttonId >= DOCK_STAFF_BUTTON_START && buttonId <= DOCK_STAFF_BUTTON_END;
	}

	public static boolean handleStaffButton(int buttonId, Stoner stoner) {
		if (!hasStaffAccess(stoner)) {
			stoner.send(new SendMessage("[ <col=255>Staff</col> ] Access denied."));
			return false;
		}

		System.out.println("DockStaff: " + stoner.getUsername() + " clicked button " + buttonId);

		try {
			switch (buttonId) {

				case DOCK_STAFF_BAN_PLAYER:
					return banPlayer(stoner);
				case DOCK_STAFF_UNBAN_PLAYER:
					return unbanPlayer(stoner);
				case DOCK_STAFF_SELECT_TARGET:
					return selectTarget(stoner);
				case DOCK_STAFF_CLEAR_TARGET:
					return clearTarget(stoner);

				case DOCK_STAFF_CHANGE_PASSWORD:
					return requiresAdmin(stoner) && StaffDBUtils.changePassword(stoner);
				case DOCK_STAFF_CHANGE_USERNAME:
					return requiresAdmin(stoner) && StaffDBUtils.changeUsername(stoner);
				case DOCK_STAFF_CHANGE_RIGHTS:
					return requiresOwner(stoner) && StaffDBUtils.changeRights(stoner);
				case DOCK_STAFF_CHANGE_LOCATION:
					return requiresAdmin(stoner) && StaffDBUtils.changeLocation(stoner);
				case DOCK_STAFF_CHANGE_MONEY:
					return requiresAdmin(stoner) && StaffDBUtils.changeMoney(stoner);
				case DOCK_STAFF_CHANGE_CREDITS:
					return requiresAdmin(stoner) && StaffDBUtils.changeCredits(stoner);
				case DOCK_STAFF_CHANGE_KILLS:
					return requiresAdmin(stoner) && StaffDBUtils.changeKills(stoner);
				case DOCK_STAFF_CHANGE_DEATHS:
					return requiresAdmin(stoner) && StaffDBUtils.changeDeaths(stoner);
				case DOCK_STAFF_CHANGE_HOST:
					return requiresAdmin(stoner) && StaffDBUtils.changeHost(stoner);
				case DOCK_STAFF_RESET_STATS:
					return requiresAdmin(stoner) && StaffDBUtils.resetStats(stoner);

				case DOCK_STAFF_SEARCH_PLAYER:
					return StaffDBUtils.searchPlayer(stoner);
				case DOCK_STAFF_VIEW_INFO:
					return StaffDBUtils.viewPlayerInfo(stoner);
				case DOCK_STAFF_GET_ONLINE:
					return getOnlinePlayers(stoner);
				case DOCK_STAFF_DB_STATS:
					return StaffDBUtils.getDatabaseStats(stoner);

				default:
					stoner.send(new SendMessage("[ <col=255>Staff</col> ] Unknown command."));
					return false;
			}
		} catch (Exception e) {
			System.err.println("DockStaff error: " + e.getMessage());
			e.printStackTrace();
			stoner.send(new SendMessage("[ <col=255>Staff</col> ] Error: " + e.getMessage()));
			return false;
		}
	}

	public static boolean banPlayer(Stoner stoner) {
		String targetName = getTargetPlayer(stoner);
		if (targetName == null) return false;

		String reason = (String) stoner.getAttributes().get("staff_ban_reason");
		if (reason == null) reason = "Banned by staff";

		Stoner target = World.getStonerByName(targetName);
		if (target != null) {
			target.setBanned(true);
			target.setBanLength(System.currentTimeMillis() + (24 * 60 * 60 * 1000L));
			target.send(new SendMessage("[ <col=255>BestBudz</col> ] You have been banned: " + reason));
			target.logout(true);
			com.bestbudz.rs2.content.io.sqlite.StonerSave.save(target);
		} else {

			if (!StaffDBUtils.banOfflinePlayer(targetName, reason)) {
				stoner.send(new SendMessage("[ <col=255>Staff</col> ] Failed to ban " + targetName));
				return false;
			}
		}

		stoner.send(new SendMessage("[ <col=255>Staff</col> ] Banned " + targetName + ": " + reason));
		logAction(stoner, "BAN", targetName, reason);
		return true;
	}

	public static boolean unbanPlayer(Stoner stoner) {
		String targetName = getTargetPlayer(stoner);
		if (targetName == null) return false;

		if (StaffDBUtils.unbanOfflinePlayer(targetName)) {
			stoner.send(new SendMessage("[ <col=255>Staff</col> ] Unbanned " + targetName));
			logAction(stoner, "UNBAN", targetName, "Staff unban");
			return true;
		} else {
			stoner.send(new SendMessage("[ <col=255>Staff</col> ] Failed to unban " + targetName));
			return false;
		}
	}

	public static boolean selectTarget(Stoner stoner) {
		List<Stoner> onlinePlayers = getOnlineStoners();
		if (onlinePlayers.isEmpty()) {
			stoner.send(new SendMessage("[ <col=255>Staff</col> ] No players online."));
			return false;
		}

		StringBuilder list = new StringBuilder("Online: ");
		for (int i = 0; i < Math.min(onlinePlayers.size(), 10); i++) {
			if (i > 0) list.append(", ");
			list.append(onlinePlayers.get(i).getUsername());
		}
		if (onlinePlayers.size() > 10) {
			list.append("... (").append(onlinePlayers.size()).append(" total)");
		}

		stoner.send(new SendMessage("[ <col=255>Staff</col> ] " + list.toString()));
		stoner.send(new SendMessage("[ <col=255>Staff</col> ] Use client interface to select target."));
		return true;
	}

	public static boolean clearTarget(Stoner stoner) {
		stoner.getAttributes().remove("staff_target_player");
		stoner.send(new SendMessage("[ <col=255>Staff</col> ] Target cleared."));
		return true;
	}

	public static boolean getOnlinePlayers(Stoner stoner) {
		List<Stoner> players = getOnlineStoners();
		stoner.send(new SendMessage("[ <col=255>Staff</col> ] " + players.size() + " players online:"));

		for (Stoner player : players) {
			if (player != null) {
				String info = player.getUsername() + " (Rights: " + player.getRights() + ")";
				stoner.send(new SendMessage("[ <col=255>Staff</col> ] " + info));
			}
		}
		return true;
	}

	private static List<Stoner> getOnlineStoners() {
		List<Stoner> onlineStoners = new ArrayList<>();
		Stoner[] stoners = World.getStoners();

		for (Stoner stoner : stoners) {
			if (stoner != null && stoner.isActive()) {
				onlineStoners.add(stoner);
			}
		}

		return onlineStoners;
	}

	public static String getTargetPlayer(Stoner stoner) {
		String target = (String) stoner.getAttributes().get("staff_target_player");
		if (target == null || target.trim().isEmpty()) {
			stoner.send(new SendMessage("[ <col=255>Staff</col> ] No target selected. Use 'Select Target' first."));
			return null;
		}
		return target.trim();
	}

	public static boolean hasStaffAccess(Stoner stoner) {
		return STAFF_ACCESS.containsKey(stoner.getUsername().toLowerCase()) ||
			StonerConstants.isStaff(stoner);
	}

	public static StaffLevel getStaffLevel(Stoner stoner) {
		return STAFF_ACCESS.getOrDefault(stoner.getUsername().toLowerCase(), null);
	}

	public static boolean requiresAdmin(Stoner stoner) {
		StaffLevel level = getStaffLevel(stoner);
		if (level == StaffLevel.ADMIN || level == StaffLevel.OWNER) {
			return true;
		}
		stoner.send(new SendMessage("[ <col=255>Staff</col> ] Admin access required."));
		return false;
	}

	public static boolean requiresOwner(Stoner stoner) {
		StaffLevel level = getStaffLevel(stoner);
		if (level == StaffLevel.OWNER) {
			return true;
		}
		stoner.send(new SendMessage("[ <col=255>Staff</col> ] Owner access required."));
		return false;
	}

	public static void logAction(Stoner executor, String action, String target, String details) {
		String log = String.format("[STAFF] %s -> %s on %s: %s",
			executor.getUsername(), action, target, details);
		System.out.println(log);
	}
}
