package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class ArmadylGodswordEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double divinePower = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.3);
			int baseDamage = attacker.getLastDamageDealt();

			double stormMultiplier = 0.7 + (divinePower * 0.25);
			int stormDamage = (int)(baseDamage * stormMultiplier);

			if (stormDamage > 0) {
				long newHp = Math.max(0, victim.getGrades()[3] - stormDamage);
				victim.getGrades()[3] = newHp;

				attacker.getClient().queueOutgoingPacket(new SendMessage("Divine storm strikes with incredible force for " +
					stormDamage + " massive damage!"));
			}

			int blessingPower = (int)((baseDamage + stormDamage) * 0.25);
			if (blessingPower > 0) {
				attacker.getAttributes().set("armadyl_blessing_attack", blessingPower);
				attacker.getAttributes().set("armadyl_blessing_strength", blessingPower);
				attacker.getAttributes().set("armadyl_blessing_accuracy", blessingPower * 2);
				attacker.getAttributes().set("armadyl_blessing_end", System.currentTimeMillis() + 25000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("Armadyl's divine blessing empowers you! (+" +
					blessingPower + " Attack/Strength, +" + (blessingPower * 2) + " Accuracy)"));
			}

			if (divinePower > 2.0) {
				int perfectAttacks = (int)(divinePower - 1.0);
				attacker.getAttributes().set("perfect_precision", perfectAttacks);
				attacker.getAttributes().set("perfect_precision_end", System.currentTimeMillis() + 15000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("Divine precision grants " + perfectAttacks +
					" perfect strike" + (perfectAttacks > 1 ? "s" : "") + "!"));
			}

			if (divinePower > 2.5) {
				double healthPercent = (double)victim.getGrades()[3] / victim.getMaxGrades()[3];
				if (healthPercent < 0.25) {
					double executeChance = (divinePower - 2.5) * 15;
					if (com.bestbudz.core.util.Utility.random(100) < executeChance) {
						victim.getGrades()[3] = 0;

						attacker.getClient().queueOutgoingPacket(new SendMessage("DIVINE JUDGMENT! Armadyl's justice is absolute!"));
						return;
					}
				}
			}

			Object masteryObj = attacker.getAttributes().get("storm_mastery");
			int currentMastery = (masteryObj instanceof Integer) ? (Integer)masteryObj : 0;

			currentMastery++;
			attacker.getAttributes().set("storm_mastery", currentMastery);
			attacker.getAttributes().set("storm_mastery_end", System.currentTimeMillis() + 30000);

			if (currentMastery >= 3) {
				int masteryBonus = currentMastery * 5;
				attacker.getClient().queueOutgoingPacket(new SendMessage("Storm mastery builds divine power! (+" +
					masteryBonus + "% damage)"));
			}

			if (baseDamage + stormDamage > 50) {
				int wrathDamage = (int)((baseDamage + stormDamage) * 0.4);

				target.getAttributes().set("divine_wrath_damage", wrathDamage);
				target.getAttributes().set("divine_wrath_radius", 3);
				target.getAttributes().set("divine_wrath_end", System.currentTimeMillis() + 6000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("DIVINE WRATH! Lightning spreads across the battlefield!"));
			}

			FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + stormDamage);
		}
	}
}
