package com.bestbudz.rs2.auto.combat.config;

public class AutoCombatConfig {

	public static final int DEFAULT_RADIUS = 8;
	public static final int SPECIAL_ATTACK_CHANCE = 25;

	public static final boolean COMBAT_FIRST_MODE = true;

	public static final long MIN_STYLE_DURATION_MS = 180000;
	public static final long MAX_STYLE_DURATION_MS = 420000;
	public static final long GEAR_CHECK_INTERVAL_MS = 30000;
	public static final long VARIETY_FORCE_INTERVAL_MS = 600000;

	public static final int STYLE_SWITCH_BASE_CHANCE = 15;
	public static final int STYLE_SWITCH_INCREASE_PER_MINUTE = 5;

	public static final int MELEE_MAX_EFFECTIVE_DISTANCE = 3;
	public static final int SAGITTARIUS_MAX_EFFECTIVE_DISTANCE = 8;
	public static final int MEDIUM_DISTANCE_THRESHOLD = 2;
	public static final int LONG_DISTANCE_THRESHOLD = 4;
	public static final int VERY_LONG_DISTANCE_THRESHOLD = 6;

	public static final int SPECIAL_WEAPON_BONUS = 100;
	public static final int TWO_HANDED_WEAPON_BONUS = 30;
	public static final int ATTACK_BONUS_MULTIPLIER = 2;
	public static final int STRENGTH_BONUS_MULTIPLIER = 5;

	public static final int STYLE_SPECIFIC_ARMOR_BONUS = 100;
	public static final int MELEE_ATTACK_BONUS_MULTIPLIER = 10;
	public static final int MELEE_STRENGTH_BONUS_MULTIPLIER = 15;
	public static final int MELEE_DEFENSE_BONUS_MULTIPLIER = 5;
	public static final int MELEE_DEFENSE_BONUS_MULTIPLIER_NO_OFFENSE = 5;
	public static final int SAGITTARIUS_ATTACK_BONUS_MULTIPLIER = 15;
	public static final int SAGITTARIUS_STRENGTH_BONUS_MULTIPLIER = 10;
	public static final int SAGITTARIUS_DEFENSE_BONUS_MULTIPLIER = 3;
	public static final int MAGE_ATTACK_BONUS_MULTIPLIER = 15;
	public static final int MAGE_DAMAGE_BONUS_MULTIPLIER = 12;
	public static final int MAGE_DEFENSE_BONUS_MULTIPLIER = 3;

	public static final int ARMOR_IMPROVEMENT_THRESHOLD = 75;

	public static final int MELEE_BASE_PREFERENCE = 40;
	public static final int SAGITTARIUS_BASE_PREFERENCE = 35;
	public static final int MAGE_BASE_PREFERENCE = 35;
	public static final int WEAKNESS_MODIFIER = 15;
	public static final int DISTANCE_PREFERENCE_BONUS = 20;
	public static final int STYLE_VARIETY_BONUS = 25;

	public static final int STYLE_HISTORY_SIZE = 3;
	public static final int GEAR_HISTORY_SIZE = 3;

	public static final int MINIMUM_SPECIAL_ENERGY = 25;
	public static final int SPELL_CAST_CHANCE_WITHOUT_MAGE_GEAR = 15;

	public static final int[] DAMAGE_SPELLS = {
		1160, 1163, 1166, 1169, 1172, 1175, 1177, 1181, 1183, 1185, 1188, 1189,
		12861, 12871, 12881, 12891, 12901, 12911, 12919, 12929, 12939, 12951,
		12963, 12975, 12987, 12999, 13011, 13023
	};

	public static final int[] SELF_CONTAINED_WEAPONS = {4214, 10034, 10033, 12924, 12926};

	public static final int WEAPON_SLOT = 3;
	public static final int SHIELD_SLOT = 5;
	public static final int AMMO_SLOT = 13;
	public static final int[] ARMOR_SLOTS = {0, 1, 2, 4, 7, 9, 10, 12};

	public static final boolean ENABLE_DEBUG_OUTPUT = false;
	public static final boolean ENABLE_DISCORD_BOT_DEBUG = true;
}
