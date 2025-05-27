package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Bounty store
 * 
 * @author Daniel
 */
public class BountyShop extends Shop {

	/**
	 * Id of Bounty shop
	 */
	public static final int SHOP_ID = 7;

	/**
	 * Price of items in Bounty store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	case 11840:
		return 100_000;
	case 10551:
	case 553:
		return 250_000;
	case 12004:
		return 2_500_000;
	case 11808:
		return 5_000_000;
	case 11804:
		return 7_000_000;
	case 11806:
		return 8_000_000;
	case 11802:
		return 9_000_000;
	case 20784:
		return 10_000_000;
	case 6570:
		return 750_000;
	case 4151:
		return 900_000;
	case 12800:
		return 300_000;
	case 1377:
		return 600_000;
	case 1434:
		return 150_000;
	case 11941:
		return 150_000;
	case 3751:
	case 3753:
	case 3749:
	case 3755:
		return 234_000;
	case 4091:
		return 360_000;
	case 4093:
		return 240_000;
	case 4089:
		return 45_000;
	case 4095:
	case 4097:
		return 30_000;
	case 1127:
		return 255_000;
	case 6585:
		return 850_000;
	case 1079:
	case 1093:
		return 192_000;
	case 12759:
	case 12761:
	case 12763:
	case 12757:
	case 12771:
	case 12769:
		return 500_000;
	case 12798:
		return 250_000;
	case 12802:
		return 350_000;
	case 12804:
		return 25_000_000;
	case 4740:
		return 360;
	case 892:
		return 600;
	case 13111:
		return 1_000_000;
	case 12846:
		return 8_000_000;
	case 12855:
	case 12856:
		return 2_500_000;
	case 12786:
		return 350_000;
	case 11907:
		return 6_000_000;
	case 12954:
		return 500_000;
	case 12849:
		return 100_000;
	
	}
	return 2147483647;
}

/**
 * All items in Bounty store
 */
public BountyShop() {
	super(SHOP_ID, 
			new Item[] 
			{ new Item(4151), new Item(6585), new Item(6570), 
			new Item(11802), new Item(11804), new Item(11806), 
			new Item(11808), new Item(12004), new Item(20784), 
			new Item(10551), new Item(11840),  
			new Item(12759), new Item(12761), new Item(12763), 
			new Item(12757), new Item(12771),new Item(12769), 
			new Item(12798), new Item(12800), new Item(12802), 
			new Item(12804), new Item(4740), new Item(892), 
			new Item(13111), new Item(12846), new Item(12855), 
			new Item(12856), new Item(12786), new Item(11907), 
			new Item(553), new Item(12954), new Item(12849) 
	}, false, "Bounty Store");
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

		if (player.getBountyPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Bounty points to buy that."));
			return;
		}

		player.setBountyPoints(player.getBountyPoints() - amount * getPrice(id));

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
		return "Bounty points";
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
