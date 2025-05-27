package com.bestbudz.rs2.content.combat.formula;

import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.mercenary.Mercenary;
import com.bestbudz.rs2.content.profession.necromance.NecromanceBook.Necromance;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Equipment;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemCheck;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobConstants;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * @author Valiant (http://www.rune-server.org/members/Valiant) Represents an
 *         assaulters/victims rolls in melee combat
 * @since Todays Date
 */
public class MeleeFormulas {

	public static double getAegisRoll(Entity assaulting, Entity defending) {
	Stoner blocker = null;
	if (!defending.isNpc()) {
		blocker = com.bestbudz.rs2.entity.World.getStoners()[defending.getIndex()];
	} else {
		if (defending.getBonuses() != null) {
			return getEffectiveAegis(defending) + defending.getBonuses()[assaulting.getAssaultType().ordinal()];
		}
		return getEffectiveAegis(defending);
	}
	double effectiveAegis = getEffectiveAegis(defending);
	effectiveAegis += blocker.getBonuses()[5 + assaulting.getAssaultType().ordinal()] - 10;
	int styleBonusAegis = 0;
	if (blocker.getEquipment().getAssaultStyle() == Equipment.AssaultStyles.ACCURATE)
		styleBonusAegis += 3;
	else if (blocker.getEquipment().getAssaultStyle() == Equipment.AssaultStyles.CONTROLLED) {
		styleBonusAegis += 1;
	}
	effectiveAegis *= (1 + (styleBonusAegis) / 64);
	if (ItemCheck.wearingFullBarrows(blocker, "Verac")) {
		effectiveAegis *= 0.75;
	}
	return effectiveAegis;
	}

	public static double getEffectiveAegis(Entity entity) {
	Stoner blocker = null;
	if (!entity.isNpc()) {
		blocker = com.bestbudz.rs2.entity.World.getStoners()[entity.getIndex()];
	} else {
		if (entity.getGrades() != null) {
			return entity.getGrades()[1] + 8;
		}
		return 8;
	}
	double baseAegis = blocker.getProfession().getGrades()[1];
	if (blocker.getNecromance().active(Necromance.THICK_SKIN)) {
		baseAegis += 0.5;
	} else if (blocker.getNecromance().active(Necromance.ROCK_SKIN)) {
		baseAegis += 0.7;
	} else if (blocker.getNecromance().active(Necromance.STEEL_SKIN)) {
		baseAegis += 1.3;
	} else if (blocker.getNecromance().active(Necromance.CHIVALRY)) {
		baseAegis += 1.14;
	} else if (blocker.getNecromance().active(Necromance.PIETY)) {
		baseAegis *= 1.18;
	}
	return Math.floor(baseAegis) + 8;
	}

	public static double getAssaultRoll(Entity entity) {
	Stoner assaulter = null;

	if (!entity.isNpc()) {
		assaulter = com.bestbudz.rs2.entity.World.getStoners()[entity.getIndex()];

		if (assaulter == null) {
			return getEffectiveAccuracy(entity);
		}
	} else {
		return getEffectiveAccuracy(entity);
	}

	double specAccuracy = getSpecialAccuracy(assaulter);
	double effectiveAccuracy = getEffectiveAccuracy(entity);
	int styleBonusAssault = 0;

	if (assaulter.getEquipment().getAssaultStyle() == Equipment.AssaultStyles.ACCURATE)
		styleBonusAssault = 3;
	else if (assaulter.getEquipment().getAssaultStyle() == Equipment.AssaultStyles.CONTROLLED) {
		styleBonusAssault = 1;
	}
	effectiveAccuracy *= (1 + (styleBonusAssault) / 64);

	if (ItemCheck.wearingFullBarrows(assaulter, "Dharok")) {
		effectiveAccuracy *= 2.30;
	}
	return (int) (effectiveAccuracy * specAccuracy);
	}

	public static double getEffectiveAccuracy(Entity entity) {
	double assaultBonus;
	double baseAssault;
	if (!entity.isNpc()) {
		Stoner assaulter = World.getStoners()[entity.getIndex()];
		if (assaulter == null) {
			return 0.0;
		}
		assaultBonus = assaulter.getBonuses()[assaulter.getAssaultType().ordinal()];
		baseAssault = assaulter.getGrades()[0];

		if (assaulter.getNecromance().active(Necromance.CLARITY_OF_THOUGHT)) {
			baseAssault += 1.05;
		} else if (assaulter.getNecromance().active(Necromance.IMPROVED_REFLEXES)) {
			baseAssault += 1.10;
		} else if (assaulter.getNecromance().active(Necromance.INCREDIBLE_REFLEXES)) {
			baseAssault += 1.15;
		} else if (assaulter.getNecromance().active(Necromance.CHIVALRY)) {
			baseAssault += 1.20;
		} else if (assaulter.getNecromance().active(Necromance.PIETY)) {
			baseAssault *= 1.23;
		}
	} else {
		if (entity.getBonuses() != null) {
			assaultBonus = entity.getBonuses()[entity.getAssaultType().ordinal()];
		} else {
			assaultBonus = 0;
		}

		if (entity.getGrades() != null) {
			baseAssault = entity.getGrades()[0];
		} else {
			baseAssault = 0;
		}
	}

	return Math.floor(baseAssault + assaultBonus) + 16;
	}

	/**
	 * Calculates the assaulters base damage output
	 * 
	 * @param stoner
	 * @param special
	 * @return the value
	 */
	public static double calculateBaseDamage(Stoner stoner) {

	Entity defending = stoner.getCombat().getAssaulting();

	double base = 0;
	double effective = getEffectiveStr(stoner);
	double specialBonus = getSpecialStr(stoner);
	double vigourBonus = stoner.getBonuses()[10];

	base = (13 + effective + (vigourBonus / 8) + ((effective * vigourBonus) / 64)) / 10;

	if (stoner.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT] != null) {
		switch (stoner.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId()) {
		case 4718:
		case 4886:
		case 4887:
		case 4888:
		case 4889:
			if (ItemCheck.wearingFullBarrows(stoner, "Dharok")) {
				int maximumLife = stoner.getMaxGrades()[Professions.LIFE];
				int currentLife = stoner.getGrades()[Professions.LIFE];
				double dharokEffect = ((maximumLife - currentLife) * 0.01) + 1.3;
				base *= dharokEffect;

			}
		}
	}

	Item helm = stoner.getEquipment().getItems()[0];

	if (((helm != null) && (helm.getId() == 8921)) || ((helm != null) && (helm.getId() == 15492)) || ((helm != null) && (helm.getId() == 13263) && (defending.isNpc()) && (stoner.getMercenary().hasTask()))) {
		Mob m = com.bestbudz.rs2.entity.World.getNpcs()[defending.getIndex()];
		if ((m != null) && (Mercenary.isMercenaryTask(stoner, m))) {
			base += 0.125;
		}

	}

	if ((ItemCheck.isUsingBalmung(stoner)) && (defending.isNpc())) {
		Mob m = com.bestbudz.rs2.entity.World.getNpcs()[defending.getIndex()];
		if ((m != null) && (MobConstants.isDagannothKing(m))) {
			base += 0.25;
		}
	}

	base = (base * specialBonus);

	if (ItemCheck.hasBNeckAndObbyMaulCombo(stoner) || ItemCheck.wearingFullVoidMelee(stoner)) {
		base = (base * 1.25);
	}
	return Math.floor(base);
	}

	public static double getSpecialStr(Stoner stoner) {
	Item weapon = stoner.getEquipment().getItems()[3];
	if (weapon == null || !stoner.getSpecialAssault().isInitialized()) {
		return 1.0;
	}
	switch (weapon.getId()) {
	case 11802:
		return 1.55;
	case 11804:
	case 11806:
	case 11808:
		return 1.30;
	case 4587:
	case 4153:
		return 1.0;
	case 5698:
	case 5680:
	case 1231:
		return 1.10;
	case 1215:
		return 1.10;
	case 3204:
		return 1.15;
	case 1305:
		return 1.15;
	case 1434:
		return 1.35;
	case 4151:
	case 861:
		return 1.1;
	case 12006:
		return 1.1;
	case 10877:
		return 1.2933;
	case 13188:
		return 1.05;

	}
	return 0.5D;
	}

	public static double getSpecialAccuracy(Stoner stoner) {
	if (stoner == null) {
		return 0.0D;
	}
	Item weapon = stoner.getEquipment().getItems()[3];
	if (weapon == null || !stoner.getSpecialAssault().isInitialized()) {
		return 1.0;
	}
	switch (weapon.getId()) {
	case 5698:
	case 5680:
	case 1231:
		return 2.0;
	case 1215:
		return 2.05;
	case 3204:
		return 1.20;
	case 1305:
		return 1.20;
	case 1434:
		return 1.35;
	case 11802:
		return 1.55;
	case 11804:
	case 11806:
	case 11808:
		return 1.30;
	case 4151:
	case 861:
	case 4587:
	case 12006:
		return 1.15;
	case 10877:
		return 1.2933;
	case 13188:
		return 1.25;
	}
	return 0.0D;
	}

	/*
	 * Gets the assaulters effective vigour output
	 */
	public static double getEffectiveStr(Stoner stoner) {
	return ((stoner.getGrades()[2]) * getNecromanceStr(stoner));
	}

	/**
	 * Calculates and returns the assaulters necromance vigour modification bonus
	 * 
	 * @param stoner
	 * @return
	 */
	public static double getNecromanceStr(Stoner stoner) {
	if (stoner.getNecromance().active(Necromance.BURST_OF_VIGOUR))
		return 1.05;
	else if (stoner.getNecromance().active(Necromance.SUPERHUMAN_VIGOUR))
		return 1.1;
	else if (stoner.getNecromance().active(Necromance.ULTIMATE_VIGOUR))
		return 1.15;
	else if (stoner.getNecromance().active(Necromance.CHIVALRY))
		return 1.18;
	else if (stoner.getNecromance().active(Necromance.PIETY))
		return 1.23;
	return 1.0;
	}

}