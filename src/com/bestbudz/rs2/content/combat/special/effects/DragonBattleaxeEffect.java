package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class DragonBattleaxeEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		double berserkerFury = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.25);

		int currentAttack = (int)attacker.getGrades()[0];
		int currentDefense = (int)attacker.getGrades()[1];
		int currentStrength = (int)attacker.getGrades()[2];

		int attackDrain = (int)(currentAttack * (0.1 + berserkerFury * 0.05));
		int defenseDrain = (int)(currentDefense * (0.15 + berserkerFury * 0.05));
		int strengthBoost = attackDrain + defenseDrain;

		attacker.getGrades()[0] = Math.max(1, currentAttack - attackDrain);
		attacker.getGrades()[1] = Math.max(1, currentDefense - defenseDrain);
		attacker.getGrades()[2] = Math.min(attacker.getMaxGrades()[2] + strengthBoost,
			currentStrength + strengthBoost);

		attacker.getProfession().update(0);
		attacker.getProfession().update(1);
		attacker.getProfession().update(2);

		attacker.getClient().queueOutgoingPacket(new SendMessage("BERSERKER'S FURY! You sacrifice " +
			attackDrain + " Attack and " + defenseDrain + " Defense for +" + strengthBoost + " Strength!"));

		int furyDuration = (int)(20000 + berserkerFury * 10000);
		attacker.getAttributes().set("berserker_state", true);
		attacker.getAttributes().set("berserker_fury_level", (int)(berserkerFury * 10));
		attacker.getAttributes().set("berserker_state_end", System.currentTimeMillis() + furyDuration);

		int furyDamageBonus = (int)(30 + berserkerFury * 20);
		attacker.getAttributes().set("fury_damage_bonus", furyDamageBonus);
		attacker.getAttributes().set("fury_damage_end", System.currentTimeMillis() + furyDuration);

		attacker.getClient().queueOutgoingPacket(new SendMessage("Berserker fury grants +" +
			furyDamageBonus + "% damage for " + (furyDuration/1000) + " seconds!"));

		if (berserkerFury > 1.8) {
			int rageGain = (int)(berserkerFury * 3);
			attacker.getAttributes().set("blood_rage_gain", rageGain);
			attacker.getAttributes().set("blood_rage_end", System.currentTimeMillis() + furyDuration);

			attacker.getClient().queueOutgoingPacket(new SendMessage("BLOOD RAGE! Gain +" +
				rageGain + " fury for each point of damage taken!"));
		}

		if (berserkerFury > 2.0) {
			attacker.getAttributes().set("unstoppable_force", true);
			attacker.getAttributes().set("immunity_freeze", System.currentTimeMillis() + furyDuration);
			attacker.getAttributes().set("immunity_stun", System.currentTimeMillis() + furyDuration);
			attacker.getAttributes().set("immunity_slow", System.currentTimeMillis() + furyDuration);

			attacker.getClient().queueOutgoingPacket(new SendMessage("UNSTOPPABLE FORCE! Immune to movement restrictions!"));
		}

		if (berserkerFury > 2.2) {
			double healthPercent = (double)attacker.getGrades()[3] / attacker.getMaxGrades()[3];
			if (healthPercent < 0.5) {
				int resilience = (int)((0.5 - healthPercent) * 40);
				attacker.getAttributes().set("berserker_resilience", resilience);
				attacker.getAttributes().set("berserker_resilience_end", System.currentTimeMillis() + furyDuration);

				attacker.getClient().queueOutgoingPacket(new SendMessage("BERSERKER'S RESILIENCE! +" +
					resilience + "% damage reduction while wounded!"));
			}
		}

		if (berserkerFury > 2.5) {
			attacker.getAttributes().set("rampage_mode", true);
			attacker.getAttributes().set("rampage_speed_stack", 0);
			attacker.getAttributes().set("rampage_mode_end", System.currentTimeMillis() + furyDuration);

			attacker.getClient().queueOutgoingPacket(new SendMessage("RAMPAGE MODE! Attack speed increases with consecutive hits!"));
		}

		if (berserkerFury > 2.8) {
			int roarPower = (int)(berserkerFury * 8);

			attacker.getAttributes().set("berserker_roar_debuff", roarPower);
			attacker.getAttributes().set("berserker_roar_radius", 4);
			attacker.getAttributes().set("berserker_roar_end", System.currentTimeMillis() + 15000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("BERSERKER'S ROAR! All nearby creatures cower! (-" +
				roarPower + " mob combat effectiveness)"));
		}

		if (berserkerFury > 3.2) {

			attacker.getAttributes().set("legendary_berserker", true);
			attacker.getAttributes().set("stat_cap_increase", (int)(berserkerFury * 50));
			attacker.getAttributes().set("legendary_berserker_end", System.currentTimeMillis() + furyDuration);

			int critChance = (int)((berserkerFury - 3.2) * 20);
			attacker.getAttributes().set("berserker_crit_chance", critChance);
			attacker.getAttributes().set("berserker_crit_multiplier", 250);

			attacker.getClient().queueOutgoingPacket(new SendMessage("LEGENDARY BERSERKER! You transcend mortal limits! (" +
				critChance + "% chance for 250% damage crits)"));
		}

		attacker.getAttributes().set("fury_attack_restore", attackDrain);
		attacker.getAttributes().set("fury_defense_restore", defenseDrain);
		attacker.getAttributes().set("fury_restore_rate", furyDuration / 10);

		attacker.getAttributes().set("pre_berserker_attack", currentAttack);
		attacker.getAttributes().set("pre_berserker_defense", currentDefense);
		attacker.getAttributes().set("pre_berserker_strength", currentStrength);

		FormulaData.updateCombatEvolution(attacker, target, true, strengthBoost);

		attacker.getClient().queueOutgoingPacket(new SendMessage("The dragon battleaxe awakens your inner berserker!"));
	}
}
