package com.mayhem.rs2.content.skill.mining;

import java.util.Arrays;

import com.mayhem.core.util.Utility;
import com.mayhem.core.util.chance.Chance;
import com.mayhem.core.util.chance.WeightedChance;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles Paydirt
 * @author Divine
 *
 */
public class PayDirt {
	
	/**
	 * Paydirt Identification
	 */
	private final static Item PAYDIRT = new Item(12011);
	
	
	/**
	 * All possible ores from paydirt
	 */
	public static Chance<Item> ORES = new Chance<Item>(Arrays.asList(
		
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(453, 1)),//coal
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(444, 1)),//gold
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(447, 1)),//mith
			new WeightedChance<Item>(WeightedChance.RARE, new Item(449, 1)),//addy
			new WeightedChance<Item>(WeightedChance.RARE, new Item(451, 1)),//rune
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(12012, 1))//golden nugget
					
	));
	
	/**
	 * Handles using the paydirt
	 * @param player
	 */
	public static void openPayDirt(Player player) {
		Item reward = ORES.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getSkill().addExperience(14, 20);
		player.getInventory().remove(PAYDIRT);
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You get some " + formatted_name + " ."));
	}

}
