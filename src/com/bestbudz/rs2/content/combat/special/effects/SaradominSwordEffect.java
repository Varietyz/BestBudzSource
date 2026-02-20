package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class SaradominSwordEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double divinePower = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.3);
			int baseDamage = attacker.getLastDamageDealt();

			int lightningBonus = (int)(baseDamage * (0.4 + divinePower * 0.2));

			if (lightningBonus > 0) {
				long newHp = Math.max(0, victim.getGrades()[3] - lightningBonus);
				victim.getGrades()[3] = newHp;

				attacker.getClient().queueOutgoingPacket(new SendMessage("Divine lightning strikes for " +
					lightningBonus + " additional damage!"));
			}

			int hybridBonus = (int)(divinePower * 8);
			attacker.getAttributes().set("hybrid_melee_bonus", hybridBonus);
			attacker.getAttributes().set("hybrid_magic_bonus", hybridBonus);
			attacker.getAttributes().set("hybrid_bonus_end", System.currentTimeMillis() + 20000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Divine hybrid mastery empowers you! (+" +
				hybridBonus + " Melee/Magic power)"));

			int protection = (int)((baseDamage + lightningBonus) * 0.2);
			attacker.getAttributes().set("divine_sword_protection", protection);
			attacker.getAttributes().set("divine_immunity", System.currentTimeMillis() + 15000);
			attacker.getAttributes().set("divine_sword_end", System.currentTimeMillis() + 18000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Divine protection shields you! (+" +
				protection + " damage reduction + status immunity)"));

			if (divinePower > 2.0) {
				double chainChance = (divinePower - 2.0) * 25;
				if (com.bestbudz.core.util.Utility.random(100) < chainChance) {
					int chainDamage = (int)((baseDamage + lightningBonus) * 0.6);

					target.getAttributes().set("lightning_chain_damage", chainDamage);
					target.getAttributes().set("lightning_chain_radius", 2);
					target.getAttributes().set("lightning_chain_end", System.currentTimeMillis() + 5000);

					attacker.getClient().queueOutgoingPacket(new SendMessage("LIGHTNING CHAIN! Divine energy spreads!"));
				}
			}

			if (baseDamage + lightningBonus > 20) {
				int healthRestore = (int)((baseDamage + lightningBonus) * 0.3 * divinePower);
				int prayerRestore = (int)((baseDamage + lightningBonus) * 0.2 * divinePower);

				if (attacker.getGrades()[3] < attacker.getMaxGrades()[3]) {
					long actualHealthRestore = Math.min(healthRestore,
						attacker.getMaxGrades()[3] - attacker.getGrades()[3]);
					attacker.getGrades()[3] += actualHealthRestore;
					attacker.getProfession().update(3);

					attacker.getClient().queueOutgoingPacket(new SendMessage("Divine energy restores " +
						actualHealthRestore + " health!"));
				}

				if (attacker.getGrades()[5] < attacker.getMaxGrades()[5]) {
					long actualPrayerRestore = Math.min(prayerRestore,
						attacker.getMaxGrades()[5] - attacker.getGrades()[5]);
					attacker.getGrades()[5] += actualPrayerRestore;
					attacker.getProfession().update(5);

					attacker.getClient().queueOutgoingPacket(new SendMessage("Divine wisdom restores " +
						actualPrayerRestore + " resonance!"));
				}
			}

			if (divinePower > 2.5) {

				boolean isEvil = victim.getAttributes().get("poison_damage") != null ||
					victim.getAttributes().get("cursed") != null ||
					victim.getAttributes().get("chaos_effect") != null;

				if (isEvil) {
					int furyDamage = (int)((baseDamage + lightningBonus) * 0.5);

					long newHp = Math.max(0, victim.getGrades()[3] - furyDamage);
					victim.getGrades()[3] = newHp;

					attacker.getClient().queueOutgoingPacket(new SendMessage("RIGHTEOUS FURY! Your blade purges evil!"));
				}
			}

			if (divinePower > 2.8) {
				int sacredPower = (int)(divinePower * 6);

				attacker.getAttributes().set("sacred_ground_healing", sacredPower);
				attacker.getAttributes().set("sacred_ground_protection", sacredPower);
				attacker.getAttributes().set("sacred_ground_radius", 2);
				attacker.getAttributes().set("sacred_ground_end", System.currentTimeMillis() + 25000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("SACRED GROUND! Divine energy consecrates the area!"));
			}

			if (divinePower > 3.0) {
				double healthPercent = (double)victim.getGrades()[3] / victim.getMaxGrades()[3];
				if (healthPercent > 0.8) {
					double judgmentChance = (divinePower - 3.0) * 20;
					if (com.bestbudz.core.util.Utility.random(100) < judgmentChance) {
						int judgmentDamage = (int)(victim.getMaxGrades()[3] * 0.25);

						long newHp = Math.max(0, victim.getGrades()[3] - judgmentDamage);
						victim.getGrades()[3] = newHp;

						attacker.getClient().queueOutgoingPacket(new SendMessage("DIVINE JUDGMENT! Saradomin's justice is absolute!"));
					}
				}
			}

			attacker.getAttributes().remove("poison_damage");
			attacker.getAttributes().remove("venom_damage");
			attacker.getAttributes().remove("stat_drain");
			attacker.getAttributes().remove("curse_effect");

			attacker.getClient().queueOutgoingPacket(new SendMessage("Divine light purifies you of ailments!"));

			Object saintObj = attacker.getAttributes().get("sword_saint_level");
			int saintLevel = (saintObj instanceof Integer) ? (Integer)saintObj : 0;

			saintLevel++;
			attacker.getAttributes().set("sword_saint_level", saintLevel);
			attacker.getAttributes().set("sword_saint_end", System.currentTimeMillis() + 30000);

			if (saintLevel >= 5) {
				int saintBonus = saintLevel * 3;
				attacker.getClient().queueOutgoingPacket(new SendMessage("Sword Saint mastery grows! (+" +
					saintBonus + "% hybrid combat effectiveness)"));
			}

			FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + lightningBonus);
		}
	}
}
