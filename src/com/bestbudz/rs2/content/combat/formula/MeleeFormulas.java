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

/**
 * Enhanced Melee Combat System - "Warrior's Spirit"
 * Features: Berserker rage, combat rhythm, weapon mastery, battle flow
 */
public class MeleeFormulas {

	// === BERSERKER RAGE SYSTEM ===
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
				// Successful hits build rage and rhythm
				intensity = Math.min(10.0, intensity + 0.3 + (damage / 100.0));
				combatRhythm++;
				bloodlust = Math.min(15, bloodlust + 1);

				// Perfect timing builds battle trance (1-3 second intervals)
				if (timeDelta >= 1000 && timeDelta <= 3000) {
					battleTrance = Math.min(3.0, battleTrance + 0.1);
					combatRhythm += 2; // Bonus rhythm for timing
				}
			} else {
				// Misses break rhythm but can fuel berserker fury
				combatRhythm = Math.max(0, combatRhythm - 3);
				battleTrance = Math.max(0.8, battleTrance - 0.05);

				// Miss in high rage state fuels berserker fury
				if (intensity > 5.0) {
					intensity = Math.min(10.0, intensity + 0.1); // Fury from frustration
				} else {
					intensity = Math.max(0.0, intensity - 0.2);
				}
			}

			// Rage naturally decays over time
			if (timeDelta > 4000) { // 4 seconds of inactivity
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

	// === WEAPON MASTERY SYSTEM ===
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

			// Proficiency levels
			if (proficiency >= 1000) bonus += 0.5;      // Grandmaster
			else if (proficiency >= 500) bonus += 0.3;  // Master
			else if (proficiency >= 200) bonus += 0.2;  // Expert
			else if (proficiency >= 50) bonus += 0.1;   // Skilled

			// Perfect strike ratio bonus
			if (proficiency > 0) {
				double perfectRatio = (double)perfectStrikes / proficiency;
				if (perfectRatio >= 0.8) bonus += 0.2;    // Legendary precision
				else if (perfectRatio >= 0.6) bonus += 0.15; // Master precision
				else if (perfectRatio >= 0.4) bonus += 0.1;  // Good precision
			}

			return bonus * weaponBond;
		}
	}

	// === ENHANCED ORIGINAL METHODS ===

	public static double getAegisRoll(Entity assaulting, Entity defending) {
		Stoner blocker = null;
		if (!defending.isNpc()) {
			blocker = World.getStoners()[defending.getIndex()];
		} else {
			if (defending.getBonuses() != null) {
				double baseAegis = getEffectiveAegis(defending) + defending.getBonuses()[assaulting.getAssaultType().ordinal()];

				// NPCs get defensive instincts based on combat pressure
				if (assaulting instanceof Stoner) {
					BerserkerRage attackerRage = BerserkerRage.getOrCreate(assaulting);
					if (attackerRage.getIntensity() > 7.0) {
						baseAegis *= 1.2; // NPCs become more defensive against berserkers
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

		// Battle experience bonus - defenders learn from taking hits
		BerserkerRage defenderExp = BerserkerRage.getOrCreate(defending);
		if (defenderExp.getBloodlust() >= 5) {
			effectiveAegis *= 1.15; // Battle-hardened defense
		}

		// Counter-rage system - high rage attackers face adaptive defense
		if (assaulting instanceof Stoner) {
			BerserkerRage attackerRage = BerserkerRage.getOrCreate(assaulting);
			if (attackerRage.getIntensity() > 8.0) {
				effectiveAegis *= 1.1; // Defenders adapt to berserker patterns
			}
		}

		if (ItemCheck.wearingFullBarrows(blocker, "Verac")) {
			effectiveAegis *= 0.75;
		}

		return effectiveAegis;
	}

	public static double calculateEnhancedMeleeDamage(Stoner stoner, double baseDamage) {
		// Apply berserker scaling first
		BerserkerRage rage = BerserkerRage.getOrCreate(stoner);
		double rageScaling = 1.0 + (rage.getIntensity() * 0.08); // Up to 80% at max rage

		// Weapon mastery scaling
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

		// Berserker rage affects accuracy
		BerserkerRage rage = BerserkerRage.getOrCreate(assaulter);
		double rageAccuracy = 1.0;

		if (rage.getIntensity() >= 8.0) {
			// High rage: wild swings, reduced accuracy but can break through defense
			rageAccuracy = 0.85 + (rage.getCombatRhythm() * 0.02);
		} else if (rage.getIntensity() >= 4.0) {
			// Moderate rage: focused aggression
			rageAccuracy = 1.0 + (rage.getIntensity() * 0.05);
		}

		// Battle trance improves accuracy
		rageAccuracy *= rage.getBattleTrance();

		effectiveAccuracy *= rageAccuracy;

		int styleBonusAssault = 0;

		if (assaulter.getEquipment().getAssaultStyle() == Equipment.AssaultStyles.ACCURATE) {
			styleBonusAssault = 3;
			// Perfect accuracy with high rhythm
			if (rage.getCombatRhythm() >= 10) {
				styleBonusAssault = 6;
			}
		} else if (assaulter.getEquipment().getAssaultStyle() == Equipment.AssaultStyles.CONTROLLED) {
			styleBonusAssault = 1;
			// Controlled style benefits from battle trance
			styleBonusAssault = (int)(styleBonusAssault * rage.getBattleTrance());
		}

		effectiveAccuracy *= (1 + (styleBonusAssault) / 64);

		// Dharok's with berserker synergy
		if (ItemCheck.wearingFullBarrows(assaulter, "Dharok")) {
			effectiveAccuracy *= 2.30;

			// Desperate fury bonus when low health + high rage
			if (rage.getIntensity() > 6.0) {
				long currentLife = assaulter.getGrades()[Professions.LIFE];
				long maxLife = assaulter.getMaxGrades()[Professions.LIFE];
				if (currentLife < maxLife * 0.3) { // Below 30% health
					effectiveAccuracy *= 1.5; // Desperate berserker bonus
				}
			}
		}

		// Apply resonance accuracy bonus for players
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

		// Berserker rage damage scaling
		BerserkerRage rage = BerserkerRage.getOrCreate(stoner);
		double rageDamage = 1.0 + (rage.getIntensity() * 0.12); // Up to 120% at max rage
		base *= rageDamage;

		// Combat rhythm bonus damage
		if (rage.getCombatRhythm() >= 15) {
			base *= 1.4; // Legendary warrior flow
		} else if (rage.getCombatRhythm() >= 10) {
			base *= 1.25; // Master warrior flow
		} else if (rage.getCombatRhythm() >= 5) {
			base *= 1.1; // Good combat flow
		}

		// Weapon mastery bonus
		Item weapon = stoner.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT];
		if (weapon != null) {
			WeaponMastery mastery = WeaponMastery.getOrCreate(stoner, weapon.getId());
			base *= mastery.getMasteryBonus();

			// Dharok's set with berserker synergy
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

						// Berserker dharok synergy - more damage when raging and low health
						if (rage.getIntensity() > 5.0) {
							dharokEffect *= 1.0 + (rage.getIntensity() * 0.05);
						}

						base *= dharokEffect;
					}
					break;
			}
		}

		// Mercenary task bonuses
		Item helm = stoner.getEquipment().getItems()[0];
		if (((helm != null) && (helm.getId() == 8921))
			|| ((helm != null) && (helm.getId() == 15492))
			|| ((helm != null) && (helm.getId() == 13263) && (defending != null && defending.isNpc()) && (stoner.getMercenary().hasTask()))) {

			if (defending != null && defending.isNpc()) {
				Mob m = World.getNpcs()[defending.getIndex()];
				if ((m != null) && (Mercenary.isMercenaryTask(stoner, m))) {
					base += 0.125;

					// Professional killer rage
					if (rage.getBloodlust() >= 10) {
						base += 0.075; // Additional 7.5% for bloodthirsty mercenary
					}
				}
			}
		}

		// Balmung special case
		if ((ItemCheck.isUsingBalmung(stoner)) && (defending != null && defending.isNpc())) {
			Mob m = World.getNpcs()[defending.getIndex()];
			if ((m != null) && (MobConstants.isDagannothKing(m))) {
				base += 0.25;

				// Legendary weapon mastery
				WeaponMastery balmungMastery = WeaponMastery.getOrCreate(stoner, 6724); // Assuming Balmung ID
				if (balmungMastery.getProficiency() >= 100) {
					base += 0.15; // Master of legendary weapons
				}
			}
		}

		base = (base * specialBonus);

		// Equipment set bonuses with warrior synergy
		if (ItemCheck.hasBNeckAndObbyMaulCombo(stoner)) {
			base = (base * 1.25);

			// Berserker necklace synergy
			if (rage.getIntensity() > 7.0) {
				base = (base * 1.15); // Berserker obsidian mastery
			}
		}

		if (ItemCheck.wearingFullVoidMelee(stoner)) {
			base = (base * 1.25);

			// Void warrior synergy
			if (rage.getCombatRhythm() >= 8) {
				base = (base * 1.1); // Void combat mastery
			}
		}

		// Apply advance level scaling with warrior mastery
		int lifeAdv = stoner.getProfessionAdvances()[0];
		if (lifeAdv > 0) {
			base += base * (lifeAdv * 0.03);
		}

		int assaultAdv = stoner.getProfessionAdvances()[1];
		if (assaultAdv > 0) {
			base += base * (assaultAdv * 0.03);

			// Master warrior bonus
			if (assaultAdv >= 15) {
				base += base * (rage.getCombatRhythm() * 0.003); // 0.3% per rhythm for masters
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
			case 11802: // AGS
				baseModifier = 1.55;
				break;
			case 11804: // BGS
			case 11806: // CGS
			case 11808: // SGS
				baseModifier = 1.30;
				break;
			case 4587: // DDS
			case 4153: // Granite maul
				baseModifier = 1.0;
				break;
			case 5698: // DH axe
			case 5680: // DH hammer
			case 1231: // DH sword
				baseModifier = 1.10;
				break;
			case 1215: // Dragon dagger
				baseModifier = 1.10;
				break;
			case 3204: // Dragon halberd
				baseModifier = 1.15;
				break;
			case 1305: // Dragon longsword
				baseModifier = 1.15;
				break;
			case 1434: // Dragon mace
				baseModifier = 1.35;
				break;
			case 4151: // Abyssal whip
			case 861: // Magic shortbow
				baseModifier = 1.1;
				break;
			case 12006: // Abyssal bludgeon
				baseModifier = 1.1;
				break;
			case 10877: // Barrelchest anchor
				baseModifier = 1.2933;
				break;
			case 13188: // Abyssal dagger
				baseModifier = 1.05;
				break;
			default:
				baseModifier = 0.5;
				break;
		}

		// Weapon mastery enhances special attacks
		WeaponMastery mastery = WeaponMastery.getOrCreate(stoner, weapon.getId());
		if (mastery.getProficiency() >= 500) {
			baseModifier *= 1.3; // Grandmaster special attacks
		} else if (mastery.getProficiency() >= 200) {
			baseModifier *= 1.2; // Master special attacks
		} else if (mastery.getProficiency() >= 50) {
			baseModifier *= 1.1; // Skilled special attacks
		}

		// Berserker rage amplifies special attacks
		BerserkerRage rage = BerserkerRage.getOrCreate(stoner);
		if (rage.getIntensity() >= 9.0) {
			baseModifier *= 1.5; // Legendary berserker special
		} else if (rage.getIntensity() >= 6.0) {
			baseModifier *= 1.25; // Berserker special
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

		// Perfect combat rhythm guarantees special accuracy
		BerserkerRage rage = BerserkerRage.getOrCreate(stoner);
		if (rage.getCombatRhythm() >= 20) {
			baseModifier *= 2.0; // Legendary perfect special
		} else if (rage.getCombatRhythm() >= 12) {
			baseModifier *= 1.5; // Master special timing
		}

		// Weapon mastery improves special accuracy
		WeaponMastery mastery = WeaponMastery.getOrCreate(stoner, weapon.getId());
		baseModifier *= (1.0 + (mastery.getProficiency() / 2000.0)); // Up to 50% bonus at 1000 proficiency

		return baseModifier;
	}

	public static double getEffectiveStr(Stoner stoner) {
		double baseStr = stoner.getGrades()[2];

		// Berserker rage amplifies strength
		BerserkerRage rage = BerserkerRage.getOrCreate(stoner);
		double rageStr = 1.0 + (rage.getIntensity() * 0.08); // Up to 80% strength boost

		// Bloodlust increases raw power
		if (rage.getBloodlust() >= 10) {
			rageStr *= 1.2; // Bloodthirsty warrior bonus
		}

		return (baseStr * getResonanceStr(stoner) * rageStr);
	}

	public static double getResonanceStr(Stoner stoner) {
		// Enhanced necromancy integration with warrior spirit
		BerserkerRage rage = BerserkerRage.getOrCreate(stoner);

		// Death magic flows stronger through berserkers
		if (rage.getIntensity() >= 7.0) {
			return 1.15; // Necromantic berserker synergy
		}

		return 1.0;
	}

	// === COMBAT EVENT INTEGRATION ===

	/**
	 * Call this after every melee attack to update warrior systems
	 */
	public static void updateMeleeCombat(Stoner warrior, Entity target, boolean hit, int damage) {
		if (warrior == null) return;

		// Update berserker rage
		BerserkerRage rage = BerserkerRage.getOrCreate(warrior);
		rage.buildRage(hit, damage);

		// Update weapon mastery
		Item weapon = warrior.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT];
		if (weapon != null) {
			WeaponMastery mastery = WeaponMastery.getOrCreate(warrior, weapon.getId());

			// Perfect hit criteria: high damage or critical timing
			boolean perfectHit = (damage > 0 && (damage >= 25 || rage.getCombatRhythm() >= 5));
			mastery.gainProficiency(perfectHit);
		}

		// Update general combat evolution
		FormulaData.updateCombatEvolution(warrior, target, hit, damage);
	}

	/**
	 * Get warrior status for player feedback
	 */
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

	/**
	 * Get weapon mastery status
	 */
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