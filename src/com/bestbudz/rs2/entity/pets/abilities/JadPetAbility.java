package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.pets.PetCombatUtils;

public class JadPetAbility extends PetAbility {

	public JadPetAbility() {
		super("Jad Combat Mastery", 30, 0.25); // 25% chance, 30 tick cooldown
	}

	@Override
	protected void performAbility(Stoner pet, Entity target) {
		if (target == null || target.isDead()) {
			return;
		}

		// Get the combat type the pet is currently using
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
				performMeleeAttack(pet, target); // Default to melee
				break;
		}
	}

	/**
	 * Jad's signature melee attack - Crushing Stomp
	 */
	private void performMeleeAttack(Stoner pet, Entity target) {
		// Area stomp damage around the target
		java.util.List<Entity> nearbyNpcs = PetCombatUtils.getHostileNpcsInRange(pet, 3);
		for (Entity entity : nearbyNpcs) {
			// Check if this NPC is near the target (within stomp range)
			int deltaX = Math.abs(entity.getLocation().getX() - target.getLocation().getX());
			int deltaY = Math.abs(entity.getLocation().getY() - target.getLocation().getY());
			int distanceToTarget = Math.max(deltaX, deltaY);

			if (distanceToTarget <= 1) { // Within stomp range of target
				int damage = 20 + (int)(Math.random() * 25); // High melee damage
				Hit hit = new Hit(pet, damage, Hit.HitTypes.MELEE);
				entity.hit(hit);
			}
		}
	}

	/**
	 * Jad's signature ranged attack - Explosive Boulder
	 */
	private void performRangedAttack(Stoner pet, Entity target) {
		// High single target damage with area effect
		int mainDamage = 25 + (int)(Math.random() * 20);
		Hit mainHit = new Hit(pet, mainDamage, Hit.HitTypes.SAGITTARIUS);
		target.hit(mainHit);

		// Splash damage around the target location
		java.util.List<Entity> nearbyNpcs = PetCombatUtils.getHostileNpcsInRange(pet, 2);
		for (Entity npc : nearbyNpcs) {
			// Check if this NPC is near the target (within splash range)
			int deltaX = Math.abs(npc.getLocation().getX() - target.getLocation().getX());
			int deltaY = Math.abs(npc.getLocation().getY() - target.getLocation().getY());
			int distanceToTarget = Math.max(deltaX, deltaY);

			if (distanceToTarget <= 2 && npc != target) { // Within splash range and not main target
				int splashDamage = 10 + (int)(Math.random() * 15);
				Hit splashHit = new Hit(pet, splashDamage, Hit.HitTypes.SAGITTARIUS);
				npc.hit(splashHit);
			}
		}
	}

	/**
	 * Jad's signature magic attack - Inferno Blast (now with area effect)
	 */
	private void performMagicAttack(Stoner pet, Entity target) {
		// Magic damage to primary target
		int damage = 18 + (int)(Math.random() * 22);
		Hit hit = new Hit(pet, damage, Hit.HitTypes.MAGE);
		target.hit(hit);

		// Apply burn effect to primary target
		applyBurnEffect(target);

		// Area magic damage to adjacent enemies (2 tile range)
		java.util.List<Entity> nearbyNpcs = PetCombatUtils.getHostileNpcsInRange(pet, 4);
		for (Entity npc : nearbyNpcs) {
			// Check if this NPC is adjacent to the target (within 2 tiles)
			int deltaX = Math.abs(npc.getLocation().getX() - target.getLocation().getX());
			int deltaY = Math.abs(npc.getLocation().getY() - target.getLocation().getY());
			int distanceToTarget = Math.max(deltaX, deltaY);

			if (distanceToTarget <= 2 && npc != target) { // Within 2 tiles and not main target
				int aoeDamage = 12 + (int)(Math.random() * 18);
				Hit aoeHit = new Hit(pet, aoeDamage, Hit.HitTypes.MAGE);
				npc.hit(aoeHit);

				// Apply burn effect to adjacent targets too
				applyBurnEffect(npc);
			}
		}

		// Magic has a chance to heal the pet slightly
		if (Math.random() < 0.3) { // 30% chance
			long healAmount = 5 + (long)(Math.random() * 10);
			long newHP = Math.min(pet.getGrades()[3] + healAmount, pet.getMaxGrades()[3]);
			pet.getGrades()[3] = newHP;
		}
	}

	/**
	 * Apply burn effect to target
	 */
	private void applyBurnEffect(Entity target) {
		// Set burn attribute for damage over time
		target.getAttributes().set("JAD_BURN_DAMAGE", 5);
		target.getAttributes().set("JAD_BURN_TICKS", 5); // 5 ticks of burn
		target.getAttributes().set("JAD_BURN_START", System.currentTimeMillis());
	}

	/**
	 * Get the current combat type the pet is using
	 */
	private CombatTypes getCurrentCombatType(Stoner pet) {
		// Use the pet's combat system to determine current combat type
		return pet.getCombat().getCombatType();
	}

	@Override
	public boolean canActivate(Stoner pet) {
		// Always check if we can activate, but still respect cooldown and chance
		return super.canActivate(pet);
	}

	/**
	 * Override to handle burn damage over time
	 */
	@Override
	public void onDealDamage(Stoner pet, Entity target, long damage) {
		// Process burn damage if target has burn effect
		Integer burnDamage = (Integer) target.getAttributes().get("JAD_BURN_DAMAGE");
		Integer burnTicks = (Integer) target.getAttributes().get("JAD_BURN_TICKS");
		Long burnStart = (Long) target.getAttributes().get("JAD_BURN_START");

		if (burnDamage != null && burnTicks != null && burnStart != null) {
			long timePassed = System.currentTimeMillis() - burnStart;
			int ticksPassed = (int)(timePassed / 600); // 600ms per tick

			if (ticksPassed > 0 && burnTicks > 0) {
				// Apply burn damage using proper Hit system
				Hit burnHit = new Hit(pet, burnDamage, Hit.HitTypes.POISON); // Use poison type for DoT
				target.hit(burnHit);

				// Reduce burn ticks
				int newBurnTicks = burnTicks - ticksPassed;
				if (newBurnTicks <= 0) {
					// Remove burn effect
					target.getAttributes().remove("JAD_BURN_DAMAGE");
					target.getAttributes().remove("JAD_BURN_TICKS");
					target.getAttributes().remove("JAD_BURN_START");
				} else {
					// Update burn state
					target.getAttributes().set("JAD_BURN_TICKS", newBurnTicks);
					target.getAttributes().set("JAD_BURN_START", System.currentTimeMillis());
				}
			}
		}
	}
}