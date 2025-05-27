package com.bestbudz.rs2.content.profession.woodcarving.fletchable;

import com.bestbudz.rs2.entity.item.Item;

public final class FletchableItem {

	private final Item product;

	private final int grade;

	private final double experience;

	public FletchableItem(Item product, int grade, double experience) {
	this.product = product;
	this.grade = grade;
	this.experience = experience;
	}

	public Item getProduct() {
	return product;
	}

	public int getGrade() {
	return grade;
	}

	public double getExperience() {
	return experience;
	}
}