package com.bestbudz.rs2.content.shopping.impl;

import com.bestbudz.rs2.content.shopping.Shop;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Shop for tokkul currency
 * 
 * @author Jaybane
 */
public class TokkulShop extends Shop {

	/**
	 * Item id of tokkul
	 */
	public static final int TOKKUL = 6529;

	/**
	 * Id of tokkul store
	 */
	public static final int SHOP_ID = 4;

	/**
	 * Prices of items in store
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	case 6571:
		return 25000;
	case 6568:
		return 15000;
	case 6524:
	case 6528:
		return 20000;
	}

	return 2147483647;
	}

	/**
	 * Items in store
	 */
	public TokkulShop() {
	super(SHOP_ID, new Item[] { new Item(6571, 1), }, false, "Tokkul Shop");
	}

	@Override
	public void buy(Stoner stoner, int slot, int id, int amount) {
	if (!hasItem(slot, id))
		return;
	if (get(slot).getAmount() == 0)
		return;
	if (amount > get(slot).getAmount()) {
		amount = get(slot).getAmount();
	}

	Item buying = new Item(id, amount);

	if (!stoner.getBox().hasSpaceFor(buying)) {
		if (!buying.getDefinition().isStackable()) {
			int slots = stoner.getBox().getFreeSlots();
			if (slots > 0) {
				buying.setAmount(slots);
				amount = slots;
			} else {
				stoner.getClient().queueOutgoingPacket(new SendMessage("You do not have enough box space to buy this item."));
			}
		} else {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You do not have enough box space to buy this item."));
			return;
		}
	}

	if (stoner.getBox().getItemAmount(6529) < amount * getPrice(id)) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Tokull to buy that."));
		return;
	}

	stoner.getBox().remove(6529, amount * getPrice(id));

	stoner.getBox().add(buying);
	update();
	}

	@Override
	public int getBuyPrice(int id) {
	return 0;
	}

	@Override
	public String getCurrencyName() {
	return "Tokkul";
	}

	@Override
	public int getSellPrice(int id) {
	return getPrice(id);
	}

	@Override
	public boolean sell(Stoner stoner, int id, int amount) {
	stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot sell items to this shop."));
	return false;
	}
}
