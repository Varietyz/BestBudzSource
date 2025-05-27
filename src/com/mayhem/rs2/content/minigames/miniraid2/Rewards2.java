package com.mayhem.rs2.content.minigames.miniraid2;

import java.util.Arrays;

import com.mayhem.core.util.Utility;
import com.mayhem.core.util.chance.Chance;
import com.mayhem.core.util.chance.WeightedChance;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles MINI RAID rewards
 * @author Divine
 *
 */
public class Rewards2 {
	
	/**
	 * All possible loots from Mini Raidv2
	 */
	public static Chance<Item> REWARDS = new Chance<Item>(Arrays.asList(
			
			//common
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (537, Utility.randomNumber(65))),//dbones
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (3052, Utility.randomNumber(50))), //grimy snapdragon
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (990, Utility.randomNumber(15))), //crystal key
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (995, Utility.randomNumber(750000))), //coins
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (2722, 1)), //hard clue
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (2723, 1)), //hard clue
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (12789, 1)), //clue box
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (13307, Utility.randomNumber(10000))), //blood money
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (454, Utility.randomNumber(300))), //coal
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (448, Utility.randomNumber(150))), //mith ore
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (218, Utility.randomNumber(80))), //herb
			new WeightedChance<Item>(WeightedChance.COMMON, new Item (216, Utility.randomNumber(80))), //herb
			
			//uncommon
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (537, Utility.randomNumber(100))), //dbones
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (11944, Utility.randomNumber(80))), //lava dragon bone
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (11935, Utility.randomNumber(75))), //raw darkcrab
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (995, Utility.randomNumber(1250000))), //coins
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (6199, 1)), //mbox
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (13307, Utility.randomNumber(20000))), //blood money
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (12897, 1)), //box of bones
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (450, Utility.randomNumber(150))), //addy ore
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (218, Utility.randomNumber(100))), //herb
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (216, Utility.randomNumber(100))), //herb
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (11212, Utility.randomNumber(250))), //dragon arrows
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item (11230, Utility.randomNumber(250))), //dragon arrows
			
			//rare
			new WeightedChance<Item>(WeightedChance.RARE, new Item (4151, 1)), //whip
			new WeightedChance<Item>(WeightedChance.RARE, new Item (12004, 1)), //tentacle
			new WeightedChance<Item>(WeightedChance.RARE, new Item (4202, 1)), //Charos  15% to drop rates
			new WeightedChance<Item>(WeightedChance.RARE, new Item (2722, 1)), //hard clue
			new WeightedChance<Item>(WeightedChance.RARE, new Item (2723, 1)), //hard clue
			new WeightedChance<Item>(WeightedChance.RARE, new Item (12789, 1)), //clue box
			
			
			//very rare
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item (12897, 1)), //hard clue
			new WeightedChance<Item>(WeightedChance.RARE, new Item (20526, 1)), //event key
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item (6465, 1)), //Charos (a) 20% to drop rates
			
			
			//legendary
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14491, 1)), //pernix
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14492, 1)), //pernix
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14493, 1)) //pernix
			//new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14024, 1)), //Chaotic Crossbow
			//new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14026, 1)), //Chaotic Kiteshield
			//new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14028, 1)), //Chaotic Sword
			//new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14030, 1)), //Chaotic Maul
			//new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14032, 1)), //Chaotic Rapier
			//new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(14034, 1)) //Chaotic Staff
			
	));
	
	/**
	 * Handles giving rewards for the Mini raid 2
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
