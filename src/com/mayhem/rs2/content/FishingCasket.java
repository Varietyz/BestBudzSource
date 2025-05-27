package com.mayhem.rs2.content;

import java.util.Arrays;

import com.mayhem.core.util.Utility;
import com.mayhem.core.util.chance.Chance;
import com.mayhem.core.util.chance.WeightedChance;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles fishing caskets
 * @author Trebble
 *
 */
public class FishingCasket {
	/**
	 * casket Identification
	 */
	private final static Item CASKET = new Item(7956);
	
	
	/**
	 * All possible loots from caskets
	 */
	public static Chance<Item> FISHINGCASKET = new Chance<Item>(Arrays.asList(
			
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(995, 25000)),//coins
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(995, 50000)),//coins
			new WeightedChance<Item>(WeightedChance.RARE, new Item(995, 100000)),//coins
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(995, 200000)),//coins
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(390, 50)),//manta
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(360, 50)),//tuna
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(378, 50)),//lobster
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(372, 50)),//swordfish
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(396, 50)),//sea turtle
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(7945, 50)),//monkfish
			new WeightedChance<Item>(WeightedChance.RARE, new Item(3143, 50)),//karambwan 
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11935, 50)), //dark crab
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(384, 30)),//shark
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(13320, 1)),//heron
			new WeightedChance<Item>(WeightedChance.RARE, new Item(272, 1)), //pet goldfish
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(7993, 1))//pet shark
			
	));
	
	
	/**
	 * Handles opening the casket
	 * @param player
	 */
	public static void openFishingCasket(Player player) {
		Item reward = FISHINGCASKET.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().remove(CASKET);
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You get a " + formatted_name + " ."));
	}
	
	

}
