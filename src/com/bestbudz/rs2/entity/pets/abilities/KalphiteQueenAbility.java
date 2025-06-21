package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.pets.PetCombatUtils;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class KalphiteQueenAbility extends PetAbility {

	public KalphiteQueenAbility() {
		super("Kalphite Rage", 50, 0.15); // 15% chance, 50 tick cooldown
	}

	@Override
	protected void performAbility(Stoner pet, Entity target) {
		// Use existing animation/graphic system
		pet.getUpdateFlags().sendAnimation(new Animation(6240));
		pet.getUpdateFlags().sendGraphic(new Graphic(1055, true));
		pet.getUpdateFlags().sendForceMessage("*chittering angrily*");

		// Apply temporary attack boost using PetCombatUtils utility
		PetCombatUtils.applyTemporaryBonus(pet, "ATTACK", 15, 30000); // 30 seconds

		// Deal extra damage using proper Hit system
		if (target != null && !target.isDead()) {
			int extraDamage = 15 + (int)(Math.random() * 15);
			Hit hit = new Hit(pet, extraDamage, Hit.HitTypes.MELEE); // Physical rage attack
			target.hit(hit);
		}
	}
}