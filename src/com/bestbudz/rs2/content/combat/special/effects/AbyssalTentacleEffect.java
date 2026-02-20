package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class AbyssalTentacleEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double chaosIntensity = FormulaData.getCombatEffectiveness(attacker);
			int baseDamage = attacker.getLastDamageDealt();

			double bonusDamageMultiplier = 0.25 + (chaosIntensity * 0.15);
			int bonusDamage = (int)(baseDamage * bonusDamageMultiplier);

			if (bonusDamage > 0) {

				long currentHp = victim.getGrades()[3];
				long newHp = Math.max(0, currentHp - bonusDamage);
				victim.getGrades()[3] = newHp;

				attacker.getClient().queueOutgoingPacket(new SendMessage("Your tentacle lashes with chaotic fury for " + bonusDamage + " additional damage!"));
			}

			double multistrikeChance = 15 + (chaosIntensity * 8);
			if (Utility.random(100) < multistrikeChance) {
				int additionalStrikes = chaosIntensity > 2.5 ? 2 : 1;

				for (int i = 0; i < additionalStrikes; i++) {
					final int strikeNumber = i + 1;
					final int strikeDamage = (int)(baseDamage * (0.3 + (chaosIntensity * 0.1)));
					final int totalStrikes = additionalStrikes;

					TaskQueue.queue(new Task(strikeNumber) {
						@Override
						public void execute() {

							if (victim.isDead()) {
								stop();
								return;
							}

							long currentHp = victim.getGrades()[3];
							long newHp = Math.max(0, currentHp - strikeDamage);
							victim.getGrades()[3] = newHp;

							victim.getUpdateFlags().sendGraphic(new Graphic(181 + strikeNumber));

							attacker.getClient().queueOutgoingPacket(new SendMessage("@red@Chaos strike " + strikeNumber + " hits for " + strikeDamage + " damage!"));

							if (strikeNumber == totalStrikes) {
								attacker.getClient().queueOutgoingPacket(new SendMessage("@gre@Your tentacle's chaos assault is complete!"));
							}

							stop();
						}

						@Override
						public void onStop() {}
					});
				}

				attacker.getClient().queueOutgoingPacket(new SendMessage("@yel@Your tentacle begins a chaotic assault with " + additionalStrikes + " additional strike" +
					(additionalStrikes > 1 ? "s" : "") + "!"));
			}

			victim.getUpdateFlags().sendGraphic(new Graphic(181));

			FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + bonusDamage);
		}
	}
}
