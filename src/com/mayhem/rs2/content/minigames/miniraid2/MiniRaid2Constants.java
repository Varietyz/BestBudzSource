package com.mayhem.rs2.content.minigames.miniraid2;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.minigames.miniraid2.MainMonster2;
import com.mayhem.rs2.content.minigames.miniraid2.Monster11;
import com.mayhem.rs2.content.minigames.miniraid2.Monster22;
import com.mayhem.rs2.content.minigames.miniraid2.Monster33;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.mob.Mob;

/**
 * MiniRaid 2 constants
 * @author Mod Divine
 *
 */
public class MiniRaid2Constants {

	/**
	 * Starting location coordinates
	 */
	public static final int START_X = 1680, START_Y = 9901, START_X_MOD = 1, START_Y_MOD = 1;

	/**
	 * Main Monster IDs
	 */
	public static final int[] MAIN_MONSTER2_IDS = { 7605 };

	/**
	 * Main Monster spawn locations
	 */
	public static final Location[] MAIN_MONSTER2_SPAWN_LOCATIONS = { new Location(1695, 9887) };

	/**
	 * Monster 3 IDs
	 */
	public static final int[] MONSTER33 = { 7421 };

	/**
	 * Monster 1 IDs
	 */
	public static final int[] MONSTER11 = { 7421 };

	/*
	 * Monster 2 IDs
	 */
	public static final int[] MONSTER22 = { 7421 };


	public static final int[][] MONSTERS2 = { MONSTER11, MONSTER22, MONSTER33 };

	/**
	 * Gets a random  location
	 * @param z
	 * @return
	 */
	public static Location getRandomRaidLocation(int z) {
		return new Location(START_X + Utility.randomNumber(START_X_MOD), START_Y + Utility.randomNumber(START_Y_MOD));
	}

	/**
	 * Gets random monsters
	 * @param l
	 * @param game
	 * @param main monster
	 * @return
	 */
	public static Mob getRandomMonster(Location l, MiniRaid2Game game, MainMonster2 mainmonster2) {
		int r = Utility.randomNumber(MONSTERS2.length);
		final int id = MONSTERS2[r][Utility.randomNumber(MONSTERS2[r].length)];

		for (int i : MONSTER33) {
			if (id == i) {
				return new Monster33(l, game);
			}
		}

		for (int i : MONSTER11) {
			if (id == i) {
				return new Monster11(l, game);
			}
		}

		for (int i : MONSTER22) {
			if (id == i) {
				return new Monster22(l, game, mainmonster2);
			}
		}

		return new Monsters2(game, id, l) {

			@Override
			public void tick() {
			}

		};
	}
}

