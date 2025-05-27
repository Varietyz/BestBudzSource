package com.mayhem.rs2.content;

import java.util.Arrays;

import com.mayhem.core.util.Utility;
import com.mayhem.core.util.chance.Chance;
import com.mayhem.core.util.chance.WeightedChance;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles Bird nests
 * @author Trebble
 *
 */
public class BirdNests {
	/**
	 * Bird nest Identification
	 */
	private final static Item BIRD_NEST_SEED = new Item(5073);
	
	private final static Item BIRD_NEST_RING = new Item(5074);
	
	private final static Item BIRD_NEST_EASY = new Item(19712);
	
	private final static Item BIRD_NEST_MEDIUM = new Item(19714);
	
	private final static Item BIRD_NEST_HARD = new Item(19716);
	
	/**
	 * All possible loots from Bird nests
	 */
	public static Chance<Item> SEED_NESTS = new Chance<Item>(Arrays.asList(
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(5096, 10)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(5100, 10)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(5295, 10)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(5297, 10)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(5298, 10)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(5299, 10)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(5300, 10)), 
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(13307, Utility.randomNumber(150)))

			
			
	));
	
	public static Chance<Item> RING_NESTS = new Chance<Item>(Arrays.asList(
			
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1635, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1637, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1639, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1641, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1643, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1645, 1)),
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(13307, Utility.randomNumber(150)))
			
	));
	
public static Chance<Item> EASY_NESTS = new Chance<Item>(Arrays.asList(
			
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
	
	public static Chance<Item> MEDIUM_NESTS = new Chance<Item>(Arrays.asList(
			
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2803, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2805, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2807, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2809, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2801, 1))
			
	));
	
public static Chance<Item> HARD_NESTS = new Chance<Item>(Arrays.asList(
			
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2722, 1)), 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2723, 1))
			
	));
	
	
	/**
	 * Handles opening the Bird nests
	 * @param player
	 */
	public static void openSeedNest(Player player) {
		player.getInventory().remove(BIRD_NEST_SEED);
		player.getInventory().add(5075, 1);
		Item reward = SEED_NESTS.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You get a " + formatted_name + " ."));
	}
	
	
	public static void openRingNest(Player player) {
		player.getInventory().remove(BIRD_NEST_RING);
		player.getInventory().add(5075, 1);
		Item reward = RING_NESTS.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You get a " + formatted_name + " ."));
	}
	
	public static void openEasyNest(Player player) {
		player.getInventory().remove(BIRD_NEST_EASY);
		player.getInventory().add(5075, 1);
		Item reward = EASY_NESTS.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You get a " + formatted_name + " ."));
	}
	
	public static void openMediumNest(Player player) {
		player.getInventory().remove(BIRD_NEST_MEDIUM);
		player.getInventory().add(5075, 1);
		Item reward = MEDIUM_NESTS.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You get a " + formatted_name + " ."));
	}
	
	public static void openHardNest(Player player) {
		player.getInventory().remove(BIRD_NEST_HARD);
		player.getInventory().add(5075, 1);
		Item reward = HARD_NESTS.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You get a " + formatted_name + " ."));
	}

}
