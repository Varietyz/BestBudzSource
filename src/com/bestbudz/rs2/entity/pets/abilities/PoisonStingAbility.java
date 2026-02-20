package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class PoisonStingAbility extends PetAbility {

	public PoisonStingAbility() {
		super("Poison Sting", 40, 0.20);
	}

	@Override
	protected void performAbility(Stoner pet, Entity target) {
		pet.getUpdateFlags().sendForceMessage("*stings venomously*");

		if (target != null && !target.isDead()) {
			target.getUpdateFlags().sendGraphic(new Graphic(172, true));

			target.poison(15);

			int damage = 5 + (int)(Math.random() * 10);
			Hit hit = new Hit(pet, damage, Hit.HitTypes.POISON);
			target.hit(hit);
		}
	}
}
