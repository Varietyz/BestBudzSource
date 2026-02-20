package com.bestbudz.rs2.content.combat.formula;

import com.bestbudz.core.definitions.SagittariusWeaponDefinition;
import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.content.profession.mercenary.Mercenary;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.item.ItemCheck;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class RangeFormulas {

	public static final double[][] SAGITTARIUS_RESONANCE_MODIFIERS = {
		{3.0D, 0.05D}, {11.0D, 0.1D}, {19.0D, 0.15D}
	};

	public static class WindPattern {
		private double velocity = 0.0;
		private double direction = 0.0;
		private long lastUpdate = 0;
		private int stability = 0;

		public static WindPattern getOrCreate(Entity entity) {
			Object stored = entity.getAttributes().get("wind_pattern");
			if (stored instanceof WindPattern) {
				return (WindPattern) stored;
			}
			WindPattern pattern = new WindPattern();
			entity.getAttributes().set("wind_pattern", pattern);
			return pattern;
		}

		public void updateWind() {
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastUpdate > 3000) {
				velocity = Math.random() * 2.0 - 1.0;
				direction = Math.random() * 360.0;
				stability = (int)(Math.random() * 10) + 1;
				lastUpdate = currentTime;
			}
		}

		public double getVelocity() { return velocity; }
		public double getDirection() { return direction; }
		public int getStability() { return stability; }
	}

	public static class HunterInstinct {
		private int trackingStreak = 0;
		private double predatorFocus = 1.0;
		private long lastHit = 0;
		private int consecutiveMisses = 0;

		public static HunterInstinct getOrCreate(Entity entity) {
			Object stored = entity.getAttributes().get("hunter_instinct");
			if (stored instanceof HunterInstinct) {
				return (HunterInstinct) stored;
			}
			HunterInstinct instinct = new HunterInstinct();
			entity.getAttributes().set("hunter_instinct", instinct);
			return instinct;
		}

		public void recordHit() {
			trackingStreak++;
			consecutiveMisses = 0;
			predatorFocus = Math.min(2.5, predatorFocus + 0.15);
			lastHit = System.currentTimeMillis();
		}

		public void recordMiss() {
			trackingStreak = Math.max(0, trackingStreak - 2);
			consecutiveMisses++;
			predatorFocus = Math.max(0.7, predatorFocus - 0.08);
		}

		public int getTrackingStreak() { return trackingStreak; }
		public double getPredatorFocus() { return predatorFocus; }
		public int getConsecutiveMisses() { return consecutiveMisses; }
	}

	private static double calculateArrowPhysics(Stoner archer, Entity target) {
		WindPattern wind = WindPattern.getOrCreate(archer);
		wind.updateWind();

		double distance = calculateDistance(archer, target);
		double optimalRange = getOptimalRange(archer);

		double rangeEfficiency = 1.0;
		if (distance <= optimalRange * 1.2 && distance >= optimalRange * 0.8) {
			rangeEfficiency = 1.25;
		} else if (distance > optimalRange * 2.0) {
			rangeEfficiency = 0.75;
		}

		double windCompensation = 1.0;
		if (wind.getStability() < 5) {
			windCompensation = 0.85 + (archer.getProfession().getGrades()[4] / 1000.0);
		}

		double trajectory = Math.sin(distance / 100.0) * wind.getVelocity() * 0.1;

		return rangeEfficiency * windCompensation * (1.0 - Math.abs(trajectory));
	}

	private static double calculateDistance(Entity archer, Entity target) {

		return Math.abs(archer.getIndex() - target.getIndex()) / 10.0 + 5.0;
	}

	private static double getOptimalRange(Stoner archer) {
		Item weapon = archer.getEquipment().getItems()[3];
		if (weapon == null) return 15.0;

		switch (weapon.getId()) {
			case 11785: return 25.0;
			case 12926: return 12.0;
			case 11235: return 20.0;
			default: return 15.0;
		}
	}

	public static long calculateRangeAegis(Entity defending) {
		Stoner defender = null;

		if (!defending.isNpc()) {
			defender = World.getStoners()[defending.getIndex()];
		} else {
			if (defending.getBonuses() != null && defending.getGrades() != null) {
				long baseAegis = defending.getGrades()[1] + defending.getBonuses()[9] + (defending.getBonuses()[9] / 2);

				WindPattern wind = WindPattern.getOrCreate(defending);
				wind.updateWind();
				double windResistance = 1.0 + (wind.getStability() / 20.0);

				return (long)(baseAegis * windResistance);
			}
			return 0;
		}

		long aegisGrade = defender.getProfession().getGrades()[1];
		long baseAegis = aegisGrade + defender.getBonuses()[9] + (defender.getBonuses()[9] / 2);

		WindPattern wind = WindPattern.getOrCreate(defender);
		wind.updateWind();

		double environmentalAwareness = 1.0;
		if (defender.getProfession().getGrades()[4] > 80) {
			environmentalAwareness = 1.0 + (wind.getVelocity() * 0.05);
		}

		return (long)(baseAegis * environmentalAwareness);
	}

	public static int calculateRangeAssault(Entity entity) {
		Stoner assaulter = null;

		if (!entity.isNpc()) {
			assaulter = World.getStoners()[entity.getIndex()];

			if ((assaulter != null) && (ItemCheck.hasDFireShield(assaulter)) && (assaulter.getMage().isDFireShieldEffect())) {
				return 100;
			}
		} else {
			if (entity.getGrades() != null) {
				long rangeGrade = entity.getGrades()[4];
				return (int) (rangeGrade + (entity.getBonuses()[4] * 1.25));
			}
			return 0;
		}

		double rangeGrade = assaulter.getProfession().getGrades()[4];

		HunterInstinct instinct = HunterInstinct.getOrCreate(assaulter);
		rangeGrade *= instinct.getPredatorFocus();

		if (instinct.getTrackingStreak() >= 5) {
			rangeGrade *= 1.35;
		} else if (instinct.getTrackingStreak() >= 3) {
			rangeGrade *= 1.15;
		}

		if (instinct.getConsecutiveMisses() >= 3) {
			rangeGrade *= 0.75;
		}

		if (assaulter.getSpecialAssault().isInitialized()) {
			rangeGrade *= getSagittariusSpecialAccuracyModifier(assaulter);

			if (instinct.getTrackingStreak() >= 7) {
				rangeGrade *= 1.5;
			}
		}

		if (ItemCheck.wearingFullVoidSagittarius(assaulter)) {
			rangeGrade += assaulter.getMaxGrades()[4] * 5.5;

			if (assaulter.getSpecialAssault().isInitialized()) {
				rangeGrade *= 5.50;

				if (instinct.getPredatorFocus() > 2.0) {
					rangeGrade *= 1.25;
				}
			}
		}

		if (assaulter.getCombat().getAssaulting() != null) {
			double physicsModifier = calculateArrowPhysics(assaulter, assaulter.getCombat().getAssaulting());
			rangeGrade *= physicsModifier;
		}

		if (!entity.isNpc()) {
			Stoner stoner = World.getStoners()[entity.getIndex()];
			if (stoner != null) {
				double accuracyBonus = stoner.getResonance().getAccuracyBonus();
				rangeGrade *= (1.0 + accuracyBonus);
			}
		}
		return (int) (rangeGrade + (assaulter.getBonuses()[4] * 3.50));
	}

	public static int getEffectiveSagittariusVigour(Stoner stoner) {
		Item weapon = stoner.getEquipment().getItems()[3];

		if ((weapon == null) || (weapon.getSagittariusDefinition() == null)) {
			return 0;
		}

		if (weapon.getId() == 12926) {
			if (stoner.getToxicBlowpipe().getBlowpipeAmmo() == null) {
				return 0;
			}
			int baseVigour = stoner.getToxicBlowpipe().getBlowpipeAmmo().getSagittariusVigourBonus() + 40;

			HunterInstinct instinct = HunterInstinct.getOrCreate(stoner);
			if (instinct.getTrackingStreak() >= 10) {
				baseVigour = (int)(baseVigour * 1.3);
			}

			return baseVigour;
		}

		int rStr = stoner.getBonuses()[12];

		if ((weapon.getSagittariusDefinition().getType() == SagittariusWeaponDefinition.SagittariusTypes.THROWN)
			|| (weapon.getSagittariusDefinition().getArrows() == null)
			|| (weapon.getSagittariusDefinition().getArrows().length == 0)) {
			rStr = weapon.getSagittariusVigourBonus();
		} else {
			Item ammo = stoner.getEquipment().getItems()[13];
			if (ammo != null) {
				rStr = ammo.getSagittariusVigourBonus();

				if (stoner.getProfession().getGrades()[4] > 90) {
					rStr = (int)(rStr * 1.15);
				}
			}
		}

		return rStr;
	}

	public static int enhancedSagittariusMaxHit(Stoner stoner) {
		int baseMaxHit = getSagittariusMaxHit(stoner);

		HunterInstinct instinct = HunterInstinct.getOrCreate(stoner);
		double precisionScaling = 1.0 + (instinct.getTrackingStreak() * 0.02);

		double enhancedHit = FormulaData.applyEmergentScaling(stoner, baseMaxHit * precisionScaling);
		return (int)enhancedHit;
	}

	public static int getSagittariusMaxHit(Stoner stoner) {
		double pBonus = 1.0D;
		int vBonus = 0;
		int sBonus = 0;

		HunterInstinct instinct = HunterInstinct.getOrCreate(stoner);
		pBonus *= instinct.getPredatorFocus();

		if (ItemCheck.wearingFullVoidSagittarius(stoner)) {
			vBonus = 80;

			if (instinct.getTrackingStreak() >= 15) {
				vBonus = 120;
			}
		}

		switch (stoner.getEquipment().getAssaultStyle()) {
			case ACCURATE:
				sBonus = 3;
				if (instinct.getConsecutiveMisses() == 0) {
					sBonus = 5;
				}
				break;
			case AGGRESSIVE:
				sBonus = 2;
				if (instinct.getTrackingStreak() >= 5) {
					sBonus = 4;
				}
				break;
			case DEFENSIVE:
				sBonus = 1;
				break;
			default:
				break;
		}

		long str = stoner.getProfession().getGrades()[4];
		double eS = (int) (str * pBonus + sBonus + vBonus);
		int rngStr = getEffectiveSagittariusVigour(stoner);
		double base = 5.0D + (eS + 8.0D) * (rngStr + 64) / 64.0D;

		if (instinct.getTrackingStreak() >= 20) {
			base *= 1.5;
		} else if (instinct.getTrackingStreak() >= 10) {
			base *= 1.25;
		}

		if (stoner.getSpecialAssault().isInitialized()) {
			base = (int) (base * getSagittariusSpecialModifier(stoner));

			if (instinct.getTrackingStreak() >= 7) {
				base *= 1.2;
			}
		}

		Item helm = stoner.getEquipment().getItems()[0];
		if ((helm != null) && (helm.getId() == 15492) && (stoner.getCombat().getAssaulting().isNpc()) && (stoner.getMercenary().hasTask())) {
			Mob m = World.getNpcs()[stoner.getCombat().getAssaulting().getIndex()];
			if ((m != null) && (Mercenary.isMercenaryTask(stoner, m))) {
				base += base * 0.125D;

				if (instinct.getTrackingStreak() >= 5) {
					base += base * 0.075D;
				}
			}
		}

		int lifeAdv = stoner.getProfessionAdvances()[3];
		if (lifeAdv > 0) {
			base += base * (lifeAdv * 0.03);
		}

		int resonanceAdv = stoner.getProfessionAdvances()[5];
		if (resonanceAdv > 0) {
			base += base * (resonanceAdv * 0.01);
		}

		int sagitAdv = stoner.getProfessionAdvances()[4];
		if (sagitAdv > 0) {
			base += base * (sagitAdv * 0.08);

			if (sagitAdv >= 10) {
				base += base * (instinct.getTrackingStreak() * 0.005);
			}
		}

		int weedAdv = stoner.getProfessionAdvances()[16];
		if (weedAdv > 0) {
			base += base * (weedAdv * 0.01);
		}

		int mercAdv = stoner.getProfessionAdvances()[18];
		if (mercAdv > 0) {
			base += base * (mercAdv * 0.02);
		}

		WindPattern wind = WindPattern.getOrCreate(stoner);
		if (wind.getStability() >= 8) {
			base *= 1.1;
		}

		double resonanceMultiplier = stoner.getResonance().applyResonanceEffects(1.0, Combat.CombatTypes.SAGITTARIUS);
		base *= resonanceMultiplier;

		return (int) base / 11;
	}

	public static double getSagittariusSpecialAccuracyModifier(Stoner stoner) {
		Item weapon = stoner.getEquipment().getItems()[3];
		Item arrow = stoner.getEquipment().getItems()[13];

		if (weapon == null) {
			return 1.0D;
		}

		double baseModifier = 1.0D;

		switch (weapon.getId()) {
			case 11785:
				baseModifier = 2.0D;
				break;
			case 12926:
				baseModifier = 1.4D;
				break;
			case 11235:
				if (arrow != null && arrow.getId() == 11212) {
					baseModifier = 1.5D;
				} else {
					baseModifier = 1.3D;
				}
				break;
			case 13883:
			case 15241:
				baseModifier = 1.2D;
				break;
			case 9185:
				if (arrow != null) {
					switch (arrow.getId()) {
						case 9243:
							baseModifier = 1.15D;
							break;
						case 9244:
							baseModifier = 1.65D;
							break;
						case 9245:
							baseModifier = 1.15D;
							break;
					}
				}
				break;
		}

		HunterInstinct instinct = HunterInstinct.getOrCreate(stoner);
		if (instinct.getTrackingStreak() >= 8) {
			baseModifier *= 1.25;
		}

		return baseModifier;
	}

	public static double getSagittariusSpecialModifier(Stoner stoner) {
		Item weapon = stoner.getEquipment().getItems()[3];
		Item arrow = stoner.getEquipment().getItems()[13];

		if (weapon == null) {
			return 1.0D;
		}

		double baseModifier = 1.0D;

		switch (weapon.getId()) {
			case 12926:
				baseModifier = 1.5D;
				break;
			case 11235:
				if (arrow != null && arrow.getId() == 11212) {
					baseModifier = 1.5D;
				} else {
					baseModifier = 1.3D;
				}
				break;
			case 13883:
			case 15241:
				baseModifier = 1.2D;
				break;
			case 11785:
			case 9185:
				if (arrow != null) {
					switch (arrow.getId()) {
						case 9243:
							baseModifier = 1.15D;
							break;
						case 9244:
							baseModifier = 1.45D;
							break;
						case 9245:
							baseModifier = 1.15D;
							break;
						default:
							baseModifier = 1.0D;
							break;
					}
				}
				break;
		}

		HunterInstinct instinct = HunterInstinct.getOrCreate(stoner);
		if (instinct.getTrackingStreak() >= 25) {
			baseModifier *= 2.0;
		} else if (instinct.getTrackingStreak() >= 12) {
			baseModifier *= 1.5;
		}

		return baseModifier;
	}

	public static void updateRangedCombat(Stoner archer, Entity target, boolean hit, int damage) {
		if (archer == null) return;

		HunterInstinct instinct = HunterInstinct.getOrCreate(archer);

		if (hit) {
			instinct.recordHit();
		} else {
			instinct.recordMiss();
		}

		FormulaData.updateCombatEvolution(archer, target, hit, damage);
	}

	public static String getHunterStatus(Stoner archer) {
		if (archer == null) return "Unknown";

		HunterInstinct instinct = HunterInstinct.getOrCreate(archer);

		if (instinct.getTrackingStreak() >= 25) return "Legendary Marksman";
		if (instinct.getTrackingStreak() >= 20) return "Apex Predator";
		if (instinct.getTrackingStreak() >= 15) return "Master Hunter";
		if (instinct.getTrackingStreak() >= 10) return "Expert Archer";
		if (instinct.getTrackingStreak() >= 7) return "Steady Shot";
		if (instinct.getTrackingStreak() >= 5) return "Hunter's Mark";
		if (instinct.getTrackingStreak() >= 3) return "Tracking";
		if (instinct.getConsecutiveMisses() >= 5) return "Lost Focus";
		if (instinct.getConsecutiveMisses() >= 3) return "Unsteady";

		return "Aiming";
	}
}
