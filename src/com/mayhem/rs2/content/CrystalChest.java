package com.mayhem.rs2.content;

import com.mayhem.core.util.ItemNames;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.achievements.AchievementHandler;
import com.mayhem.rs2.content.achievements.AchievementList;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.entity.Animation;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles crystal chest
 * 
 * @author Daniel
 *
 */
public class CrystalChest {

	/**
	 * Ids of key halves
	 */
	public static final Item[] KEY_HALVES = { new Item(985), new Item(987) };

	/**
	 * Crystal key Id
	 */
	public static final Item KEY = new Item(989);

	/**
	 * Creates the key
	 * 
	 * @param player
	 */
	public static void createKey(final Player player) {
		if (player.getInventory().contains(KEY_HALVES)) {
			player.getInventory().remove(KEY_HALVES[0]);
			player.getInventory().remove(KEY_HALVES[1]);
			player.getInventory().add(KEY);
			DialogueManager.sendItem1(player, "You have combined the two parts to form a key.", KEY.getId());
		}
	}
	
	/**
	 * Chest rewards
	 */
	public static final Item[] COMMON_CHEST_REWARDS = {

			new Item(ItemNames.RUNE_FULL_HELM), new Item(ItemNames.RUNE_PLATEBODY), new Item(ItemNames.RUNE_KITESHIELD), new Item(ItemNames.RUNE_PLATELEGS), new Item(ItemNames.RUNE_PLATESKIRT), 
			new Item(ItemNames.RUNE_BOOTS), new Item(ItemNames.RUNE_CHAINBODY), new Item(ItemNames.RUNE_CROSSBOW), new Item(ItemNames.RING_OF_RECOIL), new Item(392, Utility.random(75)), 
			new Item(372, Utility.random(50)), new Item(2364, Utility.random(5)), new Item(452, Utility.random(10)), new Item(212, Utility.random(10)), new Item(216, Utility.random(10)), 
			new Item(218, Utility.random(10)), new Item(200, Utility.random(20)), new Item(206, Utility.random(20)), new Item(210, Utility.random(10)), new Item(1618, Utility.random(20)), 
			new Item(1622, Utility.random(20)), new Item(1620, Utility.random(25)), new Item(1624, Utility.random(30)),
			new Item(ItemNames.TAN_CAVALIER),new Item(20166), new Item(4740, Utility.random(500)), new Item(ItemNames.DARK_CAVALIER), new Item(ItemNames.BLACK_CAVALIER), new Item(ItemNames.BLACK_BERET), 
			new Item(ItemNames.RED_HEADBAND), new Item(ItemNames.PIRATES_HAT), new Item(ItemNames.BROWN_HEADBAND), new Item(ItemNames.SHARK), new Item(ItemNames.MONKEY_NUTS), new Item(ItemNames.EYE_PATCH),
			new Item (382, Utility.random(10)), new Item (318, 50), new Item (1704), new Item(ItemNames.DIAMOND_AMULET), new Item (ItemNames.DIAMOND), new Item (13307, Utility.random(500)), 
			new Item (384, Utility.random(20)),  new Item (387, Utility.random(20)), new Item (995, Utility.random(25000)), new Item (336, Utility.random(200)), new Item (334, Utility.random(20)), 
			new Item (330, Utility.random(50)), new Item (995, Utility.random(10000)), new Item (988, Utility.random(3)), new Item (360, Utility.random(20)), 
			
			};

	

	public static final Item[] UNCOMMON_CHEST_REWARDS = {new Item(537, Utility.random(35)), new Item(13442, Utility.random(50)), new Item(13440, Utility.random(88)),
			new Item(11935, Utility.random(60)), new Item(11937, Utility.random(115)), new Item(990, Utility.random(4)), new Item(6199, 1), new Item(21820, Utility.random(2)),
			new Item(11212, Utility.random(65)), new Item(11237, Utility.random(101)), new Item(21350, Utility.random(85)), new Item(21326, Utility.random(40)), new Item(11849, Utility.random(5)), 
			new Item(989), new Item(12641), new Item(377, Utility.random(100)), new Item(ItemNames.LOBSTER_POT), new Item(380, Utility.random(50)), new Item (986, Utility.random(4)), 
			new Item (3027, Utility.random(6)), new Item (140, Utility.random(10)), new Item (543, Utility.random(5)), new Item (545, Utility.random(10)), new Item (21976, Utility.random(20)), 
			new Item(ItemNames.COAL_NOTE, Utility.random(20)), new Item (2358, Utility.random(10)), new Item (9194, Utility.random(5)), new Item (9191, Utility.random(40)), 
			new Item (9192, Utility.random(30)), new Item (9193, Utility.random(10)), new Item (11237, Utility.random(20)), new Item (884, Utility.random(500)), new Item (886, Utility.random(100)), 
			new Item (888, Utility.random(50)), new Item (892, Utility.random(40)), new Item (11212, Utility.random(30)), new Item(995, Utility.random(75000))
			};
	
	

	public static final Item[] RARE_CHEST_REWARDS = {new Item(2572), new Item(2581), new Item(2577), new Item(2581), new Item(6571), new Item(11840), new Item(8789, Utility.random(1000)),  new Item(12026), 
			 new Item(12061), new Item(7158), new Item(995, Utility.random(1000000)), new Item(290), new Item(ItemNames.UNCUT_DRAGONSTONE), new Item(ItemNames.UNCUT_ONYX), 
			 new Item(ItemNames.DRAGON_SPEAR), new Item(ItemNames.DRAGON_2H_SWORD), new Item(11935, Utility.random(20)), new Item(537, Utility.random(120)), new Item(11944, Utility.random(30)),
			 new Item(12526), new Item(5565), new Item(9246, Utility.random(200)), new Item(6581), new Item(6199), new Item(11849, Utility.random(25)), new Item (20199), new Item (20202), new Item(19559),
			 new Item(13307, Utility.random(10000))
			 }; 

	
	/**
	 * Searches the chest
	 * 
	 * @param player
	 * @param x
	 * @param y
	 */

	public static void searchChest(final Player player, final int x, final int y) {
		if (player.getInventory().contains(KEY)) {
			player.send(new SendMessage("You unlock the chest with your key."));
			player.getInventory().remove(KEY);
			AchievementHandler.activate(player, AchievementList.OPEN_70_CRYSTAL_CHESTS, 1);
			player.getUpdateFlags().sendAnimation(new Animation(881));
			player.getInventory().add(new Item(995, Utility.random(1000)));
			Item itemReceived;
			switch (Utility.random(2000)) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				itemReceived = Utility.randomElement(UNCOMMON_CHEST_REWARDS);
				break;
			case 25:
				itemReceived = Utility.randomElement(RARE_CHEST_REWARDS);
				break;
			default:
				itemReceived = Utility.randomElement(COMMON_CHEST_REWARDS);
			}

			player.getInventory().addOrCreateGroundItem(itemReceived.getId(), itemReceived.getAmount(), true);
			player.send(new SendMessage("You find " + Utility.determineIndefiniteArticle(itemReceived.getDefinition().getName()) + " " + itemReceived.getDefinition().getName() + " in the chest."));
			if (itemReceived.getDefinition().getGeneralPrice() < 100_000) {
				switch (Utility.random(200)) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
					itemReceived = Utility.randomElement(UNCOMMON_CHEST_REWARDS);
					break;
				case 25:
					itemReceived = Utility.randomElement(RARE_CHEST_REWARDS);
					break;
				default:
					itemReceived = Utility.randomElement(COMMON_CHEST_REWARDS);
				}
				player.getInventory().addOrCreateGroundItem(itemReceived.getId(), itemReceived.getAmount(), true);
				player.send(new SendMessage("You find " + Utility.determineIndefiniteArticle(itemReceived.getDefinition().getName()) + " " + itemReceived.getDefinition().getName() + " in the chest."));
			}
		} else {
			player.send(new SendMessage("You need a key to open this chest."));
		}
	}

}
