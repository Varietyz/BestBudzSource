package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Toxic Blowpipe Special - "Venom Barrage"
 * A rapid burst of toxic darts that deals escalating damage and applies powerful poison
 */
public class ToxicBlowpipeEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		int baseDamage = attacker.getLastDamageDealt();
		if (baseDamage == 0) return;

		double venomMastery = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.25);

		// Venom Barrage - multiple rapid-fire shots
		int barrageShots = venomMastery > 2.5 ? 4 : venomMastery > 2.0 ? 3 : 2;
		int totalBarrageDamage = 0;

		for (int shot = 0; shot < barrageShots; shot++) {
			// Each shot does 30-50% of base damage, modified by mastery
			double shotMultiplier = 0.3 + (shot * 0.05) + (venomMastery * 0.05);
			int shotDamage = (int)(baseDamage * shotMultiplier);

			if (target.isNpc() && shotDamage > 0) {
				Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
				if (victim != null) {
					long newHp = Math.max(0, victim.getGrades()[3] - shotDamage);
					victim.getGrades()[3] = newHp;
					totalBarrageDamage += shotDamage;
				}
			}
		}

		attacker.getClient().queueOutgoingPacket(new SendMessage("Your blowpipe unleashes a venom barrage of " + barrageShots +
			" shots for " + totalBarrageDamage + " total damage!"));

		// Toxic Regeneration - heal based on damage dealt
		int totalDamage = baseDamage + totalBarrageDamage;
		long healingAmount = (long)(totalDamage * (0.4 + venomMastery * 0.15)); // 40-91% healing

		if (healingAmount > 9 && attacker.getGrades()[3] < attacker.getMaxGrades()[3]) {
			long actualHealing = Math.min(healingAmount,
				attacker.getMaxGrades()[3] - attacker.getGrades()[3]);
			attacker.getGrades()[3] += actualHealing;
			attacker.getProfession().update(3);

			attacker.getClient().queueOutgoingPacket(
				new SendMessage("Toxic vitality regenerates " + actualHealing + " Life!"));
		}

		// Concentrated Venom - apply stacking poison
		if (target.isNpc() && venomMastery > 1.5) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim != null) {
				int venomDamage = (int)(4 + venomMastery * 2); // 4-9 venom damage
				int venomDuration = (int)(8 + venomMastery); // 8-11 ticks

				// Store enhanced venom effect
				victim.getAttributes().set("concentrated_venom", venomDamage);
				victim.getAttributes().set("concentrated_venom_ticks", venomDuration);
			}
		}

		// Rapid Fire Mode - increased attack speed
		if (venomMastery > 2.0) {
			int rapidFireBonus = (int)((venomMastery - 2.0) * 20); // Up to 16% attack speed
			attacker.getAttributes().set("rapid_fire_mode", rapidFireBonus);
			attacker.getAttributes().set("rapid_fire_end", System.currentTimeMillis() + 15000); // 15 seconds

			attacker.getClient().queueOutgoingPacket(new SendMessage("Rapid fire mode activated! (+" + rapidFireBonus + "% attack speed)"));
		}

		// Toxic Cloud - area damage over time
		if (totalDamage > 30 && venomMastery > 2.2) {
			int cloudDamage = totalDamage / 4;
			int cloudDuration = (int)(5 + venomMastery); // 5-8 ticks

			// Store cloud effect at target location
			target.getAttributes().set("toxic_cloud_damage", cloudDamage);
			target.getAttributes().set("toxic_cloud_ticks", cloudDuration);

			attacker.getClient().queueOutgoingPacket(new SendMessage("A toxic cloud forms around your target!"));
		}

		// Venom Adaptation - temporary poison immunity for attacker
		if (venomMastery > 2.5) {
			attacker.getAttributes().set("venom_immunity", System.currentTimeMillis() + 25000); // 25 seconds
			attacker.getClient().queueOutgoingPacket(new SendMessage("Your body adapts to the toxins, granting immunity!"));
		}

		// Master Toxicologist - chance for instant kill on low health targets
		if (venomMastery > 3.0 && target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim != null) {
				double healthPercent = (double)victim.getGrades()[3] / victim.getMaxGrades()[3];

				if (healthPercent < 0.15) { // Below 15% health
					double executeChance = (venomMastery - 3.0) * 20; // Up to 4% chance
					if (com.bestbudz.core.util.Utility.random(100) < executeChance) {
						victim.getGrades()[3] = 0; // Instant kill

						attacker.getClient().queueOutgoingPacket(new SendMessage("VENOM EXECUTION! Your toxins finishedBloodTrial off the creature!"));
					}
				}
			}
		}

		FormulaData.updateCombatEvolution(attacker, target, true, totalDamage);
	}
}