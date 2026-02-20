package com.bestbudz.rs2.content.profession.bankstanding;

import com.bestbudz.rs2.entity.Location;

public class BankStandingConstants {

	public static final int[] BANK_OBJECT_IDS = {
		10517, 10518,
		11744, 11748,
		2693,
		4483,
		24101,
		14367,
	};

	public static final int[] BANK_NPC_IDS = {
		394,
		395,
	};

	private static final boolean[] BANK_OBJECT_LOOKUP = new boolean[30000];
	private static final boolean[] BANK_NPC_LOOKUP = new boolean[10000];

	static {

		for (int objectId : BANK_OBJECT_IDS) {
			if (objectId < BANK_OBJECT_LOOKUP.length) {
				BANK_OBJECT_LOOKUP[objectId] = true;
			}
		}

		for (int npcId : BANK_NPC_IDS) {
			if (npcId < BANK_NPC_LOOKUP.length) {
				BANK_NPC_LOOKUP[npcId] = true;
			}
		}
	}

	public static int getManhattanDistance(Location loc1, Location loc2) {
		if (loc1 == null || loc2 == null) {
			return Integer.MAX_VALUE;
		}

		int deltaX = Math.abs(loc1.getX() - loc2.getX());
		int deltaY = Math.abs(loc1.getY() - loc2.getY());

		return deltaX + deltaY;
	}

}
