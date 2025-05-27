package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Shop for Coins currency
 * 
 * @author Trebble
 */
public class CustomShop extends Shop {
	
	/**
	 * Item id of coins
	 */
	public static final int COINS = 995;
	
	/**
	 * Id of customs store
	 */
	public static final int SHOP_ID = 100;

	/**
	 * Prices of items in store
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
		switch (id) {
		case 12026:
		case 12056:
		case 12086:
			return 25000000;
		case 12028:
		case 12058:
		case 12088:
			return 30000000;
		case 12030:
		case 12060:
		case 12090:
			return 35000000;
		case 12032:
		case 12062:
		case 12092:
			return 40000000;
		case 12034:
		case 12064:
		case 12094:
			return 45000000;
		case 12036:
		case 12066:
		case 12096:
			return 50000000;
		case 12038:
		case 12068:
		case 12098:
			return 55000000;
		case 12040:
		case 12070:
		case 12100:
			return 60000000;
		case 12042:
		case 12072:
		case 12102:
			return 65000000;
		}

		return 2147483647;
	}

	/**
	 * Items in store
	 */
	public CustomShop() {
		super(SHOP_ID, new Item[] { 
				new Item(12026, 1),
				new Item(12028, 1),
				new Item(12030, 1),
				new Item(12032, 1),
				new Item(12034, 1),
				new Item(12036, 1),
				new Item(12038, 1),
				new Item(12040, 1),
				new Item(12042, 1),
				new Item(12056, 1),
				new Item(12058, 1),
				new Item(12060, 1),
				new Item(12062, 1),
				new Item(12064, 1),
				new Item(12066, 1),
				new Item(12068, 1),
				new Item(12070, 1),
				new Item(12072, 1),
				new Item(12086, 1),
				new Item(12088, 1),
				new Item(12090, 1),
				new Item(12092, 1),
				new Item(12094, 1),
				new Item(12096, 1),
				new Item(12098, 1),
				new Item(12100, 1),
				new Item(12102, 1),
			}, false, "Recolored item Shop");
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

		if (player.getInventory().getItemAmount(995) < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Gold Coins to buy that."));
			return;
		}

		player.getInventory().remove(995, amount * getPrice(id));

		player.getInventory().add(buying);
		update();
	}

	@Override
	public int getBuyPrice(int id) {
		return 0;
	}

	@Override
	public String getCurrencyName() {
		return "Coins";
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
