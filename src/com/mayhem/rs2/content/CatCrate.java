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
public class CatCrate {
	/**
	 * casket Identification
	 */
	private final static Item CRATE = new Item(20703);
	
	
	/**
	 * All possible loots from caskets
	 */
	public static Chance<Item> CATCRATE = new Chance<Item>(Arrays.asList(
			
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1555, 1)),//kittens
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1556, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1557, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1558, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1559, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1560, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1561, 1)),//cats
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1562, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1563, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1564, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1565, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1566, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1567, 1)),//overgrown cats
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1568, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1569, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1570, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1571, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1572, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(7581, 1)),//hell cats
			new WeightedChance<Item>(WeightedChance.RARE, new Item(7582, 1)),
			new WeightedChance<Item>(WeightedChance.RARE, new Item(7583, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(1554, 1)),//love cats
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(7771, 1))//clockwork cat
			
			
			
			
	));
	
	
	/**
	 * Handles opening the casket
	 * @param player
	 */
	public static void openCatCrate(Player player) {
		Item reward = CATCRATE.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().remove(CRATE);
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You get a " + formatted_name + " ."));
	}
	
	

}
