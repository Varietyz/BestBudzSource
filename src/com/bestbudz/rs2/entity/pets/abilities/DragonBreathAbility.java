package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.pets.PetCombatUtils;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class DragonBreathAbility extends PetAbility {

	public DragonBreathAbility() {
		super("Dragon Breath", 75, 0.12);
	}

	@Override
	protected void performAbility(Stoner pet, Entity target) {
		pet.getUpdateFlags().sendAnimation(new Animation(81));
		pet.getUpdateFlags().sendForceMessage("*breathes fire*");

		// Area damage using existing combat system
		java.util.List<Entity> nearbyNpcs = PetCombatUtils.getHostileNpcsInRange(pet, 2);
		for (Entity entity : nearbyNpcs) {
			entity.getUpdateFlags().sendGraphic(new Graphic(444, true));
			int damage = 8 + (int)(Math.random() * 12);
			Hit hit = new Hit(pet, damage, Hit.HitTypes.MAGE); // Dragon breath is magical fire
			entity.hit(hit);
		}
	}
}