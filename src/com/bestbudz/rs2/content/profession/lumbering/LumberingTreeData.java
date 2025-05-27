package com.bestbudz.rs2.content.profession.lumbering;

import com.bestbudz.core.cache.map.ObjectDef;

public enum LumberingTreeData {
	NORMAL_TREE(1, 1511, -1, -1, 125.0D),
	DEAD_TREE(1, 1511, -1, -1, 125.0D),
	OAK_TREE(1, 1521, -1, -1, 137.5D),
	WILLOW_TREE(1, 1519, -1, -1, 167.5D),
	MAPLE_TREE(1, 1517, -1, -1, 300.0D),
	YEW_TREE(1, 1515, -1, -1, 605.0D),
	MAGIC_TREE(1, 1513, -1, -1, 950.0D);

	int gradeRequired;
	int reward;
	int replacementId;
	int respawnTimer;
	double experience;

	public static LumberingTreeData forId(int objectId) {
	ObjectDef def = ObjectDef.getObjectDef(objectId);

	if (def == null || def.name == null) {
		return null;
	}

	String name = def.name.toLowerCase().trim();

	switch (name) {
	case "dead tree":
		return DEAD_TREE;
	case "oak tree":
	case "oak":
		return OAK_TREE;
	case "willow tree":
	case "willow":
		return WILLOW_TREE;
	case "maple tree":
	case "maple":
		return MAPLE_TREE;
	case "yew tree":
	case "yew":
		return YEW_TREE;
	case "magic tree":
	case "magic":
		return MAGIC_TREE;
	case "tree":
		return NORMAL_TREE;
	default:
		return null;
	}
	}

	private LumberingTreeData(int grade, int reward, int replacement, int respawnTimer, double experience) {
	gradeRequired = grade;
	this.reward = reward;
	replacementId = replacement;
	this.respawnTimer = respawnTimer;
	this.experience = experience;
	}

	public double getExperience() {
	return experience;
	}

	public int getGradeRequired() {
	return gradeRequired;
	}

	public int getReplacement() {
	return replacementId;
	}

	public int getRespawnTimer() {
	return respawnTimer;
	}

	public int getReward() {
	return reward;
	}
}
