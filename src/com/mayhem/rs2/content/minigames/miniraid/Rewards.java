package com.mayhem.rs2.content.minigames.miniraid;

import java.util.Arrays;

import com.mayhem.core.util.Utility;
import com.mayhem.core.util.chance.Chance;
import com.mayhem.core.util.chance.WeightedChance;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles MINI RAID 2 rewards
 * @author Divine
 *
 */
public class Rewards {
	
	/**
	 * All possible loots from Mini Raid
	 */
	public static Chance<Item> REWARDS = new Chance<Item>(Arrays.asList(
			
			//common
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (537, Utility.randomNumber(45))),//dbones
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (3052, Utility.randomNumber(30))), //grimy snapdragon
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (3050, Utility.randomNumber(30))), //herb
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (2486, Utility.randomNumber(30))), //herb
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (990, Utility.randomNumber(15))), //crystal key
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (995, Utility.randomNumber(500000))), //coins
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (2722, 1)), //hard clue
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (2723, 1)), //hard clue
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (12789, 1)), //clue box
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (454, Utility.randomNumber(250))), //coal
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (448, Utility.randomNumber(100))), //mith ore
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (218, Utility.randomNumber(70))), //herb
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (216, Utility.randomNumber(70))), //herb
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (214, Utility.randomNumber(100))), //herb
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (212, Utility.randomNumber(100))), //herb
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (210, Utility.randomNumber(100))), //herb
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (208, Utility.randomNumber(100))), //herb
			
			//uncommon
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (3052, Utility.randomNumber(70))), //grimy snapdragon
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (3049, Utility.randomNumber(70))), //herb
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (2486, Utility.randomNumber(70))), //herb
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (990, Utility.randomNumber(30))), //crystal key
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (537, Utility.randomNumber(100))), //dbones
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (11944, Utility.randomNumber(80))), //lava dragon bone
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (11935, Utility.randomNumber(75))), //raw darkcrab
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (995, Utility.randomNumber(1000000))), //coins
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (11840, 1)), //Dragon boots
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (6199, 1)), //mbox
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (450, Utility.randomNumber(150))), //addy ore
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (218, Utility.randomNumber(100))), //herb
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (216, Utility.randomNumber(100))), //herb
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (214, Utility.randomNumber(130))), //herb
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (212, Utility.randomNumber(130))), //herb
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (210, Utility.randomNumber(130))), //herb
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (208, Utility.randomNumber(130))), //herb
			
			//rare
			new WeightedChance<Item>(WeightedChance.RARE, new Item (11920, 1)),//dragon pickaxe
			new WeightedChance<Item>(WeightedChance.RARE, new Item (6739, 1)),//dragon axe
			new WeightedChance<Item>(WeightedChance.RARE, new Item (452, Utility.randomNumber(100))), //runite
			new WeightedChance<Item>(WeightedChance.RARE, new Item (2722, 1)), //hard clue
			new WeightedChance<Item>(WeightedChance.RARE, new Item (2723, 1)), //hard clue
			new WeightedChance<Item>(WeightedChance.RARE, new Item (12789, 1)), //clue box
			
			
			//very rare
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item (12897, 1)), //hard clue
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item (4202, 1)), //Charos  15% to drop rates
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item (6465, 1)), //Charos (a) 20% to drop rates
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(14010, 1)),//Pet Tekton
			
			
			//legendary
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14491, 1)), //pernix
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14492, 1)), //pernix
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14997, 1)), //torva
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14493, 1)), //pernix
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14494, 1)), //virtus
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14998, 1)), //torva
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14495, 1)), //virtus
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14999, 1)), //torva
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14496, 1)), //virtus
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14990, 1)), //zaryte bow
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14002, 1)), //Justiciar sword
            new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14008, 1)), //Justiciar shield
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14006, 1))//Pet Vespula
			
	));
	
	/**
	 * Handles giving rewards for the Mini raid
	 * @param player
	 */
	public static void getRewards(Player player) {
		Item reward = REWARDS.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().addOrCreateGroundItem(reward);
		if (reward.getDefinition().getGeneralPrice() >= 5_000_000) {
			World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved " + formatted_name + " from the Dungeon of Flames!");
		}
	}

}
