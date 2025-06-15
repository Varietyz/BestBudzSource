package com.bestbudz.rs2.entity.pets;

import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.pets.abilities.PetAbilityRegistry;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.pets.abilities.PetAbility;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.pathfinding.StraightPathFinder;
import com.bestbudz.rs2.entity.Location;

/**
 * FIXED: Pet combat system that works within existing infrastructure
 * Now properly handles combat engagement like AutoCombat does
 */
public class PetCombat {

	public static void initializePetCombat(Stoner pet, PetData petData, Stoner owner) {
		// Store references for combat system
		pet.getAttributes().set("PET_OWNER", owner);
		pet.getAttributes().set("PET_DATA", petData);

		// Get and store pet ability if it has one
		PetAbility ability = PetAbilityRegistry.getAbility(petData);
		if (ability != null) {
			pet.getAttributes().set("PET_ABILITY", ability);
		}

		// CRITICAL FIX: Enable combat settings for pets
		pet.setRetaliate(true);

		// IMPORTANT: Keep pets as NPCs but enable player-like combat
		pet.setNpc(true); // They need NPC animations!

		// ENHANCED: Set proper combat type for pets (defaulting to melee)
		pet.getCombat().setCombatType(Combat.CombatTypes.MELEE);
		pet.updateCombatType();

		// Set up combat capabilities
		setupPetCombatCapabilities(pet, petData);

		System.out.println("DEBUG: Pet combat initialized for " + pet.getUsername());
		System.out.println("  Combat type: " + pet.getCombat().getCombatType());
		System.out.println("  Retaliate: " + pet.isRetaliate());
		System.out.println("  Is NPC: " + pet.isNpc()); // Should be true
	}

	/**
	 * NEW: Setup pet combat capabilities - ensures pets can actually deal damage
	 */
	private static void setupPetCombatCapabilities(Stoner pet, PetData petData) {
		// Ensure pet has a basic "weapon" for damage calculation
		// This is critical - without equipment data, damage calculations fail

		// Set up basic melee equipment stats for damage calculation
		pet.getAttributes().set("PET_BASE_DAMAGE", getPetBaseDamage(petData));
		pet.getAttributes().set("PET_ATTACK_SPEED", 4); // Standard attack speed

		// Override combat interface methods if needed
		pet.getAttributes().set("PET_COMBAT_READY", true);

		System.out.println("DEBUG: Pet capabilities set - Base damage: " + getPetBaseDamage(petData));
	}

	/**
	 * NEW: Get base damage for pet based on its type
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
	 * FIXED: Process pet combat AI - called during pet processing
	 * Now uses proper combat engagement like AutoCombat
	 */
	public static void processPetCombat(Pet pet) {
		Stoner petStoner = pet.getPetStoner();
		Stoner owner = pet.getOwner();

		if (petStoner == null || owner == null || !petStoner.isActive()) {
			return;
		}

		// CRITICAL FIX: Handle combat like AutoCombat does
		handlePetCombatLogic(petStoner, owner);

		// Process pet abilities if in combat
		if (petStoner.getCombat().inCombat()) {
			processPetAbilities(petStoner);
		}
	}

	/**
	 * NEW: Handle pet combat logic similar to AutoCombat
	 */
	private static void handlePetCombatLogic(Stoner pet, Stoner owner) {
		// Check if pet is being attacked but not fighting back
		if (pet.getCombat().inCombat()) {
			Entity currentTarget = pet.getCombat().getAssaulting();

			// Case 1: Pet is being attacked but has no target
			if (currentTarget == null) {
				Mob attacker = findPetAttacker(pet);
				if (attacker != null && isPetTargetReachable(pet, attacker)) {
					engagePetTargetWithPathfinding(pet, attacker);
					return;
				}
			}
			// Case 2: Pet has target but it's invalid/unreachable
			else if (currentTarget.isDead() || !currentTarget.isActive() ||
				(currentTarget.isNpc() && (!isPetTargetReachable(pet, (Mob)currentTarget) ||
					getPetDistanceToTarget(pet, (Mob)currentTarget) > 8))) {
				Mob attacker = findPetAttacker(pet);
				if (attacker != null && isPetTargetReachable(pet, attacker)) {
					engagePetTargetWithPathfinding(pet, attacker);
					return;
				}
			}
		}

		// Check if owner needs assistance (original logic)
		if (shouldAssistOwner(pet, owner)) {
			assistOwner(pet, owner);
		}
	}

	/**
	 * NEW: Find the NPC that is attacking the pet (like AutoCombat does)
	 */
	private static Mob findPetAttacker(Stoner pet) {
		// Check nearby NPCs to see which one is attacking the pet
		for (Mob npc : World.getNpcs()) {
			if (npc == null || npc.isDead() || !npc.isActive()) continue;

			// Check if this NPC is targeting the pet
			if (npc.getCombat().inCombat() && npc.getCombat().getAssaulting() == pet) {
				return npc;
			}
		}
		return null;
	}

	/**
	 * NEW: Check if pet target is reachable using pathfinding (like AutoCombat)
	 */
	private static boolean isPetTargetReachable(Stoner pet, Mob target) {
		if (target == null || target.getLocation() == null) {
			return false;
		}

		try {
			Location petLoc = pet.getLocation();
			Location targetLoc = target.getLocation();

			// Quick distance check first
			int distance = getPetDistanceToTarget(pet, target);
			if (distance > 8) {
				return false;
			}

			// Use pathfinding logic like AutoCombat does
			boolean pathClear = false;

			// Get edges of pet location
			Location[] petEdges = com.bestbudz.rs2.GameConstants.getEdges(
				petLoc.getX(), petLoc.getY(), pet.getSize());

			// Check if any edge of the pet can reach the target
			for (Location edge : petEdges) {
				if (StraightPathFinder.isInteractionPathClear(edge, targetLoc)) {
					pathClear = true;
					break;
				}
				if (StraightPathFinder.isInteractionPathClear(targetLoc, edge)) {
					pathClear = true;
					break;
				}
			}

			return pathClear;

		} catch (Exception e) {
			System.out.println("Error checking pet target reachability: " + e.getMessage());
			return false;
		}
	}

	/**
	 * NEW: Get distance between pet and target (like AutoCombat)
	 */
	private static int getPetDistanceToTarget(Stoner pet, Mob target) {
		int deltaX = Math.abs(pet.getLocation().getX() - target.getLocation().getX());
		int deltaY = Math.abs(pet.getLocation().getY() - target.getLocation().getY());
		return Math.max(deltaX, deltaY); // Chebyshev distance
	}

	/**
	 * NEW: Engage target with pathfinding (like AutoCombat CombatHandler)
	 */
	private static void engagePetTargetWithPathfinding(Stoner pet, Mob target) {
		if (target == null) return;

		try {
			// Calculate distance to target
			int distance = getPetDistanceToTarget(pet, target);

			// If target is not adjacent, use pathfinding to get closer
			if (distance > 1) {
				// Use RS317PathFinder to navigate towards the target
				com.bestbudz.rs2.entity.pathfinding.RS317PathFinder.findRoute(
					pet,
					target.getLocation().getX(),
					target.getLocation().getY(),
					true,  // run if possible
					1,     // size x
					1      // size y
				);

				// Use following system to keep tracking the target
				pet.getFollowing().setFollow(target, com.bestbudz.rs2.entity.following.Following.FollowType.COMBAT);
			}

			// CRITICAL FIX: Set the combat target properly
			pet.getCombat().setAssault(target);

			System.out.println("DEBUG: Pet " + pet.getUsername() + " engaged combat with NPC " + target.getId());

		} catch (Exception e) {
			System.out.println("Error in pet combat engagement: " + e.getMessage());
			// Fallback: just set the assault target
			pet.getCombat().setAssault(target);
		}
	}

	/**
	 * ENHANCED: Check if pet should assist owner (improved logic)
	 */
	private static boolean shouldAssistOwner(Stoner pet, Stoner owner) {
		// Pet already in combat with someone else
		if (pet.getCombat().inCombat()) {
			Entity petTarget = pet.getCombat().getAssaulting();
			// Only skip assist if pet is actively fighting a valid target
			if (petTarget != null && !petTarget.isDead() && petTarget.isActive()) {
				return false;
			}
		}

		// Owner not in combat
		if (!owner.getCombat().inCombat()) {
			return false;
		}

		// Check if owner is being attacked by NPC
		Entity ownerAssaulting = owner.getCombat().getAssaulting();
		if (ownerAssaulting != null && ownerAssaulting.isNpc()) {
			// Check distance to owner
			int distance = getPetDistanceToTarget(pet, owner);
			if (distance <= 8) {
				// Check if target is reachable
				return isPetTargetReachable(pet, (Mob)ownerAssaulting);
			}
		}

		return false;
	}

	/**
	 * ENHANCED: Make pet assist owner using proper combat engagement
	 */
	private static void assistOwner(Stoner pet, Stoner owner) {
		Entity target = owner.getCombat().getAssaulting();
		if (target != null && target.isNpc()) {
			// Use the new engagement method with pathfinding
			engagePetTargetWithPathfinding(pet, (Mob)target);
		}
	}

	/**
	 * NEW: Get distance between pet and owner
	 */
	private static int getPetDistanceToTarget(Stoner pet, Stoner target) {
		int deltaX = Math.abs(pet.getLocation().getX() - target.getLocation().getX());
		int deltaY = Math.abs(pet.getLocation().getY() - target.getLocation().getY());
		return Math.max(deltaX, deltaY);
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
	 * Handle pet taking damage - can be called from existing hit processing
	 */
	public static void onPetTakeDamage(Stoner pet, int damage) {
		PetAbility ability = (PetAbility) pet.getAttributes().get("PET_ABILITY");
		if (ability != null) {
			ability.onTakeDamage(pet, damage);
		}

		// CRITICAL FIX: Ensure pet retaliates when taking damage
		if (pet.isRetaliate() && !pet.getCombat().inCombat()) {
			// Find who attacked the pet and fight back
			Mob attacker = findPetAttacker(pet);
			if (attacker != null && isPetTargetReachable(pet, attacker)) {
				engagePetTargetWithPathfinding(pet, attacker);
			}
		}
	}

	/**
	 * Handle pet dealing damage - can be called from existing combat processing
	 */
	public static void onPetDealDamage(Stoner pet, Entity target, int damage) {
		PetAbility ability = (PetAbility) pet.getAttributes().get("PET_ABILITY");
		if (ability != null) {
			ability.onDealDamage(pet, target, damage);
		}

		// Debug output for damage dealing
		System.out.println("DEBUG: Pet " + pet.getUsername() + " dealt " + damage + " damage to " +
			(target.isNpc() ? "NPC" : "Player"));
	}

	/**
	 * Apply temporary combat bonuses using existing attribute system
	 */
	public static void applyTemporaryBonus(Stoner pet, String bonusType, int amount, long durationMs) {
		pet.getAttributes().set("TEMP_" + bonusType, amount);
		pet.getAttributes().set("TEMP_" + bonusType + "_EXPIRE", System.currentTimeMillis() + durationMs);
	}

	/**
	 * Get temporary bonus if still active
	 */
	public static int getTemporaryBonus(Stoner pet, String bonusType) {
		Long expireTime = (Long) pet.getAttributes().get("TEMP_" + bonusType + "_EXPIRE");
		if (expireTime != null && System.currentTimeMillis() < expireTime) {
			Integer bonus = (Integer) pet.getAttributes().get("TEMP_" + bonusType);
			return bonus != null ? bonus : 0;
		} else {
			// Clean up expired bonuses
			pet.getAttributes().remove("TEMP_" + bonusType);
			pet.getAttributes().remove("TEMP_" + bonusType + "_EXPIRE");
			return 0;
		}
	}

	/**
	 * Check if pet can use special abilities based on owner's skills
	 */
	public static boolean canUseAbilities(Stoner pet) {
		Stoner owner = (Stoner) pet.getAttributes().get("PET_OWNER");
		if (owner == null) {
			return false;
		}

		// Could check summoning level or other requirements here
		// For now, all pets can use abilities
		return true;
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
			int distance = getPetDistanceToTarget(pet, npc);

			if (distance <= range && isPetTargetReachable(pet, npc)) {
				hostileNpcs.add(npc);
			}
		}

		return hostileNpcs;
	}
}