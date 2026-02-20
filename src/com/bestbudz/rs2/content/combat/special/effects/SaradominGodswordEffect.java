package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class SaradominGodswordEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		int baseDamage = attacker.getLastDamageDealt();
		if (baseDamage == 0) return;

		double divineGrace = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.3);

		double smiteMultiplier = 0.25 + (divineGrace * 0.15);
		int smiteDamage = (int)(baseDamage * smiteMultiplier);

		if (target.isNpc() && smiteDamage > 0) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim != null) {
				long newHp = Math.max(0, victim.getGrades()[3] - smiteDamage);
				victim.getGrades()[3] = newHp;

				attacker.getClient().queueOutgoingPacket(new SendMessage("Divine light smites the creature for " +
					smiteDamage + " holy damage!"));
			}
		}

		int totalDamage = baseDamage + smiteDamage;
		long healthHealing = (long)(totalDamage * 0.6 * divineGrace);
		long prayerHealing = (long)(totalDamage * 0.3 * divineGrace);

		if (healthHealing > 9 && attacker.getGrades()[3] < attacker.getMaxGrades()[3]) {
			long availableHealing = attacker.getMaxGrades()[3] - attacker.getGrades()[3];
			long actualHealing = Math.min(healthHealing, availableHealing);
			attacker.getGrades()[3] += actualHealing;
			attacker.getProfession().update(3);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Divine grace restores " + actualHealing + " Life!"));
		}

		if (prayerHealing > 4 && attacker.getGrades()[5] < attacker.getMaxGrades()[5]) {
			long availablePrayer = attacker.getMaxGrades()[5] - attacker.getGrades()[5];
			long actualPrayer = Math.min(prayerHealing, availablePrayer);
			attacker.getGrades()[5] += actualPrayer;
			attacker.getProfession().update(5);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Divine wisdom restores " + actualPrayer + " Resonance!"));
		}

		if (divineGrace > 2.0) {
			int blessingBoost = (int)(totalDamage * 0.2);

			attacker.getAttributes().set("divine_blessing_attack", blessingBoost);
			attacker.getAttributes().set("divine_blessing_strength", blessingBoost);
			attacker.getAttributes().set("divine_blessing_defense", blessingBoost);
			attacker.getAttributes().set("divine_blessing_end", System.currentTimeMillis() + 20000);

			attacker.getClient().queueOutgoingPacket(
				new SendMessage("Divine blessing enhances your combat prowess! (+" +
					blessingBoost + " Attack/Strength/Defense)"));
		}

		if (divineGrace > 2.5) {
			double furyChance = (divineGrace - 2.5) * 40;
			if (com.bestbudz.core.util.Utility.random(100) < furyChance) {
				int furyDamage = totalDamage;

				attacker.getAttributes().set("righteous_fury", furyDamage);
				attacker.getAttributes().set("righteous_fury_end", System.currentTimeMillis() + 15000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("RIGHTEOUS FURY! Your next attack will deal devastating damage!"));
			}
		}

		if (totalDamage > 25) {

			attacker.getAttributes().remove("poison_damage");
			attacker.getAttributes().remove("venom_damage");

			attacker.getAttributes().set("divine_immunity", System.currentTimeMillis() + 30000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Purifying light cleanses you and grants divine protection!"));
		}

		if (divineGrace > 2.8) {
			int consecrationPower = (int)(totalDamage * 0.15);
			attacker.getClient().queueOutgoingPacket(new SendMessage("Divine energy consecrates the battlefield!"));

			attacker.getAttributes().set("consecration_power", consecrationPower);
			attacker.getAttributes().set("consecration_end", System.currentTimeMillis() + 25000);
		}

		FormulaData.updateCombatEvolution(attacker, target, true, totalDamage);
	}
}
