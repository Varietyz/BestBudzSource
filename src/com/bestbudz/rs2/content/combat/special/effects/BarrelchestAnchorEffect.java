package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Barrelchest Anchor Special - "Tidal Smash"
 * A crushing blow that deals bonus damage and creates waves of combat reduction
 */
public class BarrelchestAnchorEffect implements CombatEffect {
	private static final int[] COMBAT_STATS = {0, 1, 2, 4}; // Attack, Defense, Strength, Range

	@Override
	public void execute(Stoner attacker, Entity target) {
		int baseDamage = attacker.getLastDamageDealt();
		double tidalForce = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.2);

		if (baseDamage == 0) return;

		Stoner victim = null;
		if (!target.isNpc()) {
			victim = World.getStoners()[target.getIndex()];
		}

		// Tidal Crush - bonus damage based on anchor's weight
		double crushMultiplier = 0.3 + (tidalForce * 0.15); // 30-60% bonus damage
		int crushDamage = (int)(baseDamage * crushMultiplier);

		if (crushDamage > 0 && victim != null) {
			long newHp = Math.max(0, victim.getGrades()[3] - crushDamage);
			victim.getGrades()[3] = newHp;
			victim.getProfession().update(3);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Your anchor crashes down with tidal force for " + crushDamage + " additional damage!"));
			victim.getClient().queueOutgoingPacket(new SendMessage("The massive anchor crushes you for " + crushDamage + " damage!"));
		}

		// Combat Wave - reduce multiple combat stats
		int waveReduction = (int)(baseDamage * tidalForce * 0.08); // 8-22% of damage as reduction

		if (waveReduction > 0) {
			for (int statId : COMBAT_STATS) {
				int statReduction = Math.min(waveReduction, (int)target.getGrades()[statId]);
				if (statReduction > 0) {
					target.getGrades()[statId] -= statReduction;

					if (victim != null) {
						victim.getProfession().update(statId);
					}
				}
			}

			attacker.getClient().queueOutgoingPacket(
				new SendMessage("Tidal waves drain your opponent's combat prowess!"));

			if (victim != null) {
				victim.getClient().queueOutgoingPacket(
					new SendMessage("Crushing tidal force weakens your combat abilities!"));
			}
		}

		// Anchor's Weight - chance for devastating critical hit
		if (tidalForce > 2.0) {
			double criticalChance = (tidalForce - 2.0) * 20; // Up to 8% chance
			if (com.bestbudz.core.util.Utility.random(100) < criticalChance) {
				int criticalDamage = (int)(baseDamage * 0.8); // 80% additional damage

				if (victim != null) {
					long newHp = Math.max(0, victim.getGrades()[3] - criticalDamage);
					victim.getGrades()[3] = newHp;
					victim.getProfession().update(3);

					attacker.getClient().queueOutgoingPacket(new SendMessage("ANCHOR SLAM! Your weapon delivers a crushing critical blow!"));
					victim.getClient().queueOutgoingPacket(new SendMessage("The anchor's full weight crashes down on you!"));
				}
			}
		}

		// Seafarer's Strength - temporary damage boost for attacker
		if (baseDamage > 20 && tidalForce > 1.5) {
			int strengthBoost = waveReduction / 2;

			// Store temporary strength boost
			attacker.getAttributes().set("seafarer_strength", strengthBoost);
			attacker.getAttributes().set("seafarer_strength_end", System.currentTimeMillis() + 12000); // 12 seconds

			attacker.getClient().queueOutgoingPacket(
				new SendMessage("The sea's power flows through you! (+" + strengthBoost + " Strength)"));
		}

		// Area Splash - affect nearby combatants (conceptual for 3x3 area)
		if (tidalForce > 2.3) {
			int splashDamage = crushDamage / 3;
			attacker.getClient().queueOutgoingPacket(new SendMessage("Tidal force splashes across the battlefield!"));

			// In a full implementation, this would damage/debuff nearby enemies
			// For now, just indicate the area effect occurred
		}

		FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + crushDamage);
	}
}