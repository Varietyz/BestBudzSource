package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Shop for Coins currency
 * 
 * @author Divine
 */
public class CustomShop2 extends Shop {
	
	/**
	 * Item id of coins
	 */
	public static final int COINS = 995;
	
	/**
	 * Id of customs store
	 */
	public static final int SHOP_ID = 101;

	/**
	 * Prices of items in store
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
		switch (id) {
		case 12027:
		case 12029:
		case 12033:
		case 12031:
			return 50000000;
		case 12057:
		case 12059:
		case 12061:
		case 12063:
			return 50000000;
		case 12093:
		case 12089:
		case 12087:
		case 12091:
			return 50000000;
		case 12106:
		case 12104:
		case 12076:
		case 12074:
		case 12046:
		case 12044:
			return 100000000;
		
		}

		return 2147483647;
	}

	/**
	 * Items in store
	 */
	public CustomShop2() {
		super(SHOP_ID, new Item[] { 
				new Item(12027, 1),
				new Item(12029, 1),
				new Item(12031, 1),
				new Item(12033, 1),
				new Item(12057, 1),
				new Item(12059, 1),
				new Item(12061, 1),
				new Item(12063, 1),
				new Item(12087, 1),
				new Item(12089, 1),
				new Item(12091, 1),
				new Item(12093, 1),
				new Item(12044, 1),
				new Item(12046, 1),
				new Item(12074, 1),
				new Item(12076, 1),
				new Item(12104, 1),
				new Item(12106, 1),
			}, false, "Recolored item Shop 2");
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
