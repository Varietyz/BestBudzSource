package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Skill point store
 * 
 * @author Divine
 */
public class SkillPointShop2 extends Shop {

	/**
	 * Id of Skill point shop
	 */
	public static final int SHOP_ID = 121;

	/**
	 * Price of items in Skill point store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	
		case 290: 
			return 80000;
		case 12789:
			return 60000;
		case 13648:
			return 25000;
		case 13649:
			return 50000;
		case 13650:
			return 95000;
			

	}
	return 20000;
}

/**
 * All items in skill point shop
 */
public SkillPointShop2() {
	super(SHOP_ID, new Item[] { 
			
			
			new Item(290, 25), // Super Mystery Box
			new Item(4067, 25), // Vote Ticket
			new Item(7956, 25), // Fishing Casket 
			new Item(5073, 25), // Bird Nest 
			new Item(19836, 25), // Firemaking Casket 
			new Item(12789), // Clue Box 
			new Item(13648), // Easy Clue Bottle
			new Item(13649), // Medium Clue Bottle 
			new Item(13650), // Hard Clue Bottle 
			
			
			
			}, 
			false, "Skill point Shop");
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

		if (player.getskillPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Skill points to buy that."));
			return;
		}

		player.setskillPoints(player.getskillPoints() - amount * getPrice(id));

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
		return "Skill Points";
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
