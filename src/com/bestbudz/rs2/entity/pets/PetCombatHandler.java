package com.bestbudz.rs2.entity.pets;

import com.bestbudz.core.definitions.NpcCombatDefinition;
import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import static com.bestbudz.rs2.entity.pets.PetCombatSystem.findPetObjectFromStoner;
import com.bestbudz.rs2.entity.pets.abilities.PetAbility;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Pet combat handler - ONLY for initialization and utility methods
 * Active combat style selection moved to PetCombatSystem
 */
public class PetCombatHandler {

	/**
	 * Get pet's max hit for current combat type
	 * Called from StonerCombatInterface.getMaxHit()
	 */
	public static int getPetMaxHit(Stoner pet, CombatTypes type) {
		// Check for current max hit (set during combat setup)
		Integer currentMaxHit = (Integer) pet.getAttributes().get("PET_CURRENT_MAX_HIT");
		if (currentMaxHit != null) {
			return currentMaxHit;
		}

		// Final fallback based on pet base damage
		Integer baseDamage = (Integer) pet.getAttributes().get("PET_BASE_DAMAGE");
		return baseDamage != null ? baseDamage / 4 : 25; // Conservative estimate
	}

	/**
	 * Enable pet to use AutoCombat system
	 */
	public static boolean shouldPetUseAutoCombat(Stoner pet) {
		// Pets should NOT use AutoCombat - they use PetCombatSystem
		return false;
	}

	/**
	 * Called when pet deals damage - trigger ability events
	 */
	public static void onPetDealDamage(Stoner pet, Entity target, long damage) {
		PetAbility ability = (PetAbility) pet.getAttributes().get("PET_ABILITY");
		if (ability != null) {
			ability.onDealDamage(pet, target, damage);
		}

		// ===== FIXED: Find Pet object from pet stoner =====
		Pet petObject = findPetObjectFromStoner(pet);
		if (petObject != null) {
			processPetDamage(petObject, (int)damage);
		}
	}

	/**
	 * Called when pet takes damage - trigger defensive abilities
	 */
	public static void onPetTakeDamage(Stoner pet, long damage) {
		PetAbility ability = (PetAbility) pet.getAttributes().get("PET_ABILITY");
		if (ability != null) {
			ability.onTakeDamage(pet, damage);
		}
	}

	public static void processPetDamage(Pet pet, int damage) {
		// Notify PetMaster of combat activity
		if (pet.getOwner() != null && pet.getOwner().getPetMaster() != null) {
			pet.getOwner().getPetMaster().onPetCombat(pet);
		}
	}
}