package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class ZamorakianSpearEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		double infernalPower = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.25);
		int baseDamage = attacker.getLastDamageDealt();

		double fireMultiplier = 0.5 + (infernalPower * 0.2);
		int fireDamage = (int)(baseDamage * fireMultiplier);

		if (target.isNpc() && fireDamage > 0) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim != null) {
				long newHp = Math.max(0, victim.getGrades()[3] - fireDamage);
				victim.getGrades()[3] = newHp;

				attacker.getClient().queueOutgoingPacket(new SendMessage("Your spear erupts with infernal fire, burning the creature for " +
					fireDamage + " fire damage!"));
			}
		}

		performInfernalKnockback(attacker, target, (int)infernalPower);

		if (infernalPower > 1.8) {
			int burnDamage = (int)(baseDamage * 0.3);
			int burnDuration = (int)(6 + infernalPower);

			target.getAttributes().set("burning_ground_damage", burnDamage);
			target.getAttributes().set("burning_ground_ticks", burnDuration);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Infernal flames scorch the ground around your target!"));
		}

		if (infernalPower > 2.5) {
			double burstChance = (infernalPower - 2.5) * 20;
			if (com.bestbudz.core.util.Utility.random(100) < burstChance) {
				int burstDamage = baseDamage + fireDamage;

				if (target.isNpc()) {
					Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
					if (victim != null) {
						long newHp = Math.max(0, victim.getGrades()[3] - burstDamage);
						victim.getGrades()[3] = newHp;
					}
				}

				attacker.getClient().queueOutgoingPacket(new SendMessage("HELLFIRE BURST! Infernal energy explodes around your target!"));

				target.getAttributes().set("hellfire_burst_damage", burstDamage / 2);
				target.getAttributes().set("hellfire_burst_radius", 2);
			}
		}

		if (fireDamage > 20) {
			int empowerment = fireDamage / 3;

			attacker.getAttributes().set("infernal_empowerment", empowerment);
			attacker.getAttributes().set("infernal_empowerment_end", System.currentTimeMillis() + 20000);

			attacker.getClient().queueOutgoingPacket(
				new SendMessage("Infernal power flows through you! (+" + empowerment + " fire damage)"));
		}

		if (infernalPower > 2.0) {
			attacker.getAttributes().set("fire_immunity", System.currentTimeMillis() + 15000);
			attacker.getClient().queueOutgoingPacket(new SendMessage("Infernal energy grants you fire immunity!"));
		}

		if (baseDamage + fireDamage > 35) {
			int wrathBoost = (int)((baseDamage + fireDamage) * 0.2);

			attacker.getAttributes().set("zamorak_wrath_attack", wrathBoost);
			attacker.getAttributes().set("zamorak_wrath_strength", wrathBoost);
			attacker.getAttributes().set("zamorak_wrath_end", System.currentTimeMillis() + 18000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Zamorak's wrath empowers your combat! (+" + wrathBoost + " Attack/Strength)"));
		}

		attacker.getClient().queueOutgoingPacket(new SendMessage("Your spear channels the fires of Zamorak!"));
		FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + fireDamage);
	}

	private void performInfernalKnockback(Stoner attacker, Entity target, int power) {

		boolean knockbackSuccessful = false;

		for (int distance = power + 1; distance > 0; distance--) {
			if (attemptInfernalKnockback(target, -distance, 0) ||
				attemptInfernalKnockback(target, distance, 0) ||
				attemptInfernalKnockback(target, 0, distance) ||
				attemptInfernalKnockback(target, 0, -distance)) {

				knockbackSuccessful = true;

				if (distance >= 2 && target.isNpc()) {
					int impactDamage = distance * power * 3;
					Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
					if (victim != null) {
						long newHp = Math.max(0, victim.getGrades()[3] - impactDamage);
						victim.getGrades()[3] = newHp;

						attacker.getClient().queueOutgoingPacket(new SendMessage("The creature crashes into infernal flames for " +
							impactDamage + " burn damage!"));
					}
				}

				if (distance >= 2) {
					target.getAttributes().set("infernal_trail", System.currentTimeMillis() + 8000);
					attacker.getClient().queueOutgoingPacket(new SendMessage("The creature leaves a trail of fire!"));
				}

				break;
			}
		}

		if (!knockbackSuccessful) {
			attacker.getClient().queueOutgoingPacket(new SendMessage("Your infernal thrust meets unyielding resistance!"));
		}
	}

	private boolean attemptInfernalKnockback(Entity target, int deltaX, int deltaY) {
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
