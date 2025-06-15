package com.bestbudz.rs2.content.combat.formula;

import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.content.profession.mage.spells.Charge;
import com.bestbudz.rs2.content.profession.mercenary.Mercenary;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Enhanced Mage Combat System - "Arcane Mastery"
 * Features: Elemental attunement, spell weaving, mana resonance, magical flow
 */
public class MageFormulas {

	// === ELEMENTAL ATTUNEMENT SYSTEM ===
	public static class ElementalAttunement {
		private double fireAffinity = 1.0;
		private double waterAffinity = 1.0;
		private double airAffinity = 1.0;
		private double earthAffinity = 1.0;
		private int dominantElement = 0; // 0=none, 1=fire, 2=water, 3=air, 4=earth
		private double elementalMastery = 1.0;

		public static ElementalAttunement getOrCreate(Entity entity) {
			Object stored = entity.getAttributes().get("elemental_attunement");
			if (stored instanceof ElementalAttunement) {
				return (ElementalAttunement) stored;
			}
			ElementalAttunement attunement = new ElementalAttunement();
			entity.getAttributes().set("elemental_attunement", attunement);
			return attunement;
		}

		public void attuneTo(int elementType, boolean successful) {
			double gain = successful ? 0.05 : 0.01;

			switch (elementType) {
				case 1: // Fire
					fireAffinity = Math.min(3.0, fireAffinity + gain);
					if (fireAffinity > Math.max(waterAffinity, Math.max(airAffinity, earthAffinity))) {
						dominantElement = 1;
					}
					break;
				case 2: // Water
					waterAffinity = Math.min(3.0, waterAffinity + gain);
					if (waterAffinity > Math.max(fireAffinity, Math.max(airAffinity, earthAffinity))) {
						dominantElement = 2;
					}
					break;
				case 3: // Air
					airAffinity = Math.min(3.0, airAffinity + gain);
					if (airAffinity > Math.max(fireAffinity, Math.max(waterAffinity, earthAffinity))) {
						dominantElement = 3;
					}
					break;
				case 4: // Earth
					earthAffinity = Math.min(3.0, earthAffinity + gain);
					if (earthAffinity > Math.max(fireAffinity, Math.max(waterAffinity, airAffinity))) {
						dominantElement = 4;
					}
					break;
			}

			// Calculate overall elemental mastery
			elementalMastery = (fireAffinity + waterAffinity + airAffinity + earthAffinity) / 4.0;
		}

		public double getAffinityFor(int elementType) {
			switch (elementType) {
				case 1: return fireAffinity;
				case 2: return waterAffinity;
				case 3: return airAffinity;
				case 4: return earthAffinity;
				default: return 1.0;
			}
		}

		public int getDominantElement() { return dominantElement; }
		public double getElementalMastery() { return elementalMastery; }
	}

	// === SPELL WEAVING SYSTEM ===
	public static class SpellWeaving {
		private int comboCast = 0;
		private int lastSpellId = -1;
		private long lastCastTime = 0;
		private double magicalFlow = 1.0;
		private int perfectCasts = 0;
		private double channelPower = 1.0;

		public static SpellWeaving getOrCreate(Entity entity) {
			Object stored = entity.getAttributes().get("spell_weaving");
			if (stored instanceof SpellWeaving) {
				return (SpellWeaving) stored;
			}
			SpellWeaving weaving = new SpellWeaving();
			entity.getAttributes().set("spell_weaving", weaving);
			return weaving;
		}

		public void castSpell(int spellId, boolean successful) {
			long currentTime = System.currentTimeMillis();
			double timeDelta = currentTime - lastCastTime;

			if (successful) {
				// Perfect timing builds magical flow (1.5-2.5 second intervals)
				if (timeDelta >= 1500 && timeDelta <= 2500) {
					magicalFlow = Math.min(2.8, magicalFlow + 0.15);
					perfectCasts++;

					// Spell combination detection
					if (spellId != lastSpellId && lastSpellId != -1) {
						comboCast++;
						channelPower = Math.min(2.5, channelPower + 0.1);
					}
				} else {
					magicalFlow = Math.min(2.5, magicalFlow + 0.08);
				}
			} else {
				// Failed casts disrupt flow
				magicalFlow = Math.max(0.7, magicalFlow - 0.12);
				comboCast = Math.max(0, comboCast - 2);
				channelPower = Math.max(0.8, channelPower - 0.05);
			}

			// Flow naturally decays over time
			if (timeDelta > 5000) { // 5 seconds of inactivity
				magicalFlow = Math.max(1.0, magicalFlow * 0.8);
				comboCast = 0;
				channelPower = Math.max(1.0, channelPower * 0.9);
			}

			lastSpellId = spellId;
			lastCastTime = currentTime;
		}

		public int getComboCast() { return comboCast; }
		public double getMagicalFlow() { return magicalFlow; }
		public int getPerfectCasts() { return perfectCasts; }
		public double getChannelPower() { return channelPower; }
	}

	// === MANA RESONANCE SYSTEM ===
	public static class ManaResonance {
		private double resonanceLevel = 1.0;
		private int spellStreak = 0;
		private double arcanePower = 1.0;

		public static ManaResonance getOrCreate(Entity entity) {
			Object stored = entity.getAttributes().get("mana_resonance");
			if (stored instanceof ManaResonance) {
				return (ManaResonance) stored;
			}
			ManaResonance resonance = new ManaResonance();
			entity.getAttributes().set("mana_resonance", resonance);
			return resonance;
		}

		public void buildResonance(boolean spellSuccess, int spellPower) {
			if (spellSuccess) {
				spellStreak++;
				resonanceLevel = Math.min(3.5, resonanceLevel + 0.1 + (spellPower / 1000.0));
				arcanePower = Math.min(2.0, arcanePower + 0.05);
			} else {
				spellStreak = Math.max(0, spellStreak - 3);
				resonanceLevel = Math.max(0.8, resonanceLevel - 0.08);
				arcanePower = Math.max(0.9, arcanePower - 0.03);
			}
		}

		public double getResonanceLevel() { return resonanceLevel; }
		public int getSpellStreak() { return spellStreak; }
		public double getArcanePower() { return arcanePower; }
	}

	// === SPELL ELEMENT DETECTION ===
	private static int getSpellElement(int spellId) {
		// Fire spells
		if (spellId >= 1152 && spellId <= 1158) return 1; // Fire Strike to Fire Surge
		if (spellId == 12037) return 1; // Fire Wave

		// Water spells
		if (spellId >= 1163 && spellId <= 1169) return 2; // Water Strike to Water Surge

		// Air spells
		if (spellId >= 1152 && spellId <= 1158) return 3; // Air Strike to Air Surge

		// Earth spells
		if (spellId >= 1174 && spellId <= 1180) return 4; // Earth Strike to Earth Surge

		// Special spells
		if (spellId >= 1190 && spellId <= 1192) return 1; // God spells (fire-based)

		return 0; // Non-elemental
	}

	// === ENHANCED ORIGINAL METHODS ===

	public static double getEffectiveMageAccuracy(Entity entity) {
		Stoner assaulter = null;

		if (!entity.isNpc()) {
			assaulter = com.bestbudz.rs2.entity.World.getStoners()[entity.getIndex()];
		} else {
			double assaultBonus = 0;
			double baseAssault = 0;

			if (entity.getBonuses() != null) {
				assaultBonus = entity.getBonuses()[3];
			}

			if (entity.getGrades() != null) {
				baseAssault = entity.getGrades()[6];
			}

			double baseAccuracy = Math.floor(baseAssault + assaultBonus) + 8;

			// NPCs get magical resistance scaling
			SpellWeaving weaving = SpellWeaving.getOrCreate(entity);
			if (weaving.getComboCast() > 5) {
				baseAccuracy *= 0.9; // NPCs resist combo casting
			}

			return baseAccuracy;
		}

		double assaultBonus = assaulter.getBonuses()[3];
		double baseAssault = assaulter.getProfession().getGrades()[6];
		double baseAccuracy = Math.floor(baseAssault + assaultBonus) + 15;

		// Magical flow enhances accuracy
		SpellWeaving weaving = SpellWeaving.getOrCreate(assaulter);
		baseAccuracy *= weaving.getMagicalFlow();

		// Elemental mastery bonus
		ElementalAttunement attunement = ElementalAttunement.getOrCreate(assaulter);
		baseAccuracy *= (1.0 + (attunement.getElementalMastery() - 1.0) * 0.3);

		// Perfect casting streak bonus
		if (weaving.getPerfectCasts() >= 10) {
			baseAccuracy *= 1.35; // Archmage precision
		} else if (weaving.getPerfectCasts() >= 5) {
			baseAccuracy *= 1.2; // Master wizard precision
		}

		return baseAccuracy;
	}

	public static double getMageAssaultRoll(Entity entity) {
		double specAccuracy = 1.0;
		double effectiveAccuracy = getEffectiveMageAccuracy(entity);

		if (!entity.isNpc()) {
			Stoner mage = com.bestbudz.rs2.entity.World.getStoners()[entity.getIndex()];
			if (mage != null) {
				double resonanceAccuracyBonus = mage.getResonance().getAccuracyBonus();
				effectiveAccuracy *= (1.0 + resonanceAccuracyBonus);
				// Spell combination mastery
				SpellWeaving weaving = SpellWeaving.getOrCreate(mage);
				if (weaving.getComboCast() >= 8) {
					specAccuracy = 1.5; // Legendary spell weaving
				} else if (weaving.getComboCast() >= 5) {
					specAccuracy = 1.25; // Master spell weaving
				}

				// Mana resonance affects accuracy
				ManaResonance resonance = ManaResonance.getOrCreate(mage);
				specAccuracy *= resonance.getResonanceLevel();

				// Channel power multiplier
				specAccuracy *= weaving.getChannelPower();
			}
		}

		int styleBonusAssault = 0;
		effectiveAccuracy *= (1 + (styleBonusAssault) / 64);

		return (int) (effectiveAccuracy * specAccuracy);
	}

	public static double getMageAegisRoll(Entity entity) {
		Stoner blocker = null;
		if (!entity.isNpc()) {
			blocker = com.bestbudz.rs2.entity.World.getStoners()[entity.getIndex()];
		} else {
			if (entity.getBonuses() != null && entity.getGrades() != null) {
				double effectiveAegis = getEffectiveMageAegis(entity);
				effectiveAegis += entity.getBonuses()[8];
				long grade = entity.getGrades()[6];
				effectiveAegis = (int) (Math.floor(grade * 1.10) + Math.floor(effectiveAegis * 0.25));

				// NPCs get anti-magic resistance based on magical pressure
				ElementalAttunement nearby = ElementalAttunement.getOrCreate(entity);
				if (nearby.getElementalMastery() > 2.0) {
					effectiveAegis *= 1.15; // Strong magical fields strengthen defenses
				}

				return effectiveAegis;
			}
			return 0;
		}

		int styleBonusAegis = 0;
		double effectiveAegis = getEffectiveMageAegis(entity);
		effectiveAegis += blocker.getBonuses()[8];
		long grade = blocker.getProfession().getGrades()[6];
		effectiveAegis = (int) (Math.floor(grade * 1.10) + Math.floor(effectiveAegis * 0.25));

		// Magical knowledge improves magic defense
		SpellWeaving weaving = SpellWeaving.getOrCreate(blocker);
		if (weaving.getPerfectCasts() >= 15) {
			effectiveAegis *= 1.25; // Arcane understanding
		}

		// Elemental resistance
		ElementalAttunement attunement = ElementalAttunement.getOrCreate(blocker);
		if (attunement.getDominantElement() > 0) {
			effectiveAegis *= 1.1; // Elemental mastery provides resistance
		}

		effectiveAegis *= (1 + (styleBonusAegis) / 64);

		return effectiveAegis;
	}

	public static double getEffectiveMageAegis(Entity entity) {
		Stoner blocker = null;
		if (!entity.isNpc()) {
			blocker = com.bestbudz.rs2.entity.World.getStoners()[entity.getIndex()];
		} else {
			if (entity.getGrades() != null) {
				return Math.floor(entity.getGrades()[1]) + 8;
			}
			return 0;
		}
		double baseAegis = blocker.getProfession().getGrades()[1];
		return Math.floor(baseAegis) + 10;
	}

	public static int enhancedMageMaxHit(Stoner stoner) {
		int baseMaxHit = mageMaxHit(stoner);

		// Apply arcane scaling
		SpellWeaving weaving = SpellWeaving.getOrCreate(stoner);
		double arcaneScaling = 1.0 + (weaving.getComboCast() * 0.03); // 3% per combo

		// Elemental affinity scaling
		int spellId = stoner.getMage().getSpellCasting().getCurrentSpellId();
		if (spellId != -1) {
			int element = getSpellElement(spellId);
			if (element > 0) {
				ElementalAttunement attunement = ElementalAttunement.getOrCreate(stoner);
				arcaneScaling *= attunement.getAffinityFor(element);
			}
		}

		double enhancedHit = FormulaData.applyEmergentScaling(stoner, baseMaxHit * arcaneScaling);
		return (int)enhancedHit;
	}

	public static int mageMaxHit(Stoner stoner) {
		int spellId = stoner.getMage().getSpellCasting().getCurrentSpellId();

		if (spellId == -1) {
			return 0;
		}

		double damage = stoner.getMage().getSpellCasting().getDefinition(spellId).getBaseMaxHit();
		double damageMultiplier = 1.0;

		// Elemental attunement affects damage
		int spellElement = getSpellElement(spellId);
		if (spellElement > 0) {
			ElementalAttunement attunement = ElementalAttunement.getOrCreate(stoner);
			damageMultiplier *= attunement.getAffinityFor(spellElement);

			// Dominant element mastery bonus
			if (attunement.getDominantElement() == spellElement) {
				damageMultiplier *= 1.2; // 20% bonus for dominant element
			}
		}

		// Spell weaving bonuses
		SpellWeaving weaving = SpellWeaving.getOrCreate(stoner);
		damageMultiplier *= weaving.getMagicalFlow();

		// Combo casting power
		if (weaving.getComboCast() >= 10) {
			damageMultiplier *= 1.6; // Legendary spell weaving
		} else if (weaving.getComboCast() >= 6) {
			damageMultiplier *= 1.35; // Master spell weaving
		} else if (weaving.getComboCast() >= 3) {
			damageMultiplier *= 1.15; // Skilled spell weaving
		}

		// Channel power amplification
		damageMultiplier *= weaving.getChannelPower();

		// Mana resonance scaling
		ManaResonance resonance = ManaResonance.getOrCreate(stoner);
		damageMultiplier *= resonance.getResonanceLevel();

		// Spell streak power
		if (resonance.getSpellStreak() >= 15) {
			damageMultiplier *= 1.4; // Arcane mastery
		} else if (resonance.getSpellStreak() >= 10) {
			damageMultiplier *= 1.25; // Magical expertise
		} else if (resonance.getSpellStreak() >= 5) {
			damageMultiplier *= 1.1; // Magical focus
		}

		// Mercenary task bonus
		Item helm = stoner.getEquipment().getItems()[0];
		if ((helm != null) && (helm.getId() == 15492) && (stoner.getCombat().getAssaulting() != null && stoner.getCombat().getAssaulting().isNpc()) && (stoner.getMercenary().hasTask())) {
			Mob m = com.bestbudz.rs2.entity.World.getNpcs()[stoner.getCombat().getAssaulting().getIndex()];
			if ((m != null) && (Mercenary.isMercenaryTask(stoner, m))) {
				damageMultiplier += 0.125D;

				// Battlemage precision
				if (weaving.getPerfectCasts() >= 8) {
					damageMultiplier += 0.075D; // Additional 7.5% for perfect casting assassins
				}
			}
		}

		// Dragon fire shield special case
		if (stoner.getMage().isDFireShieldEffect()) {
			return 23;
		}

		// God spell charges with elemental synergy
		if ((spellId >= 1190) && (spellId <= 1192) && (Charge.isChargeActive(stoner))) {
			damageMultiplier += 0.6D;

			// Fire mastery enhances god spells
			ElementalAttunement attunement = ElementalAttunement.getOrCreate(stoner);
			if (attunement.getAffinityFor(1) >= 2.0) { // Fire affinity
				damageMultiplier += 0.2D; // Fire god synergy
			}
		}

		// High level magic scaling with resonance
		if (stoner.getProfession().getGrades()[6] > stoner.getProfession().getGradeForExperience(6, stoner.getProfession().getExperience()[6])
			&& stoner.getProfession().getGradeForExperience(6, stoner.getProfession().getExperience()[6]) >= 95) {
			double extraLevels = stoner.getProfession().getGrades()[6] - 420;
			damageMultiplier += .03 * extraLevels;

			// Transcendent magic user
			if (extraLevels >= 180) { // Level 600+
				damageMultiplier *= resonance.getArcanePower();
			}
		}

		// Staff bonuses with magical synergy
		if (stoner.getEquipment().getItems()[3] != null) {
			switch (stoner.getEquipment().getItems()[3].getId()) {
				case 20076: // Staff of light
				case 20074: // Staff of the dead
					damageMultiplier += 0.15;

					// Light/Death magic resonance
					if (weaving.getMagicalFlow() > 2.0) {
						damageMultiplier += 0.1; // Enhanced staff mastery
					}
					break;
				case 20086: // Toxic staff of the dead
					damageMultiplier += 0.8;

					// Toxic magic amplification
					if (resonance.getSpellStreak() >= 8) {
						damageMultiplier += 0.2; // Toxic mastery
					}
					break;
			}
		}

		// Special spell cases
		if (spellId > 0) {
			switch (spellId) {
				case 12037: // Fire Wave
					damage += stoner.getProfession().getGrades()[6] / 10;

					// Fire mastery scaling
					ElementalAttunement attunement = ElementalAttunement.getOrCreate(stoner);
					if (attunement.getAffinityFor(1) >= 2.5) {
						damage += stoner.getProfession().getGrades()[6] / 20; // Additional fire wave power
					}
					break;
			}
		}

		// Apply advance level scaling with arcane mastery
		int lifeAdvances = stoner.getProfessionAdvances()[3];
		if (lifeAdvances > 0) {
			damageMultiplier += lifeAdvances * 0.03;
		}

		int resonanceAdvances = stoner.getProfessionAdvances()[5];
		if (resonanceAdvances > 0) {
			damageMultiplier += resonanceAdvances * 0.01;
		}

		int mageAdvances = stoner.getProfessionAdvances()[6];
		if (mageAdvances > 0) {
			damageMultiplier += mageAdvances * 0.08;

			// Archmage elemental mastery
			if (mageAdvances >= 15) {
				ElementalAttunement attunement = ElementalAttunement.getOrCreate(stoner);
				damageMultiplier += attunement.getElementalMastery() * 0.05; // 5% per elemental mastery level
			}
		}

		int weedsmokingAdvances = stoner.getProfessionAdvances()[16];
		if (weedsmokingAdvances > 0) {
			damageMultiplier += weedsmokingAdvances * 0.01;
		}

		int mercenaryAdvances = stoner.getProfessionAdvances()[18];
		if (mercenaryAdvances > 0) {
			damageMultiplier += mercenaryAdvances * 0.02;
		}

		damage *= damageMultiplier;
    double resonanceMultiplier =
        stoner.getResonance().applyResonanceEffects(1.0, Combat.CombatTypes.MAGE);
		damage *= resonanceMultiplier;

		return (int) damage;
	}

	// === COMBAT EVENT INTEGRATION ===

	/**
	 * Call this after every magic attack to update arcane systems
	 */
	public static void updateMagicCombat(Stoner mage, Entity target, boolean hit, int damage, int spellId) {
		if (mage == null) return;

		// Update spell weaving
		SpellWeaving weaving = SpellWeaving.getOrCreate(mage);
		weaving.castSpell(spellId, hit);

		// Update elemental attunement
		int spellElement = getSpellElement(spellId);
		if (spellElement > 0) {
			ElementalAttunement attunement = ElementalAttunement.getOrCreate(mage);
			attunement.attuneTo(spellElement, hit);
		}

		// Update mana resonance
		ManaResonance resonance = ManaResonance.getOrCreate(mage);
		resonance.buildResonance(hit, damage);

		// Update general combat evolution
		FormulaData.updateCombatEvolution(mage, target, hit, damage);
	}

	/**
	 * Get mage status for player feedback
	 */
	public static String getMageStatus(Stoner mage) {
		if (mage == null) return "Unknown";

		SpellWeaving weaving = SpellWeaving.getOrCreate(mage);
		ManaResonance resonance = ManaResonance.getOrCreate(mage);
		ElementalAttunement attunement = ElementalAttunement.getOrCreate(mage);

		if (weaving.getComboCast() >= 15 && resonance.getSpellStreak() >= 20) return "Archmage Supreme";
		if (weaving.getComboCast() >= 12) return "Master Spellweaver";
		if (resonance.getSpellStreak() >= 15) return "Arcane Master";
		if (weaving.getComboCast() >= 8) return "Spell Weaver";
		if (attunement.getElementalMastery() >= 2.5) return "Elemental Master";
		if (resonance.getSpellStreak() >= 10) return "Mana Resonance";
		if (weaving.getComboCast() >= 5) return "Combo Caster";
		if (weaving.getMagicalFlow() >= 2.0) return "Magical Flow";
		if (attunement.getDominantElement() > 0) return getElementName(attunement.getDominantElement()) + " Adept";
		if (resonance.getSpellStreak() >= 5) return "Focused Caster";
		if (weaving.getComboCast() >= 3) return "Spell Student";

		return "Apprentice";
	}

	/**
	 * Get elemental mastery status
	 */
	public static String getElementalStatus(Stoner mage) {
		ElementalAttunement attunement = ElementalAttunement.getOrCreate(mage);

		if (attunement.getElementalMastery() >= 3.0) return "Elemental Transcendent";
		if (attunement.getElementalMastery() >= 2.5) return "Elemental Grandmaster";
		if (attunement.getElementalMastery() >= 2.0) return "Elemental Master";
		if (attunement.getElementalMastery() >= 1.5) return "Elemental Adept";
		if (attunement.getDominantElement() > 0) return getElementName(attunement.getDominantElement()) + " Specialist";

		return "Balanced Caster";
	}

	private static String getElementName(int element) {
		switch (element) {
			case 1: return "Fire";
			case 2: return "Water";
			case 3: return "Air";
			case 4: return "Earth";
			default: return "Neutral";
		}
	}

	/**
	 * Get spell weaving combo description
	 */
	public static String getComboDescription(Stoner mage) {
		SpellWeaving weaving = SpellWeaving.getOrCreate(mage);
		int combo = weaving.getComboCast();

		if (combo >= 15) return "Legendary Weaving";
		if (combo >= 12) return "Master Combination";
		if (combo >= 8) return "Expert Weaving";
		if (combo >= 5) return "Skilled Combo";
		if (combo >= 3) return "Basic Weaving";

		return "Single Cast";
	}
}