package com.bestbudz.rs2.content.minigames.duelarena;

import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemContainer;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItems;

public class StakingContainer extends ItemContainer {
	private final Stoner stoner;

	public StakingContainer(Stoner p) {
	super(28, ItemContainer.ContainerTypes.STACK, true, true);
	stoner = p;
	}

	@Override
	public boolean allowZero(int id) {
	return false;
	}

	public void offer(int id, int amount, int slot) {

	if (!stoner.getDueling().canAppendStake()) {
		return;
	}

	if (!Item.getDefinition(id).isTradable()) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot stake that item."));
		return;
	}

	int invAmount = stoner.getBox().getItemAmount(id);

	if (invAmount == 0)
		return;
	if (invAmount < amount) {
		amount = invAmount;
	}

	int removed = stoner.getBox().remove(new Item(id, amount));

	if (removed > 0) {
		add(id, removed);

		withdraw(getItemSlot(995), -(amount));
	} else {
		return;
	}

	stoner.getDueling().onStake();
	stoner.getDueling().getInteracting().getDueling().onStake();

	update();
	}

	@Override
	public void onAdd(Item item) {
	}

	@Override
	public void onFillContainer() {
	}

	@Override
	public void onMaxStack() {
	}

	@Override
	public void onRemove(Item item) {
	}

	@Override
	public void update() {
	stoner.getClient().queueOutgoingPacket(new SendUpdateItems(6669, getItems()));
	stoner.getClient().queueOutgoingPacket(new SendUpdateItems(3322, stoner.getBox().getItems()));
	if (stoner.getDueling().getInteracting() != null)
		stoner.getDueling().getInteracting().getClient().queueOutgoingPacket(new SendUpdateItems(6670, getItems()));
	}

	public void withdraw(int slot, int amount) {
	if ((get(slot) == null) || (!stoner.getDueling().canAppendStake())) {
		return;
	}

	int id = get(slot).getId();

	int removed = remove(id, amount);

	if (removed > 0)
		stoner.getBox().add(new Item(id, removed));
	else {
		return;
	}

	stoner.getDueling().onStake();
	stoner.getDueling().getInteracting().getDueling().onStake();

	update();
	}
}
