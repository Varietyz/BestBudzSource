package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class DragonScimitarEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (!target.isNpc() || attacker.getLastDamageDealt() <= 0) return;

		Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
		if (victim == null) return;

		double dragonicMastery = FormulaData.getCombatEffectiveness(attacker);
		int baseDamage = attacker.getLastDamageDealt();

		int flurryStrikes = dragonicMastery > 2.5 ? 3 : dragonicMastery > 1.8 ? 2 : 1;
		int totalFlurryDamage = 0;

		for (int strike = 0; strike < flurryStrikes; strike++) {

			double strikeMultiplier = 0.4 + (strike * 0.2) + (dragonicMastery * 0.05);
			int strikeDamage = (int)(baseDamage * strikeMultiplier);

			if (strikeDamage > 0) {
				long newHp = Math.max(0, victim.getGrades()[3] - strikeDamage);
				victim.getGrades()[3] = newHp;
				totalFlurryDamage += strikeDamage;
			}
		}

		attacker.getClient().queueOutgoingPacket(new SendMessage("Your scimitar unleashes a draconic flurry, striking " + flurryStrikes +
			" times for " + totalFlurryDamage + " total damage!"));

		int furyBonus = totalFlurryDamage / 4;
		attacker.getAttributes().set("dragon_fury", furyBonus);
		attacker.getAttributes().set("dragon_fury_end", System.currentTimeMillis() + 8000);

		attacker.getClient().queueOutgoingPacket(new SendMessage("Dragon's fury builds within you! (+" + furyBonus + " next attack damage)"));

		if (dragonicMastery > 2.0) {
			int precisionAttacks = (int)(dragonicMastery - 1.0);
			attacker.getAttributes().set("dragon_precision", precisionAttacks);
			attacker.getAttributes().set("dragon_precision_end", System.currentTimeMillis() + 15000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Dragon precision guides your next " + precisionAttacks + " attack" +
				(precisionAttacks > 1 ? "s" : "") + "!"));
		}

		if (flurryStrikes >= 3) {
			int intimidation = totalFlurryDamage / 5;
			victim.getAttributes().set("draconic_intimidation", intimidation);
			victim.getAttributes().set("draconic_intimidation_end", System.currentTimeMillis() + 10000);
		}

		int defenseReduction = (int)(baseDamage * 0.2 * dragonicMastery);
		if (defenseReduction > 0 && victim.getGrades()[1] > 0) {
			long currentDef = victim.getGrades()[1];
			victim.getGrades()[1] = Math.max(0, currentDef - defenseReduction);

			victim.getAttributes().set("scale_break_reduction", defenseReduction);
			victim.getAttributes().set("scale_break_end", System.currentTimeMillis() + 12000);
		}

		FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + totalFlurryDamage);
	}
}
