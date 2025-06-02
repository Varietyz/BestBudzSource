package com.bestbudz.rs2.content.profession.bankstanding;

import com.bestbudz.rs2.entity.Location;

/**
 * Constants and utility methods for Bank Standing skill
 */
public class BankStandingConstants {

	// Bank object IDs - expanded list with more common bank objects
	public static final int[] BANK_OBJECT_IDS = {
		10517, 10518, // Standard bank booths
		11744, 11748, // Bank chests
		2693,         // Bank booth (basic)
		4483,         // Bank booth (Catherby)
		24101,        // Bank booth (Edgeville)
		14367,        // Bank booth (Port Khazard)
	};

	// Bank NPC IDs - expanded list with more bankers
	public static final int[] BANK_NPC_IDS = {
		394,  // Banker (male)
		395,  // Banker (female)
	};

	// Bank standing range (tiles)
	public static final int BANK_STANDING_RANGE = 2;

	// XP multiplier when bank standing
	public static final double BANK_STANDING_XP_MULTIPLIER = 1.5;

	// Performance optimization - cache for bank object/NPC lookups
	private static final boolean[] BANK_OBJECT_LOOKUP = new boolean[30000];
	private static final boolean[] BANK_NPC_LOOKUP = new boolean[10000];

	// Static initialization for lookup arrays
	static {
		// Initialize bank object lookup
		for (int objectId : BANK_OBJECT_IDS) {
			if (objectId < BANK_OBJECT_LOOKUP.length) {
				BANK_OBJECT_LOOKUP[objectId] = true;
			}
		}

		// Initialize bank NPC lookup
		for (int npcId : BANK_NPC_IDS) {
			if (npcId < BANK_NPC_LOOKUP.length) {
				BANK_NPC_LOOKUP[npcId] = true;
			}
		}
	}

	/**
	 * Fast lookup for bank objects using cached array
	 */
	public static boolean isBankObject(int objectId) {
		return objectId >= 0 && objectId < BANK_OBJECT_LOOKUP.length && BANK_OBJECT_LOOKUP[objectId];
	}

	/**
	 * Fast lookup for bank NPCs using cached array
	 */
	public static boolean isBankNPC(int npcId) {
		return npcId >= 0 && npcId < BANK_NPC_LOOKUP.length && BANK_NPC_LOOKUP[npcId];
	}

	/**
	 * Check if two locations are within specified range
	 */
	public static boolean isWithinRange(Location loc1, Location loc2, int range) {
		if (loc1 == null || loc2 == null) {
			return false;
		}

		int deltaX = Math.abs(loc1.getX() - loc2.getX());
		int deltaY = Math.abs(loc1.getY() - loc2.getY());

		return deltaX <= range && deltaY <= range;
	}

	/**
	 * Get Chebyshev distance between two locations (max of X and Y deltas)
	 */
	public static int getDistance(Location loc1, Location loc2) {
		if (loc1 == null || loc2 == null) {
			return Integer.MAX_VALUE;
		}

		int deltaX = Math.abs(loc1.getX() - loc2.getX());
		int deltaY = Math.abs(loc1.getY() - loc2.getY());

		return Math.max(deltaX, deltaY); // Chebyshev distance
	}

	/**
	 * Get Manhattan distance between two locations (sum of X and Y deltas)
	 */
	public static int getManhattanDistance(Location loc1, Location loc2) {
		if (loc1 == null || loc2 == null) {
			return Integer.MAX_VALUE;
		}

		int deltaX = Math.abs(loc1.getX() - loc2.getX());
		int deltaY = Math.abs(loc1.getY() - loc2.getY());

		return deltaX + deltaY;
	}

	/**
	 * Check if a location is within bank standing range of any known bank location
	 * This could be used for pre-calculated bank positions if needed
	 */
	public static boolean isNearKnownBankLocation(Location playerLoc) {
		// This could be expanded with hardcoded bank locations for better performance
		// For now, we rely on the dynamic object/NPC checking in BankStanding.java
		return false;
	}

	/**
	 * Get recommended bank standing locations (could be used for hints/guides)
	 */
	public static Location[] getRecommendedBankLocations() {
		return new Location[] {
			new Location(3092, 3244, 0), // Edgeville bank
			new Location(3095, 3491, 0), // Varrock West bank
			new Location(3253, 3420, 0), // Varrock East bank
			new Location(2946, 3368, 0), // Falador East bank
			new Location(3012, 3355, 0), // Falador West bank
			new Location(3208, 3220, 0), // Lumbridge bank
			new Location(3270, 3166, 0), // Al Kharid bank
			new Location(2724, 3492, 0), // Seers' Village bank
			new Location(2612, 3331, 0), // Ardougne North bank
			new Location(2655, 3283, 0), // Ardougne South bank
		};
	}
}