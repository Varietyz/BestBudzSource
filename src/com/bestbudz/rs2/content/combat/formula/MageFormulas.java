package com.bestbudz.rs2.content.combat.formula;

import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.content.profession.mage.spells.Charge;
import com.bestbudz.rs2.content.profession.mercenary.Mercenary;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class MageFormulas {

	public static class ElementalAttunement {
		private double fireAffinity = 1.0;
		private double waterAffinity = 1.0;
		private double airAffinity = 1.0;
		private double earthAffinity = 1.0;
		private int dominantElement = 0;
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
				case 1:
					fireAffinity = Math.min(3.0, fireAffinity + gain);
					if (fireAffinity > Math.max(waterAffinity, Math.max(airAffinity, earthAffinity))) {
						dominantElement = 1;
					}
					break;
				case 2:
					waterAffinity = Math.min(3.0, waterAffinity + gain);
					if (waterAffinity > Math.max(fireAffinity, Math.max(airAffinity, earthAffinity))) {
						dominantElement = 2;
					}
					break;
				case 3:
					airAffinity = Math.min(3.0, airAffinity + gain);
					if (airAffinity > Math.max(fireAffinity, Math.max(waterAffinity, earthAffinity))) {
						dominantElement = 3;
					}
					break;
				case 4:
					earthAffinity = Math.min(3.0, earthAffinity + gain);
					if (earthAffinity > Math.max(fireAffinity, Math.max(waterAffinity, airAffinity))) {
						dominantElement = 4;
					}
					break;
			}

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

				if (timeDelta >= 1500 && timeDelta <= 2500) {
					magicalFlow = Math.min(2.8, magicalFlow + 0.15);
					perfectCasts++;

					if (spellId != lastSpellId && lastSpellId != -1) {
						comboCast++;
						channelPower = Math.min(2.5, channelPower + 0.1);
					}
				} else {
					magicalFlow = Math.min(2.5, magicalFlow + 0.08);
				}
			} else {

				magicalFlow = Math.max(0.7, magicalFlow - 0.12);
				comboCast = Math.max(0, comboCast - 2);
				channelPower = Math.max(0.8, channelPower - 0.05);
			}

			if (timeDelta > 5000) {
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

	private static int getSpellElement(int spellId) {

		if (spellId >= 1152 && spellId <= 1158) return 1;
		if (spellId == 12037) return 1;

		if (spellId >= 1163 && spellId <= 1169) return 2;

		if (spellId >= 1152 && spellId <= 1158) return 3;

		if (spellId >= 1174 && spellId <= 1180) return 4;

		if (spellId >= 1190 && spellId <= 1192) return 1;

		return 0;
	}

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

			SpellWeaving weaving = SpellWeaving.getOrCreate(entity);
			if (weaving.getComboCast() > 5) {
				baseAccuracy *= 0.9;
			}

			return baseAccuracy;
		}

		double assaultBonus = assaulter.getBonuses()[3];
		double baseAssault = assaulter.getProfession().getGrades()[6];
		double baseAccuracy = Math.floor(baseAssault + assaultBonus) + 15;

		SpellWeaving weaving = SpellWeaving.getOrCreate(assaulter);
		baseAccuracy *= weaving.getMagicalFlow();

		ElementalAttunement attunement = ElementalAttunement.getOrCreate(assaulter);
		baseAccuracy *= (1.0 + (attunement.getElementalMastery() - 1.0) * 0.3);

		if (weaving.getPerfectCasts() >= 10) {
			baseAccuracy *= 1.35;
		} else if (weaving.getPerfectCasts() >= 5) {
			baseAccuracy *= 1.2;
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

				SpellWeaving weaving = SpellWeaving.getOrCreate(mage);
				if (weaving.getComboCast() >= 8) {
					specAccuracy = 1.5;
				} else if (weaving.getComboCast() >= 5) {
					specAccuracy = 1.25;
				}

				ManaResonance resonance = ManaResonance.getOrCreate(mage);
				specAccuracy *= resonance.getResonanceLevel();

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

				ElementalAttunement nearby = ElementalAttunement.getOrCreate(entity);
				if (nearby.getElementalMastery() > 2.0) {
					effectiveAegis *= 1.15;
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

		SpellWeaving weaving = SpellWeaving.getOrCreate(blocker);
		if (weaving.getPerfectCasts() >= 15) {
			effectiveAegis *= 1.25;
		}

		ElementalAttunement attunement = ElementalAttunement.getOrCreate(blocker);
		if (attunement.getDominantElement() > 0) {
			effectiveAegis *= 1.1;
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

		SpellWeaving weaving = SpellWeaving.getOrCreate(stoner);
		double arcaneScaling = 1.0 + (weaving.getComboCast() * 0.03);

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

		int spellElement = getSpellElement(spellId);
		if (spellElement > 0) {
			ElementalAttunement attunement = ElementalAttunement.getOrCreate(stoner);
			damageMultiplier *= attunement.getAffinityFor(spellElement);

			if (attunement.getDominantElement() == spellElement) {
				damageMultiplier *= 1.2;
			}
		}

		SpellWeaving weaving = SpellWeaving.getOrCreate(stoner);
		damageMultiplier *= weaving.getMagicalFlow();

		if (weaving.getComboCast() >= 10) {
			damageMultiplier *= 1.6;
		} else if (weaving.getComboCast() >= 6) {
			damageMultiplier *= 1.35;
		} else if (weaving.getComboCast() >= 3) {
			damageMultiplier *= 1.15;
		}

		damageMultiplier *= weaving.getChannelPower();

		ManaResonance resonance = ManaResonance.getOrCreate(stoner);
		damageMultiplier *= resonance.getResonanceLevel();

		if (resonance.getSpellStreak() >= 15) {
			damageMultiplier *= 1.4;
		} else if (resonance.getSpellStreak() >= 10) {
			damageMultiplier *= 1.25;
		} else if (resonance.getSpellStreak() >= 5) {
			damageMultiplier *= 1.1;
		}

		Item helm = stoner.getEquipment().getItems()[0];
		if ((helm != null) && (helm.getId() == 15492) && (stoner.getCombat().getAssaulting() != null && stoner.getCombat().getAssaulting().isNpc()) && (stoner.getMercenary().hasTask())) {
			Mob m = com.bestbudz.rs2.entity.World.getNpcs()[stoner.getCombat().getAssaulting().getIndex()];
			if ((m != null) && (Mercenary.isMercenaryTask(stoner, m))) {
				damageMultiplier += 0.125D;

				if (weaving.getPerfectCasts() >= 8) {
					damageMultiplier += 0.075D;
				}
			}
		}

		if (stoner.getMage().isDFireShieldEffect()) {
			return 23;
		}

		if ((spellId >= 1190) && (spellId <= 1192) && (Charge.isChargeActive(stoner))) {
			damageMultiplier += 0.6D;

			ElementalAttunement attunement = ElementalAttunement.getOrCreate(stoner);
			if (attunement.getAffinityFor(1) >= 2.0) {
				damageMultiplier += 0.2D;
			}
		}

		if (stoner.getProfession().getGrades()[6] > stoner.getProfession().getGradeForExperience(6, stoner.getProfession().getExperience()[6])
			&& stoner.getProfession().getGradeForExperience(6, stoner.getProfession().getExperience()[6]) >= 95) {
			double extraLevels = stoner.getProfession().getGrades()[6] - 420;
			damageMultiplier += .03 * extraLevels;

			if (extraLevels >= 180) {
				damageMultiplier *= resonance.getArcanePower();
			}
		}

		if (stoner.getEquipment().getItems()[3] != null) {
			switch (stoner.getEquipment().getItems()[3].getId()) {
				case 20076:
				case 20074:
					damageMultiplier += 0.15;

					if (weaving.getMagicalFlow() > 2.0) {
						damageMultiplier += 0.1;
					}
					break;
				case 20086:
					damageMultiplier += 0.8;

					if (resonance.getSpellStreak() >= 8) {
						damageMultiplier += 0.2;
					}
					break;
			}
		}

		if (spellId > 0) {
			switch (spellId) {
				case 12037:
					damage += stoner.getProfession().getGrades()[6] / 10;

					ElementalAttunement attunement = ElementalAttunement.getOrCreate(stoner);
					if (attunement.getAffinityFor(1) >= 2.5) {
						damage += stoner.getProfession().getGrades()[6] / 20;
					}
					break;
			}
		}

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

			if (mageAdvances >= 15) {
				ElementalAttunement attunement = ElementalAttunement.getOrCreate(stoner);
				damageMultiplier += attunement.getElementalMastery() * 0.05;
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

	public static void updateMagicCombat(Stoner mage, Entity target, boolean hit, int damage, int spellId) {
		if (mage == null) return;

		SpellWeaving weaving = SpellWeaving.getOrCreate(mage);
		weaving.castSpell(spellId, hit);

		int spellElement = getSpellElement(spellId);
		if (spellElement > 0) {
			ElementalAttunement attunement = ElementalAttunement.getOrCreate(mage);
			attunement.attuneTo(spellElement, hit);
		}

		ManaResonance resonance = ManaResonance.getOrCreate(mage);
		resonance.buildResonance(hit, damage);

		FormulaData.updateCombatEvolution(mage, target, hit, damage);
	}

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
