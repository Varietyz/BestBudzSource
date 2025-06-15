package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class PoisonStingAbility extends PetAbility {

	public PoisonStingAbility() {
		super("Poison Sting", 40, 0.20);
	}

	@Override
	protected void performAbility(Stoner pet, Entity target) {
		pet.getUpdateFlags().sendAnimation(new Animation(6260));
		pet.getUpdateFlags().sendForceMessage("*stings venomously*");

		if (target != null && !target.isDead()) {
			target.getUpdateFlags().sendGraphic(new Graphic(172, true));

			// Use existing poison system
			target.poison(15);

			// Immediate damage
			int damage = 5 + (int)(Math.random() * 10);
			target.getGrades()[3] = Math.max(0, target.getGrades()[3] - damage);
			target.getUpdateFlags().sendHit(damage, (byte)0, (byte)0);
		}
	}
}