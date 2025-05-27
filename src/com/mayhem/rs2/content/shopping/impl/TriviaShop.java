package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Trivia store
 * 
 * @author Dez
 */
public class TriviaShop extends Shop {

	/**
	 * Id of Trivia shop
	 */
	public static final int SHOP_ID = 362;

	/**
	 * Price of items in Trivia store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	
	case 6199:
		return 60;
	case 13192:
		return 330;
	case 9925:
	case 9924:
	case 9923:
		return 150;
	case 9921: 
	case 9922:
		return 75;
	case 6571:
		return 80;
	case 12419:
	case 12420:
	case 12421:
		return 120;
	case 4565:
		return 600;
	case 21859:
		return 2000;
	case 19927:
	case 19933:
	case 19936:
		return 100;
	
		
	

	}
	return 15;
}

/**
 * All items in trivia
 */
public TriviaShop() {
	super(SHOP_ID, new Item[] {
			
			
		
			new Item(989, 25),
			new Item(1712),
			new Item(6199,25),
			new Item(13192),
			new Item(9925),
			new Item(9924),
			new Item(9923),
			new Item(9921),
			new Item(9922),
			new Item(6571),
			new Item(12419),
			new Item(12420),
			new Item(12421),
			new Item(4565),
			new Item(21859),
			new Item(19927),
			new Item(19933),
			new Item(19936),
			
			
			
			
			}, 
			false, "Trivia point Shop");
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

		if (player.gettriviaPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Trivia points to buy that."));
			return;
		}

		player.settriviaPoints(player.gettriviaPoints() - amount * getPrice(id));

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
		return "TriviaPoints";
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
