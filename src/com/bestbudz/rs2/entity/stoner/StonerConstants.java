package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.io.StonerSaveUtil;
import com.bestbudz.rs2.entity.Location;

/**
 * Handles stoner constants
 * 
 * @author JayBane
 *
 */
public final class StonerConstants {

	/**
	 * Array of owner usernames
	 */
	public static final String[] OWNER_USERNAME = { "jaybane", "bestbudz", "ikushz" };

	/**
	 * Side bar interface IDs
	 */
	public static final int[] SIDEBAR_INTERFACE_IDS = { 2423, 3917, 29400, 3213, 1644, 5608, 0, 51500, 5065, 5715, 18128, 904, 147, 52500, 2449 };

	/**
	 * Max item count
	 */
	public static final int MAX_ITEM_COUNT = 21411;

	/**
	 * Lumbridge teleport
	 */
	public static Location LUMBRIDGE = new Location(3222, 3216 + Utility.randomNumber(2), 0);

	/**
	 * Home teleport
	 */
	public static Location HOME = new Location(3434, 2890, 0);

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
	 * @param stoner
	 */
	public static void doStarter(Stoner stoner) {
	stoner.setAppearanceUpdateRequired(true);
	stoner.getEquipment().onLogin();
	StonerSaveUtil.setReceivedStarter(stoner);
	stoner.getRunEnergy().setRunning(true);
	stoner.setProfilePrivacy(false);
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
	public static boolean isOverrideObjectExistance(Stoner p, int objectId, int x, int y, int z) {
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

	public static boolean isHighClass(Stoner stoner) {
	final int[] ranks = { 2, 3, 4 };
	for (int i = 0; i < ranks.length; i++) {
		if (stoner.getRights() == ranks[i]) {
			return true;
		}
	}
	return false;
	}

	public static boolean isStoner(Stoner stoner) {
	if (stoner.getRights() == 0) {
		return true;
	}
	return false;
	}

	public static boolean isStaff(Stoner stoner) {
	if (stoner.getRights() == 1 || stoner.getRights() == 2 || stoner.getRights() == 3 || stoner.getRights() == 4) {
		return true;
	}

	return false;
	}

	public static boolean isModerator(Stoner stoner) {
	if (stoner.getRights() == 1) {
		return true;
	}
	return false;
	}

	public static boolean isAdministrator(Stoner stoner) {
	if (stoner.getRights() == 2) {
		return true;
	}
	return false;
	}

	/**
	 * Checks if stoner is Owner
	 * 
	 * @param p
	 * @return
	 */
	public static boolean isOwner(Stoner p) {
	return p.getAttributes().get("ownerkey") != null || p.getUsername().equalsIgnoreCase("jaybane");
	}

	/**
	 * Checks if stoner is Developer
	 * 
	 * @param p
	 * @return
	 */
	public static boolean isDeveloper(Stoner p) {
	return p.getAttributes().get("developerkey") != null || p.getUsername().equalsIgnoreCase("jaybane") || p.getUsername().equalsIgnoreCase("bestbudz") || p.getUsername().equalsIgnoreCase("seba");
	}

	/**
	 * Checks if stoner is setting appearance
	 * 
	 * @param stoner
	 * @return
	 */
	public static final boolean isSettingAppearance(Stoner stoner) {
	return stoner.getAttributes().get("setapp") != null;
	}

}
