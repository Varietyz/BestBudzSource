package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class DragonSpearEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		double dragonicPower = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.25);
		int baseDamage = attacker.getLastDamageDealt();

		double pierceMultiplier = 0.4 + (dragonicPower * 0.2);
		int pierceDamage = (int)(baseDamage * pierceMultiplier);

		if (target.isNpc() && pierceDamage > 0) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim != null) {
				long newHp = Math.max(0, victim.getGrades()[3] - pierceDamage);
				victim.getGrades()[3] = newHp;

				attacker.getClient().queueOutgoingPacket(new SendMessage("Your spear pierces through the creature's defenses for " +
					pierceDamage + " additional damage!"));
			}
		}

		performDragonicKnockback(attacker, target, (int)dragonicPower);

		if (dragonicPower > 2.0) {
			double followUpChance = (dragonicPower - 2.0) * 30;
			if (com.bestbudz.core.util.Utility.random(100) < followUpChance) {
				int followUpDamage = (int)(baseDamage * 0.6);

				if (target.isNpc() && followUpDamage > 0) {
					Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
					if (victim != null) {
						long newHp = Math.max(0, victim.getGrades()[3] - followUpDamage);
						victim.getGrades()[3] = newHp;

						attacker.getClient().queueOutgoingPacket(new SendMessage("FOLLOW-UP THRUST! Your spear strikes again!"));
					}
				}
			}
		}

		if (baseDamage > 15) {
			int armorPen = (int)(baseDamage * 0.3 * dragonicPower);

			if (target.isNpc() && armorPen > 0) {
				Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
				if (victim != null && victim.getGrades()[1] > 0) {
					long currentDef = victim.getGrades()[1];
					victim.getGrades()[1] = Math.max(0, currentDef - armorPen);

					victim.getAttributes().set("armor_pierce", armorPen);
					victim.getAttributes().set("armor_pierce_end", System.currentTimeMillis() + 10000);
				}
			}
		}

		if (dragonicPower > 1.8) {
			int momentumBonus = (int)((dragonicPower - 1.0) * 10);
			attacker.getAttributes().set("draconic_momentum", momentumBonus);
			attacker.getAttributes().set("draconic_momentum_end", System.currentTimeMillis() + 12000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Draconic momentum increases your attack speed! (+" + momentumBonus + "% speed)"));
		}

		attacker.getClient().queueOutgoingPacket(new SendMessage("Your dragon spear channels ancient power!"));
		FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + pierceDamage);
	}

	private void performDragonicKnockback(Stoner attacker, Entity target, int power) {

		boolean knockbackSuccessful = false;

		for (int distance = power; distance > 0; distance--) {
			if (attemptKnockback(target, -distance, 0) ||
				attemptKnockback(target, distance, 0) ||
				attemptKnockback(target, 0, distance) ||
				attemptKnockback(target, 0, -distance)) {

				knockbackSuccessful = true;

				if (distance >= 2 && target.isNpc()) {
					int knockbackDamage = distance * power * 2;
					Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
					if (victim != null) {
						long newHp = Math.max(0, victim.getGrades()[3] - knockbackDamage);
						victim.getGrades()[3] = newHp;

						attacker.getClient().queueOutgoingPacket(new SendMessage("The creature crashes backward for " + knockbackDamage + " impact damage!"));
					}
				}
				break;
			}
		}

		if (!knockbackSuccessful) {
			attacker.getClient().queueOutgoingPacket(new SendMessage("Your spear thrust meets immovable resistance!"));
		}
	}

	private boolean attemptKnockback(Entity target, int deltaX, int deltaY) {
		try {
			if (!Region.getRegion(target.getLocation().getX(), target.getLocation().getY())
				.blockedWest(target.getLocation().getX(), target.getLocation().getY(),
					target.getLocation().getZ())) {
				target.getMovementHandler().walkTo(deltaX, deltaY);
				return true;
			}
		} catch (Exception e) {

			target.getMovementHandler().walkTo(deltaX > 0 ? 1 : deltaX < 0 ? -1 : 0,
				deltaY > 0 ? 1 : deltaY < 0 ? -1 : 0);
		}
		return false;
	}
}
