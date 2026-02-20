package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class StaffOfDeadEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		double necroticPower = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.3);
		int magicLevel = (int)attacker.getGrades()[6];

		int barrierStrength = (int)(50 + (magicLevel * 0.8) + (necroticPower * 30));
		int reflectionPower = (int)(25 + (necroticPower * 15));
		int barrierDuration = (int)(15000 + (necroticPower * 5000));

		attacker.getAttributes().set("necrotic_barrier_strength", barrierStrength);
		attacker.getAttributes().set("necrotic_barrier_reflection", reflectionPower);
		attacker.getAttributes().set("necrotic_barrier_end", System.currentTimeMillis() + barrierDuration);

		attacker.getClient().queueOutgoingPacket(new SendMessage("Necromantic barrier shields you! (" +
			barrierStrength + " absorption, " + reflectionPower + "% reflection)"));

		int masteryBonus = (int)(necroticPower * 12);
		attacker.getAttributes().set("death_magic_damage", masteryBonus);
		attacker.getAttributes().set("death_magic_accuracy", masteryBonus * 2);
		attacker.getAttributes().set("death_magic_end", System.currentTimeMillis() + 25000);

		attacker.getClient().queueOutgoingPacket(new SendMessage("Death magic flows through you! (+" +
			masteryBonus + " Magic damage, +" + (masteryBonus * 2) + " Magic accuracy)"));

		if (necroticPower > 2.0) {
			int drainPower = (int)((necroticPower - 2.0) * 8);
			int auraDuration = (int)(8000 + necroticPower * 2000);

			attacker.getAttributes().set("soul_drain_aura", drainPower);
			attacker.getAttributes().set("soul_drain_radius", 3);
			attacker.getAttributes().set("soul_drain_aura_end", System.currentTimeMillis() + auraDuration);

			attacker.getClient().queueOutgoingPacket(new SendMessage("SOUL DRAIN AURA! Death energy surrounds you! (" +
				drainPower + " damage/tick to nearby creatures)"));
		}

		if (necroticPower > 1.8) {
			attacker.getAttributes().set("undead_immunity_poison", System.currentTimeMillis() + 20000);
			attacker.getAttributes().set("undead_immunity_disease", System.currentTimeMillis() + 20000);
			attacker.getAttributes().set("undead_immunity_fear", System.currentTimeMillis() + 20000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Undead affinity grants immunity to poison, disease, and fear!"));
		}

		if (necroticPower > 2.2) {
			int regenRate = (int)(necroticPower * 3);
			attacker.getAttributes().set("necrotic_regeneration", regenRate);
			attacker.getAttributes().set("necrotic_regeneration_end", System.currentTimeMillis() + 30000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Necrotic regeneration activated! (" +
				regenRate + "% of damage dealt heals you)"));
		}

		if (necroticPower > 2.8) {
			int executeThreshold = (int)(15 + necroticPower * 5);
			double executeChance = (necroticPower - 2.8) * 25;

			attacker.getAttributes().set("death_execute_threshold", executeThreshold);
			attacker.getAttributes().set("death_execute_chance", (int)executeChance);
			attacker.getAttributes().set("death_execute_end", System.currentTimeMillis() + 20000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("DEATH'S EMBRACE! Creatures below " +
				executeThreshold + "% health face instant death!"));
		}

		if (necroticPower > 3.0) {
			int phaseChance = (int)((necroticPower - 3.0) * 15);
			attacker.getAttributes().set("spectral_phase_chance", phaseChance);
			attacker.getAttributes().set("spectral_form_end", System.currentTimeMillis() + 25000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("SPECTRAL FORM! " + phaseChance +
				"% chance to phase through attacks!"));
		}

		if (necroticPower > 2.5) {
			int dominionPower = (int)(necroticPower * 4);

			attacker.getAttributes().set("necro_dominion_debuff", dominionPower);
			attacker.getAttributes().set("necro_dominion_radius", 4);
			attacker.getAttributes().set("necro_dominion_end", System.currentTimeMillis() + 20000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("RESONANCE'S DOMINION! Death magic weakens all nearby creatures! (-" +
				dominionPower + " creature combat effectiveness)"));
		}

		Object harvestObj = attacker.getAttributes().get("soul_harvest_count");
		int harvestCount = (harvestObj instanceof Integer) ? (Integer)harvestObj : 0;

		if (harvestCount > 0) {
			int harvestBonus = harvestCount * 5;
			attacker.getAttributes().set("soul_harvest_bonus", harvestBonus);
			attacker.getAttributes().set("soul_harvest_bonus_end", System.currentTimeMillis() + 45000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Harvested souls empower you! (+" +
				harvestBonus + "% all damage from " + harvestCount + " souls)"));
		}

		if (necroticPower > 3.5) {
			attacker.getAttributes().set("dark_enlightenment", true);
			attacker.getAttributes().set("perfect_spell_accuracy", true);
			attacker.getAttributes().set("unlimited_runes", true);
			attacker.getAttributes().set("dark_enlightenment_end", System.currentTimeMillis() + 30000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("DARK ENLIGHTENMENT! Perfect magical mastery achieved!"));
		}

		FormulaData.updateCombatEvolution(attacker, null, true, 0);

		attacker.getAttributes().set("death_magic_resonance", necroticPower);
		attacker.getAttributes().set("death_magic_resonance_end", System.currentTimeMillis() + 60000);
	}
}
