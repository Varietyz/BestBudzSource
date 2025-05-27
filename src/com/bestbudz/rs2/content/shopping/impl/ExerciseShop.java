package com.bestbudz.rs2.content.shopping.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.content.shopping.Shop;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class ExerciseShop extends Shop {

	public static final int SHOP_ID = 91;

	public ExerciseShop() {
	super(91, new Item[] {

			new Item(2997, 1), new Item(9472, 1), new Item(3257, 1) }, false, "Exercisement Shop");

	for (Item i : getItems())
		if (i != null)
			i.getDefinition().setUntradable();
	}

	public static final int getPrice(int id) {

	if (id == 3257) {
		return 1250;
	}

	return 500;
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

	if (stoner.getBox().getItemAmount(2996) < amount * getPrice(id)) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You do not have enough exercisement tokens to buy that."));
		return;
	}

	stoner.getBox().remove(2996, amount * getPrice(id), true);

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
	return "Exercisement tokens";
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
