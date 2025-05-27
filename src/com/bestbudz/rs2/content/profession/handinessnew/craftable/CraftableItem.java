package com.bestbudz.rs2.content.profession.handinessnew.craftable;

import com.bestbudz.rs2.entity.item.Item;

public final class CraftableItem {

	private final Item product;
	private final Item requiredItem;
	private final int grade;
	private final double experience;

	public CraftableItem(Item product, Item requiredItem, int grade, double experience) {
	this.product = product;
	this.requiredItem = requiredItem;
	this.grade = grade;
	this.experience = experience;
	}

	public Item getProduct() {
	return product;
	}

	public Item getRequiredItem() {
	return requiredItem;
	}

	public int getGrade() {
	return grade;
	}

	public double getExperience() {
	return experience;
	}
}