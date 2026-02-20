package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class ZamorakianHastaEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		double chaosIntensity = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.3);
		int baseDamage = attacker.getLastDamageDealt();

		double chaosVariance = 0.5 + (Math.random() * 1.0 * chaosIntensity);
		int chaosDamage = (int)(baseDamage * chaosVariance);

		if (target.isNpc() && chaosDamage > 0) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim != null) {
				long newHp = Math.max(0, victim.getGrades()[3] - chaosDamage);
				victim.getGrades()[3] = newHp;

				if (chaosVariance > 1.2) {
					attacker.getClient().queueOutgoingPacket(new SendMessage("CHAOS SURGE! Your hasta channels pure chaos for " +
						chaosDamage + " devastating damage!"));
				} else if (chaosVariance < 0.8) {
					attacker.getClient().queueOutgoingPacket(new SendMessage("Chaos diminishes your strike to " + chaosDamage + " damage."));
				} else {
					attacker.getClient().queueOutgoingPacket(new SendMessage("Your hasta strikes with chaotic force for " + chaosDamage + " damage!"));
				}
			}
		}

		performChaoticDisplacement(attacker, target, (int)chaosIntensity);

		int chaosEffect = com.bestbudz.core.util.Utility.random(100);

		if (chaosEffect < 15 + (chaosIntensity * 5)) {
			applyRandomChaosEffect(attacker, target, chaosIntensity);
		}

		if (chaosIntensity > 2.0) {
			double favorChance = (chaosIntensity - 2.0) * 15;
			if (com.bestbudz.core.util.Utility.random(100) < favorChance) {
				int favorDamage = baseDamage + chaosDamage;

				attacker.getAttributes().set("zamorak_favor", favorDamage);
				attacker.getAttributes().set("zamorak_favor_end", System.currentTimeMillis() + 12000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("ZAMORAK'S FAVOR! Your next attack will deal chaos-enhanced damage!"));
			}
		}

		if (Math.abs(chaosVariance - 1.0) > 0.4) {
			int masteryBonus = (int)(Math.abs(chaosVariance - 1.0) * 50);
			attacker.getAttributes().set("chaos_mastery", masteryBonus);
			attacker.getAttributes().set("chaos_mastery_end", System.currentTimeMillis() + 18000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Chaos mastery increases your unpredictability! (+" + masteryBonus + " chaos power)"));
		}

		attacker.getClient().queueOutgoingPacket(new SendMessage("Your hasta channels the chaos of Zamorak!"));
		FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + chaosDamage);
	}

	private void performChaoticDisplacement(Stoner attacker, Entity target, int power) {

		boolean displaceSelf = com.bestbudz.core.util.Utility.random(100) < 30;
		Entity displaced = displaceSelf ? attacker : target;

		int[] directions = {-power, -1, 0, 1, power};
		int deltaX = directions[com.bestbudz.core.util.Utility.random(directions.length)];
		int deltaY = directions[com.bestbudz.core.util.Utility.random(directions.length)];

		if (attemptChaosDisplacement(displaced, deltaX, deltaY)) {
			if (displaceSelf) {
				attacker.getClient().queueOutgoingPacket(new SendMessage("Chaos warps you through space!"));
			} else {
				attacker.getClient().queueOutgoingPacket(new SendMessage("Chaotic energy displaces the creature!"));
			}
		}
	}

	private void applyRandomChaosEffect(Stoner attacker, Entity target, double chaosIntensity) {
		int effect = com.bestbudz.core.util.Utility.random(6);
		int effectPower = (int)(10 + chaosIntensity * 5);

		switch (effect) {
			case 0:
				attacker.getAttributes().set("chaos_strength", effectPower);
				attacker.getAttributes().set("chaos_strength_end", System.currentTimeMillis() + 15000);
				attacker.getClient().queueOutgoingPacket(new SendMessage("Chaotic energy surges through your muscles! (+" + effectPower + " Strength)"));
				break;

			case 1:
				attacker.getAttributes().set("chaos_speed", effectPower);
				attacker.getAttributes().set("chaos_speed_end", System.currentTimeMillis() + 12000);
				attacker.getClient().queueOutgoingPacket(new SendMessage("Chaos accelerates your movements! (+" + effectPower + "% speed)"));
				break;

			case 2:
				if (target.isNpc()) {
					Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
					if (victim != null) {

						int[] stats = {0, 1, 2, 4, 5, 6};
						int statToDrain = stats[com.bestbudz.core.util.Utility.random(stats.length)];

						if (victim.getGrades()[statToDrain] > effectPower) {
							victim.getGrades()[statToDrain] -= effectPower;
						}
					}
				}
				break;

			case 3:
				attacker.getAttributes().set("chaos_reflection", effectPower);
				attacker.getAttributes().set("chaos_reflection_end", System.currentTimeMillis() + 10000);
				attacker.getClient().queueOutgoingPacket(new SendMessage("Chaos creates a reflective barrier! (" + effectPower + "% damage reflection)"));
				break;

			case 4:
				if (attacker.getGrades()[3] < attacker.getMaxGrades()[3]) {
					long healing = Math.min(effectPower * 2, attacker.getMaxGrades()[3] - attacker.getGrades()[3]);
					attacker.getGrades()[3] += healing;
					attacker.getProfession().update(3);
					attacker.getClient().queueOutgoingPacket(new SendMessage("Chaotic energy regenerates " + healing + " health!"));
				}
				break;

			case 5:
				if (target.isNpc()) {
					target.getAttributes().set("chaos_confusion", effectPower);
					target.getAttributes().set("chaos_confusion_end", System.currentTimeMillis() + 8000);
				}
				break;
		}
	}

	private boolean attemptChaosDisplacement(Entity entity, int deltaX, int deltaY) {
		try {
			if (!Region.getRegion(entity.getLocation().getX(), entity.getLocation().getY())
				.blockedWest(entity.getLocation().getX(), entity.getLocation().getY(),
					entity.getLocation().getZ())) {
				entity.getMovementHandler().walkTo(deltaX, deltaY);
				return true;
			}
		} catch (Exception e) {

			entity.getMovementHandler().walkTo(deltaX > 0 ? 1 : deltaX < 0 ? -1 : 0,
				deltaY > 0 ? 1 : deltaY < 0 ? -1 : 0);
		}
		return false;
	}
}
