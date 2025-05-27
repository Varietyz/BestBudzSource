package com.bestbudz;

/**
 * The game settings for the server
 * 
 * @author BestBudz Team
 */
public class BestbudzConstants {

	/**
	 * The version of BestBudz
	 */
	public static final double VERSION = 1;

	/**
	 * Checks if in development mode
	 */
	public static boolean DEV_MODE = false;

	/**
	 * Walking check
	 */
	public static boolean WALK_CHECK = true;

	/**
	 * Checks if the world is staff only
	 */
	public static boolean STAFF_ONLY = false;

	/**
	 * Check to see if double experience is enabled
	 */
	public static final boolean doubleExperience = true;

	/**
	 * Strings that are classified as bad
	 */
	public static final String[] BAD_STRINGS = { "fag", "f4g", "faggot", "nigger", "fuck", "bitch", "whore", "slut", "gay", "lesbian", "scape", ".net", ".com", ".org", "vagina", "dick", "cock", "penis", "hoe", "soulsplit", "ikov", "retard", "cunt", };

	/**
	 * Holds all usernames that can not be used
	 */
	public static final String[] BAD_USERNAMES = { "mod", "admin", "moderator", "administrator", "owner", "m0d", "adm1n", "0wner", "retard", "Nigga", "nigger", "n1gger", "n1gg3r", "nigg3r", "n1gga", "cock", "faggot", "fag", "anus", "arse", "fuck", "bastard", "bitch", "cunt", "chode", "damn", "dick", "faggit", "gay", "homo", "jizz", "lesbian", "negro", "pussy", "penis", "queef", "twat", "titty", "whore", "b1tch" };

	/**
	 * Strings that may not be used for yelltitles
	 */
	public static final String[] BAD_TITLES = { "owner", "0wner", "own3r", "0wn3r", "admin", "administrator", "dev", "developer", "mod", "m0d", "moderator", "m0derator", };

	/**
	 * All the staff members
	 */
	public final static String[] STAFF_MEMBERS = { "jaybane", "bestbudz", };

	/**
	 * Login Messages for stoners
	 */
	public static final String[] LOGIN_MESSAGES = { "There are currently@dre@ /s/ </col> Stoners chillin on the server.", "Be sure to spark one up at 4:20!", };

	/**
	 * Messages for identifying items in the DropTable interface
	 */
	public static final String[] ITEM_IDENTIFICATION_MESSAGES = { "Dis /s/ i like, man!", };

	/**
	 * Item dismantling data
	 */
	public static final int[][] ITEM_DISMANTLE_DATA = { { 12436, 6585 }, // Amulet of fury (or)
			{ 12807, 11926 }, // Odium ward (or)
			{ 12806, 11924 }, // Malediction ward (or)
			{ 12797, 11920 }, // Dragon pickaxe (or)
	};

	/**
	 * Holds all doors that can not be opened
	 */
	public static final int[] BLOCKED_DOORS = { 26502, 26503, 26504, 26505, 24306, 24309, 26760, 2104, 2102, 2100, 26461, 4799, 7129 };

}
