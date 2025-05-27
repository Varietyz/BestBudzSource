package com.mayhem.rs2.content.skill.crafting;

import com.mayhem.core.util.GameDefinitionLoader;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.achievements.AchievementHandler;
import com.mayhem.rs2.content.achievements.AchievementList;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles amulet stringing
 * @author Daniel
 *
 */
public class AmuletStringing {
	
	
	/**
	 * Amulet Data
	 * @author Daniel
	 *
	 */
	public static enum AmuletData {
		GOLD(1673, 1692),
		SAPPHIRE(1675, 1694),
		EMERALD(1677, 1696),
		RUBY(1679, 1698),
		DIAMOND(1681, 1700),
		DRAGONSTONE(1683, 1702),
		ONYX(6579, 6581);
		
		private int amuletId, product;
		
		private AmuletData(final int amuletId, final int product) {
			this.amuletId = amuletId;
			this.product = product;
		}
		
		public int getAmuletId() {
			return amuletId;
		}
		
		public int getProduct() {
			return product;
		}
	}
	
	/**
	 * Strings the amulet
	 * @param player
	 * @param itemUsed
	 * @param usedWith
	 */
	public static void stringAmulet(final Player player, final int itemUsed, final int usedWith) {
		final int amuletId = (itemUsed == 1759 ? usedWith : itemUsed);
		for (final AmuletData a : AmuletData.values()) {
			if (amuletId == a.getAmuletId()) {
				
				//Removes the items
				player.getInventory().remove(1759, 1);
				player.getInventory().remove(amuletId, 1);
				
				//Adds the item
				player.getInventory().add(a.getProduct(), 1);
				
				//Gives experience
				player.getSkill().addExperience(Skills.CRAFTING, Utility.random(3));
				player.skillPoints += 35;
				handleDesAmulet1(player);
				handleDesAmulet2(player);
				handleDesAmulet3(player);
				
				//Gets the name of item
				String name = GameDefinitionLoader.getItemDef(a.getProduct()).getName();
				
				//Send the message
				player.send(new SendMessage("You have have strung " + Utility.getAOrAn(name) + " " + name + ". You now have @blu@" + player.skillPoints + "</col> Skill points."));
				
				//Send the achievement
				AchievementHandler.activate(player, AchievementList.STRING_100_AMULETS, 1);
				
			}
		}
	}
	public static void handleDesAmulet1(Player player) {

		int random = Utility.randomNumber(200);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13133, 1)) {
				player.getEquipment().remove(new Item(13133, 1));
				player.getEquipment().equip(new Item(13134), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Desert Amulet has been upgraded!")); }
			}
		}

	public static void handleDesAmulet2(Player player) {

		int random = Utility.randomNumber(300);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13134, 1)) {
				player.getEquipment().remove(new Item(13134, 1));
				player.getEquipment().equip(new Item(13135), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Desert Amulet has been upgraded!")); }
		}
	}
	public static void handleDesAmulet3(Player player) {

		int random = Utility.randomNumber(1000);
		if (random == 1) {
			if (player.getEquipment().isWearingItem(13135, 1)) {
				player.getEquipment().remove(new Item(13135, 1));
				player.getEquipment().equip(new Item(13136), 1);
				player.setAppearanceUpdateRequired(true);
				player.getUpdateFlags().setUpdateRequired(true);
				player.getClient().queueOutgoingPacket(new SendMessage("@blu@Your Desert Amulet has been upgraded!")); }
		}
	}

}
