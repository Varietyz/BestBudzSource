package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Shop for Platinum currency
 * 
 * @author Divine
 */
public class PlatinumShop extends Shop {
	
	/**
	 * Item id of platinum
	 */
	public static final int PLATINUM = 13204;
	
	/**
	 * Id of platinum store
	 */
	public static final int SHOP_ID = 120;

	/**
	 * Prices of items in store
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
		switch (id) {
		
		case 4202:
			return 12000;
		case 20526:
			return 3500;
		case 20595:
		case 20517:
		case 20520:
			return 1000;
		case 12853:
			return 5000;
		case 12817:
			return 9500;
		case 12821:
			return 10500;
		case 12825:
			return 9000;
		case 11731:
			return 500;
			
			
			
			
		
		}

		return 2147483647;
	}

	/**
	 * Items in store
	 */
	public PlatinumShop() {
		super(SHOP_ID, new Item[] { 
				new Item(11731),
				new Item(4202),
				new Item(20595),
				new Item(20517),
				new Item(20520),
				new Item(12853),
				new Item(12817),
				new Item(12821),
				new Item(12825),
			}, false, "Platinum Shop");
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

		if (player.getInventory().getItemAmount(13204) < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Platinum to buy that."));
			return;
		}

		player.getInventory().remove(13204, amount * getPrice(id));

		player.getInventory().add(buying);
		update();
	}

	@Override
	public int getBuyPrice(int id) {
		return 0;
	}

	@Override
	public String getCurrencyName() {
		return "Platinum";
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

