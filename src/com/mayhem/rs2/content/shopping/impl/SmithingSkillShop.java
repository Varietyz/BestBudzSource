package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Smithing store
 * 
 * @author Dez
 */
public class SmithingSkillShop extends Shop {

	/**
	 * Id of Smithing shop
	 */
	public static final int SHOP_ID = 323;

	/**
	 * Price of items in Smithing Skill store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	
	case 2528:
		return 10;
	case 4692:
		return 15;
	case 7668:
		return 500;
	case 11707:
		return 100;
	case 11200:
		return 250;
	case 13106:
		return 250;
	case 13107:
		return 500;
	case 20693:
		return 300;


	}
	return 2147483647;
}

/**
 * All items in smithing
 */
public SmithingSkillShop() {
	super(SHOP_ID, new Item[] { new Item(4692), new Item(7668), new Item(11707), new Item(11200), new Item(13106), new Item(13107), new Item(20693) }, 
			false, "Smithing Skillpoint Shop");
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

		if (player.getSmithingPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Smithing points to buy that."));
			return;
		}

		player.setSmithingPoints(player.getSmithingPoints() - amount * getPrice(id));

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
		return "SmithingPoints";
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
