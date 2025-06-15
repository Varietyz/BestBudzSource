package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Base class for pet special abilities
 */
public abstract class PetAbility {

	protected final String name;
	protected final int cooldownTicks;
	protected final double activationChance;

	public PetAbility(String name, int cooldownTicks, double activationChance) {
		this.name = name;
		this.cooldownTicks = cooldownTicks;
		this.activationChance = activationChance;
	}

	/**
	 * Check if the ability can be activated
	 */
	public boolean canActivate(Stoner pet) {
		// Check cooldown
		Long lastUsed = (Long) pet.getAttributes().get("ABILITY_COOLDOWN_" + name);
		if (lastUsed != null) {
			long ticksPassed = System.currentTimeMillis() - lastUsed;
			if (ticksPassed < cooldownTicks * 600) { // 600ms per tick
				return false;
			}
		}

		// Check activation chance
		return Math.random() < activationChance;
	}

	/**
	 * Execute the ability
	 */
	public final void execute(Stoner pet, Entity target) {
		if (!canActivate(pet)) {
			return;
		}

		// Set cooldown
		pet.getAttributes().set("ABILITY_COOLDOWN_" + name, System.currentTimeMillis());

		// Execute the actual ability
		performAbility(pet, target);
	}

	/**
	 * Override this method to implement the actual ability effect
	 */
	protected abstract void performAbility(Stoner pet, Entity target);

	/**
	 * Called when the pet takes damage - override for defensive abilities
	 */
	public void onTakeDamage(Stoner pet, int damage) {
		// Default: do nothing
	}

	/**
	 * Called when the pet deals damage - override for offensive triggers
	 */
	public void onDealDamage(Stoner pet, Entity target, int damage) {
		// Default: do nothing
	}

	public String getName() {
		return name;
	}

	public int getCooldownTicks() {
		return cooldownTicks;
	}

	public double getActivationChance() {
		return activationChance;
	}
}