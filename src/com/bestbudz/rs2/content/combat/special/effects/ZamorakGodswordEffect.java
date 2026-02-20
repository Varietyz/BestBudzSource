package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class ZamorakGodswordEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double chaosIntensity = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.25);
			int baseDamage = attacker.getLastDamageDealt();

			double surgeVariance = 0.6 + (Math.random() * 0.8 * chaosIntensity);
			int surgeDamage = (int)(baseDamage * surgeVariance);

			if (surgeDamage > 0) {
				long newHp = Math.max(0, victim.getGrades()[3] - surgeDamage);
				victim.getGrades()[3] = newHp;

				if (surgeVariance > 1.2) {
					attacker.getClient().queueOutgoingPacket(new SendMessage("CHAOS SURGE! Zamorak's power unleashes " +
						surgeDamage + " devastating damage!"));
				} else {
					attacker.getClient().queueOutgoingPacket(new SendMessage("Chaotic energy strikes for " +
						surgeDamage + " damage!"));
				}
			}

			int chaosDisruption = (int)(victim.getGrades()[0] * (0.3 + chaosIntensity * 0.1));
			if (chaosDisruption > 0) {
				victim.getGrades()[0] = Math.max(0, victim.getGrades()[0] - chaosDisruption);
				victim.getGrades()[1] = Math.max(0, victim.getGrades()[1] - chaosDisruption);

				victim.getAttributes().set("chaos_disruption", chaosDisruption);
				victim.getAttributes().set("chaos_disruption_end", System.currentTimeMillis() + 12000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("Chaos disrupts the creature's combat abilities! (-" +
					chaosDisruption + " Attack/Defense)"));
			}

			if (chaosIntensity > 2.0) {
				int effectType = com.bestbudz.core.util.Utility.random(4);
				int effectPower = (int)(chaosIntensity * 10);

				switch (effectType) {
					case 0:
						attacker.getAttributes().set("chaos_strength_boost", effectPower);
						attacker.getAttributes().set("chaos_strength_end", System.currentTimeMillis() + 15000);
						attacker.getClient().queueOutgoingPacket(new SendMessage("Chaos grants immense strength! (+" +
							effectPower + " Strength)"));
						break;

					case 1:
						attacker.getAttributes().set("chaos_attack_speed", effectPower);
						attacker.getAttributes().set("chaos_speed_end", System.currentTimeMillis() + 12000);
						attacker.getClient().queueOutgoingPacket(new SendMessage("Chaos accelerates your attacks! (+" +
							effectPower + "% speed)"));
						break;

					case 2:
						attacker.getAttributes().set("chaos_protection", effectPower);
						attacker.getAttributes().set("chaos_protection_end", System.currentTimeMillis() + 18000);
						attacker.getClient().queueOutgoingPacket(new SendMessage("Chaos shields you from harm! (+" +
							effectPower + " Defense)"));
						break;

					case 3:
						if (attacker.getGrades()[3] < attacker.getMaxGrades()[3]) {
							long healing = Math.min(effectPower * 2, attacker.getMaxGrades()[3] - attacker.getGrades()[3]);
							attacker.getGrades()[3] += healing;
							attacker.getProfession().update(3);
							attacker.getClient().queueOutgoingPacket(new SendMessage("Chaos energy heals you for " +
								healing + " health!"));
						}
						break;
				}
			}

			if (chaosIntensity > 2.5) {
				double wrathChance = (chaosIntensity - 2.5) * 12;
				if (com.bestbudz.core.util.Utility.random(100) < wrathChance) {
					int wrathDamage = baseDamage + surgeDamage;

					long newHp = Math.max(0, victim.getGrades()[3] - wrathDamage);
					victim.getGrades()[3] = newHp;

					attacker.getClient().queueOutgoingPacket(new SendMessage("ZAMORAK'S WRATH! Divine chaos erupts!"));
				}
			}

			FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + surgeDamage);
		}
	}
}
