package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Dragon Claws Special - "Rending Strikes"
 * A devastating four-hit combo that scales damage based on accuracy
 */
public class DragonClawsEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double dragonicFury = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.2);
			int baseDamage = attacker.getLastDamageDealt();

			// Rending Combo - damage distribution based on first hit success
			int totalComboDamage = 0;
			String comboType;

			if (baseDamage > 0) {
				// Successful first hit - escalating damage pattern
				int hit2 = (int)(baseDamage * (0.5 + dragonicFury * 0.1)); // 50-70% of first hit
				int hit3 = (int)(baseDamage * (0.25 + dragonicFury * 0.05)); // 25-35% of first hit
				int hit4 = (int)(baseDamage * (0.25 + dragonicFury * 0.05)); // 25-35% of first hit

				totalComboDamage = hit2 + hit3 + hit4;
				comboType = "Perfect Rending Combo";
			} else {
				// Failed first hit - potential for massive follow-up
				double missRecoveryChance = 30 + (dragonicFury * 15); // 30-60% chance
				if (com.bestbudz.core.util.Utility.random(100) < missRecoveryChance) {
					// Redemption strike - one big hit
					totalComboDamage = (int)(25 + dragonicFury * 15); // 25-40 damage
					comboType = "Redemption Strike";
				} else {
					// Complete whiff - minimal damage spread
					totalComboDamage = (int)(5 + dragonicFury * 3); // 5-11 damage
					comboType = "Glancing Strikes";
				}
			}

			// Apply combo damage
			if (totalComboDamage > 0) {
				long newHp = Math.max(0, victim.getGrades()[3] - totalComboDamage);
				victim.getGrades()[3] = newHp;

				attacker.getClient().queueOutgoingPacket(new SendMessage(comboType + "! Your claws deal " +
					totalComboDamage + " additional damage!"));
			}

			// Armor Shredding - reduce defense based on total damage
			int armorShred = (int)((baseDamage + totalComboDamage) * 0.15 * dragonicFury);
			if (armorShred > 0 && victim.getGrades()[1] > 0) {
				victim.getGrades()[1] = Math.max(0, victim.getGrades()[1] - armorShred);
			}

			// Draconic Momentum - build attack speed after successful combo
			if (baseDamage > 0 && totalComboDamage > 15) {
				int momentum = (int)(dragonicFury * 8);
				attacker.getAttributes().set("draconic_momentum", momentum);
				attacker.getAttributes().set("draconic_momentum_end", System.currentTimeMillis() + 10000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("Successful combo builds draconic momentum! (+" +
					momentum + "% attack speed)"));
			}

			FormulaData.updateCombatEvolution(attacker, target, baseDamage > 0 || totalComboDamage > 15,
				baseDamage + totalComboDamage);
		}
	}
}