package com.bestbudz.rs2.entity.mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData.Allegiance;
import com.bestbudz.rs2.content.minigames.pestcontrol.PestControlGame;
import com.bestbudz.rs2.content.profession.fisher.Fisher;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Holds constant variables for mobs
 * 
 * @author Jaybane
 * 
 */
public final class MobConstants {

	/**
	 * The logger for printing information.
	 */
	private static Logger logger = Logger.getLogger(MobConstants.class.getSimpleName());

	/**
	 * Npcs that will be logged
	 */
	public static final int[] LOGGED_NPCS = new int[] { 4315, 6618, 6619, 6610, 6612, 6615, 5779, 6609, 2054, 494, 2265, 2266, 2267, 319, 6342, 8, 415, 4005, 2042, 239, 2215, 3162, 2205, 3129, 3127 };

	public static final List<Mob> GODWARS_BOSSES = new ArrayList<>();

	public static List<Mob> getGodWarsBossMob(Allegiance npc) {
	return GODWARS_BOSSES.stream().filter(mob -> GodWarsData.forId(mob.getId()) != null && GodWarsData.forId(mob.getId()).getAllegiance() == npc).collect(Collectors.toList());
	}

	/**
	 * Mob disappear delay
	 */
	public static enum MobDissapearDelay {

		BARRELCHEST(5666, (byte) 15);

		private final int id;
		private final byte delay;
		public static final Map<Integer, Byte> data = new HashMap<Integer, Byte>();

		public static final void declare() {
		for (MobDissapearDelay i : values())
			data.put(Integer.valueOf(i.id), Byte.valueOf(i.delay));
		}

		public static final int getDelay(int id) {
		return data.get(Integer.valueOf(id)) != null ? data.get(Integer.valueOf(id)).byteValue() : 5;
		}

		private MobDissapearDelay(int id, byte delay) {
		this.id = id;
		this.delay = delay;
		}
	}

	public static HashMap<Integer, Integer> MERCENARY_REQUIREMENTS = new HashMap<>();
	/**
	 * The random chance a mob will walk
	 */
	public static final byte RANDOM_WALK_CHANCE = 12;

	/**
	 * The maximum random walking distance from their location
	 */
	public static final byte MAX_RANDOM_WALK_DISTANCE = 2;

	/**
	 * The default respawn rate for npcs
	 */
	public static final byte DEFAULT_RESPAWN_TIME = 50;

	/**
	 * An array containing aggressive npcs
	 */
	private static final byte[] aggressive = new byte[18000];

	/**
	 * A list containing non aggressive npcs
	 */
	private static String[] NON_AGGRESSIVE_NPCS = { "zulrah", "saradomin wizard", "zamorak wizard", "man", "woman", "gnome", "dwarf", "cow", "guard", "goblin", "banker", "tzhaar-xil", "tzhaar-ket" };

	/**
	 * A list of mobs that don't follow
	 */
	private static final int[] NO_FOLLOW_MOBS = { 1457, 3943, 3847 };

	/**
	 * A list of flying mobs
	 */
	public static final int[] FLYING_MOBS = { 3176, 3166, 3167, 3174, 2042, 2043, 2044, 3165, 3162, 3163, 3164 };

	public static final void declare() {
	int count = 0;

	for (int i = 0; i < 10000; i++) {
		NpcDefinition def = GameDefinitionLoader.getNpcDefinition(i);

		if ((def != null) && (def.getName() != null)) {
			String name = def.getName().toLowerCase();

			for (String k : NON_AGGRESSIVE_NPCS) {
				if (name.contains(k.toLowerCase())) {
					aggressive[i] = 1;
					count++;
					break;
				}
			}
		}
	}

	int[][] mercenaryGrades = { { 4005, 1 }, { 494, 1 }, { 415, 1 }, { 8, 1 }, { 412, 1 }, { 467, 1 }, { 2, 1 }, { 3, 1 }, { 4, 1 }, { 5, 1 }, { 6, 1 }, { 7, 1 }, { 484, 1 }, { 485, 1 }, { 486, 1 }, { 487, 1 }, { 443, 1 }, { 444, 1 }, { 445, 1 }, { 446, 1 }, { 447, 1 }, { 414, 1 }, { 448, 1 }, { 449, 1 }, { 450, 1 }, { 451, 1 }, { 452, 1 }, { 453, 1 }, { 454, 1 }, { 455, 1 }, { 456, 1 }, { 457, 1 } };

	for (int[] data : mercenaryGrades) {
		MERCENARY_REQUIREMENTS.put(Integer.valueOf(data[0]), Integer.valueOf(data[1]));
	}

	logger.info("Loaded " + count + " non-aggressive mobs.");
	}

	public static final boolean face(int id) {
	return (id != 3636) && (Fisher.FisherSpots.forId(id) == null);
	}

	public static final boolean isAggressive(int id) {
	return aggressive[id] != 1;
	}

	public static boolean isAgressiveFor(Mob mob, Stoner stoner) {
	if (mob.inWilderness() || mob.getAttributes().get(PestControlGame.PEST_GAME_KEY) != null) {
		return true;
	}

	return (stoner.getController().canAssaultNPC()) && (stoner.getProfession().getCombatGrade() <= mob.getDefinition().getGrade() * 2 + 1);
	}

	/**
	 * Gets if the npc is a dagannoth king
	 * 
	 * @param m
	 *              The mob to check
	 * @return
	 */
	public static boolean isDagannothKing(Mob m) {
	int id = m.getId();
	return (id == 2265) || (id == 2267) || (id == 2266);
	}

	/**
	 * gets if the mob is a dragon or not
	 * 
	 * @param mob
	 * @return
	 */
	public static boolean isDragon(Mob mob) {
	if (mob == null) {
		return false;
	}

	if (mob.getDefinition() == null || mob.getDefinition().getName() == null) {
		return false;
	}

	return mob.getDefinition().getName().toLowerCase().contains("dragon");
	}

	/**
	 * Gets if the mob shouldn't follow or not
	 * 
	 * @param mob
	 * @return
	 */
	public static final boolean noFollow(Mob mob) {
	for (int i : NO_FOLLOW_MOBS) {
		if (mob.getId() == i) {
			return true;
		}
	}
	return false;
	}
}
