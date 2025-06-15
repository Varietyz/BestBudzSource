package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.pets.PetCombat;

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

		// Apply temporary attack boost using PetCombat utility
		PetCombat.applyTemporaryBonus(pet, "ATTACK", 15, 30000); // 30 seconds

		// Deal extra damage using existing damage system
		if (target != null && !target.isDead()) {
			int extraDamage = 15 + (int)(Math.random() * 15);
			target.getGrades()[3] = Math.max(0, target.getGrades()[3] - extraDamage);
			target.getUpdateFlags().sendHit(extraDamage, (byte)0, (byte)0);
		}
	}
}