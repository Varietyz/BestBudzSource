package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Woodcutting store
 * 
 * @author Dez
 */
public class WoodcuttingSkillShop extends Shop {

	/**
	 * Id of Woodcutting shop
	 */
	public static final int SHOP_ID = 325;

	/**
	 * Price of items in Woodcutting Skill store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	
	case 2528:
		return 10;
	
	case 10941:
		return 50;
	case 10939:
		return 100;
	case 10940:
		return 100;
	case 10933:
		return 50;
	case 771:
		return 500;
	case  6739:
		return 500;
	case 13312:
		return 600;
	

	}
	return 2147483647;
}

/**
 * All items in woodcutting
 */
public WoodcuttingSkillShop() {
	super(SHOP_ID, new Item[] { 
			new Item(6739),
			new Item(10941),
			new Item(10939),
			new Item(10940),
			new Item(10933),
			new Item(771) }, 
			false, "Woodcutting Skillpoint Shop");
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

		if (player.getwoodcuttingPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Woodcutting points to buy that."));
			return;
		}

		player.setwoodcuttingPoints(player.getwoodcuttingPoints() - amount * getPrice(id));

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
		return "WoodcuttingPoints";
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
