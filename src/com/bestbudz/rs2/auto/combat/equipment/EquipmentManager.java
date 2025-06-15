package com.bestbudz.rs2.auto.combat.equipment;

import com.bestbudz.rs2.auto.combat.config.AutoCombatConfig;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Manages equipment selection and optimization for different combat styles
 */
public class EquipmentManager {

	private final Stoner stoner;
	private final WeaponSelector weaponSelector;
	private final ArmorOptimizer armorOptimizer;
	private final AmmoManager ammoManager;

	public EquipmentManager(Stoner stoner) {
		this.stoner = stoner;
		this.weaponSelector = new WeaponSelector(stoner);
		this.armorOptimizer = new ArmorOptimizer(stoner);
		this.ammoManager = new AmmoManager(stoner);
	}

	/**
	 * Get current combat style based on equipped weapon
	 */
	public CombatTypes getCurrentCombatStyle() {
		Item weapon = stoner.getEquipment().getItems()[AutoCombatConfig.WEAPON_SLOT];
		if (weapon == null) return null;

		return weaponSelector.determineWeaponStyle(weapon);
	}

	/**
	 * Check if player has gear available for a specific style
	 */
	public boolean hasGearForStyle(CombatTypes style) {
		return weaponSelector.findBestWeapon(style) != null;
	}

	/**
	 * Equip gear for specified combat style
	 */
	public boolean equipStyleGear(CombatTypes style) {
		try {
			// First equip weapon
			Item weapon = weaponSelector.findBestWeapon(style);
			if (weapon == null) {
				return false;
			}

			if (!equipItem(weapon, AutoCombatConfig.WEAPON_SLOT)) {
				return false;
			}

			// Optimize all armor slots for the combat style
			armorOptimizer.optimizeArmorForStyle(style);

			// Style-specific equipment
			switch (style) {
				case SAGITTARIUS:
					ammoManager.ensureAmmoEquipped(weapon);
					armorOptimizer.optimizeOffhandForStyle(style);
					break;
				case MAGE:
				case MELEE:
					armorOptimizer.optimizeOffhandForStyle(style);
					break;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Equip item to specific slot
	 */
	public boolean equipItem(Item item, int targetSlot) {
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

	/**
	 * Get items from player's box/inventory
	 */
	public Item[] getBoxItems() {
		return stoner.getBox().getItems();
	}

	/**
	 * Get currently equipped items
	 */
	public Item[] getEquippedItems() {
		return stoner.getEquipment().getItems();
	}

	/**
	 * Unequip item from slot
	 */
	public boolean unequipItem(int slot) {
		try {
			stoner.getEquipment().unequip(slot);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}