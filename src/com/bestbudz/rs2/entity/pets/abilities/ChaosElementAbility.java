package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class ChaosElementAbility extends PetAbility {

	public ChaosElementAbility() {
		super("Chaos Strike", 65, 0.18);
	}

	@Override
	protected void performAbility(Stoner pet, Entity target) {
		pet.getUpdateFlags().sendAnimation(new Animation(3153));
		pet.getUpdateFlags().sendForceMessage("*channels chaos*");

		if (target != null && !target.isDead()) {
			// Random teleport target (chaos element special)
			int randomX = target.getLocation().getX() + (int)(Math.random() * 6) - 3;
			int randomY = target.getLocation().getY() + (int)(Math.random() * 6) - 3;
			//target.teleport(new com.bestbudz.rs2.entity.Location(randomX, randomY, target.getLocation().getZ()));

			target.getUpdateFlags().sendGraphic(new Graphic(343, true));

			// Deal damage using proper Hit system
			int damage = 10 + (int)(Math.random() * 15);
			Hit hit = new Hit(pet, damage, Hit.HitTypes.MAGE); // Chaos is magical
			target.hit(hit);
		}
	}
}