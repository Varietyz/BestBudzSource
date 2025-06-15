package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Dragon Battleaxe Special - "Berserker's Fury"
 * Sacrifices accuracy and defense for massive strength and damage bonuses
 */
public class DragonBattleaxeEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		double berserkerFury = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.25);

		// Berserker Transformation - trade accuracy/defense for raw power
		int currentAttack = (int)attacker.getGrades()[0];
		int currentDefense = (int)attacker.getGrades()[1];
		int currentStrength = (int)attacker.getGrades()[2];

		// Calculate stat redistribution
		int attackDrain = (int)(currentAttack * (0.1 + berserkerFury * 0.05)); // 10-22% drain
		int defenseDrain = (int)(currentDefense * (0.15 + berserkerFury * 0.05)); // 15-27% drain
		int strengthBoost = attackDrain + defenseDrain; // All drained stats go to strength

		// Apply stat changes
		attacker.getGrades()[0] = Math.max(1, currentAttack - attackDrain);
		attacker.getGrades()[1] = Math.max(1, currentDefense - defenseDrain);
		attacker.getGrades()[2] = Math.min(attacker.getMaxGrades()[2] + strengthBoost,
			currentStrength + strengthBoost);

		// Update profession displays
		attacker.getProfession().update(0);
		attacker.getProfession().update(1);
		attacker.getProfession().update(2);

		attacker.getClient().queueOutgoingPacket(new SendMessage("BERSERKER'S FURY! You sacrifice " +
			attackDrain + " Attack and " + defenseDrain + " Defense for +" + strengthBoost + " Strength!"));

		// Berserker State - enhanced combat abilities
		int furyDuration = (int)(20000 + berserkerFury * 10000); // 20-45 second duration
		attacker.getAttributes().set("berserker_state", true);
		attacker.getAttributes().set("berserker_fury_level", (int)(berserkerFury * 10));
		attacker.getAttributes().set("berserker_state_end", System.currentTimeMillis() + furyDuration);

		// Fury Damage Bonus - massive damage increase
		int furyDamageBonus = (int)(30 + berserkerFury * 20); // 30-80% damage bonus
		attacker.getAttributes().set("fury_damage_bonus", furyDamageBonus);
		attacker.getAttributes().set("fury_damage_end", System.currentTimeMillis() + furyDuration);

		attacker.getClient().queueOutgoingPacket(new SendMessage("Berserker fury grants +" +
			furyDamageBonus + "% damage for " + (furyDuration/1000) + " seconds!"));

		// Blood Rage - gain power from taking damage
		if (berserkerFury > 1.8) {
			int rageGain = (int)(berserkerFury * 3); // 3-12 rage per damage taken
			attacker.getAttributes().set("blood_rage_gain", rageGain);
			attacker.getAttributes().set("blood_rage_end", System.currentTimeMillis() + furyDuration);

			attacker.getClient().queueOutgoingPacket(new SendMessage("BLOOD RAGE! Gain +" +
				rageGain + " fury for each point of damage taken!"));
		}

		// Unstoppable Force - immunity to movement restrictions
		if (berserkerFury > 2.0) {
			attacker.getAttributes().set("unstoppable_force", true);
			attacker.getAttributes().set("immunity_freeze", System.currentTimeMillis() + furyDuration);
			attacker.getAttributes().set("immunity_stun", System.currentTimeMillis() + furyDuration);
			attacker.getAttributes().set("immunity_slow", System.currentTimeMillis() + furyDuration);

			attacker.getClient().queueOutgoingPacket(new SendMessage("UNSTOPPABLE FORCE! Immune to movement restrictions!"));
		}

		// Berserker's Resilience - damage reduction at low health
		if (berserkerFury > 2.2) {
			double healthPercent = (double)attacker.getGrades()[3] / attacker.getMaxGrades()[3];
			if (healthPercent < 0.5) { // Below 50% health
				int resilience = (int)((0.5 - healthPercent) * 40); // Up to 20% damage reduction
				attacker.getAttributes().set("berserker_resilience", resilience);
				attacker.getAttributes().set("berserker_resilience_end", System.currentTimeMillis() + furyDuration);

				attacker.getClient().queueOutgoingPacket(new SendMessage("BERSERKER'S RESILIENCE! +" +
					resilience + "% damage reduction while wounded!"));
			}
		}

		// Rampage Mode - attack speed increases with consecutive hits
		if (berserkerFury > 2.5) {
			attacker.getAttributes().set("rampage_mode", true);
			attacker.getAttributes().set("rampage_speed_stack", 0);
			attacker.getAttributes().set("rampage_mode_end", System.currentTimeMillis() + furyDuration);

			attacker.getClient().queueOutgoingPacket(new SendMessage("RAMPAGE MODE! Attack speed increases with consecutive hits!"));
		}

		// Berserker's Roar - intimidate all nearby enemies (mobs in this case)
		if (berserkerFury > 2.8) {
			int roarPower = (int)(berserkerFury * 8);

			// Store area intimidation effect that affects nearby mobs
			attacker.getAttributes().set("berserker_roar_debuff", roarPower);
			attacker.getAttributes().set("berserker_roar_radius", 4);
			attacker.getAttributes().set("berserker_roar_end", System.currentTimeMillis() + 15000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("BERSERKER'S ROAR! All nearby creatures cower! (-" +
				roarPower + " mob combat effectiveness)"));
		}

		// Legendary Berserker - transcend normal limits
		if (berserkerFury > 3.2) {
			// Temporary stat cap increases
			attacker.getAttributes().set("legendary_berserker", true);
			attacker.getAttributes().set("stat_cap_increase", (int)(berserkerFury * 50));
			attacker.getAttributes().set("legendary_berserker_end", System.currentTimeMillis() + furyDuration);

			// Chance for devastating critical strikes
			int critChance = (int)((berserkerFury - 3.2) * 20); // Up to 3.6% chance
			attacker.getAttributes().set("berserker_crit_chance", critChance);
			attacker.getAttributes().set("berserker_crit_multiplier", 250); // 250% damage crits

			attacker.getClient().queueOutgoingPacket(new SendMessage("LEGENDARY BERSERKER! You transcend mortal limits! (" +
				critChance + "% chance for 250% damage crits)"));
		}

		// Fury Recovery - berserker state slowly restores drained stats
		attacker.getAttributes().set("fury_attack_restore", attackDrain);
		attacker.getAttributes().set("fury_defense_restore", defenseDrain);
		attacker.getAttributes().set("fury_restore_rate", furyDuration / 10); // Restore over 10 intervals

		// Store original stats for restoration
		attacker.getAttributes().set("pre_berserker_attack", currentAttack);
		attacker.getAttributes().set("pre_berserker_defense", currentDefense);
		attacker.getAttributes().set("pre_berserker_strength", currentStrength);

		FormulaData.updateCombatEvolution(attacker, target, true, strengthBoost);

		attacker.getClient().queueOutgoingPacket(new SendMessage("The dragon battleaxe awakens your inner berserker!"));
	}
}