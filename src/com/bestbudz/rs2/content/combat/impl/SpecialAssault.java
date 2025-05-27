package com.bestbudz.rs2.content.combat.impl;

import com.bestbudz.core.definitions.SpecialAssaultDefinition;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.combat.special.SpecialAssaultHandler;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSpecialBar;

public class SpecialAssault {

	private final Stoner stoner;
	private boolean initialized = false;
	private int barId1 = 0;
	private int barId2 = 0;
	private int amount = 100;
	public static final int FULL_SPECIAL = 100;
	public static final int SPECIAL_TIMER_MAX = 50;

	public SpecialAssault(Stoner stoner) {
	this.stoner = stoner;
	}

	public void afterSpecial() {
	toggleSpecial();
	SpecialAssaultHandler.updateSpecialAmount(stoner, barId2, amount);
	stoner.updateCombatType();
	}

	public boolean clickSpecialButton(int buttonId) {
	toggleSpecial();
	return true;
	}

	public void deduct(int amount) {
	this.amount -= amount;
	}

	public boolean doInstantGraniteMaulSpecial() {
	return (stoner.getCombat().getAssaulting() != null) && (stoner.getCombat().withinDistanceForAssault(stoner.getCombat().getCombatType(), false)) && (stoner.canAssault());
	}

	public int getAmount() {
	return amount;
	}

	public boolean isInitialized() {
	return initialized;
	}

	public void onEquip() {
	Item weapon = stoner.getEquipment().getItems()[3];
	updateSpecialBar(weapon == null ? 0 : weapon.getId());
	if (initialized)
		toggleSpecial();
	}

	public void setInitialized(boolean initialized) {
	this.initialized = initialized;
	}

	public void setSpecialAmount(int amount) {
	this.amount = amount;
	}

	public void tick() {
	TaskQueue.queue(new Task(stoner, 50, false, Task.StackType.NEVER_STACK, Task.BreakType.NEVER, TaskIdentifier.SPECIAL_RESTORE) {
		@Override
		public void execute() {
		if (amount < 100) {
			amount += 10;
			if (amount > 100) {
				amount = 100;
			}
			if (barId1 > 0) {
				SpecialAssaultHandler.updateSpecialBarText(stoner, barId2, amount, initialized);
				SpecialAssaultHandler.updateSpecialAmount(stoner, barId2, amount);
			}
		}
		}

		@Override
		public void onStop() {
		}
	});
	}

	public void toggleSpecial() {
	Item weapon = stoner.getEquipment().getItems()[3];
	if (weapon == null) {
		initialized = false;
		return;
	}

	if ((weapon.getId() == 4153) && (!initialized) && (doInstantGraniteMaulSpecial())) {
		initialized = true;
		stoner.getCombat().assault();
	} else {
		initialized = (!initialized);

		if (weapon.getId() == 15241) {
			int a = stoner.getCombat().getAssaultTimer();
			if (a > 0) {
				if (initialized)
					stoner.getCombat().setAssaultTimer(a + 2);
				else {
					stoner.getCombat().setAssaultTimer(a > 2 ? a - 2 : 1);
				}
			}
		}

		if (barId2 > -1)
			SpecialAssaultHandler.updateSpecialBarText(stoner, barId2, amount, initialized);
	}
	}

	public void update() {
	SpecialAssaultHandler.updateSpecialBarText(stoner, barId2, amount, initialized);
	SpecialAssaultHandler.updateSpecialAmount(stoner, barId2, amount);
	}

	public void updateSpecialBar(int weaponId) {
	SpecialAssaultDefinition def = Item.getSpecialDefinition(weaponId);

	stoner.getClient().queueOutgoingPacket(new SendConfig(78, 0));

	if (def != null) {
		barId1 = def.getBarId1();
		barId2 = def.getBarId2();
		def.getButton();

		if (weaponId == 15486)
			stoner.getClient().queueOutgoingPacket(new SendConfig(78, 1));
		else {
			stoner.getClient().queueOutgoingPacket(new SendSpecialBar(0, barId1));
		}

		update();
	} else {
		barId1 = 0;
		barId2 = 0;
		stoner.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7549));
		stoner.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7561));
		stoner.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7574));
		stoner.getClient().queueOutgoingPacket(new SendSpecialBar(1, 12323));
		stoner.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7599));
		stoner.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7674));
		stoner.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7474));
		stoner.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7499));
		stoner.getClient().queueOutgoingPacket(new SendSpecialBar(1, 8493));
		stoner.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7574));
		stoner.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7624));
		stoner.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7699));
		stoner.getClient().queueOutgoingPacket(new SendSpecialBar(1, 7800));
		stoner.getClient().queueOutgoingPacket(new SendSpecialBar(-1, 0));
	}
	}
}
