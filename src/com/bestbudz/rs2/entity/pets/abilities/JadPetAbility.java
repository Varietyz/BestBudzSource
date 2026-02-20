package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.pets.PetCombatUtils;

public class JadPetAbility extends PetAbility {

	public JadPetAbility() {
		super("Jad Combat Mastery", 30, 0.25);
	}

	@Override
	protected void performAbility(Stoner pet, Entity target) {
		if (target == null || target.isDead()) {
			return;
		}

		CombatTypes combatType = getCurrentCombatType(pet);

		switch (combatType) {
			case MELEE:
				performMeleeAttack(pet, target);
				break;
			case SAGITTARIUS:
				performRangedAttack(pet, target);
				break;
			case MAGE:
				performMagicAttack(pet, target);
				break;
			default:
				performMeleeAttack(pet, target);
				break;
		}
	}

	private void performMeleeAttack(Stoner pet, Entity target) {

		java.util.List<Entity> nearbyNpcs = PetCombatUtils.getHostileNpcsInRange(pet, 3);
		for (Entity entity : nearbyNpcs) {

			int deltaX = Math.abs(entity.getLocation().getX() - target.getLocation().getX());
			int deltaY = Math.abs(entity.getLocation().getY() - target.getLocation().getY());
			int distanceToTarget = Math.max(deltaX, deltaY);

			if (distanceToTarget <= 1) {
				int damage = 20 + (int)(Math.random() * 25);
				Hit hit = new Hit(pet, damage, Hit.HitTypes.MELEE);
				entity.hit(hit);
			}
		}
	}

	private void performRangedAttack(Stoner pet, Entity target) {

		int mainDamage = 25 + (int)(Math.random() * 20);
		Hit mainHit = new Hit(pet, mainDamage, Hit.HitTypes.SAGITTARIUS);
		target.hit(mainHit);

		java.util.List<Entity> nearbyNpcs = PetCombatUtils.getHostileNpcsInRange(pet, 2);
		for (Entity npc : nearbyNpcs) {

			int deltaX = Math.abs(npc.getLocation().getX() - target.getLocation().getX());
			int deltaY = Math.abs(npc.getLocation().getY() - target.getLocation().getY());
			int distanceToTarget = Math.max(deltaX, deltaY);

			if (distanceToTarget <= 2 && npc != target) {
				int splashDamage = 10 + (int)(Math.random() * 15);
				Hit splashHit = new Hit(pet, splashDamage, Hit.HitTypes.SAGITTARIUS);
				npc.hit(splashHit);
			}
		}
	}

	private void performMagicAttack(Stoner pet, Entity target) {

		int damage = 18 + (int)(Math.random() * 22);
		Hit hit = new Hit(pet, damage, Hit.HitTypes.MAGE);
		target.hit(hit);

		applyBurnEffect(target);

		java.util.List<Entity> nearbyNpcs = PetCombatUtils.getHostileNpcsInRange(pet, 4);
		for (Entity npc : nearbyNpcs) {

			int deltaX = Math.abs(npc.getLocation().getX() - target.getLocation().getX());
			int deltaY = Math.abs(npc.getLocation().getY() - target.getLocation().getY());
			int distanceToTarget = Math.max(deltaX, deltaY);

			if (distanceToTarget <= 2 && npc != target) {
				int aoeDamage = 12 + (int)(Math.random() * 18);
				Hit aoeHit = new Hit(pet, aoeDamage, Hit.HitTypes.MAGE);
				npc.hit(aoeHit);

				applyBurnEffect(npc);
			}
		}

		if (Math.random() < 0.3) {
			long healAmount = 5 + (long)(Math.random() * 10);
			long newHP = Math.min(pet.getGrades()[3] + healAmount, pet.getMaxGrades()[3]);
			pet.getGrades()[3] = newHP;
		}
	}

	private void applyBurnEffect(Entity target) {

		target.getAttributes().set("JAD_BURN_DAMAGE", 5);
		target.getAttributes().set("JAD_BURN_TICKS", 5);
		target.getAttributes().set("JAD_BURN_START", System.currentTimeMillis());
	}

	private CombatTypes getCurrentCombatType(Stoner pet) {

		return pet.getCombat().getCombatType();
	}

	@Override
	public boolean canActivate(Stoner pet) {

		return super.canActivate(pet);
	}

	@Override
	public void onDealDamage(Stoner pet, Entity target, long damage) {

		Integer burnDamage = (Integer) target.getAttributes().get("JAD_BURN_DAMAGE");
		Integer burnTicks = (Integer) target.getAttributes().get("JAD_BURN_TICKS");
		Long burnStart = (Long) target.getAttributes().get("JAD_BURN_START");

		if (burnDamage != null && burnTicks != null && burnStart != null) {
			long timePassed = System.currentTimeMillis() - burnStart;
			int ticksPassed = (int)(timePassed / 600);

			if (ticksPassed > 0 && burnTicks > 0) {

				Hit burnHit = new Hit(pet, burnDamage, Hit.HitTypes.POISON);
				target.hit(burnHit);

				int newBurnTicks = burnTicks - ticksPassed;
				if (newBurnTicks <= 0) {

					target.getAttributes().remove("JAD_BURN_DAMAGE");
					target.getAttributes().remove("JAD_BURN_TICKS");
					target.getAttributes().remove("JAD_BURN_START");
				} else {

					target.getAttributes().set("JAD_BURN_TICKS", newBurnTicks);
					target.getAttributes().set("JAD_BURN_START", System.currentTimeMillis());
				}
			}
		}
	}
}
