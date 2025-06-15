package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Armadyl Crossbow Special - "Divine Bolt"
 * A piercing shot blessed by Armadyl that penetrates armor and grants divine favor
 */
public class ArmadylCrossbowEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double divineFavor = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.2);
			int baseDamage = attacker.getLastDamageDealt();

			// Check bolt type for enhanced effects
			Item bolts = attacker.getEquipment().getItems()[13];
			boolean dragonBolts = (bolts != null && bolts.getId() == 9244);
			boolean enchantedBolts = (bolts != null && (bolts.getId() == 9243 || bolts.getId() == 9245));

			// Divine Penetration - ignores armor and deals bonus damage
			double penetrationMultiplier = 0.5 + (divineFavor * 0.15); // 50-80% bonus
			if (dragonBolts) penetrationMultiplier += 0.2; // Dragon bolts +20%
			if (enchantedBolts) penetrationMultiplier += 0.1; // Enchanted bolts +10%

			int penetrationDamage = (int)(baseDamage * penetrationMultiplier);

			if (penetrationDamage > 0) {
				long newHp = Math.max(0, victim.getGrades()[3] - penetrationDamage);
				victim.getGrades()[3] = newHp;

				String boltType = dragonBolts ? "dragon" : enchantedBolts ? "enchanted" : "divine";
				attacker.getClient().queueOutgoingPacket(new SendMessage("Your " + boltType +
					" bolt pierces for " + penetrationDamage + " divine damage!"));
			}

			// Armor Piercing - reduce defense effectiveness of target
			int armorPierce = (int)((baseDamage + penetrationDamage) * 0.2 * divineFavor);
			if (armorPierce > 0 && victim.getGrades()[1] > 0) {
				victim.getGrades()[1] = Math.max(0, victim.getGrades()[1] - armorPierce);
			}

			// Armadyl's Blessing - accuracy and range bonuses
			int blessingPower = (int)(divineFavor * 8);
			attacker.getAttributes().set("armadyl_crossbow_accuracy", blessingPower * 2);
			attacker.getAttributes().set("armadyl_crossbow_range", blessingPower);
			attacker.getAttributes().set("armadyl_crossbow_end", System.currentTimeMillis() + 15000);

			attacker.getClient().queueOutgoingPacket(new SendMessage("Armadyl's blessing enhances your aim! (+" +
				(blessingPower * 2) + " Accuracy, +" + blessingPower + " Range)"));

			// Divine Guidance - next few shots have perfect accuracy
			if (divineFavor > 2.0) {
				int guidedShots = (int)(divineFavor - 1.0); // 1-3 guided shots
				attacker.getAttributes().set("divine_guidance", guidedShots);
				attacker.getAttributes().set("divine_guidance_end", System.currentTimeMillis() + 20000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("Divine guidance grants " + guidedShots +
					" perfect shot" + (guidedShots > 1 ? "s" : "") + "!"));
			}

			// Bolt Mastery - special effects based on bolt type
			if (dragonBolts && divineFavor > 1.8) {
				// Dragon Bolt Explosion
				double explosionChance = (divineFavor - 1.8) * 25; // Up to 11% chance
				if (com.bestbudz.core.util.Utility.random(100) < explosionChance) {
					int explosionDamage = (int)((baseDamage + penetrationDamage) * 0.4);

					long newHp = Math.max(0, victim.getGrades()[3] - explosionDamage);
					victim.getGrades()[3] = newHp;

					attacker.getClient().queueOutgoingPacket(new SendMessage("DRAGON BOLT EXPLOSION! Divine fire erupts!"));

					// Area explosion effect
					target.getAttributes().set("dragon_explosion_damage", explosionDamage / 2);
					target.getAttributes().set("dragon_explosion_radius", 2);
				}
			} else if (enchantedBolts) {
				// Enchanted Bolt Effects (vary by bolt type)
				int enchantEffect = com.bestbudz.core.util.Utility.random(3);
				int effectPower = (int)(divineFavor * 6);

				switch (enchantEffect) {
					case 0: // Sapphire - Magic drain
						if (victim.getGrades()[6] > effectPower) {
							victim.getGrades()[6] -= effectPower;
						}
						break;

					case 1: // Emerald - Poison effect (would need poison implementation for mobs)
						// Since mobs don't have poison method, apply damage over time via attribute
						victim.getAttributes().set("emerald_poison_damage", 4 + (int)divineFavor);
						victim.getAttributes().set("emerald_poison_end", System.currentTimeMillis() + 15000);
						break;

					case 2: // Ruby - Lifeforce drain
						double lifeDrain = victim.getGrades()[3] * 0.15;
						long drainAmount = Math.min((long)lifeDrain, victim.getGrades()[3] - 1);
						if (drainAmount > 0) {
							victim.getGrades()[3] -= drainAmount;
						}
						break;
				}
			}

			// Divine Protection - temporary damage reduction for attacker
			if (baseDamage + penetrationDamage > 25) {
				int protection = (int)((baseDamage + penetrationDamage) * 0.15);
				attacker.getAttributes().set("divine_protection", protection);
				attacker.getAttributes().set("divine_protection_end", System.currentTimeMillis() + 12000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("Divine protection shields you! (+" +
					protection + " damage reduction)"));
			}

			FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + penetrationDamage);
		}
	}
}