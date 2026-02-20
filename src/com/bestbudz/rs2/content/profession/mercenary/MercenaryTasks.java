package com.bestbudz.rs2.content.profession.mercenary;

import com.bestbudz.core.util.Utility;

public class MercenaryTasks {

	private static final String[] NORMAL_TASK_POOL = {
		"man", "cow", "rock crab", "chaos druid", "hill giant", "giant bat",
		"crawling hand", "skeleton", "black knight", "poison scorpion",
		"poison spider", "chaos dwarf", "banshee",

		"baby dragon", "red dragon", "lesser demon", "greater demon",
		"green dragon", "fire giant", "moss giant",

		"bronze dragon", "iron dragon", "steel dragon", "black dragon",
		"lava dragon", "hellhound", "black demon", "abyssal demon", "dark beast"
	};

	private static final String[] BOSS_TASK_POOL = {
		"zulrah", "king black dragon", "kree'arra", "commander zilyana",
		"corporeal beast", "barrelchest", "kraken", "giant mole",
		"chaos elemental", "callisto", "vet'ion", "chaos fanatic", "general graardor"
	};

	public static String getRandomNormalTask() {
		return NORMAL_TASK_POOL[Utility.randomNumber(NORMAL_TASK_POOL.length)];
	}

	public static String getRandomBossTask() {
		return BOSS_TASK_POOL[Utility.randomNumber(BOSS_TASK_POOL.length)];
	}
}