package com.mayhem.rs2.content;

import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.content.skill.prayer.BoneBurying.Bones;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;

/**
 * @author Andy || ReverendDread Apr 18, 2017
 */
public class Bonecrusher {

	/**
	 * Handles crushing of bones when player has bone crusher.
	 * @param player {@link Player} The player.
	 * @param item {@link Item} the drop.
	 * @return if crushed or not.
	 */
	public static boolean crush(Player player, Item item) {
		Bones bones = Bones.forBoneId(item.getId());
		if (bones == null)
			return false;
		if (!player.getInventory().hasItemId(13116))
			return false;
		if (player.getSkill().locked())
			return false;	
		player.getSkill().addExperience(Skills.PRAYER, bones.getExp());
		return true;
	}
	
}
