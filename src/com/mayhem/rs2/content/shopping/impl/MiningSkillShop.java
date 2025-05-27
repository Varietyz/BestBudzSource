package com.mayhem.rs2.content.shopping.impl;

import com.mayhem.rs2.content.shopping.Shop;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Mining store
 * 
 * @author Dez
 */
public class MiningSkillShop extends Shop {

	/**
	 * Id of Mining shop
	 */
	public static final int SHOP_ID = 330;

	/**
	 * Price of items in Mining Skill store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
		switch (id) {
		
		case 2528:
			return 10;
		
		case 12013:
			return 100;
		
		case 12014:
			return 100;
			
		case 12015:
			return 100;
			
		case 12016:
			return 100;
		case 7500:
			return 300;
		case 11920:
			return 500;
		

		}
		return 2147483647;
	}

	/**
	 * All items in mining
	 */
	public MiningSkillShop() {
		super(SHOP_ID, new Item[] { 
				new Item(11920),
				new Item(12013),
				new Item(12014),
				new Item(12015),
				new Item(12016),
				new Item(7500),
				}, 
				false, "Mining Skillpoint Shop");
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

		if (player.getminingPoints() < amount * getPrice(id)) {
			player.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Mining points to buy that."));
			return;
		}

		player.setminingPoints(player.getminingPoints() - amount * getPrice(id));

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
		return "Mining Points";
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
