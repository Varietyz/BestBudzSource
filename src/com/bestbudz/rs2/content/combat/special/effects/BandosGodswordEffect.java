package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class BandosGodswordEffect implements CombatEffect {

	public static final int[] BGS_DRAIN_IDS = {1, 2, 5, 0, 6, 4};

	@Override
	public void execute(Stoner attacker, Entity target) {
		int baseDamage = attacker.getLastDamageDealt();
		double warlordsFury = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.25);

		double devastationIntensity = Math.min(2.0, (baseDamage / 30.0) * warlordsFury);
		int totalStatReduction = (int)(baseDamage * devastationIntensity * 0.15);

		Mob victim = null;
		if (target.isNpc()) {
			victim = World.getNpcs()[target.getIndex()];
		}

		if (totalStatReduction <= 0) return;

		int crushDamage = (int)(baseDamage * (0.2 + warlordsFury * 0.1));
		if (crushDamage > 0 && victim != null) {
			long newHp = Math.max(0, victim.getGrades()[3] - crushDamage);
			victim.getGrades()[3] = newHp;

			attacker.getClient().queueOutgoingPacket(new SendMessage("Your godsword crushes the creature for " +
				crushDamage + " devastating damage!"));
		}

		int remainingReduction = totalStatReduction;
		int statsAffected = 0;

		for (int statId : BGS_DRAIN_IDS) {
			if (remainingReduction <= 0) break;

			if (target.getGrades()[statId] > 0) {
				int reduction = Math.min(remainingReduction, (int)target.getGrades()[statId]);
				target.getGrades()[statId] -= reduction;
				remainingReduction -= reduction;
				statsAffected++;

				String statName = com.bestbudz.rs2.content.profession.Professions.PROFESSION_NAMES[statId];
				attacker.getClient().queueOutgoingPacket(new SendMessage("Bandos' might devastates the creature's " + statName + "!"));
			}
		}

		if (statsAffected >= 3 && warlordsFury > 1.8) {

			int dominanceBoost = totalStatReduction / 4;

			attacker.getAttributes().set("warlord_dominance_attack", dominanceBoost);
			attacker.getAttributes().set("warlord_dominance_strength", dominanceBoost);
			attacker.getAttributes().set("warlord_dominance_end", System.currentTimeMillis() + 15000);

			attacker.getClient().queueOutgoingPacket(
				new SendMessage("Bandos grants you the strength of a warlord! (+" + dominanceBoost + " Attack/Strength)"));
		}

		if (warlordsFury > 2.2 && victim != null) {
			int intimidationRange = 3;
			int intimidationEffect = totalStatReduction / 3;

			target.getAttributes().set("warlord_intimidation", intimidationEffect);
			target.getAttributes().set("warlord_intimidation_range", intimidationRange);
			target.getAttributes().set("warlord_intimidation_end", System.currentTimeMillis() + 10000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Your warlord's presence intimidates the battlefield!"));
		}

		FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + crushDamage);
	}
}
