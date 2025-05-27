package com.mayhem.rs2.content;

import java.util.Arrays;

import com.mayhem.core.util.Utility;
import com.mayhem.core.util.chance.Chance;
import com.mayhem.core.util.chance.WeightedChance;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles ClueBox caskets
 * @author Divine
 *
 */
public class ClueBox {
	/**
	 * casket Identification
	 */
	private final static Item CASKET = new Item(12789);
	
	
	/**
	 * All possible loots from caskets
	 */
	public static Chance<Item> CLUEBOX = new Chance<Item>(Arrays.asList(
			
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2682, 1)),//easy
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2683, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2684, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2685, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2677, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2678, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2679, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2680, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2681, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2803, 1)),//medium 
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2805, 1)), 
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2807, 1)), 
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2809, 1)), 
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2801, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2722, 1)),//hard 
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2723, 1))
			
			
	));
	
	
	/**
	 * Handles opening the casket
	 * @param player
	 */
	public static void openClueBox(Player player) {
		Item reward = CLUEBOX.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().remove(CASKET);
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You get a " + formatted_name + " ."));
	}
	
	

}
