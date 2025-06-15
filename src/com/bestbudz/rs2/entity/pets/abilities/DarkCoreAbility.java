package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class DarkCoreAbility extends PetAbility {

	public DarkCoreAbility() {
		super("Dark Absorption", 90, 0.08);
	}

	@Override
	protected void performAbility(Stoner pet, Entity target) {
		pet.getUpdateFlags().sendAnimation(new Animation(827));
		pet.getUpdateFlags().sendGraphic(new Graphic(86, true));
		pet.getUpdateFlags().sendForceMessage("*absorbs darkness*");

		if (target != null && !target.isDead()) {
			// Drain target's health and heal pet
			int drainAmount = 8 + (int)(Math.random() * 12);
			target.getGrades()[3] = Math.max(0, target.getGrades()[3] - drainAmount);
			target.getUpdateFlags().sendHit(drainAmount, (byte)0, (byte)0);

			// Heal pet
			long newHP = Math.min(pet.getGrades()[3] + drainAmount/2, pet.getMaxGrades()[3]);
			pet.getGrades()[3] = newHP;
			pet.getUpdateFlags().sendGraphic(new Graphic(436, true));
		}
	}
}