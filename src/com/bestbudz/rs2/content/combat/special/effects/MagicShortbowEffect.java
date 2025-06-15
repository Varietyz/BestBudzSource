package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Magic Shortbow Special - "Rapid Fire"
 * A quick double-shot that builds speed and accuracy with consecutive uses
 */
public class MagicShortbowEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double arcaneSpeed = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.2);
			int baseDamage = attacker.getLastDamageDealt();

			// Rapid Fire - guaranteed second shot with scaling damage
			double secondShotMultiplier = 0.7 + (arcaneSpeed * 0.1); // 70-90% of first shot
			int secondShot = Math.max(5, (int)(baseDamage * secondShotMultiplier)); // Minimum 5 damage

			long newHp = Math.max(0, victim.getGrades()[3] - secondShot);
			victim.getGrades()[3] = newHp;

			attacker.getClient().queueOutgoingPacket(new SendMessage("Your bow fires rapidly for " +
				secondShot + " additional damage!"));

			// Speed Stacking - build attack speed with consecutive rapid fires
			Object speedObj = attacker.getAttributes().get("rapid_fire_stack");
			int currentStack = (speedObj instanceof Integer) ? (Integer)speedObj : 0;

			currentStack++;
			attacker.getAttributes().set("rapid_fire_stack", currentStack);
			attacker.getAttributes().set("rapid_fire_stack_end", System.currentTimeMillis() + 15000);

			int speedBonus = Math.min(25, currentStack * 3); // Max 25% speed boost
			attacker.getAttributes().set("rapid_fire_speed", speedBonus);
			attacker.getClient().queueOutgoingPacket(new SendMessage("Rapid fire builds speed! (+" +
				speedBonus + "% attack speed, Stack: " + currentStack + ")"));

			// Archer's Focus - accuracy improves with speed
			if (currentStack >= 3) {
				int focusBonus = currentStack * 4;
				attacker.getAttributes().set("archer_focus", focusBonus);
				attacker.getAttributes().set("archer_focus_end", System.currentTimeMillis() + 12000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("Archer's focus sharpens! (+" +
					focusBonus + "% accuracy)"));
			}

			// Magic Enhancement - bow gains magical properties
			if (arcaneSpeed > 1.8) {
				int magicEffect = com.bestbudz.core.util.Utility.random(3);
				int effectPower = (int)(arcaneSpeed * 5);

				switch (magicEffect) {
					case 0: // Fire Arrows
						int fireDamage = (int)((baseDamage + secondShot) * 0.2);
						if (fireDamage > 0) {
							long newHp2 = Math.max(0, victim.getGrades()[3] - fireDamage);
							victim.getGrades()[3] = newHp2;

							attacker.getClient().queueOutgoingPacket(new SendMessage("Magic fire enchants your arrows! (+" +
								fireDamage + " fire damage)"));
						}
						break;

					case 1: // Ice Arrows
						victim.getAttributes().set("ice_slow", effectPower);
						victim.getAttributes().set("ice_slow_end", System.currentTimeMillis() + 8000);

						attacker.getClient().queueOutgoingPacket(new SendMessage("Icy magic slows your target!"));
						break;

					case 2: // Lightning Arrows
						int shockChance = 30 + (int)(arcaneSpeed * 10);
						if (com.bestbudz.core.util.Utility.random(100) < shockChance) {
							int shockDamage = effectPower;
							long newHp3 = Math.max(0, victim.getGrades()[3] - shockDamage);
							victim.getGrades()[3] = newHp3;

							attacker.getClient().queueOutgoingPacket(new SendMessage("Lightning strikes your target!"));
						}
						break;
				}
			}

			// Perfect Rhythm - chance for triple shot
			if (currentStack >= 5 && arcaneSpeed > 2.2) {
				double tripleChance = (currentStack - 5) * 3 + (arcaneSpeed - 2.2) * 10; // Up to 11% chance
				if (com.bestbudz.core.util.Utility.random(100) < tripleChance) {
					int thirdShot = (int)(baseDamage * 0.5);

					if (thirdShot > 0) {
						long newHp4 = Math.max(0, victim.getGrades()[3] - thirdShot);
						victim.getGrades()[3] = newHp4;

						attacker.getClient().queueOutgoingPacket(new SendMessage("PERFECT RHYTHM! A third arrow strikes!"));
					}
				}
			}

			// Bow Mastery - unlock special techniques at high stacks
			if (currentStack >= 7) {
				attacker.getAttributes().set("bow_mastery", true);
				attacker.getAttributes().set("bow_mastery_end", System.currentTimeMillis() + 20000);

				// Master archer abilities
				attacker.getAttributes().set("arrow_conservation", 50); // 50% chance to not consume arrows
				attacker.getClient().queueOutgoingPacket(new SendMessage("BOW MASTERY! You achieve perfect archery form!"));
			}

			// Wind Arrows - chance for knockback effect
			if (arcaneSpeed > 2.5) {
				double windChance = (arcaneSpeed - 2.5) * 20; // Up to 8% chance
				if (com.bestbudz.core.util.Utility.random(100) < windChance) {
					// Conceptual knockback
					victim.getAttributes().set("wind_knockback", System.currentTimeMillis() + 3000);

					attacker.getClient().queueOutgoingPacket(new SendMessage("Wind arrows create a gust!"));
				}
			}

			FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + secondShot);
		}
	}
}