package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.pets.PetCombat;

public class ExplosiveAbility extends PetAbility {

	public ExplosiveAbility() {
		super("Chinchompa Explosion", 80, 0.15);
	}

	@Override
	protected void performAbility(Stoner pet, Entity target) {
		pet.getUpdateFlags().sendAnimation(new Animation(952));
		pet.getUpdateFlags().sendForceMessage("*BOOM!*");

		// Area explosion using existing systems
		java.util.List<Entity> nearbyNpcs = PetCombat.getHostileNpcsInRange(pet, 1);
		for (Entity entity : nearbyNpcs) {
			entity.getUpdateFlags().sendGraphic(new Graphic(157, true));
			int damage = 12 + (int)(Math.random() * 18);
			entity.getGrades()[3] = Math.max(0, entity.getGrades()[3] - damage);
			entity.getUpdateFlags().sendHit(damage, (byte)0, (byte)0);
		}

		// Pet takes self damage
		int selfDamage = 3 + (int)(Math.random() * 5);
		pet.getGrades()[3] = Math.max(1, pet.getGrades()[3] - selfDamage);
		pet.getUpdateFlags().sendHit(selfDamage, (byte)0, (byte)0);
	}
}