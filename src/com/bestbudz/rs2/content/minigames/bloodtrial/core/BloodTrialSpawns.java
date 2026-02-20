package com.bestbudz.rs2.content.minigames.bloodtrial.core;

import com.bestbudz.core.util.Utility;
import static com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialSpawnLOC.SPAWN_LOCATIONS;
import com.bestbudz.rs2.entity.Location;

public class BloodTrialSpawns
{
	private static final boolean[] usedSpawns = new boolean[SPAWN_LOCATIONS.length];

	public static Location getRandomSpawnLocation(int z) {

		if (areAllSpawnsUsed()) {
			resetUsedSpawns();
		}

		int attempts = 0;
		int index;

		do {
			index = Utility.randomNumber(SPAWN_LOCATIONS.length);
			attempts++;
		} while (usedSpawns[index] && attempts < SPAWN_LOCATIONS.length * 2);

		usedSpawns[index] = true;

		Location spawn = SPAWN_LOCATIONS[index];
		return new Location(spawn.getX(), spawn.getY(), z);
	}

	public static void resetUsedSpawns() {
		for (int i = 0; i < usedSpawns.length; i++) {
			usedSpawns[i] = false;
		}
	}

	private static boolean areAllSpawnsUsed() {
		for (boolean used : usedSpawns) {
			if (!used) return false;
		}
		return true;
	}
}
