package com.bestbudz.rs2.content.profession.lumbering;

import java.util.HashMap;
import java.util.Map;

import com.bestbudz.rs2.entity.Animation;

public enum LumberingAxeData {

	DRAGON_AXE(6575, 1, 20, new Animation(2846));

	public static final void declare() {
	for (LumberingAxeData data : values())
		axes.put(Integer.valueOf(data.getId()), data);
	}

	int itemId;
	int gradeRequired;
	int bonus;
	Animation animation;

	private static Map<Integer, LumberingAxeData> axes = new HashMap<Integer, LumberingAxeData>();

	public static LumberingAxeData forId(int id) {
	return axes.get(Integer.valueOf(id));
	}

	private LumberingAxeData(int id, int grade, int bonus, Animation animation) {
	itemId = id;
	gradeRequired = grade;
	this.bonus = bonus;
	this.animation = animation;
	}

	public Animation getAnimation() {
	return animation;
	}

	public int getBonus() {
	return bonus;
	}

	public int getId() {
	return itemId;
	}

	public int getGradeRequired() {
	return gradeRequired;
	}
}
