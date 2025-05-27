package com.mayhem.rs2.content;

import java.util.Arrays;

import com.mayhem.core.util.Utility;
import com.mayhem.core.util.chance.Chance;
import com.mayhem.core.util.chance.WeightedChance;
import com.mayhem.rs2.content.membership.RankHandler;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles donator Box rewards
 * @author 
 *
 */
public class MembersBox {
	/**
	 * Mystery box Identification
	 */
	private final static Item DONATOR_BOX = new Item(290);
	
	/**
	 * All possible loots from Mystery Box
	 */
	public static Chance<Item> LOOTS = new Chance<Item>(Arrays.asList(
			//Common Items 
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12397, 1)),//mostly cheaper items from donator shop 1
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12393, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12395, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12351, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12441, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12443, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(4565, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12373, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12335, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12337, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(12439, 1)),
			new WeightedChance<Item>(WeightedChance.COMMON, new Item(7462, 1)),
			
			//Uncommon Items 
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(2572, 1)), //Ring of wealth
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(6731, 1)), //Seers ring
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(6733, 1)), //Archers ring
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(6735, 1)), //Warrior ring
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(6737, 1)), //Berserker ring
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11899, 1)), //quiver
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11900, 1)), //decorative ranger
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11901, 1)), //
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11898, 1)), //decorative mage
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11897, 1)), //
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11896, 1)), //
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11808, 1)), //
			
			//Rare Items 
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(6585, 1)), //Amulet of fury
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(4151, 1)), //Whip
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(3140, 1)), //Dragon chainbody
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(5000, 1)), //vesta
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(5001, 1)), //
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(5002, 1)), //
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(5008, 1)), //statius
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(5009, 1)), //
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(5010, 1)), //
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(5011, 1)), //
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(12004, 1)), //tent whip
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11907, 1)), //trident of the seas
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(12899, 1)), //trident of the swamp
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11802, 1)), //armadyl godsword
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11804, 1)), //bandos godsword
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11806, 1)), //saradomin godsword
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11808, 1)), //zamorak godsword
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11770, 1)), //Seers ring i 
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11771, 1)), //Archers ring i 
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11772, 1)), //Warrior ring i
			new WeightedChance<Item>(WeightedChance.UNCOMMON, new Item(11773, 1)), //Berserker ring i
			
			
			//Very Rare Items
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11283, 1)), //Dragonfire shield
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11335, 1)), //Dragon full helm
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12829, 1)), //Spirit shield
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12922, 1)), //Tanzanite fang
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12932, 1)), //Magic fang
			new WeightedChance<Item>(WeightedChance.RARE, new Item(6570, 1)), //Fire cape
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13239, 1)),//boots
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13237, 1)),//
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13235, 1)),//
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11832, 1)),//Bandos
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11834, 1)),//
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11836, 1)),//
			new WeightedChance<Item>(WeightedChance.RARE, new Item(12783, 1)),//row (i) scroll
			new WeightedChance<Item>(WeightedChance.RARE, new Item(20784, 1)),//dclaws
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13271, 1)),//abyssal dagger
			new WeightedChance<Item>(WeightedChance.RARE, new Item(13263, 1)),//abyssal bludgeon
			new WeightedChance<Item>(WeightedChance.RARE, new Item(21295, 1)),//inferNal cape
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1053, 1)),//hween masks
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1055, 1)),//
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1057, 1)),//
			new WeightedChance<Item>(WeightedChance.RARE, new Item(10847, 1)),//black hween mask
			new WeightedChance<Item>(WeightedChance.RARE, new Item(1050, 1)),//santa hat
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11791, 1)),//staff of the dead
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11785, 1)),//armadyl crossbow
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11830, 1)),//armadyl chestplate
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11828, 1)),//armadyl chainskirt
			new WeightedChance<Item>(WeightedChance.RARE, new Item(11826, 1)),//armadyl helm
			
			
			//Legendary Items
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(19553, 1)),//zenyte jewelry
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(19547, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(19550, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(19544, 1)),
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(5016, 1)),//divine ss
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12817, 1)),//ely ss
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12821, 1)),//spec ss
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12825, 1)),//arcane ss
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(13576, 1)),//dragon warhammer
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(1038, 1)),//phat
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(1040, 1)),//phat
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(1042, 1)),//phat
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(1044, 1)),//phat
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(1046, 1)),//phat
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(1048, 1)),//phat
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11863, 1)),//rainbow phat
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(11862, 1)),//black phat
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(12399, 1)),//phat w/ specs
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(21012, 1)),//dhunter cbow
			new WeightedChance<Item>(WeightedChance.VERY_RARE, new Item(4084, 1))//sled
	));
	
	/**
	 * Handles opening the Mystery Box
	 * @param player
	 */
	public static void open(Player player) {
		Item reward = LOOTS.nextObject().get(); 
		String name = reward.getDefinition().getName();
		String formatted_name = Utility.getAOrAn(name) + " " + name;
		player.getInventory().remove(DONATOR_BOX);
		player.getInventory().addOrCreateGroundItem(reward);
		player.send(new SendMessage("You have opened the and were rewarded with " + formatted_name + " ."));
		if (reward.getDefinition().getGeneralPrice() >= 500_000) {
			World.sendGlobalMessage("@mbl@" + player.determineIcon(player) + " " + player.getUsername() + " has recieved " + formatted_name + " from a Donator box!");
		}
	}

}
