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

			int randomX = target.getLocation().getX() + (int)(Math.random() * 6) - 3;
			int randomY = target.getLocation().getY() + (int)(Math.random() * 6) - 3;

			target.getUpdateFlags().sendGraphic(new Graphic(343, true));

			int damage = 10 + (int)(Math.random() * 15);
			Hit hit = new Hit(pet, damage, Hit.HitTypes.MAGE);
			target.hit(hit);
		}
	}
}
