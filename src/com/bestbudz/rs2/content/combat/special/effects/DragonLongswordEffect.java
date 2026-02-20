package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class DragonLongswordEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double dragonicReach = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.2);
			int baseDamage = attacker.getLastDamageDealt();

			double sweepMultiplier = 0.35 + (dragonicReach * 0.15);
			int sweepDamage = (int)(baseDamage * sweepMultiplier);

			if (sweepDamage > 0) {
				long newHp = Math.max(0, victim.getGrades()[3] - sweepDamage);
				victim.getGrades()[3] = newHp;

				attacker.getClient().queueOutgoingPacket(new SendMessage("Your longsword sweeps with draconic power for " +
					sweepDamage + " additional damage!"));
			}

			int accuracyBonus = (int)(dragonicReach * 12);
			int accuracyAttacks = dragonicReach > 2.0 ? 3 : 2;

			attacker.getAttributes().set("dragon_accuracy", accuracyBonus);
			attacker.getAttributes().set("dragon_accuracy_attacks", accuracyAttacks);
			attacker.getAttributes().set("dragon_accuracy_end", System.currentTimeMillis() + 10000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Dragon precision guides your next " +
				accuracyAttacks + " attacks! (+" + accuracyBonus + "% accuracy)"));

			int reachDefense = (int)(baseDamage * 0.2 * dragonicReach);
			if (reachDefense > 0) {
				attacker.getAttributes().set("reach_defense", reachDefense);
				attacker.getAttributes().set("reach_defense_end", System.currentTimeMillis() + 8000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("Superior reach grants defensive advantage! (+" +
					reachDefense + " Defense)"));
			}

			if (dragonicReach > 2.2) {
				double cleaveChance = (dragonicReach - 2.2) * 20;
				if (com.bestbudz.core.util.Utility.random(100) < cleaveChance) {
					int cleaveDamage = sweepDamage / 2;

					target.getAttributes().set("cleave_damage", cleaveDamage);
					target.getAttributes().set("cleave_radius", 1);

					attacker.getClient().queueOutgoingPacket(new SendMessage("CLEAVING STRIKE! Your sweep affects nearby foes!"));
				}
			}

			Object masteryObj = attacker.getAttributes().get("sword_mastery");
			int currentMastery = (masteryObj instanceof Integer) ? (Integer)masteryObj : 0;

			if (baseDamage > 0) {
				currentMastery++;
				attacker.getAttributes().set("sword_mastery", currentMastery);
				attacker.getAttributes().set("sword_mastery_end", System.currentTimeMillis() + 20000);

				if (currentMastery >= 5) {
					int masteryBonus = currentMastery * 2;
					attacker.getClient().queueOutgoingPacket(new SendMessage("Sword mastery improves! (+" +
						masteryBonus + "% melee damage)"));
				}
			}

			FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + sweepDamage);
		}
	}
}
