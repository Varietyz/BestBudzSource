package com.bestbudz.rs2.content.profession.handiness;

import com.bestbudz.rs2.entity.item.Item;
import java.util.HashMap;
import java.util.Map;

public enum Spinnable {
	BOWSTRING(new Item(1779), new Item(1777), 15.0D, 10),
	WOOL(new Item(1737), new Item(1759), 2.5D, 1),
	ROPE(new Item(10814), new Item(955), 25.0D, 30),
	MAGE_STRING(new Item(6051), new Item(6038), 30.0D, 19),
	YEW_STRING(new Item(6049), new Item(9438), 15.0D, 10),
	SINEW_STRING(new Item(9436), new Item(9438), 15.0D, 10);

	public static Map<Integer, Spinnable> spins = new HashMap<Integer, Spinnable>();
	private final Item item;
	private final Item outcome;
	private final double experience;
	private final int requiredGrade;

	Spinnable(Item item, Item outcome, double experience, int requiredGrade) {
	this.item = item;
	this.outcome = outcome;
	this.experience = experience;
	this.requiredGrade = requiredGrade;
	}

	public static final void declare() {
	for (Spinnable spinnable : values())
		spins.put(Integer.valueOf(spinnable.getItem().getId()), spinnable);
	}

	public static Spinnable forId(int id) {
	return spins.get(Integer.valueOf(id));
	}

	public double getExperience() {
	return experience;
	}

	public Item getItem() {
	return item;
	}

	public Item getOutcome() {
	return outcome;
	}

	public int getRequiredGrade() {
	return requiredGrade;
	}
}
