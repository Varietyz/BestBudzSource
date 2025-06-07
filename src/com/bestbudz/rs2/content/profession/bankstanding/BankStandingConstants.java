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

}