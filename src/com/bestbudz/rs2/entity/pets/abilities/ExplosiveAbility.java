package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.pets.PetCombatUtils;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class ExplosiveAbility extends PetAbility {

	public ExplosiveAbility() {
		super("Chinchompa Explosion", 80, 0.15);
	}

	@Override
	protected void performAbility(Stoner pet, Entity target) {
		pet.getUpdateFlags().sendAnimation(new Animation(952));
		pet.getUpdateFlags().sendForceMessage("*BOOM!*");

		java.util.List<Entity> nearbyNpcs = PetCombatUtils.getHostileNpcsInRange(pet, 1);
		for (Entity entity : nearbyNpcs) {
			entity.getUpdateFlags().sendGraphic(new Graphic(157, true));
			int damage = 12 + (int)(Math.random() * 18);
			Hit hit = new Hit(pet, damage, Hit.HitTypes.SAGITTARIUS);
			entity.hit(hit);
		}

		int selfDamage = 3 + (int)(Math.random() * 5);
		Hit selfHit = new Hit(pet, selfDamage, Hit.HitTypes.SAGITTARIUS);
		pet.hit(selfHit);
	}
}
