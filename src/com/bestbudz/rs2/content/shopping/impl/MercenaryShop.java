package com.bestbudz.rs2.content.shopping.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.content.shopping.Shop;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class MercenaryShop extends Shop {

	public static final int SHOP_ID = 6;

	public MercenaryShop() {
	super(SHOP_ID, new Item[] { new Item(4155), new Item(2528), new Item(11866), new Item(11864), new Item(4212), new Item(4224), new Item(10548), new Item(10551), new Item(10555), new Item(6720), new Item(4166), new Item(4164), new Item(1844), new Item(1845), new Item(1846), new Item(4081), new Item(4170), new Item(6708), new Item(7454), new Item(7455), new Item(7456), new Item(7457), new Item(7458), new Item(7459), new Item(7460), new Item(7461), new Item(7462),

	}, false, "Mercenary Store");
	}

	public static final int getPrice(int id) {
	switch (id) {
	case 4155:
		return 0;
	case 2528:
		return 15;
	case 11866:
		return 50;
	case 11864:
		return 450;
	case 4212:
	case 4224:
		return 350;
	case 6720:
		return 5;
	case 4166:
		return 5;
	case 4164:
		return 5;
	case 1844:
		return 5;
	case 1845:
		return 5;
	case 1846:
		return 5;
	case 4081:
		return 5;
	case 4170:
		return 10;
	case 6708:
		return 5;
	case 10548:
		return 85;
	case 10551:
		return 125;
	case 10555:
		return 75;
	case 7454:
		return 10;
	case 7455:
		return 15;
	case 7456:
		return 20;
	case 7457:
		return 25;
	case 7458:
		return 30;
	case 7459:
		return 35;
	case 7460:
		return 40;
	case 7461:
		return 45;
	case 7462:
		return 50;
	}
	return 2147483647;
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

	if (stoner.getMercenaryPoints() < amount * getPrice(id)) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You do not have enough Mercenary points to buy that."));
		return;
	}

	stoner.setMercenaryPoints(stoner.getMercenaryPoints() - amount * getPrice(id));

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
	return "Mercenary points";
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
