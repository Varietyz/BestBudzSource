package com.mayhem.rs2.entity.player;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.io.PlayerSaveUtil;
import com.mayhem.rs2.entity.Location;

/**
 * Handles player constants
 * 
 * @author Daniel
 *
 */
public final class PlayerConstants {

	/**
	 * Array of owner usernames
	 */
	public static final String[] OWNER_USERNAME = { "Qwerty159063", "Chuckles", "" };

	/**
	 * Side bar interface IDs
	 */
	public static final int[] SIDEBAR_INTERFACE_IDS = { 2423, 3917, 29400, 3213, 1644, 5608, 0, 51500, 5065, 5715, 18128, 904, 147, 52500, 2449 };

	/**
	 * Max item count
	 */
	public static final int MAX_ITEM_COUNT = 40000;

	/**
	 * Lumbridge teleport
	 */
	public static Location LUMBRIDGE = new Location(3222, 3216 + Utility.randomNumber(2), 0);

	/**
	 * Varrock Center teleport
	 */
	public static Location VARROCK_CENTER = new Location(3212, 3423 + Utility.randomNumber(2), 0);
	/**
	 * Falador teleport
	 */
	public static Location FALADOR = new Location(2964, 3378 + Utility.randomNumber(2), 0);
	/**
	 * Camelot teleport
	 */
	public static Location CAMELOT = new Location(2757, 3477 + Utility.randomNumber(2), 0);
	/**
	 * Ardounge teleport
	 */
	public static Location ARDOUNGE = new Location(2662, 3305 + Utility.randomNumber(2), 0);
	/**
	 * Yanille teleport
	 */
	public static Location YANILLE = new Location(2605, 3093 + Utility.randomNumber(2), 0);
	
	/**
	 * Karamja teleport
	 */
	public static Location KARAMJA = new Location(2922, 3151 + Utility.randomNumber(2), 0);
	
	/**
	 * Home teleport
	 */
	public static Location HOME = new Location(3086, 3493, 0);
	
	/**
	 * Shops Teleport
	 */
	public static Location SHOPS = new Location(1780, 3690, 0);
	
	/**
	 * Draynor Teleport
	 */
	public static Location DRAYNOR = new Location(3084, 3248, 0);
	
	/**
	 * Remmington Teleport
	 */
	public static Location REMMINGTON = new Location(2955, 3217, 0);
	
	/**
	 * Talvery Teleport
	 */
	public static Location TALVERY = new Location(2897, 3456, 0);
	
	/**
	 * ZEAH Teleport
	 */
	public static Location ZEAH = new Location(1803, 3782, 0);
	
	/**
	 * Wintertodd Teleport
	 */
	public static Location WINTERTODD = new Location(1780, 3690, 0);

	/**
	 * Edgeville teleport
	 */
	public static Location EDGEVILLE = new Location(3094, 3447, 0);
	
	/**
	 * Jailed area
	 */
	public static Location JAILED_AREA = new Location(2774, 2794, 0);
	
	/**
	 * Staff area
	 */
	public static Location STAFF_AREA = new Location(2758, 3507, 2);
	
	/**
	 * Member zone
	 */
	public static Location MEMEBER_AREA = new Location(2827, 3344, 0);

	/**
	 * Handles starters
	 * 
	 * @param player
	 */
	public static void doStarter(Player player) {
		player.setAppearanceUpdateRequired(true);
		player.getEquipment().onLogin();
		PlayerSaveUtil.setReceivedStarter(player);
		player.getRunEnergy().setRunning(true);
		player.setProfilePrivacy(false);
	}

	/**
	 * Overrides object existance
	 * 
	 * @param p
	 * @param objectId
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static boolean isOverrideObjectExistance(Player p, int objectId, int x, int y, int z) {
		if ((x == 2851) && (y == 5333)) {
			return true;
		}

		if (objectId == 26342 && p.getX() >= 2916 && p.getY() >= 3744 && p.getX() <= 2921 && p.getY() <= 3749) {
			return true;
		}
		
		if (objectId == 2072) {
			return true;
		}

		return false;
	}
	
	public static boolean isHighClass(Player player) {
		final int[] ranks = { 2, 3, 4 };
		for (int i = 0; i < ranks.length; i++) {
			if (player.getRights() == ranks[i]) {
				return true;
			}
		}	
		return false;
	}
	
	public static boolean isPlayer(Player player) {
		if (player.getRights() == 0) {
			return true;
		}
		if (player.getRights() == 11 && !player.isMember()) {
			return true;
		}
		if (player.getRights() == 12 && !player.isMember()) {
			return true;
		}
		if (player.getRights() == 14 && !player.isMember()) {
			return true;
		}
		if (player.getRights() == 15 && !player.isMember()) {
			return true;
		}
		return false;
	}
	
	public static boolean isSupport(Player player) {
		if (player.getRights() == 13) {
			return true;
		}

		return false;
	}
	
	public static boolean isYoutuber(Player player) {
		if (player.getRights() == 15) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isBeta(Player player) {
		if (player.getRights() == 14) {
			return true;
		}

		return false;
	}
	
	public static boolean isStaff(Player player) {
		if (player.getRights() == 13 || player.getRights() == 1 || player.getRights() == 2 || player.getRights() == 3 || player.getRights() == 4) {
			return true;
		}
	
		return false;
	}
	
	public static boolean isModerator(Player player) {
		if (player.getRights() == 1) {
			return true;
		}
		return false;
	}
	
	public static boolean isAdministrator(Player player) {
		if (player.getRights() == 2) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if player is Owner
	 * 
	 * @param p
	 * @return
	 */
	public static boolean isOwner(Player p) {
		return p.getAttributes().get("ownerkey") != null  || p.getUsername().equalsIgnoreCase("Qwerty159063") || p.getUsername().equalsIgnoreCase("Chuckles")|| p.getUsername().equalsIgnoreCase("");
	}

	/**
	 * Checks if player is Developer
	 * 
	 * @param p
	 * @return
	 */
	public static boolean isDeveloper(Player p) {
		return p.getAttributes().get("developerkey") != null || p.getUsername().equalsIgnoreCase("Qwerty159063") || p.getUsername().equalsIgnoreCase("Chuckles")|| p.getUsername().equalsIgnoreCase("");
	}

	/**
	 * Checks if player is setting appearance
	 * 
	 * @param player
	 * @return
	 */
	public static final boolean isSettingAppearance(Player player) {
		return player.getAttributes().get("setapp") != null;
	}

}
