package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.pets.PetCombatUtils;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class KalphiteQueenAbility extends PetAbility {

	public KalphiteQueenAbility() {
		super("Kalphite Rage", 50, 0.15);
	}

	@Override
	protected void performAbility(Stoner pet, Entity target) {

		pet.getUpdateFlags().sendForceMessage("*chittering angrily*");

		PetCombatUtils.applyTemporaryBonus(pet, "ATTACK", 15, 30000);

		if (target != null && !target.isDead()) {
			int extraDamage = 15 + (int)(Math.random() * 15);
			Hit hit = new Hit(pet, extraDamage, Hit.HitTypes.MELEE);
			target.hit(hit);
		}
	}
}
