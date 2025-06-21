package com.bestbudz.rs2.entity.pets;

import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.pets.abilities.PetAbility;

/**
 * Pet combat system - handles pet initialization, capabilities, and abilities
 * Combat targeting/AI removed - pets now use standard Stoner combat flow
 */
public class PetCombatUtils
{

	/**
	 * Initialize pet combat capabilities and store NPC combat data
	 */
	public static void initializePetCombat(Stoner pet, PetData petData, Stoner owner) {
		// Store references for combat system
		pet.getAttributes().set("PET_OWNER", owner);
		pet.getAttributes().set("PET_DATA", petData);

		PetAbility ability = PetAbilityRegistry.getAbility(petData);
		if (ability != null) {
			pet.getAttributes().set("PET_ABILITY", ability);
		}

		setupPetCombatCapabilities(pet, petData);
		processPetAbilities(pet);
	}

	/**
	 * Setup pet combat capabilities - base damage and attack speed
	 */
	private static void setupPetCombatCapabilities(Stoner pet, PetData petData) {
		// Set up basic combat stats for damage calculation
		pet.getAttributes().set("PET_BASE_DAMAGE", getPetBaseDamage(petData));
		pet.getAttributes().set("PET_ATTACK_SPEED", 4); // Standard attack speed
		pet.getAttributes().set("PET_COMBAT_READY", true);

		System.out.println("DEBUG: Pet capabilities set - Base damage: " + getPetBaseDamage(petData));
	}

	/**
	 * Get base damage for pet based on its type
	 */
	private static int getPetBaseDamage(PetData petData) {
		// Scale damage based on pet type - boss pets do more damage
		switch (petData) {
			case PRINCE_BLACK_DRAGON:
			case GENERAL_GRAARDOR:
			case COMMANDER_ZILYANA:
			case KREE_ARRA:
			case KRIL_TSUTSAROTH:
				return 200; // Boss pets - high damage

			case KALPHITE_PRINCESS_FLY:
			case KALPHITE_PRINCESS_BUG:
			case DARK_CORE:
			case CHAOS_ELEMENT:
			case SCORPIAS_OFFSPRING:
				return 150; // Mid-tier boss pets

			case BABY_DRAGON:
			case BLACK_CHINCHOMPA:
				return 100; // Medium pets

			case IMP:
			case GREEN_SNAKELING:
			case RED_SNAKELING:
			case BLUE_SNAKELING:
				return 80; // Small pets

			default:
				return 100; // Default damage
		}
	}

	/**
	 * Utility method for distance calculation
	 */
	private static int getDistance(Entity a, Entity b) {
		int deltaX = Math.abs(a.getLocation().getX() - b.getLocation().getX());
		int deltaY = Math.abs(a.getLocation().getY() - b.getLocation().getY());
		return Math.max(deltaX, deltaY); // Chebyshev distance
	}

	/**
	 * Process pet abilities using existing attribute system
	 */
	private static void processPetAbilities(Stoner pet) {
		PetAbility ability = (PetAbility) pet.getAttributes().get("PET_ABILITY");
		if (ability != null) {
			Entity target = pet.getCombat().getAssaulting();
			if (target != null && ability.canActivate(pet)) {
				ability.execute(pet, target);
			}
		}
	}

	/**
	 * Apply temporary combat bonuses using existing attribute system
	 */
	public static void applyTemporaryBonus(Stoner pet, String bonusType, int amount, long durationMs) {
		pet.getAttributes().set("TEMP_" + bonusType, amount);
		pet.getAttributes().set("TEMP_" + bonusType + "_EXPIRE", System.currentTimeMillis() + durationMs);
	}

	/**
	 * Get all hostile NPCs in range for area abilities
	 */
	public static java.util.List<Entity> getHostileNpcsInRange(Stoner pet, int range) {
		java.util.List<Entity> hostileNpcs = new java.util.ArrayList<>();

		// Use existing World.getNpcs() to find targets
		for (com.bestbudz.rs2.entity.mob.Mob npc : World.getNpcs()) {
			if (npc == null || !npc.isActive() || npc.isDead()) {
				continue;
			}

			// Check distance
			int distance = getDistance(pet, npc);

			if (distance <= range) {
				hostileNpcs.add(npc);
			}
		}

		return hostileNpcs;
	}
}