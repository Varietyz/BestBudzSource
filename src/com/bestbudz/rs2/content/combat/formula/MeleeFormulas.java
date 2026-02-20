package com.bestbudz.rs2.content.combat.formula;

import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.mercenary.Mercenary;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Equipment;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemCheck;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobConstants;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class MeleeFormulas {

	public static class BerserkerRage {
		private double intensity = 0.0;
		private int combatRhythm = 0;
		private long lastStrike = 0;
		private int bloodlust = 0;
		private double battleTrance = 1.0;

		public static BerserkerRage getOrCreate(Entity entity) {
			Object stored = entity.getAttributes().get("berserker_rage");
			if (stored instanceof BerserkerRage) {
				return (BerserkerRage) stored;
			}
			BerserkerRage rage = new BerserkerRage();
			entity.getAttributes().set("berserker_rage", rage);
			return rage;
		}

		public void buildRage(boolean hit, int damage) {
			long currentTime = System.currentTimeMillis();
			double timeDelta = currentTime - lastStrike;

			if (hit) {

				intensity = Math.min(10.0, intensity + 0.3 + (damage / 100.0));
				combatRhythm++;
				bloodlust = Math.min(15, bloodlust + 1);

				if (timeDelta >= 1000 && timeDelta <= 3000) {
					battleTrance = Math.min(3.0, battleTrance + 0.1);
					combatRhythm += 2;
				}
			} else {

				combatRhythm = Math.max(0, combatRhythm - 3);
				battleTrance = Math.max(0.8, battleTrance - 0.05);

				if (intensity > 5.0) {
					intensity = Math.min(10.0, intensity + 0.1);
				} else {
					intensity = Math.max(0.0, intensity - 0.2);
				}
			}

			if (timeDelta > 4000) {
				intensity = Math.max(0.0, intensity * 0.7);
				combatRhythm = Math.max(0, combatRhythm - 1);
				battleTrance = Math.max(1.0, battleTrance * 0.9);
				bloodlust = Math.max(0, bloodlust - 1);
			}

			lastStrike = currentTime;
		}

		public double getIntensity() { return intensity; }
		public int getCombatRhythm() { return combatRhythm; }
		public int getBloodlust() { return bloodlust; }
		public double getBattleTrance() { return battleTrance; }
	}

	public static class WeaponMastery {
		private int proficiency = 0;
		private double weaponBond = 1.0;
		private int perfectStrikes = 0;

		public static WeaponMastery getOrCreate(Entity entity, int weaponId) {
			String key = "weapon_mastery_" + weaponId;
			Object stored = entity.getAttributes().get(key);
			if (stored instanceof WeaponMastery) {
				return (WeaponMastery) stored;
			}
			WeaponMastery mastery = new WeaponMastery();
			entity.getAttributes().set(key, mastery);
			return mastery;
		}

		public void gainProficiency(boolean perfectHit) {
			proficiency++;
			weaponBond = Math.min(2.5, weaponBond + 0.01);

			if (perfectHit) {
				perfectStrikes++;
				weaponBond = Math.min(2.5, weaponBond + 0.02);
			}
		}

		public int getProficiency() { return proficiency; }
		public double getWeaponBond() { return weaponBond; }
		public int getPerfectStrikes() { return perfectStrikes; }

		public double getMasteryBonus() {
			double bonus = 1.0;

			if (proficiency >= 1000) bonus += 0.5;
			else if (proficiency >= 500) bonus += 0.3;
			else if (proficiency >= 200) bonus += 0.2;
			else if (proficiency >= 50) bonus += 0.1;

			if (proficiency > 0) {
				double perfectRatio = (double)perfectStrikes / proficiency;
				if (perfectRatio >= 0.8) bonus += 0.2;
				else if (perfectRatio >= 0.6) bonus += 0.15;
				else if (perfectRatio >= 0.4) bonus += 0.1;
			}

			return bonus * weaponBond;
		}
	}

	public static double getAegisRoll(Entity assaulting, Entity defending) {
		Stoner blocker = null;
		if (!defending.isNpc()) {
			blocker = World.getStoners()[defending.getIndex()];
		} else {
			if (defending.getBonuses() != null) {
				double baseAegis = getEffectiveAegis(defending) + defending.getBonuses()[assaulting.getAssaultType().ordinal()];

				if (assaulting instanceof Stoner) {
					BerserkerRage attackerRage = BerserkerRage.getOrCreate(assaulting);
					if (attackerRage.getIntensity() > 7.0) {
						baseAegis *= 1.2;
					}
				}

				return baseAegis;
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

		BerserkerRage defenderExp = BerserkerRage.getOrCreate(defending);
		if (defenderExp.getBloodlust() >= 5) {
			effectiveAegis *= 1.15;
		}

		if (assaulting instanceof Stoner) {
			BerserkerRage attackerRage = BerserkerRage.getOrCreate(assaulting);
			if (attackerRage.getIntensity() > 8.0) {
				effectiveAegis *= 1.1;
			}
		}

		if (ItemCheck.wearingFullBarrows(blocker, "Verac")) {
			effectiveAegis *= 0.75;
		}

		return effectiveAegis;
	}

	public static double calculateEnhancedMeleeDamage(Stoner stoner, double baseDamage) {

		BerserkerRage rage = BerserkerRage.getOrCreate(stoner);
		double rageScaling = 1.0 + (rage.getIntensity() * 0.08);

		Item weapon = stoner.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT];
		if (weapon != null) {
			WeaponMastery mastery = WeaponMastery.getOrCreate(stoner, weapon.getId());
			rageScaling *= mastery.getMasteryBonus();
		}

		double enhancedDamage = FormulaData.applyEmergentScaling(stoner, baseDamage * rageScaling);
		return enhancedDamage;
	}

	public static double getEffectiveAegis(Entity entity) {
		Stoner blocker = null;
		if (!entity.isNpc()) {
			blocker = World.getStoners()[entity.getIndex()];
		} else {
			if (entity.getGrades() != null) {
				return entity.getGrades()[1] + 8;
			}
			return 8;
		}
		double baseAegis = blocker.getProfession().getGrades()[1];

		return Math.floor(baseAegis) + 8;
	}

	public static double getAssaultRoll(Entity entity) {
		Stoner assaulter = null;

		if (!entity.isNpc()) {
			assaulter = World.getStoners()[entity.getIndex()];

			if (assaulter == null) {
				return getEffectiveAccuracy(entity);
			}
		} else {
			return getEffectiveAccuracy(entity);
		}

		double specAccuracy = getSpecialAccuracy(assaulter);
		double effectiveAccuracy = getEffectiveAccuracy(entity);

		BerserkerRage rage = BerserkerRage.getOrCreate(assaulter);
		double rageAccuracy = 1.0;

		if (rage.getIntensity() >= 8.0) {

			rageAccuracy = 0.85 + (rage.getCombatRhythm() * 0.02);
		} else if (rage.getIntensity() >= 4.0) {

			rageAccuracy = 1.0 + (rage.getIntensity() * 0.05);
		}

		rageAccuracy *= rage.getBattleTrance();

		effectiveAccuracy *= rageAccuracy;

		int styleBonusAssault = 0;

		if (assaulter.getEquipment().getAssaultStyle() == Equipment.AssaultStyles.ACCURATE) {
			styleBonusAssault = 3;

			if (rage.getCombatRhythm() >= 10) {
				styleBonusAssault = 6;
			}
		} else if (assaulter.getEquipment().getAssaultStyle() == Equipment.AssaultStyles.CONTROLLED) {
			styleBonusAssault = 1;

			styleBonusAssault = (int)(styleBonusAssault * rage.getBattleTrance());
		}

		effectiveAccuracy *= (1 + (styleBonusAssault) / 64);

		if (ItemCheck.wearingFullBarrows(assaulter, "Dharok")) {
			effectiveAccuracy *= 2.30;

			if (rage.getIntensity() > 6.0) {
				long currentLife = assaulter.getGrades()[Professions.LIFE];
				long maxLife = assaulter.getMaxGrades()[Professions.LIFE];
				if (currentLife < maxLife * 0.3) {
					effectiveAccuracy *= 1.5;
				}
			}
		}

		if (!entity.isNpc()) {
			Stoner stoner = World.getStoners()[entity.getIndex()];
			if (stoner != null) {
				double accuracyBonus = stoner.getResonance().getAccuracyBonus();
				effectiveAccuracy *= (1.0 + accuracyBonus);
			}
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

	public static double calculateBaseDamage(Stoner stoner) {
		Entity defending = stoner.getCombat().getAssaulting();

		double base = 0;
		double effective = getEffectiveStr(stoner);
		double specialBonus = getSpecialStr(stoner);
		double vigourBonus = stoner.getBonuses()[10];

		base = (13 + effective + (vigourBonus / 8) + ((effective * vigourBonus) / 64)) / 10;

		BerserkerRage rage = BerserkerRage.getOrCreate(stoner);
		double rageDamage = 1.0 + (rage.getIntensity() * 0.12);
		base *= rageDamage;

		if (rage.getCombatRhythm() >= 15) {
			base *= 1.4;
		} else if (rage.getCombatRhythm() >= 10) {
			base *= 1.25;
		} else if (rage.getCombatRhythm() >= 5) {
			base *= 1.1;
		}

		Item weapon = stoner.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT];
		if (weapon != null) {
			WeaponMastery mastery = WeaponMastery.getOrCreate(stoner, weapon.getId());
			base *= mastery.getMasteryBonus();

			switch (weapon.getId()) {
				case 4718:
				case 4886:
				case 4887:
				case 4888:
				case 4889:
					if (ItemCheck.wearingFullBarrows(stoner, "Dharok")) {
						long maximumLife = stoner.getMaxGrades()[Professions.LIFE];
						long currentLife = stoner.getGrades()[Professions.LIFE];
						double dharokEffect = ((maximumLife - currentLife) * 0.01) + 1.3;

						if (rage.getIntensity() > 5.0) {
							dharokEffect *= 1.0 + (rage.getIntensity() * 0.05);
						}

						base *= dharokEffect;
					}
					break;
			}
		}

		Item helm = stoner.getEquipment().getItems()[0];
		if (((helm != null) && (helm.getId() == 8921))
			|| ((helm != null) && (helm.getId() == 15492))
			|| ((helm != null) && (helm.getId() == 13263) && (defending != null && defending.isNpc()) && (stoner.getMercenary().hasTask()))) {

			if (defending != null && defending.isNpc()) {
				Mob m = World.getNpcs()[defending.getIndex()];
				if ((m != null) && (Mercenary.isMercenaryTask(stoner, m))) {
					base += 0.125;

					if (rage.getBloodlust() >= 10) {
						base += 0.075;
					}
				}
			}
		}

		if ((ItemCheck.isUsingBalmung(stoner)) && (defending != null && defending.isNpc())) {
			Mob m = World.getNpcs()[defending.getIndex()];
			if ((m != null) && (MobConstants.isDagannothKing(m))) {
				base += 0.25;

				WeaponMastery balmungMastery = WeaponMastery.getOrCreate(stoner, 6724);
				if (balmungMastery.getProficiency() >= 100) {
					base += 0.15;
				}
			}
		}

		base = (base * specialBonus);

		if (ItemCheck.hasBNeckAndObbyMaulCombo(stoner)) {
			base = (base * 1.25);

			if (rage.getIntensity() > 7.0) {
				base = (base * 1.15);
			}
		}

		if (ItemCheck.wearingFullVoidMelee(stoner)) {
			base = (base * 1.25);

			if (rage.getCombatRhythm() >= 8) {
				base = (base * 1.1);
			}
		}

		int lifeAdv = stoner.getProfessionAdvances()[0];
		if (lifeAdv > 0) {
			base += base * (lifeAdv * 0.03);
		}

		int assaultAdv = stoner.getProfessionAdvances()[1];
		if (assaultAdv > 0) {
			base += base * (assaultAdv * 0.03);

			if (assaultAdv >= 15) {
				base += base * (rage.getCombatRhythm() * 0.003);
			}
		}

		int aegisAdv = stoner.getProfessionAdvances()[2];
		if (aegisAdv > 0) {
			base += base * (aegisAdv * 0.02);
		}

		int vigourAdv = stoner.getProfessionAdvances()[3];
		if (vigourAdv > 0) {
			base += base * (vigourAdv * 0.04);
		}

		int resonanceAdv = stoner.getProfessionAdvances()[5];
		if (resonanceAdv > 0) {
			base += base * (resonanceAdv * 0.01);
		}

		int weedAdv = stoner.getProfessionAdvances()[16];
		if (weedAdv > 0) {
			base += base * (weedAdv * 0.01);
		}

		int mercAdv = stoner.getProfessionAdvances()[18];
		if (mercAdv > 0) {
			base += base * (mercAdv * 0.02);
		}

		double resonanceMultiplier = stoner.getResonance().applyResonanceEffects(1.0, Combat.CombatTypes.MELEE);
		base *= resonanceMultiplier;

		return Math.floor(base);
	}

	public static double getSpecialStr(Stoner stoner) {
		Item weapon = stoner.getEquipment().getItems()[3];
		if (weapon == null || !stoner.getSpecialAssault().isInitialized()) {
			return 1.0;
		}

		double baseModifier = 1.0;

		switch (weapon.getId()) {
			case 11802:
				baseModifier = 1.55;
				break;
			case 11804:
			case 11806:
			case 11808:
				baseModifier = 1.30;
				break;
			case 4587:
			case 4153:
				baseModifier = 1.0;
				break;
			case 5698:
			case 5680:
			case 1231:
				baseModifier = 1.10;
				break;
			case 1215:
				baseModifier = 1.10;
				break;
			case 3204:
				baseModifier = 1.15;
				break;
			case 1305:
				baseModifier = 1.15;
				break;
			case 1434:
				baseModifier = 1.35;
				break;
			case 4151:
			case 861:
				baseModifier = 1.1;
				break;
			case 12006:
				baseModifier = 1.1;
				break;
			case 10877:
				baseModifier = 1.2933;
				break;
			case 13188:
				baseModifier = 1.05;
				break;
			default:
				baseModifier = 0.5;
				break;
		}

		WeaponMastery mastery = WeaponMastery.getOrCreate(stoner, weapon.getId());
		if (mastery.getProficiency() >= 500) {
			baseModifier *= 1.3;
		} else if (mastery.getProficiency() >= 200) {
			baseModifier *= 1.2;
		} else if (mastery.getProficiency() >= 50) {
			baseModifier *= 1.1;
		}

		BerserkerRage rage = BerserkerRage.getOrCreate(stoner);
		if (rage.getIntensity() >= 9.0) {
			baseModifier *= 1.5;
		} else if (rage.getIntensity() >= 6.0) {
			baseModifier *= 1.25;
		}

		return baseModifier;
	}

	public static double getSpecialAccuracy(Stoner stoner) {
		if (stoner == null) {
			return 0.0D;
		}
		Item weapon = stoner.getEquipment().getItems()[3];
		if (weapon == null || !stoner.getSpecialAssault().isInitialized()) {
			return 1.0;
		}

		double baseModifier = 1.0;

		switch (weapon.getId()) {
			case 5698:
			case 5680:
			case 1231:
				baseModifier = 2.0;
				break;
			case 1215:
				baseModifier = 2.05;
				break;
			case 3204:
				baseModifier = 1.20;
				break;
			case 1305:
				baseModifier = 1.20;
				break;
			case 1434:
				baseModifier = 1.35;
				break;
			case 11802:
				baseModifier = 1.55;
				break;
			case 11804:
			case 11806:
			case 11808:
				baseModifier = 1.30;
				break;
			case 4151:
			case 861:
			case 4587:
			case 12006:
				baseModifier = 1.15;
				break;
			case 10877:
				baseModifier = 1.2933;
				break;
			case 13188:
				baseModifier = 1.25;
				break;
			default:
				return 0.0D;
		}

		BerserkerRage rage = BerserkerRage.getOrCreate(stoner);
		if (rage.getCombatRhythm() >= 20) {
			baseModifier *= 2.0;
		} else if (rage.getCombatRhythm() >= 12) {
			baseModifier *= 1.5;
		}

		WeaponMastery mastery = WeaponMastery.getOrCreate(stoner, weapon.getId());
		baseModifier *= (1.0 + (mastery.getProficiency() / 2000.0));

		return baseModifier;
	}

	public static double getEffectiveStr(Stoner stoner) {
		double baseStr = stoner.getGrades()[2];

		BerserkerRage rage = BerserkerRage.getOrCreate(stoner);
		double rageStr = 1.0 + (rage.getIntensity() * 0.08);

		if (rage.getBloodlust() >= 10) {
			rageStr *= 1.2;
		}

		return (baseStr * getResonanceStr(stoner) * rageStr);
	}

	public static double getResonanceStr(Stoner stoner) {

		BerserkerRage rage = BerserkerRage.getOrCreate(stoner);

		if (rage.getIntensity() >= 7.0) {
			return 1.15;
		}

		return 1.0;
	}

	public static void updateMeleeCombat(Stoner warrior, Entity target, boolean hit, int damage) {
		if (warrior == null) return;

		BerserkerRage rage = BerserkerRage.getOrCreate(warrior);
		rage.buildRage(hit, damage);

		Item weapon = warrior.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT];
		if (weapon != null) {
			WeaponMastery mastery = WeaponMastery.getOrCreate(warrior, weapon.getId());

			boolean perfectHit = (damage > 0 && (damage >= 25 || rage.getCombatRhythm() >= 5));
			mastery.gainProficiency(perfectHit);
		}

		FormulaData.updateCombatEvolution(warrior, target, hit, damage);
	}

	public static String getWarriorStatus(Stoner warrior) {
		if (warrior == null) return "Unknown";

		BerserkerRage rage = BerserkerRage.getOrCreate(warrior);

		if (rage.getIntensity() >= 9.5) return "Legendary Berserker";
		if (rage.getIntensity() >= 8.0) return "Apex Warrior";
		if (rage.getCombatRhythm() >= 20) return "Perfect Flow";
		if (rage.getCombatRhythm() >= 15) return "Battle Master";
		if (rage.getIntensity() >= 6.0) return "Berserker Rage";
		if (rage.getCombatRhythm() >= 10) return "Combat Flow";
		if (rage.getBloodlust() >= 10) return "Bloodthirsty";
		if (rage.getIntensity() >= 3.0) return "Battle Fury";
		if (rage.getCombatRhythm() >= 5) return "In Rhythm";
		if (rage.getIntensity() >= 1.0) return "Fighting Spirit";

		return "Calm";
	}

	public static String getWeaponMasteryStatus(Stoner warrior, int weaponId) {
		WeaponMastery mastery = WeaponMastery.getOrCreate(warrior, weaponId);

		if (mastery.getProficiency() >= 1000) return "Grandmaster";
		if (mastery.getProficiency() >= 500) return "Master";
		if (mastery.getProficiency() >= 200) return "Expert";
		if (mastery.getProficiency() >= 100) return "Skilled";
		if (mastery.getProficiency() >= 50) return "Practiced";
		if (mastery.getProficiency() >= 20) return "Learning";

		return "Novice";
	}
}
