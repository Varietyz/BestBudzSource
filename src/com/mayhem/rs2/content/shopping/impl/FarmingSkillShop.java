package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Farming store
 * 
 * @author Dez
 */
public class FarmingSkillShop extends Shop {

	/**
	 * Id of Farming shop
	 */
	public static final int SHOP_ID = 316;

	/**
	 * Price of items in Farming Skill store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	
	case 2528:
		return 20;
	
	case 7409:
		return 80;
	case 9068:
		return 50;
	case 9069:
		return 75;
	case 9070:
		return 100;
	case 9071:
		return 100;
	case 9072:
		return 50;
	case 9073:
		return 60;
	case 9074:
		return 75;
	case 12359:
		return 250;
	case 13640:
		return 150;
	case 13642:
		return 150;
	case 13644:
		return 150;
	case 13646:
		return 150;
		
	

	}
	return 2147483647;
}

/**
 * All items in farming
 */
public FarmingSkillShop() {
	super(SHOP_ID, new Item[] { new Item(13640), new Item(13642), new Item(13644), new Item(13646), new Item(9068), new Item(9069), new Item(9070), new Item(9071), new Item(9072),  new Item(9073), new Item(9074), new Item(12359) }, 
			false, "Farming Skillpoint Shop");
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

		if (player.getfarmingPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Farming points to buy that."));
			return;
		}

		player.setfarmingPoints(player.getfarmingPoints() - amount * getPrice(id));

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
		return "FarmingPoints";
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
