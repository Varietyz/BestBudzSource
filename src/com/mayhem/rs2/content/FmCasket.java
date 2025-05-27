package com.mayhem.rs2.content;

import java.util.Arrays;

import com.mayhem.core.util.Utility;
import com.mayhem.core.util.chance.Chance;
import com.mayhem.core.util.chance.WeightedChance;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles Firemaking caskets
 * @author Divine
 *
 */
public class FmCasket {
	/**
	 * casket Identification
	 */
	private final static Item CASKET = new Item(19836);
	
	
	/**
	 * All possible loots from caskets
	 */
	public static Chance<Item> FMCASKET = new Chance<Item>(Arrays.asList(
			
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(995, Utility.randomNumber(25000))),//coins
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(995, Utility.randomNumber(100000))),//coins
			new WeightedChance<Item>(WeightedChance.RARE, new Item(995, Utility.randomNumber(200000))),//coins
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(995, Utility.randomNumber(500000))),//coins
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(13307, Utility.randomNumber(1000))),//bloodmoney
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13307, Utility.randomNumber(3500))),//bloodmoney
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(20714, 1)) //book of fire
			
			
	));
	
	
	/**
	 * Handles opening the casket
	 * @param player
	 */
	public static void openFMCasket(Player player) {
		Item reward = FMCASKET.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().remove(CASKET);
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You get a " + formatted_name + " ."));
	}
	
	

}
