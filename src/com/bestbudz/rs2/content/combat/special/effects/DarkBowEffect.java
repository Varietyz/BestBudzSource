package com.bestbudz.rs2.content.combat.special.effects;

import com.bestbudz.rs2.content.combat.impl.CombatEffect;
import com.bestbudz.rs2.content.combat.formula.FormulaData;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class DarkBowEffect implements CombatEffect {

	@Override
	public void execute(Stoner attacker, Entity target) {
		if (target.isNpc()) {
			Mob victim = com.bestbudz.rs2.entity.World.getNpcs()[target.getIndex()];
			if (victim == null) return;

			double shadowPower = 1.0 + (FormulaData.getCombatEffectiveness(attacker) * 0.2);
			int baseDamage = attacker.getLastDamageDealt();

			Item arrows = attacker.getEquipment().getItems()[13];
			boolean dragonArrows = (arrows != null && (arrows.getId() == 11212 || arrows.getId() == 11227));

			double secondShotMultiplier = dragonArrows ? 0.9 + (shadowPower * 0.1) : 0.7 + (shadowPower * 0.1);
			int secondShot = Math.max(8, (int)(baseDamage * secondShotMultiplier));

			long newHp = Math.max(0, victim.getGrades()[3] - secondShot);
			victim.getGrades()[3] = newHp;

			String arrowType = dragonArrows ? "dragon" : "standard";
			attacker.getClient().queueOutgoingPacket(new SendMessage("Your " + arrowType + " arrows unleash a shadow storm for " +
				secondShot + " damage!"));

			if (dragonArrows) {

				int dragonBonus = (int)(baseDamage * 0.3 * shadowPower);
				if (dragonBonus > 0) {
					long newHp2 = Math.max(0, victim.getGrades()[3] - dragonBonus);
					victim.getGrades()[3] = newHp2;

					attacker.getClient().queueOutgoingPacket(new SendMessage("Dragon energy empowers your arrows for " +
						dragonBonus + " additional damage!"));
				}

				int fearEffect = (int)(shadowPower * 8);
				victim.getAttributes().set("dragon_fear_attack", fearEffect);
				victim.getAttributes().set("dragon_fear_defense", fearEffect);
				victim.getAttributes().set("dragon_fear_end", System.currentTimeMillis() + 10000);
			} else {

				int accuracyDebuff = (int)(shadowPower * 6);
				victim.getAttributes().set("shadow_blind", accuracyDebuff);
				victim.getAttributes().set("shadow_blind_end", System.currentTimeMillis() + 8000);
			}

			Object masteryObj = attacker.getAttributes().get("shadow_mastery");
			int currentMastery = (masteryObj instanceof Integer) ? (Integer)masteryObj : 0;

			currentMastery++;
			attacker.getAttributes().set("shadow_mastery", currentMastery);
			attacker.getAttributes().set("shadow_mastery_end", System.currentTimeMillis() + 15000);

			if (currentMastery >= 3) {
				int masteryBonus = currentMastery * 3;
				attacker.getClient().queueOutgoingPacket(new SendMessage("Shadow mastery builds! (+" +
					masteryBonus + "% range damage)"));
			}

			if (shadowPower > 2.2) {
				double areaChance = (shadowPower - 2.2) * 25;
				if (com.bestbudz.core.util.Utility.random(100) < areaChance) {
					int areaDamage = (int)((baseDamage + secondShot) * 0.3);

					target.getAttributes().set("shadow_area_damage", areaDamage);
					target.getAttributes().set("shadow_area_radius", 2);
					target.getAttributes().set("shadow_area_end", System.currentTimeMillis() + 5000);

					attacker.getClient().queueOutgoingPacket(new SendMessage("DARKNESS EMBRACE! Shadows spread around your target!"));
				}
			}

			if (baseDamage + secondShot > 40) {
				victim.getAttributes().set("hunter_mark", System.currentTimeMillis() + 20000);
				attacker.getAttributes().set("marked_target", victim.getId());
				attacker.getAttributes().set("marked_target_end", System.currentTimeMillis() + 20000);

				attacker.getClient().queueOutgoingPacket(new SendMessage("You mark the creature for the hunt!"));
			}

			FormulaData.updateCombatEvolution(attacker, target, true, baseDamage + secondShot);
		}
	}
}
