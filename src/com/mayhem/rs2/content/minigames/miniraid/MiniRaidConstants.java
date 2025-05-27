package com.mayhem.rs2.content.minigames.miniraid;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.minigames.miniraid.MainMonster;
import com.mayhem.rs2.content.minigames.miniraid.Monster1;
import com.mayhem.rs2.content.minigames.miniraid.Monster2;
import com.mayhem.rs2.content.minigames.miniraid.Monster3;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.mob.Mob;

/**
 * MiniRaid constants
 * @author Mod Divine
 *
 */
public class MiniRaidConstants {

	/**
	 * Starting location coordinates
	 */
	public static final int START_X = 3014, START_Y = 5242, START_X_MOD = 1, START_Y_MOD = 1;

	/**
	 * Main Monster IDs
	 */
	public static final int[] MAIN_MONSTER_IDS = { 7530 };

	/**
	 * Main Monster spawn locations
	 */
	public static final Location[] MAIN_MONSTER_SPAWN_LOCATIONS = { new Location(3028, 5233) };

	/**
	 * Monster 3 IDs
	 */
	public static final int[] MONSTER3 = { 7538 };

	/**
	 * Monster 1 IDs
	 */
	public static final int[] MONSTER1 = { 7528 };

	/*
	 * Monster 2 IDs
	 */
	public static final int[] MONSTER2 = { 7542 };


	public static final int[][] MONSTERS = { MONSTER1, MONSTER2, MONSTER3 };

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
	public static Mob getRandomMonster(Location l, MiniRaidGame game, MainMonster mainmonster) {
		int r = Utility.randomNumber(MONSTERS.length);
		final int id = MONSTERS[r][Utility.randomNumber(MONSTERS[r].length)];

		for (int i : MONSTER3) {
			if (id == i) {
				return new Monster3(l, game);
			}
		}

		for (int i : MONSTER1) {
			if (id == i) {
				return new Monster1(l, game);
			}
		}

		for (int i : MONSTER2) {
			if (id == i) {
				return new Monster2(l, game, mainmonster);
			}
		}

		return new Monsters(game, id, l) {

			@Override
			public void tick() {
			}

		};
	}
}

