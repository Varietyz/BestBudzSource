package com.bestbudz.rs2.content.shopping.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.content.shopping.Shop;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Shop for pest cannacredits
 * 
 * @author Jaybane
 */
public class CannaCreditsShop2 extends Shop {

	/**
	 * Id of shop
	 */
	public static final int SHOP_ID = 90;

	/**
	 * Prices of item in shop
	 * 
	 * @param id
	 * @return
	 */
	public static final int getPrice(int id) {
	switch (id) {
	case 13173:
		return 900000;

	case 1038:
	case 1040:
	case 1042:
	case 1044:
	case 1046:
	case 1048:
		return 150000;

	case 11863:
	case 11862:
	case 11847:
	case 4084:
		return 250000;

	case 12399:
		return 300000;

	case 13175:
		return 500000;

	case 3140:
	case 11335:
	case 4151:
	case 6585:
	case 12004:
		return 750;

	case 1052:
	case 11840:
	case 12954:
		return 500;

	case 6570:
		return 6000;

	case 7462:
		return 3000;

	case 11283:
	case 11235:
		return 10000;

	case 1053:
	case 1055:
	case 1057:
	case 1050:
		return 100000;

	case 6731:
	case 6733:
	case 6735:
	case 6737:
		return 6500;

	case 11907:
		return 8500;

	}

	return 150;
	}

	/**
	 * Items in shop
	 */
	public CannaCreditsShop2() {
	super(SHOP_ID, new Item[] { new Item(13173), }, false, "Being reworked");
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

	if (stoner.getCredits() < amount * getPrice(id)) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You do not have enough CannaCredits to buy that."));
		return;
	}

	stoner.setCredits(stoner.getCredits() - (amount * getPrice(id)));

	InterfaceHandler.writeText(new QuestTab(stoner));

	stoner.getBox().add(buying);
	update();
	}

	@Override
	public int getBuyPrice(int id) {
	return 0;
	}

	@Override
	public String getCurrencyName() {
	return "CannaCredits";
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
