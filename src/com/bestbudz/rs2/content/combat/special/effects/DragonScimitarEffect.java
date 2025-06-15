package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Dragon Scimitar Special - "Draconic Flurry"
 * A swift series of strikes that deals escalating damage with each hit
 */
public class DragonScimitarEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (!target.isNpc() || attacker.getLastDamageDealt() <= 0) return;

		Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
		if (victim == null) return;

		double dragonicMastery = FormulaData.getCombatEffectiveness(attacker);
		int baseDamage = attacker.getLastDamageDealt();

		// Draconic Flurry - multiple rapid strikes
		int flurryStrikes = dragonicMastery > 2.5 ? 3 : dragonicMastery > 1.8 ? 2 : 1;
		int totalFlurryDamage = 0;

		for (int strike = 0; strike < flurryStrikes; strike++) {
			// Each strike does increasing damage (40%, 60%, 80% of base)
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

		// Dragon's Fury - build momentum for next attack
		int furyBonus = totalFlurryDamage / 4;
		attacker.getAttributes().set("dragon_fury", furyBonus);
		attacker.getAttributes().set("dragon_fury_end", System.currentTimeMillis() + 8000); // 8 seconds

		attacker.getClient().queueOutgoingPacket(new SendMessage("Dragon's fury builds within you! (+" + furyBonus + " next attack damage)"));

		// Precision Strikes - chance for perfect accuracy on next few attacks
		if (dragonicMastery > 2.0) {
			int precisionAttacks = (int)(dragonicMastery - 1.0); // 1-2 guaranteed hits
			attacker.getAttributes().set("dragon_precision", precisionAttacks);
			attacker.getAttributes().set("dragon_precision_end", System.currentTimeMillis() + 15000); // 15 seconds

			attacker.getClient().queueOutgoingPacket(new SendMessage("Dragon precision guides your next " + precisionAttacks + " attack" +
				(precisionAttacks > 1 ? "s" : "") + "!"));
		}

		// Draconic Roar - intimidate target, reducing their effectiveness
		if (flurryStrikes >= 3) {
			int intimidation = totalFlurryDamage / 5;
			victim.getAttributes().set("draconic_intimidation", intimidation);
			victim.getAttributes().set("draconic_intimidation_end", System.currentTimeMillis() + 10000); // 10 seconds
		}

		// Scale Breaking - reduce target's defense temporarily
		int defenseReduction = (int)(baseDamage * 0.2 * dragonicMastery);
		if (defenseReduction > 0 && victim.getGrades()[1] > 0) {
			long currentDef = victim.getGrades()[1];
			victim.getGrades()[1] = Math.max(0, currentDef - defenseReduction);

			// Store original defense for restoration (would need timer implementation)
			victim.getAttributes().set("scale_break_reduction", defenseReduction);
			victim.getAttributes().set("scale_break_end", System.currentTimeMillis() + 12000); // 12 seconds
		}

		FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + totalFlurryDamage);
	}
}