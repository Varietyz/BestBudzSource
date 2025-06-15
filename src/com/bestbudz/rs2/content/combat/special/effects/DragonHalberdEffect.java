package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Dragon Halberd Special - "Draconic Sweep"
 * A wide sweeping attack that hits multiple targets and builds draconic power
 */
public class DragonHalberdEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double dragonicReach = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.25);
			int baseDamage = attacker.getLastDamageDealt();

			// Sweeping Strike - wide area attack with bonus damage
			double sweepMultiplier = 0.4 + (dragonicReach * 0.2); // 40-90% bonus damage
			int sweepDamage = (int)(baseDamage * sweepMultiplier);

			if (sweepDamage > 0) {
				long newHp = Math.max(0, victim.getGrades()[3] - sweepDamage);
				victim.getGrades()[3] = newHp;

				attacker.getClient().queueOutgoingPacket(new SendMessage("Your halberd sweeps with draconic power for " +
					sweepDamage + " additional damage!"));
			}

			// Wide Sweep - area damage effect (conceptual 3x3 area)
			int areaDamage = (int)((baseDamage + sweepDamage) * (0.3 + dragonicReach * 0.1));
			if (areaDamage > 0) {
				// Store area effect for potential nearby targets
				target.getAttributes().set("halberd_sweep_damage", areaDamage);
				target.getAttributes().set("halberd_sweep_radius", 2); // 2 square radius
				target.getAttributes().set("halberd_sweep_end", System.currentTimeMillis() + 3000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("Your halberd sweeps in a wide arc! (Area damage: " +
					areaDamage + ")"));
			}

			// Reach Advantage - enhanced accuracy and defense from distance
			int reachBonus = (int)(dragonicReach * 10);
			attacker.getAttributes().set("reach_advantage_accuracy", reachBonus);
			attacker.getAttributes().set("reach_advantage_defense", reachBonus / 2);
			attacker.getAttributes().set("reach_advantage_end", System.currentTimeMillis() + 12000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Superior reach grants combat advantage! (+" +
				reachBonus + " Accuracy, +" + (reachBonus / 2) + " Defense)"));

			// Dragon's Wrath - build power with successful sweeps
			Object wrathObj = attacker.getAttributes().get("dragon_wrath");
			int currentWrath = (wrathObj instanceof Integer) ? (Integer)wrathObj : 0;

			currentWrath++;
			attacker.getAttributes().set("dragon_wrath", currentWrath);
			attacker.getAttributes().set("dragon_wrath_end", System.currentTimeMillis() + 18000);

			if (currentWrath >= 3) {
				int wrathBonus = currentWrath * 4;
				attacker.getClient().queueOutgoingPacket(new SendMessage("Dragon's wrath builds! (+" +
					wrathBonus + "% damage to all attacks)"));
			}

			// Halberd Mastery - unlock special techniques
			if (dragonicReach > 2.0) {
				double techniqueChance = (dragonicReach - 2.0) * 20; // Up to 8% chance
				int technique = com.bestbudz.core.util.Utility.random(3);

				if (com.bestbudz.core.util.Utility.random(100) < techniqueChance) {
					switch (technique) {
						case 0: // Piercing Thrust
							int thrustDamage = (int)(baseDamage * 0.6);
							long newHp = Math.max(0, victim.getGrades()[3] - thrustDamage);
							victim.getGrades()[3] = newHp;

							attacker.getClient().queueOutgoingPacket(new SendMessage("PIERCING THRUST! Your halberd follows up!"));
							break;

						case 1: // Defensive Stance
							int defenseBoost = (int)(dragonicReach * 15);
							attacker.getAttributes().set("halberd_defense", defenseBoost);
							attacker.getAttributes().set("halberd_defense_end", System.currentTimeMillis() + 10000);

							attacker.getClient().queueOutgoingPacket(new SendMessage("DEFENSIVE STANCE! Your halberd guards you! (+" +
								defenseBoost + " Defense)"));
							break;

						case 2: // Trip Attack
							victim.getAttributes().set("tripped", System.currentTimeMillis() + 4000);

							attacker.getClient().queueOutgoingPacket(new SendMessage("TRIP ATTACK! Your halberd sweeps the creature's legs!"));
							break;
					}
				}
			}

			// Draconic Presence - intimidate target
			if (baseDamage + sweepDamage > 30) {
				int intimidation = (int)((baseDamage + sweepDamage) * 0.2);

				// Store intimidation effect
				target.getAttributes().set("draconic_intimidation", intimidation);
				target.getAttributes().set("draconic_intimidation_end", System.currentTimeMillis() + 8000);
			}

			// Perfect Sweep - chance for massive area damage
			if (dragonicReach > 2.8) {
				double perfectChance = (dragonicReach - 2.8) * 25; // Up to 5% chance
				if (com.bestbudz.core.util.Utility.random(100) < perfectChance) {
					int perfectDamage = baseDamage + sweepDamage;

					// Enhanced area effect
					target.getAttributes().set("perfect_sweep_damage", perfectDamage);
					target.getAttributes().set("perfect_sweep_radius", 3); // 3 square radius

					attacker.getClient().queueOutgoingPacket(new SendMessage("PERFECT SWEEP! Your halberd creates a devastating arc!"));
				}
			}

			FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + sweepDamage);
		}
	}
}