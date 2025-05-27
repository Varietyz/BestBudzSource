package com.mayhem.rs2.content;

import java.util.HashMap;

import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles opening of Tomes
 */
public enum Tomes {
	FISHING(10, 7779, 6666),
	AGILITY(16, 7782, 1000),
	THIEVING(17, 7785, 6666),
	SLAYER(18, 7788, 4444),
	MINING(14, 7791, 3333),
	FIREMAKING(11, 7794, 4444),
	WOODUCTTING(8, 7797, 3333);
	

	private final int skillId, tomesId, xp;

	private Tomes(int skillId, int tomesId, int xp) {
		this.skillId = skillId;
		this.tomesId = tomesId;
		this.xp = xp;
	}
	
	public int getSkill() {
		return skillId;
	}
	
	private static HashMap<Integer, Tomes> tomes = new HashMap<Integer, Tomes>();

	static {
		for (final Tomes tomes : Tomes.values()) {
			Tomes.tomes.put(tomes.tomesId, tomes);
		}
	}

	/**
	 * Handles opening the tomes
	 */
	public static boolean openSet(Player player, int item) {
		/* Get the tomes */
		Tomes data = Tomes.tomes.get(item);
		
		/* If tomes is nulled; return */
		if (data == null) {
			return false;
		}
		
		/* Remove the tomes set from inventory */
		player.getInventory().remove(data.tomesId);

		/* Add all the add the xp*/
		player.getSkill().addExperience(data.skillId, data.xp);

		/* Send message of successful opening */
		player.send(new SendMessage("You have been given XP, Keep up the work!"));
		return true;
	}
}