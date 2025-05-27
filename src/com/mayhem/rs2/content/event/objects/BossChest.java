package com.mayhem.rs2.content.event.objects;

import java.util.Arrays;
import com.mayhem.core.util.Utility;
import com.mayhem.core.util.chance.Chance;
import com.mayhem.core.util.chance.WeightedChance;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.object.GameObject;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * @author Andy || ReverendDread Mar 30, 2017
 */
public class BossChest extends GameObject {
	
	//Keeps track of how many times the chest is looted.
	//public static int timesLooted;
	
	//The id of this object.
	public static int chestId = 27282;
	
	//List containing all of the players who've looted this object.
	//private static List<Player> players = new ArrayList<Player>();
	
	//If stared despawn task.
	private static boolean startedDespawnTask;
	
	//The boss chest object.
	private static GameObject bossChest;
	
	/**
	 * Constructs a new GameObject - BossChest.
	 * @param id The object id.
	 * @param x The object X location.
	 * @param y The object Y location.
	 * @param z The object plane/Z location.
	 */
	public BossChest(int x, int y, int z) {
		super(chestId, x, y, z, 10, 0);
		//BossChest.timesLooted = 0;
		BossChest.bossChest = this;
	}
	
	/**
	 * Adds to times looted for {@link BossChest}
	 * @param i times looted.
	 */
	//public static void incrementTimesLooted() {
	//	timesLooted += 1;
	//}
	
	/**
	 * Checks if the interacted object is {@link BossChest}
	 * @param id The object id.
	 * @return is {@link BossChest} or not.
	 */
	public static boolean isBossChest(int id) {
		if (id == chestId) {
			return true;
		}
		return false;
	}
	
	/**
	 * All possible loots from the boss chest
	 */
	public static Chance<Item> BOSSCHEST = new Chance<Item>(Arrays.asList( 
			
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11937, Utility.randomNumber(20))),//Dark Crab
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1398, Utility.randomNumber(50))),//Battlestaff
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(210, Utility.randomNumber(50))),//grimy herb
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(11937, Utility.randomNumber(20))),//Dark Crab
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11937, Utility.randomNumber(40))),//Dark Crab
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(11998, 1)),//smoke battlestaff
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12007, 1)),//Jar of dirt
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(6685, Utility.randomNumber(20))),//sara brew
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(2435, Utility.randomNumber(30))),//Prayer potion
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(6199, 1)),//mystery box
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(212, Utility.randomNumber(30))),//avantoe
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(214, Utility.randomNumber(30))),//kwuarm
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(452, Utility.randomNumber(100))),//Runite Ore
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(220, Utility.randomNumber(30))),//grimy herb
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(1392, Utility.randomNumber(100))),//Battlestaff
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1394, Utility.randomNumber(50))),//Battlestaff
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1396, Utility.randomNumber(50))),//Battlestaff
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1398, Utility.randomNumber(50))),//Battlestaff
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1400, Utility.randomNumber(50))),//Battlestaff
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(3001, Utility.randomNumber(50))),//snapdragon
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(384, Utility.randomNumber(100))),//Raw Shark
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(1377, 1)),//Dragon Battleaxe
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(390, Utility.randomNumber(100))),//Raw Manta
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(210, Utility.randomNumber(50))),//grimy herb
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(995, Utility.randomNumber(2500000))),//coins
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(995, Utility.randomNumber(5000000))),//coins
			new WeightedChance<Item>(WeightedChance.RARE, new Item(995, Utility.randomNumber(7500000))),//coins
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(995, Utility.randomNumber(10000000))),//coins
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(13307, Utility.randomNumber(25000))),//bloodmoney
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13307, Utility.randomNumber(50000))),//bloodmoney
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(13307, Utility.randomNumber(75000))),//bloodmoney
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(13307, Utility.randomNumber(100000))),//bloodmoney
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(13307, Utility.randomNumber(150000))),//bloodmoney
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(990, Utility.randomNumber(15))),//Crystal Keys
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(990, Utility.randomNumber(25))),//Crystal Keys
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(10551, 1)),//Fighter Torso
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(19685, 1)),//Dark totem
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(12759, 1)),//Dark bow green mix
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(12761, 1)),//dark bow mix
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(12763, 1)),//dark bow mix
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(12757, 1)),//dark bow mix
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2572, 1)),//Ring of Wealth
			new WeightedChance<Item>(WeightedChance.RARE, new Item(2577, 1)),//Ranger boots
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12030, 1)),//steel phat
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12060, 1)),//steel hween
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12090, 1)),//steel santa
			new WeightedChance<Item>(WeightedChance.RARE, new Item(5000, 1)),//vesta plate
			new WeightedChance<Item>(WeightedChance.RARE, new Item(5001, 1)),//vesta legs
			new WeightedChance<Item>(WeightedChance.RARE, new Item(5002, 1)),//vesta sword
			new WeightedChance<Item>(WeightedChance.RARE, new Item(5003, 1)),//vesta spear
			new WeightedChance<Item>(WeightedChance.RARE, new Item(5004, 1)),//zuriels staff
			new WeightedChance<Item>(WeightedChance.RARE, new Item(5008, 1)),//statius top
			new WeightedChance<Item>(WeightedChance.RARE, new Item(5009, 1)),//statius legs
			new WeightedChance<Item>(WeightedChance.RARE, new Item(5010, 1)),//statius helm
			new WeightedChance<Item>(WeightedChance.RARE, new Item(5011, 1)),//statius hammer
			new WeightedChance<Item>(WeightedChance.RARE, new Item(4151, 1)),//Abyssal Whip
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12004, 1)),//kraken tent
			new WeightedChance<Item>(WeightedChance.RARE, new Item(6585, 1)),//Amulet of fury
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12783, 1)),//Ring of wealth scroll
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12771, 1)),//Lava whip mix
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12769, 1)),//Frozen whip mix
			new WeightedChance<Item>(WeightedChance.RARE, new Item(19742, 1)),//bandos upgrade scroll
			new WeightedChance<Item>(WeightedChance.RARE, new Item(21024, 1)),//Armadyl upgrade scroll
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12804, 1)),//saradomins tear
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11824, 1)),//zamorakian spear
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(20851, 1)),//Olmlet pet
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(12033, 1)),//pink phat
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(21000, 1)),//Twisted Buckler
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(20997, 1)),//Twisted Bow
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(21003, 1)),//Elder Maul
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(21015, 1)),//Dinh's Bulwark
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(21012, 1)),//Dragonhunter Crossbow
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(19553, 1)),//Amulet of torture
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(19544, 1)),//Tormented Bracelet
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(19547, 1)),//Necklace of anguish
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(19550, 1)),//Ring of suffering
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(21018, 1)),//Ancestrial
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(21021, 1)),//Ancestrial
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(21024, 1)),//Ancestrial
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(21006, 1)),//Kodai Wand
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(7142, 1)),//Rapier
			new WeightedChance<Item>(WeightedChance.LEGENDARY, new Item(12046, 1))//lava phat
			
			
			
		));
	
	/**
	 * Dispatches loot to the player.
	 * @param player
	 */
	public static void dispatchLoot(Player player) {
		//if (players.contains(player)) {
		//	DialogueManager.sendStatement(player, "You've already looted the chest.");
		//	return;
	//	}
		if (!player.getInventory().hasItemAmount(20526, 1)) {
			DialogueManager.sendStatement(player, "You need a bloody key to loot this chest.");
			return;
		}
		Item reward = BOSSCHEST.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().add(990, Utility.randomNumber(1));
		player.getInventory().add(13307, Utility.randomNumber(5000));
		player.bossPoints += 5;
		player.pvmPoints += 5;
		player.getInventory().addOrCreateGroundItem(reward);
		player.getInventory().remove(new Item(20526, 1)); //removes bloody key
		player.send(new SendMessage("You get a " + formatted_name + " + 5x Boss points, PVM points & Bloodmoney."));
		//incrementTimesLooted();
		//players.add(player);	
		player.getUpdateFlags().sendAnimation(new Animation(832));
	}

}
