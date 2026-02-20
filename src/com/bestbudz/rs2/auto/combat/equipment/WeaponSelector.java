package com.bestbudz.rs2.auto.combat.equipment;

import com.bestbudz.rs2.auto.combat.config.AutoCombatConfig;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class WeaponSelector {

	private final Stoner stoner;

	public WeaponSelector(Stoner stoner) {
		this.stoner = stoner;
	}

	public CombatTypes determineWeaponStyle(Item weapon) {
		if (weapon == null || weapon.getEquipmentDefinition() == null) {
			return null;
		}

		String name = weapon.getDefinition().getName().toLowerCase();

		if (weapon.getSagittariusDefinition() != null) {
			return CombatTypes.SAGITTARIUS;
		}

		if (name.contains("staff") || name.contains("wand")) {
			return CombatTypes.MAGE;
		}

		if (weapon.getWeaponDefinition() != null &&
			weapon.getSagittariusDefinition() == null &&
			!isMageWeapon(name) &&
			!isSagittariusWeapon(name)) {
			return CombatTypes.MELEE;
		}

		return null;
	}

	public Item findBestWeapon(CombatTypes style) {
		Item bestWeapon = null;
		int bestScore = -1;

		Item[] boxItems = stoner.getBox().getItems();
		for (int i = 0; i < boxItems.length; i++) {
			Item item = boxItems[i];
			if (item == null || item.getEquipmentDefinition() == null) continue;
			if (item.getEquipmentDefinition().getSlot() != AutoCombatConfig.WEAPON_SLOT) continue;

			if (isWeaponForStyle(item, style)) {
				int score = calculateWeaponScore(item);
				if (score > bestScore) {
					bestScore = score;
					bestWeapon = item;
				}
			}
		}

		return bestWeapon;
	}

	public boolean isWeaponForStyle(Item weapon, CombatTypes style) {
		if (weapon.getEquipmentDefinition() == null ||
			weapon.getEquipmentDefinition().getSlot() != AutoCombatConfig.WEAPON_SLOT) {
			return false;
		}

		String name = weapon.getDefinition().getName().toLowerCase();

		switch (style) {
			case MELEE:
				return isMeleeWeapon(weapon, name);
			case SAGITTARIUS:
				return isSagittariusWeapon(weapon, name);
			case MAGE:
				return isMageWeapon(name);
			default:
				return false;
		}
	}

	public int calculateWeaponScore(Item weapon) {
		int score = weapon.getId() / 100;

		if (weapon.getSpecialDefinition() != null) {
			score += AutoCombatConfig.SPECIAL_WEAPON_BONUS;
		}

		if (weapon.getWeaponDefinition() != null && weapon.getWeaponDefinition().isTwoHanded()) {
			score += AutoCombatConfig.TWO_HANDED_WEAPON_BONUS;
		}

		if (weapon.getItemBonuses() != null) {
			short[] bonuses = weapon.getItemBonuses();

			for (int i = 0; i < Math.min(5, bonuses.length); i++) {
				if (bonuses[i] > 0) {
					score += bonuses[i] * AutoCombatConfig.ATTACK_BONUS_MULTIPLIER;
				}
			}

			if (bonuses.length > 10 && bonuses[10] > 0) {
				score += bonuses[10] * AutoCombatConfig.STRENGTH_BONUS_MULTIPLIER;
			}
		}

		return score;
	}

	private boolean isMeleeWeapon(Item weapon, String name) {
		return weapon.getWeaponDefinition() != null &&
			weapon.getSagittariusDefinition() == null &&
			!isMageWeapon(name) &&
			!isSagittariusWeapon(name);
	}

	private boolean isSagittariusWeapon(Item weapon, String name) {
		return weapon.getSagittariusDefinition() != null ||
			isThrowingWeapon(name);
	}

	private boolean isSagittariusWeapon(String name) {
		return name.contains("bow") ||
			name.contains("crossbow") ||
			name.contains("c'bow") ||
			isThrowingWeapon(name);
	}

	private boolean isThrowingWeapon(String name) {
		return name.contains("dart") ||
			name.contains("knife") ||
			name.contains("javelin") ||
			name.contains("throwing");
	}

	private boolean isMageWeapon(String name) {
		return name.contains("staff") || name.contains("wand");
	}
}
