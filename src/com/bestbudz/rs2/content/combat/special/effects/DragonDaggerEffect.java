package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class DragonDaggerEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double precision = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.2);
			int baseDamage = attacker.getLastDamageDealt();

			double secondHitMultiplier = 0.75 + (precision * 0.1);
			int secondHit = (int)(baseDamage * secondHitMultiplier);

			if (secondHit > 0) {
				long newHp = Math.max(0, victim.getGrades()[3] - secondHit);
				victim.getGrades()[3] = newHp;

				attacker.getClient().queueOutgoingPacket(new SendMessage("Your dagger strikes twice for " +
					secondHit + " additional damage!"));
			}

			Object streakObj = attacker.getAttributes().get("precision_streak");
			int currentStreak = (streakObj instanceof Integer) ? (Integer)streakObj : 0;

			if (baseDamage > 0) {
				currentStreak++;
				attacker.getAttributes().set("precision_streak", currentStreak);
				attacker.getAttributes().set("precision_streak_end", System.currentTimeMillis() + 12000);

				if (currentStreak >= 5) {
					attacker.getClient().queueOutgoingPacket(new SendMessage("Perfect precision achieved! (+" +
						(currentStreak * 2) + "% accuracy)"));
				}
			} else {
				currentStreak = Math.max(0, currentStreak - 2);
				attacker.getAttributes().set("precision_streak", currentStreak);
			}

			if (precision > 2.0 && currentStreak >= 3) {
				double critChance = (precision - 2.0) * 15 + (currentStreak * 3);
				if (com.bestbudz.core.util.Utility.random(100) < critChance) {
					int critDamage = (int)((baseDamage + secondHit) * 0.5);

					if (critDamage > 0) {
						long newHp = Math.max(0, victim.getGrades()[3] - critDamage);
						victim.getGrades()[3] = newHp;

						attacker.getClient().queueOutgoingPacket(new SendMessage("VITAL STRIKE! Critical precision deals " +
							critDamage + " additional damage!"));
					}
				}
			}

			if (baseDamage > 0) {
				int speedBoost = (int)(precision * 5 + currentStreak);
				attacker.getAttributes().set("dagger_speed", speedBoost);
				attacker.getAttributes().set("dagger_speed_end", System.currentTimeMillis() + 8000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("Swift strikes increase your speed! (+" +
					speedBoost + "% attack speed)"));
			}

			FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + secondHit);
		}
	}
}
