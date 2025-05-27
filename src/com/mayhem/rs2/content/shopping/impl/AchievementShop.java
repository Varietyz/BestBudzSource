package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Shop for Achievements
 * 
 * @author Daniel
 */
public class AchievementShop extends Shop {

	/**
	 * Id of shop
	 */
	public static final int SHOP_ID = 89;

	/**
	 * Prices of item in shop
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	
	case 6199:
		return 2;
	
	case 15041:
	case 15042: 
		return 5;
		
	case 10840:
	case 10836:
	case 10837:
	case 10838:
	case 10839:
		return 10;
		
	case 20838:
	case 20840:
	case 20842:
	case 20844:
	case 20846:
		return 15;
		
	case 290:
	case 20035:
	case 20038:
	case 20041:
	case 20044:
	case 20047:
		return 20;
		
	case 22322:
	case 21653:
		return 25;
	}

	return 10000;
}

	
	
	
/**
 * Items in shop
 */
public AchievementShop() {
	super(SHOP_ID, new Item[] { 
		new Item (6199),
		new Item (15041),
		new Item (15042),
		new Item (10840),
		new Item (10836),
		new Item (10837),
		new Item (10838),
		new Item (10839),
		new Item (20838),
		new Item (20840),
		new Item (20842),
		new Item (20844),
		new Item (20846),
		new Item (20035),
		new Item (20038),
		new Item (20041),
		new Item (20044),
		new Item (20047),
		new Item (22322),
	}, false, "Achievement Store");
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

		if (player.getAchievementsPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Achievements points to buy that."));
			return;
		}

		player.addAchievementPoints(player.getAchievementsPoints() - (amount * getPrice(id)));

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
		return "Achievements points";
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
