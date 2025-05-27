package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Prayer store
 * 
 * @author Dez
 */
public class PrayerSkillShop extends Shop {

	/**
	 * Id of Prayer shop
	 */
	public static final int SHOP_ID = 321;

	/**
	 * Price of items in Prayer Skill store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	
	case 2528:
		return 10;
	
	case 13119:
		return 200;
		
	case 13120:
		return 500;
	case 12598:
		return 300;
	case 12637:
		return 1000;
	case 12638:
		return 1000;
	case 12639:
		return 1000;
	

	}
	return 2147483647;
}

/**
 * All items in prayer
 */
public PrayerSkillShop() {
	super(SHOP_ID, new Item[] { new Item(13119), new Item(13120), new Item(12598), new Item(12637), new Item(12638), new Item(12639) }, 
			false, "Prayer Skillpoint Shop");
}

	@Override
	public void buy(Player player, int slot, int id, int amount) {
		if (!hasItem(slot, id))
			return;
		if (get(slot).getAmount() == 0)
			return;
		if (amount > get(slot).getAmount()) {
			amount = get(slot).getAmount();
		}

		Item buying = new Item(id, amount);

		if (!player.getInventory().hasSpaceFor(buying)) {
			if (!buying.getDefinition().isStackable()) {
				int slots = player.getInventory().getFreeSlots();
				if (slots > 0) {
					buying.setAmount(slots);
					amount = slots;
				} else {
					player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to buy this item."));
				}
			} else {
				player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough inventory space to buy this item."));
				return;
			}
		}

		if (player.getprayerPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Prayer points to buy that."));
			return;
		}

		player.setprayerPoints(player.getprayerPoints() - amount * getPrice(id));

		//InterfaceHandler.writeText(new QuestTab(player));

		player.getInventory().add(buying);
		update();
	}

	@Override
	public int getBuyPrice(int id) {
		return 0;
	}

	@Override
	public String getCurrencyName() {
		return "PrayerPoints";
	}

	@Override
	public int getSellPrice(int id) {
		return getPrice(id);
	}

	@Override
	public boolean sell(Player player, int id, int amount) {
		player.getClient().queueOutgoingPacket(new SendMessage("You cannot sell items to this shop."));
		return false;
	}
}
