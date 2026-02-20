package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class GraniteMaulEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double brutality = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.25);
			int baseDamage = attacker.getLastDamageDealt();

			double crushMultiplier = 0.6 + (brutality * 0.2);
			int crushDamage = (int)(baseDamage * crushMultiplier);

			if (crushDamage > 0) {
				long newHp = Math.max(0, victim.getGrades()[3] - crushDamage);
				victim.getGrades()[3] = newHp;

				attacker.getClient().queueOutgoingPacket(new SendMessage("Your maul delivers a crushing blow for " +
					crushDamage + " devastating damage!"));
			}

			int armorSmash = (int)((baseDamage + crushDamage) * 0.25 * brutality);
			if (armorSmash > 0 && victim.getGrades()[1] > 0) {
				victim.getGrades()[1] = Math.max(0, victim.getGrades()[1] - armorSmash);

				attacker.getClient().queueOutgoingPacket(new SendMessage("Your maul smashes through the creature's defenses!"));

				victim.getAttributes().set("armor_smash", armorSmash);
				victim.getAttributes().set("armor_smash_end", System.currentTimeMillis() + 15000);
			}

			attacker.getAttributes().set("instant_strike", true);
			attacker.getAttributes().set("instant_strike_end", System.currentTimeMillis() + 6000);
			attacker.getClient().queueOutgoingPacket(new SendMessage("Granite power eliminates your next attack delay!"));

			if (brutality > 2.0) {
				int rageBoost = (int)((baseDamage + crushDamage) * 0.3);
				attacker.getAttributes().set("granite_rage", rageBoost);
				attacker.getAttributes().set("granite_rage_end", System.currentTimeMillis() + 12000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("Granite fury builds within you! (+" +
					rageBoost + " combat power)"));
			}

			if (baseDamage + crushDamage > 30) {
				double stunChance = brutality * 8;
				if (com.bestbudz.core.util.Utility.random(100) < stunChance) {
					victim.getAttributes().set("granite_stun", System.currentTimeMillis() + 3000);
					attacker.getClient().queueOutgoingPacket(new SendMessage("Your crushing blow staggers the creature!"));
				}
			}

			if (brutality > 2.5) {
				double comboChance = (brutality - 2.5) * 20;
				if (com.bestbudz.core.util.Utility.random(100) < comboChance) {
					int comboDamage = (int)(baseDamage * 0.4);

					if (comboDamage > 0) {
						long newHp = Math.max(0, victim.getGrades()[3] - comboDamage);
						victim.getGrades()[3] = newHp;

						attacker.getClient().queueOutgoingPacket(new SendMessage("GRANITE COMBO! Your maul strikes again!"));
					}
				}
			}

			FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + crushDamage);
		}
	}
}
