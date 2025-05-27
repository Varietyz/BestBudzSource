package com.bestbudz.rs2.entity.item;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.profession.foodie.FoodieData;
import com.bestbudz.rs2.content.profession.thchempistry.FinishedPotionData;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class ItemCheck {

	public static boolean hasAnyItem(Stoner p) {
	for (Item i : p.getBox().getItems()) {
		if (i != null) {
			return true;
		}
	}

	for (Item i : p.getEquipment().getItems()) {
		if (i != null) {
			return true;
		}
	}

	return false;
	}

	public static boolean hasIronArmour(Stoner stoner) {
	if (stoner.getBox().hasItemId(12810) || stoner.getBox().hasItemId(12811) || stoner.getBox().hasItemId(12812)) {
		return true;
	}
	if (stoner.getBank().hasItemId(12810) || stoner.getBank().hasItemId(12811) || stoner.getBank().hasItemId(12812)) {
		return true;
	}
	if (stoner.getEquipment().contains(12810) || stoner.getEquipment().contains(12811) || stoner.getEquipment().contains(12812)) {
		return true;
	}
	return false;
	}

	public static boolean hasUltimateArmour(Stoner stoner) {
	if (stoner.getBox().hasItemId(12813) || stoner.getBox().hasItemId(12814) || stoner.getBox().hasItemId(12815)) {
		return true;
	}
	if (stoner.getBank().hasItemId(12813) || stoner.getBank().hasItemId(12814) || stoner.getBank().hasItemId(12815)) {
		return true;
	}
	if (stoner.getEquipment().contains(12813) || stoner.getEquipment().contains(12814) || stoner.getEquipment().contains(12815)) {
		return true;
	}
	return false;
	}

	public static boolean hasGodCape(Stoner stoner) {
	if (stoner.getBox().hasItemId(2412) || stoner.getBox().hasItemId(2413) || stoner.getBox().hasItemId(2414)) {
		return true;
	}
	if (stoner.getBank().hasItemId(2412) || stoner.getBank().hasItemId(2413) || stoner.getBank().hasItemId(2414)) {
		return true;
	}
	if (stoner.getEquipment().contains(2412) || stoner.getEquipment().contains(2413) || stoner.getEquipment().contains(2414)) {
		return true;
	}
	return false;
	}

	public static boolean hasBNeckAndObbyMaulCombo(Stoner p) {
	Item w = p.getEquipment().getItems()[3];
	Item n = p.getEquipment().getItems()[2];

	return (w != null) && (w.getId() == 6528) && (n != null) && (n.getId() == 11128);
	}

	public static boolean hasConsumables(Stoner p) {
	for (Item i : p.getBox().getItems()) {
		if (i != null) {
			int id = i.getId();
			if ((GameDefinitionLoader.getFoodDefinition(id) != null) || (GameDefinitionLoader.getPotionDefinition(id) != null)) {
				return true;
			}
		}
	}

	return false;
	}

	public static boolean hasDFireShield(Stoner p) {
	return (p.getEquipment().getItems()[5] != null) && (p.getEquipment().getItems()[5].getId() == 11283);
	}

	public static final boolean hasEquipmentOn(Stoner p) {
	for (Item i : p.getEquipment().getItems()) {
		if (i != null) {
			return true;
		}
	}

	return false;
	}

	public static boolean hasTHChempistryIngredients(Stoner p) {
	for (Item i : p.getBox().getItems()) {
		if (i != null) {
			for (FinishedPotionData k : FinishedPotionData.values()) {
				if (i.getId() == k.getItemNeeded()) {
					return true;
				}
			}
		}
	}

	return false;
	}

	public static boolean hasRawFood(Stoner p) {
	for (Item i : p.getBox().getItems()) {
		if ((i != null) && (FoodieData.forId(i.getId()) != null)) {
			return true;
		}

	}

	return false;
	}

	/**
	 * Gets if the item is a dyed whip
	 * 
	 * @param i
	 *              The item being checked
	 * @return
	 */
	public static final boolean isItemDyedWhip(Item i) {
	if (i != null) {
		return (i.getId() == 12773) || (i.getId() == 12774);
	}
	return false;
	}

	/**
	 * Gets if the stoner is using a balmung
	 * 
	 * @param p
	 * @return
	 */
	public static boolean isUsingBalmung(Stoner p) {
	return (p.getEquipment().getItems()[3] != null) && (p.getEquipment().getItems()[3].getId() == 15403);
	}

	/**
	 * Gets if the stoner is using a crossbow
	 * 
	 * @param p
	 *              The stoner using a crossbow
	 * @return
	 */
	public static boolean isUsingCrossbow(Stoner p) {
	Item weapon = p.getEquipment().getItems()[3];

	if (weapon != null) {
		int i = weapon.getId();
		return (i == 837) || (i == 4734) || (i == 9174) || (i == 9178) || (i == 9180) || (i == 9182) || (i == 9184) || (i == 9185) || (i == 11785);
	}

	return false;
	}

	/**
	 * Gets if the stoner is wearing an anti-dragonfire shield
	 * 
	 * @param p
	 *              The stoner wearing the shield
	 * @return
	 */
	public static final boolean isWearingAntiDFireShield(Stoner p) {
	Item shield = p.getEquipment().getItems()[EquipmentConstants.SHIELD_SLOT];
	if (shield != null) {
		int index = shield.getId();
		return (index == 11283) || (index == 1540);
	}
	return false;
	}

	public static boolean wearingFullBarrows(Stoner stoner, String check) {
	int[] slots = { 0, 4, 7, 3 };
	Item[] equip = stoner.getEquipment().getItems();

	for (int i = 0; i < slots.length; i++) {
		if (equip[slots[i]] == null) {
			return false;
		}
		if (!equip[slots[i]].getDefinition().getName().contains(check)) {
			return false;
		}

	}

	return true;
	}

	public static boolean wearingFullVoidMage(Stoner stoner) {
	int[] slots = { 0, 4, 7, 9 };
	int[] ids = { 11663, 8839, 8840, 8842 };
	Item[] equip = stoner.getEquipment().getItems();

	for (int i = 0; i < slots.length; i++) {
		if (equip[slots[i]] == null) {
			return false;
		}
		if (equip[slots[i]].getId() != ids[i]) {
			return false;
		}

	}

	return true;
	}

	public static boolean wearingFullVoidMelee(Stoner stoner) {
	int[] slots = { 0, 4, 7, 9 };
	int[] ids = { 11665, 8839, 8840, 8842 };
	Item[] equip = stoner.getEquipment().getItems();

	for (int i = 0; i < slots.length; i++) {
		if (equip[slots[i]] == null) {
			return false;
		}
		if (equip[slots[i]].getId() != ids[i]) {
			return false;
		}

	}

	return true;
	}

	public static boolean wearingFullVoidSagittarius(Stoner stoner) {
	int[] slots = { 0, 4, 7, 9 };
	int[] ids = { 11664, 8839, 8840, 8842 };
	Item[] equip = stoner.getEquipment().getItems();

	for (int i = 0; i < slots.length; i++) {
		if (equip[slots[i]] == null) {
			return false;
		}
		if (equip[slots[i]].getId() != ids[i]) {
			return false;
		}

	}

	return true;
	}
}
