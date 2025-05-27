package com.mayhem.rs2.content;

import java.util.Arrays;

import com.mayhem.core.util.Utility;
import com.mayhem.core.util.chance.Chance;
import com.mayhem.core.util.chance.WeightedChance;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles Clue bottles
 * @author Trebble
 *
 */
public class ClueBottles {
	/**
	 * Clue bottle Identification
	 */
	private final static Item CLUE_BOTTLE_EASY = new Item(13648);
	
	private final static Item CLUE_BOTTLE_MEDIUM = new Item(13649);
	
	private final static Item CLUE_BOTTLE_HARD = new Item(13650);
	
	/**
	 * All possible scrolls from Clue bottles
	 */
	public static Chance<Item> EASY_CLUES = new Chance<Item>(Arrays.asList(
			
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2682, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2683, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2684, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2685, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2677, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2678, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2679, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2680, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2681, 1)) 
			
	));
	
	public static Chance<Item> MEDIUM_CLUES = new Chance<Item>(Arrays.asList(
			
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2803, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2805, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2807, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2809, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2801, 1))
			
	));
	
public static Chance<Item> HARD_CLUES = new Chance<Item>(Arrays.asList(
			
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2722, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2723, 1))
			
	));
	
	/**
	 * Handles opening the Clue bottle
	 * @param player
	 */
	public static void openEASY(Player player) {
		Item reward = EASY_CLUES.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().remove(CLUE_BOTTLE_EASY);
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You get a " + formatted_name + " ."));
	}
	
	public static void openMEDIUM(Player player) {
		Item reward = MEDIUM_CLUES.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().remove(CLUE_BOTTLE_MEDIUM);
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You get a " + formatted_name + " ."));
	}
	
	public static void openHARD(Player player) {
		Item reward = HARD_CLUES.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().remove(CLUE_BOTTLE_HARD);
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You get a " + formatted_name + " ."));
	}

}
