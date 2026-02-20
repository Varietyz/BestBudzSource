package com.bestbudz.rs2.entity.pets;

import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Entity;
import static com.bestbudz.rs2.entity.pets.PetCombatSystem.findPetObjectFromStoner;
import com.bestbudz.rs2.entity.pets.abilities.PetAbility;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class PetCombatHandler {

	public static int getPetMaxHit(Stoner pet, CombatTypes type) {

		Integer currentMaxHit = (Integer) pet.getAttributes().get("PET_CURRENT_MAX_HIT");
		if (currentMaxHit != null) {
			return currentMaxHit;
		}

		Integer baseDamage = (Integer) pet.getAttributes().get("PET_BASE_DAMAGE");
		return baseDamage != null ? baseDamage / 4 : 25;
	}

	public static boolean shouldPetUseAutoCombat(Stoner pet) {

		return false;
	}

	public static void onPetDealDamage(Stoner pet, Entity target, long damage) {
		PetAbility ability = (PetAbility) pet.getAttributes().get("PET_ABILITY");
		if (ability != null) {
			ability.onDealDamage(pet, target, damage);
		}

		Pet petObject = findPetObjectFromStoner(pet);
		if (petObject != null) {
			processPetDamage(petObject, (int)damage);
		}
	}

	public static void onPetTakeDamage(Stoner pet, long damage) {
		PetAbility ability = (PetAbility) pet.getAttributes().get("PET_ABILITY");
		if (ability != null) {
			ability.onTakeDamage(pet, damage);
		}
	}

	public static void processPetDamage(Pet pet, int damage) {

		if (pet.getOwner() != null && pet.getOwner().getPetMaster() != null) {
			pet.getOwner().getPetMaster().onPetCombat(pet);
		}
	}
}
