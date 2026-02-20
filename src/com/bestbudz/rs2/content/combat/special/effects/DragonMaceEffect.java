package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class DragonMaceEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double sacredPower = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.2);
			int baseDamage = attacker.getLastDamageDealt();

			long targetPrayer = victim.getGrades()[5];
			double smashMultiplier = 0.4 + (targetPrayer / 1000.0) + (sacredPower * 0.1);
			int smashDamage = (int)(baseDamage * smashMultiplier);

			if (smashDamage > 0) {
				long newHp = Math.max(0, victim.getGrades()[3] - smashDamage);
				victim.getGrades()[3] = newHp;

				attacker.getClient().queueOutgoingPacket(new SendMessage("Your mace smashes divine protection for " +
					smashDamage + " damage!"));
			}

			int prayerDrain = (int)((baseDamage + smashDamage) * 0.4 * sacredPower);
			if (prayerDrain > 0 && victim.getGrades()[5] > 0) {
				victim.getGrades()[5] = Math.max(0, victim.getGrades()[5] - prayerDrain);
			}

			if (prayerDrain > 0) {
				int absorbed = prayerDrain / 2;
				if (attacker.getGrades()[5] < attacker.getMaxGrades()[5]) {
					attacker.getGrades()[5] = Math.min(attacker.getMaxGrades()[5],
						attacker.getGrades()[5] + absorbed);
					attacker.getProfession().update(5);

					attacker.getClient().queueOutgoingPacket(new SendMessage("You absorb " + absorbed +
						" necromantic energy!"));
				}
			}

			if (sacredPower > 2.0) {
				double judgmentChance = (sacredPower - 2.0) * 15;
				if (com.bestbudz.core.util.Utility.random(100) < judgmentChance) {
					int judgmentDamage = baseDamage;

					long newHp = Math.max(0, victim.getGrades()[3] - judgmentDamage);
					victim.getGrades()[3] = newHp;

					attacker.getClient().queueOutgoingPacket(new SendMessage("DIVINE JUDGMENT! Sacred power condemns your foe!"));
				}
			}

			FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + smashDamage);
		}
	}
}
