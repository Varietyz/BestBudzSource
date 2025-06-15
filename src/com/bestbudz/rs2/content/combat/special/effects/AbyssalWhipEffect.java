package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Abyssal Whip Special - "Soul Rend"
 * A precise strike that deals increasing damage based on target's missing health
 */
public class AbyssalWhipEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double soulMastery = FormulaData.getCombatEffectiveness(attacker);
			int baseDamage = attacker.getLastDamageDealt();

			// Soul Rend - bonus damage based on target's missing health
			long currentHp = victim.getGrades()[3];
			long maxHp = victim.getMaxGrades()[3];
			double missingHealthPercent = 1.0 - ((double)currentHp / maxHp);

			// 20-50% bonus damage based on missing health and soul mastery
			double soulRendMultiplier = (missingHealthPercent * (0.2 + soulMastery * 0.1)) +
				(soulMastery * 0.08); // Base 8-28% from mastery
			int soulRendDamage = (int)(baseDamage * soulRendMultiplier);

			if (soulRendDamage > 0) {
				// Apply soul rend damage
				long newHp = Math.max(0, currentHp - soulRendDamage);
				victim.getGrades()[3] = newHp;

				attacker.getClient().queueOutgoingPacket(new SendMessage("Your whip rends the creature's soul for " +
					soulRendDamage + " additional damage!"));
			}

			// Vampiric Recovery - heal based on damage dealt
			if (soulMastery > 1.5) {
				int healingAmount = (int)((baseDamage + soulRendDamage) * (0.15 + soulMastery * 0.05));

				if (attacker.getGrades()[3] < attacker.getMaxGrades()[3]) {
					long newAttackerHp = Math.min(attacker.getMaxGrades()[3],
						attacker.getGrades()[3] + healingAmount);
					attacker.getGrades()[3] = newAttackerHp;
					attacker.getProfession().update(3);

					attacker.getClient().queueOutgoingPacket(
						new SendMessage("Soul energy restores " + healingAmount + " health!"));
				}
			}

			// Critical Soul Strike - chance for massive damage on low health targets
			if (missingHealthPercent > 0.7 && soulMastery > 2.0) { // Target below 30% health
				double criticalChance = (soulMastery - 2.0) * 25; // Up to 12.5% chance
				if (com.bestbudz.core.util.Utility.random(100) < criticalChance) {
					int criticalDamage = baseDamage; // 100% additional damage

					long newHp = Math.max(0, victim.getGrades()[3] - criticalDamage);
					victim.getGrades()[3] = newHp;

					attacker.getClient().queueOutgoingPacket(new SendMessage("CRITICAL SOUL STRIKE! Your whip delivers a devastating blow!"));
				}
			}

			FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + soulRendDamage);
		}
	}
}