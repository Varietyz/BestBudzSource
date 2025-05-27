package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Fishing store
 * 
 * @author Dez
 */
public class FishingSkillShop extends Shop {

	/**
	 * Id of Fishing shop
	 */
	public static final int SHOP_ID = 318;

	/**
	 * Price of items in Fishing Skill store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	
	case 2528:
		return 10;
	case 13131:
		return 200;
	case 13132:
		return 500;
	case 13135:
		return 250;
	case 272:
		return 1000;
	case 13258:
		return 350;
	case 13259:
		return 350;
	case 13260:
		return 350;
	case 13261:
		return 350;
	case 13320:
		return 600;
	

	

	}
	return 2147483647;
}

/**
 * All items in fishing
 */
public FishingSkillShop() {
	super(SHOP_ID, new Item[] { 
			new Item(13135),
			new Item(13131),
			new Item(13132),
			new Item(272),
			new Item(13258),
			new Item(13259),
			new Item(13260),
			new Item(13261),
			},
			false, "Fishing Skillpoints Shop");
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

		if (player.getfishingPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Fishing points to buy that."));
			return;
		}

		player.setfishingPoints(player.getfishingPoints() - amount * getPrice(id));

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
		return "Fishing Points";
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
