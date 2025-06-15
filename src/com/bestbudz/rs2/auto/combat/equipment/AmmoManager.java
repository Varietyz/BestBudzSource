package com.bestbudz.rs2.auto.combat.equipment;

import com.bestbudz.rs2.auto.combat.config.AutoCombatConfig;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Manages ammunition for sagittarius weapons
 */
public class AmmoManager {

	private final Stoner stoner;

	public AmmoManager(Stoner stoner) {
		this.stoner = stoner;
	}

	/**
	 * Ensure ammo is equipped for sagittarius weapons
	 */
	public void ensureAmmoEquipped(Item weapon) {
		if (!requiresAmmo(weapon)) return;
		if (stoner.getEquipment().getItems()[AutoCombatConfig.AMMO_SLOT] != null) return; // Already has ammo

		Item ammo = findBestAmmo();
		if (ammo != null) {
			equipItem(ammo, AutoCombatConfig.AMMO_SLOT);
		}
	}

	/**
	 * Check if weapon requires ammo
	 */
	public boolean requiresAmmo(Item weapon) {
		if (weapon.getSagittariusDefinition() == null) return false;

		// Check if weapon is self-contained (doesn't need ammo)
		for (int id : AutoCombatConfig.SELF_CONTAINED_WEAPONS) {
			if (weapon.getId() == id) return false;
		}

		return true;
	}

	/**
	 * Find best available ammo
	 */
	public Item findBestAmmo() {
		Item bestAmmo = null;
		int bestScore = -1;

		Item[] boxItems = stoner.getBox().getItems();
		for (int i = 0; i < boxItems.length; i++) {
			Item item = boxItems[i];
			if (item == null || item.getEquipmentDefinition() == null) continue;
			if (item.getEquipmentDefinition().getSlot() != AutoCombatConfig.AMMO_SLOT) continue;

			String name = item.getDefinition().getName().toLowerCase();
			int score = calculateAmmoScore(name);

			if (score > bestScore) {
				bestScore = score;
				bestAmmo = item;
			}
		}

		return bestAmmo;
	}

	/**
	 * Calculate ammo score for prioritization
	 */
	public int calculateAmmoScore(String name) {
		int score = 1;

		// Metal tier scoring
		if (name.contains("dragon")) score = 100;
		else if (name.contains("rune")) score = 80;
		else if (name.contains("adamant")) score = 60;
		else if (name.contains("mithril")) score = 40;
		else if (name.contains("steel")) score = 20;
		else if (name.contains("iron")) score = 10;
		else if (name.contains("bronze")) score = 5;

		// Special ammo bonuses
		if (name.contains("broad")) score += 15;
		if (name.contains("enchanted")) score += 10;

		return score;
	}

	/**
	 * Equip item to specific slot
	 */
	private boolean equipItem(Item item, int targetSlot) {
		for (int i = 0; i < stoner.getBox().getItems().length; i++) {
			Item boxItem = stoner.getBox().getItems()[i];
			if (boxItem != null && boxItem.getId() == item.getId()) {
				try {
					stoner.getEquipment().equip(boxItem, i);
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		}
		return false;
	}
}