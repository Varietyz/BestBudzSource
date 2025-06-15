package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class SaradominHealAbility extends PetAbility {

	public SaradominHealAbility() {
		super("Saradomin Blessing", 100, 0.10);
	}

	@Override
	protected void performAbility(Stoner pet, Entity target) {
		pet.getUpdateFlags().sendAnimation(new Animation(6967));
		pet.getUpdateFlags().sendGraphic(new Graphic(1046, true));
		pet.getUpdateFlags().sendForceMessage("*glows with divine light*");

		// Heal the owner using existing grade system
		Stoner owner = (Stoner) pet.getAttributes().get("PET_OWNER");
		if (owner != null && !owner.isDead()) {
			long healAmount = 10 + (long)(Math.random() * 20);
			long newHP = Math.min(owner.getGrades()[3] + healAmount, owner.getMaxGrades()[3]);
			owner.getGrades()[3] = newHP;

			owner.getUpdateFlags().sendGraphic(new Graphic(436, true));
			owner.getUpdateFlags().sendForceMessage("*feels blessed*");
		}
	}
}