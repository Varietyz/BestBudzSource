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

/**
 * Abyssal Tentacle Special - "Chaos Lash"
 * A devastating whip attack that deals bonus damage and has a chance for additional strikes
 */
public class AbyssalTentacleEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double chaosIntensity = FormulaData.getCombatEffectiveness(attacker);
			int baseDamage = attacker.getLastDamageDealt();

			// Chaos Lash - additional damage based on combat mastery
			double bonusDamageMultiplier = 0.25 + (chaosIntensity * 0.15); // 25-67% bonus damage
			int bonusDamage = (int)(baseDamage * bonusDamageMultiplier);

			if (bonusDamage > 0) {
				// Apply the bonus damage directly
				long currentHp = victim.getGrades()[3];
				long newHp = Math.max(0, currentHp - bonusDamage);
				victim.getGrades()[3] = newHp;

				attacker.getClient().queueOutgoingPacket(new SendMessage("Your tentacle lashes with chaotic fury for " + bonusDamage + " additional damage!"));
			}

			// Abyssal Multistrike - chance for additional hits
			double multistrikeChance = 15 + (chaosIntensity * 8); // 15-39% chance
			if (Utility.random(100) < multistrikeChance) {
				int additionalStrikes = chaosIntensity > 2.5 ? 2 : 1;

				// Schedule each additional strike with increasing delays
				for (int i = 0; i < additionalStrikes; i++) {
					final int strikeNumber = i + 1;
					final int strikeDamage = (int)(baseDamage * (0.3 + (chaosIntensity * 0.1))); // 30-55% of original
					final int totalStrikes = additionalStrikes;

					// Each strike happens 1 tick apart (1 tick = 600ms)
					TaskQueue.queue(new Task(strikeNumber) {
						@Override
						public void execute() {
							// Check if target is still valid
							if (victim.isDead()) {
								stop();
								return;
							}

							// Apply strike damage
							long currentHp = victim.getGrades()[3];
							long newHp = Math.max(0, currentHp - strikeDamage);
							victim.getGrades()[3] = newHp;

							// Visual feedback for each strike
							victim.getUpdateFlags().sendGraphic(new Graphic(181 + strikeNumber)); // Different graphic per strike

							// Message for individual strike
							attacker.getClient().queueOutgoingPacket(new SendMessage("@red@Chaos strike " + strikeNumber + " hits for " + strikeDamage + " damage!"));

							// Final message after all strikes complete
							if (strikeNumber == totalStrikes) {
								attacker.getClient().queueOutgoingPacket(new SendMessage("@gre@Your tentacle's chaos assault is complete!"));
							}

							stop();
						}

						@Override
						public void onStop() {}
					});
				}

				// Initial message about the multistrike
				attacker.getClient().queueOutgoingPacket(new SendMessage("@yel@Your tentacle begins a chaotic assault with " + additionalStrikes + " additional strike" +
					(additionalStrikes > 1 ? "s" : "") + "!"));
			}

			// Initial chaos resonance graphic
			victim.getUpdateFlags().sendGraphic(new Graphic(181));

			// Build combat mastery from successful chaos lash
			FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + bonusDamage);
		}
	}
}