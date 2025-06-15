package com.bestbudz.rs2.auto.combat.handlers;

import com.bestbudz.rs2.auto.combat.config.AutoCombatConfig;
import com.bestbudz.rs2.auto.combat.equipment.EquipmentManager;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Handles combat style selection logic based on target analysis and distance
 * Now deterministic and memory-aware to work with the time-based system
 */
public class StyleSelector {

	private final Stoner stoner;
	private final EquipmentManager equipmentManager;

	public StyleSelector(Stoner stoner, EquipmentManager equipmentManager) {
		this.stoner = stoner;
		this.equipmentManager = equipmentManager;
	}

	/**
	 * Determine optimal combat style based on target and distance (deterministic)
	 */
	public CombatTypes determineOptimalStyle(Mob target, int distance) {
		// If no gear equipped, prioritize distance-based selection
		CombatTypes currentStyle = equipmentManager.getCurrentCombatStyle();
		if (currentStyle == null) {
			return getDistanceBasedStyle(distance);
		}

		// Get the theoretically best style for this situation
		CombatTypes distanceBased = getOptimalStyleForDistance(distance);
		CombatTypes weaknessBased = getOptimalStyleForWeakness(target);

		// Prioritize distance appropriateness over weakness
		if (distanceBased != null && equipmentManager.hasGearForStyle(distanceBased)) {
			return distanceBased;
		} else if (weaknessBased != null && equipmentManager.hasGearForStyle(weaknessBased)) {
			return weaknessBased;
		}

		// Fallback to any available style
		return getAnyAvailableStyle();
	}

	/**
	 * Get optimal style purely based on distance (no randomness)
	 */
	private CombatTypes getOptimalStyleForDistance(int distance) {
		if (distance > AutoCombatConfig.VERY_LONG_DISTANCE_THRESHOLD) {
			return CombatTypes.MAGE; // Best for very long distances
		} else if (distance > AutoCombatConfig.MEDIUM_DISTANCE_THRESHOLD) {
			return CombatTypes.SAGITTARIUS; // Best for medium-long distances
		} else {
			return CombatTypes.MELEE; // Best for close combat
		}
	}

	/**
	 * Get optimal style based on target weaknesses (deterministic)
	 */
	private CombatTypes getOptimalStyleForWeakness(Mob target) {
		double meleeWeakness = target.getMeleeWeaknessMod();
		double sagittariusWeakness = target.getSagittariusWeaknessMod();
		double mageWeakness = target.getMageWeaknessMod();

		// Find the highest weakness (no randomness added)
		if (meleeWeakness >= sagittariusWeakness && meleeWeakness >= mageWeakness) {
			return CombatTypes.MELEE;
		} else if (sagittariusWeakness >= mageWeakness) {
			return CombatTypes.SAGITTARIUS;
		} else {
			return CombatTypes.MAGE;
		}
	}

	/**
	 * Get style based on distance when no gear is equipped (deterministic)
	 */
	private CombatTypes getDistanceBasedStyle(int distance) {
		// Prioritize by distance first, then by availability
		CombatTypes optimal = getOptimalStyleForDistance(distance);

		if (optimal != null && equipmentManager.hasGearForStyle(optimal)) {
			return optimal;
		}

		// Try fallback styles in order of preference for this distance
		CombatTypes[] fallbackOrder = getFallbackStyleOrder(null, distance);
		for (CombatTypes style : fallbackOrder) {
			if (equipmentManager.hasGearForStyle(style)) {
				return style;
			}
		}

		return CombatTypes.MELEE; // Final fallback
	}

	/**
	 * Get any available style (for when others fail)
	 */
	private CombatTypes getAnyAvailableStyle() {
		// Try in order of general preference
		CombatTypes[] preferenceOrder = {CombatTypes.MELEE, CombatTypes.SAGITTARIUS, CombatTypes.MAGE};

		for (CombatTypes style : preferenceOrder) {
			if (equipmentManager.hasGearForStyle(style)) {
				return style;
			}
		}

		return CombatTypes.MELEE; // Final fallback
	}

	/**
	 * Calculate comprehensive style score for the new system
	 * Used by AutoCombat for weighted decision making
	 */
	public int calculateStyleScore(CombatTypes style, Mob target, int distance) {
		if (style == null || !equipmentManager.hasGearForStyle(style)) {
			return -1; // Not available
		}

		int score = 0;

		// Base preference from config
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

		// Distance appropriateness bonus
		score += calculateDistanceBonus(style, distance);

		// Target weakness bonus
		score += calculateWeaknessBonus(style, target);

		return score;
	}

	/**
	 * Calculate distance appropriateness bonus
	 */
	private int calculateDistanceBonus(CombatTypes style, int distance) {
		switch (style) {
			case MELEE:
				if (distance <= AutoCombatConfig.MEDIUM_DISTANCE_THRESHOLD) {
					return AutoCombatConfig.DISTANCE_PREFERENCE_BONUS;
				} else if (distance <= AutoCombatConfig.MELEE_MAX_EFFECTIVE_DISTANCE) {
					return AutoCombatConfig.DISTANCE_PREFERENCE_BONUS / 2; // Half bonus for acceptable range
				}
				return 0;

			case SAGITTARIUS:
				if (distance > AutoCombatConfig.MEDIUM_DISTANCE_THRESHOLD &&
					distance <= AutoCombatConfig.VERY_LONG_DISTANCE_THRESHOLD) {
					return AutoCombatConfig.DISTANCE_PREFERENCE_BONUS;
				} else if (distance <= AutoCombatConfig.SAGITTARIUS_MAX_EFFECTIVE_DISTANCE) {
					return AutoCombatConfig.DISTANCE_PREFERENCE_BONUS / 2; // Half bonus for acceptable range
				}
				return 0;

			case MAGE:
				if (distance > AutoCombatConfig.VERY_LONG_DISTANCE_THRESHOLD) {
					return AutoCombatConfig.DISTANCE_PREFERENCE_BONUS;
				} else if (distance > AutoCombatConfig.LONG_DISTANCE_THRESHOLD) {
					return AutoCombatConfig.DISTANCE_PREFERENCE_BONUS / 2; // Half bonus for acceptable range
				}
				return 0; // Mage works at all distances but is best at long range

			default:
				return 0;
		}
	}

	/**
	 * Calculate target weakness bonus
	 */
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

	/**
	 * Check if current style is suboptimal for distance
	 */
	public boolean isStyleSuboptimalForDistance(CombatTypes style, int distance) {
		if (style == null) return true;

		switch (style) {
			case MELEE:
				return distance > AutoCombatConfig.MELEE_MAX_EFFECTIVE_DISTANCE;
			case SAGITTARIUS:
				return distance > AutoCombatConfig.SAGITTARIUS_MAX_EFFECTIVE_DISTANCE;
			case MAGE:
				return false; // Mage works at all distances
			default:
				return true;
		}
	}

	/**
	 * Check if style is completely ineffective (should force immediate switch)
	 */
	public boolean isStyleIneffective(CombatTypes style, int distance) {
		if (style == null) return true;

		switch (style) {
			case MELEE:
				// Melee becomes ineffective beyond max range
				return distance > AutoCombatConfig.MELEE_MAX_EFFECTIVE_DISTANCE;
			case SAGITTARIUS:
				// Sagittarius becomes ineffective beyond max range
				return distance > AutoCombatConfig.SAGITTARIUS_MAX_EFFECTIVE_DISTANCE;
			case MAGE:
				// Mage is never ineffective
				return false;
			default:
				return true;
		}
	}

	/**
	 * Get fallback style order based on target distance (deterministic)
	 */
	public CombatTypes[] getFallbackStyleOrder(Mob target, int distance) {
		if (distance <= AutoCombatConfig.MEDIUM_DISTANCE_THRESHOLD) {
			// Close range: prefer melee, then ranged options
			return new CombatTypes[]{CombatTypes.MELEE, CombatTypes.SAGITTARIUS, CombatTypes.MAGE};
		} else if (distance <= AutoCombatConfig.VERY_LONG_DISTANCE_THRESHOLD) {
			// Medium range: prefer sagittarius, then others
			return new CombatTypes[]{CombatTypes.SAGITTARIUS, CombatTypes.MAGE, CombatTypes.MELEE};
		} else {
			// Long range: prefer mage, then sagittarius, avoid melee
			return new CombatTypes[]{CombatTypes.MAGE, CombatTypes.SAGITTARIUS, CombatTypes.MELEE};
		}
	}

	/**
	 * Get the ideal style for a given distance (used by TimingManager for variety decisions)
	 */
	public CombatTypes getIdealStyleForDistance(int distance) {
		return getOptimalStyleForDistance(distance);
	}

	/**
	 * Check if a style change is urgently needed due to distance
	 */
	public boolean isUrgentStyleChangeNeeded(CombatTypes currentStyle, int distance) {
		return isStyleIneffective(currentStyle, distance);
	}
}