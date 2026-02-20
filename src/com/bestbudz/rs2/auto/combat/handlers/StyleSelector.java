package com.bestbudz.rs2.auto.combat.handlers;

import com.bestbudz.rs2.auto.combat.config.AutoCombatConfig;
import com.bestbudz.rs2.auto.combat.equipment.EquipmentManager;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class StyleSelector {

	private final Stoner stoner;
	private final EquipmentManager equipmentManager;

	public StyleSelector(Stoner stoner, EquipmentManager equipmentManager) {
		this.stoner = stoner;
		this.equipmentManager = equipmentManager;
	}

	public CombatTypes determineOptimalStyle(Mob target, int distance) {

		CombatTypes currentStyle = equipmentManager.getCurrentCombatStyle();
		if (currentStyle == null) {
			return getDistanceBasedStyle(distance);
		}

		CombatTypes distanceBased = getOptimalStyleForDistance(distance);
		CombatTypes weaknessBased = getOptimalStyleForWeakness(target);

		if (distanceBased != null && equipmentManager.hasGearForStyle(distanceBased)) {
			return distanceBased;
		} else if (weaknessBased != null && equipmentManager.hasGearForStyle(weaknessBased)) {
			return weaknessBased;
		}

		return getAnyAvailableStyle();
	}

	private CombatTypes getOptimalStyleForDistance(int distance) {
		if (distance > AutoCombatConfig.VERY_LONG_DISTANCE_THRESHOLD) {
			return CombatTypes.MAGE;
		} else if (distance > AutoCombatConfig.MEDIUM_DISTANCE_THRESHOLD) {
			return CombatTypes.SAGITTARIUS;
		} else {
			return CombatTypes.MELEE;
		}
	}

	private CombatTypes getOptimalStyleForWeakness(Mob target) {
		double meleeWeakness = target.getMeleeWeaknessMod();
		double sagittariusWeakness = target.getSagittariusWeaknessMod();
		double mageWeakness = target.getMageWeaknessMod();

		if (meleeWeakness >= sagittariusWeakness && meleeWeakness >= mageWeakness) {
			return CombatTypes.MELEE;
		} else if (sagittariusWeakness >= mageWeakness) {
			return CombatTypes.SAGITTARIUS;
		} else {
			return CombatTypes.MAGE;
		}
	}

	private CombatTypes getDistanceBasedStyle(int distance) {

		CombatTypes optimal = getOptimalStyleForDistance(distance);

		if (optimal != null && equipmentManager.hasGearForStyle(optimal)) {
			return optimal;
		}

		CombatTypes[] fallbackOrder = getFallbackStyleOrder(null, distance);
		for (CombatTypes style : fallbackOrder) {
			if (equipmentManager.hasGearForStyle(style)) {
				return style;
			}
		}

		return CombatTypes.MELEE;
	}

	private CombatTypes getAnyAvailableStyle() {

		CombatTypes[] preferenceOrder = {CombatTypes.MELEE, CombatTypes.SAGITTARIUS, CombatTypes.MAGE};

		for (CombatTypes style : preferenceOrder) {
			if (equipmentManager.hasGearForStyle(style)) {
				return style;
			}
		}

		return CombatTypes.MELEE;
	}

	public int calculateStyleScore(CombatTypes style, Mob target, int distance) {
		if (style == null || !equipmentManager.hasGearForStyle(style)) {
			return -1;
		}

		int score = 0;

		switch (style) {
			case MELEE:
				score += AutoCombatConfig.MELEE_BASE_PREFERENCE;
				break;
			case SAGITTARIUS:
				score += AutoCombatConfig.SAGITTARIUS_BASE_PREFERENCE;
				break;
			case MAGE:
				score += AutoCombatConfig.MAGE_BASE_PREFERENCE;
				break;
		}

		score += calculateDistanceBonus(style, distance);

		score += calculateWeaknessBonus(style, target);

		return score;
	}

	private int calculateDistanceBonus(CombatTypes style, int distance) {
		switch (style) {
			case MELEE:
				if (distance <= AutoCombatConfig.MEDIUM_DISTANCE_THRESHOLD) {
					return AutoCombatConfig.DISTANCE_PREFERENCE_BONUS;
				} else if (distance <= AutoCombatConfig.MELEE_MAX_EFFECTIVE_DISTANCE) {
					return AutoCombatConfig.DISTANCE_PREFERENCE_BONUS / 2;
				}
				return 0;

			case SAGITTARIUS:
				if (distance > AutoCombatConfig.MEDIUM_DISTANCE_THRESHOLD &&
					distance <= AutoCombatConfig.VERY_LONG_DISTANCE_THRESHOLD) {
					return AutoCombatConfig.DISTANCE_PREFERENCE_BONUS;
				} else if (distance <= AutoCombatConfig.SAGITTARIUS_MAX_EFFECTIVE_DISTANCE) {
					return AutoCombatConfig.DISTANCE_PREFERENCE_BONUS / 2;
				}
				return 0;

			case MAGE:
				if (distance > AutoCombatConfig.VERY_LONG_DISTANCE_THRESHOLD) {
					return AutoCombatConfig.DISTANCE_PREFERENCE_BONUS;
				} else if (distance > AutoCombatConfig.LONG_DISTANCE_THRESHOLD) {
					return AutoCombatConfig.DISTANCE_PREFERENCE_BONUS / 2;
				}
				return 0;

			default:
				return 0;
		}
	}

	private int calculateWeaknessBonus(CombatTypes style, Mob target) {
		switch (style) {
			case MELEE:
				return (int)(target.getMeleeWeaknessMod() * AutoCombatConfig.WEAKNESS_MODIFIER);
			case SAGITTARIUS:
				return (int)(target.getSagittariusWeaknessMod() * AutoCombatConfig.WEAKNESS_MODIFIER);
			case MAGE:
				return (int)(target.getMageWeaknessMod() * AutoCombatConfig.WEAKNESS_MODIFIER);
			default:
				return 0;
		}
	}

	public boolean isStyleSuboptimalForDistance(CombatTypes style, int distance) {
		if (style == null) return true;

		switch (style) {
			case MELEE:
				return distance > AutoCombatConfig.MELEE_MAX_EFFECTIVE_DISTANCE;
			case SAGITTARIUS:
				return distance > AutoCombatConfig.SAGITTARIUS_MAX_EFFECTIVE_DISTANCE;
			case MAGE:
				return false;
			default:
				return true;
		}
	}

	public boolean isStyleIneffective(CombatTypes style, int distance) {
		if (style == null) return true;

		switch (style) {
			case MELEE:

				return distance > AutoCombatConfig.MELEE_MAX_EFFECTIVE_DISTANCE;
			case SAGITTARIUS:

				return distance > AutoCombatConfig.SAGITTARIUS_MAX_EFFECTIVE_DISTANCE;
			case MAGE:

				return false;
			default:
				return true;
		}
	}

	public CombatTypes[] getFallbackStyleOrder(Mob target, int distance) {
		if (distance <= AutoCombatConfig.MEDIUM_DISTANCE_THRESHOLD) {

			return new CombatTypes[]{CombatTypes.MELEE, CombatTypes.SAGITTARIUS, CombatTypes.MAGE};
		} else if (distance <= AutoCombatConfig.VERY_LONG_DISTANCE_THRESHOLD) {

			return new CombatTypes[]{CombatTypes.SAGITTARIUS, CombatTypes.MAGE, CombatTypes.MELEE};
		} else {

			return new CombatTypes[]{CombatTypes.MAGE, CombatTypes.SAGITTARIUS, CombatTypes.MELEE};
		}
	}

	public CombatTypes getIdealStyleForDistance(int distance) {
		return getOptimalStyleForDistance(distance);
	}

	public boolean isUrgentStyleChangeNeeded(CombatTypes currentStyle, int distance) {
		return isStyleIneffective(currentStyle, distance);
	}
}
