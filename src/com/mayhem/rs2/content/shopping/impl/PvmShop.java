package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Pvm store
 * 
 * @author Dez
 */
public class PvmShop extends Shop {

	/**
	 * Id of Pvm shop
	 */
	public static final int SHOP_ID = 353;

	/**
	 * Price of items in Pvm store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	case 989:
		return 100;
	case 2572:
		return 900;
	case 22111:
		return 500;
	case 12417:
	case 22242:
	case 12415:
	case 22244:
		return 2500;
	case 2579:
	case 12769:
	case 12771:
		return 300;
	
	case 3781:	
	case 3783:
	case 3787:
		return 100;
		
		

	}
	return 1200;
}

/**
 * All items in Pvm store
 */
public PvmShop() {
	super(SHOP_ID, new Item[] {
			new Item(989, 25),
			new Item(2572),
			new Item(22111),
			new Item(12417),
			new Item(22242),
			new Item(12415),
			new Item(22244),
			new Item(22234),
			new Item(12496),
			new Item(12492),
			new Item(12494),
			new Item(12490),
			new Item(19921),
			new Item(12453),
			new Item(12449),
			new Item(12445),
			new Item(2579),
			new Item(12769),
			new Item(12771),
			new Item(3781),
			new Item(3783),
			new Item(3787),
			
			
			
			},
			false, "PVM Points Shop");
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

		if (player.getpvmPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough PVM points to buy that."));
			return;
		}

		player.setpvmPoints(player.getpvmPoints() - amount * getPrice(id));

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
		return "PVM Points";
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
