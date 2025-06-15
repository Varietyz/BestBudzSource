package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class TeleportAbility extends PetAbility {

	public TeleportAbility() {
		super("Imp Teleport", 60, 0.08);
	}

	@Override
	protected void performAbility(Stoner pet, Entity target) {
		pet.getUpdateFlags().sendAnimation(new Animation(1816));
		pet.getUpdateFlags().sendGraphic(new Graphic(343, true));
		pet.getUpdateFlags().sendForceMessage("*bamf!*");

		if (target != null && !target.isDead()) {
			// Use existing teleport method
			Location behindTarget = new Location(
				target.getLocation().getX() - 1,
				target.getLocation().getY() - 1,
				target.getLocation().getZ()
			);

			pet.teleport(behindTarget);
			pet.face(target);

			// Set flag for next attack using existing attribute system
			pet.getAttributes().set("GUARANTEED_HIT", System.currentTimeMillis() + 5000);
		}
	}
}