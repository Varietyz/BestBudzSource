package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract class PetAbility {

	protected final String name;
	protected final int cooldownTicks;
	protected final double activationChance;

	public PetAbility(String name, int cooldownTicks, double activationChance) {
		this.name = name;
		this.cooldownTicks = cooldownTicks;
		this.activationChance = activationChance;
	}

	public boolean canActivate(Stoner pet) {

		Long lastUsed = (Long) pet.getAttributes().get("ABILITY_COOLDOWN_" + name);
		if (lastUsed != null) {
			long ticksPassed = System.currentTimeMillis() - lastUsed;
			if (ticksPassed < cooldownTicks * 600) {
				return false;
			}
		}

		return Math.random() < activationChance;
	}

	public final void execute(Stoner pet, Entity target) {
		if (!canActivate(pet)) {
			return;
		}

		pet.getAttributes().set("ABILITY_COOLDOWN_" + name, System.currentTimeMillis());

		performAbility(pet, target);
	}

	protected abstract void performAbility(Stoner pet, Entity target);

	public void onTakeDamage(Stoner pet, long damage) {

	}

	public void onDealDamage(Stoner pet, Entity target, long damage) {

	}

	public String getName() {
		return name;
	}

}
