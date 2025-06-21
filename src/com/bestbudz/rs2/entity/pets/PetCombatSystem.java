package com.bestbudz.rs2.entity.pets;

import com.bestbudz.core.definitions.NpcCombatDefinition;
import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.pets.abilities.PetAbility;

/**
 * Pet combat system - Pure NPC combat without AutoCombat interference
 */
public class PetCombatSystem {

	/**
	 * Process combat for a pet - called from pet's process() method
	 */
	public static void processPetCombat(Stoner pet) {
		if (!canEngageInCombat(pet)) {
			return;
		}

		// Pure NPC combat - find target using owner's NPCs
		Mob target = findTargetForPet(pet);
		if (target != null) {
			// Select optimal style based on distance
			selectAndSetPetCombatStyle(pet, target);

			// Engage target directly
			pet.getCombat().setAssault(target);

			Entity actualTarget = pet.getCombat().getAssaulting();
			if (actualTarget != null) {
				System.out.println("DEBUG: Pet " + pet.getIndex() + " actually assaulting " + actualTarget.getIndex());
			}
		}

		// Process pet abilities
		Entity currentTarget = pet.getCombat().getAssaulting();
		if (currentTarget != null && !currentTarget.isDead()) {
			processPetAbilities(pet, currentTarget);
		}
	}

	private static void processPetAbilities(Stoner pet, Entity target) {
		PetAbility ability = (PetAbility) pet.getAttributes().get("PET_ABILITY");
		if (ability != null) {
			ability.execute(pet, target);
		}
	}

	private static boolean canEngageInCombat(Stoner pet) {
		return pet.isActive() && !pet.isDead() &&
			Boolean.TRUE.equals(pet.getAttributes().get("PET_COMBAT_READY"));
	}

	private static Mob findTargetForPet(Stoner pet) {
		try {
			Stoner owner = (Stoner) pet.getAttributes().get("PET_OWNER");
			if (owner == null) return null;

			java.util.List<com.bestbudz.rs2.entity.mob.Mob> clientNpcs = owner.getClient().getNpcs();
			if (clientNpcs == null || clientNpcs.isEmpty()) return null;

			Mob closestTarget = null;
			int closestDistance = Integer.MAX_VALUE;
			Location petLocation = pet.getLocation();

			for (com.bestbudz.rs2.entity.mob.Mob npc : clientNpcs) {
				if (npc == null || npc.isDead() || !npc.isActive()) continue;
				if (npc.getLocation().getZ() != petLocation.getZ()) continue;
				if (npc.getCombatDefinition() == null) continue;
				if (npc.isWalkToHome()) continue;

				int distance = Math.max(
					Math.abs(petLocation.getX() - npc.getLocation().getX()),
					Math.abs(petLocation.getY() - npc.getLocation().getY())
				);

				if (distance <= 8 && distance < closestDistance) {
					closestDistance = distance;
					closestTarget = npc;
				}
			}

			return closestTarget;
		} catch (Exception e) {
			return null;
		}
	}

	// Pure NPC style selection and setup
	private static void selectAndSetPetCombatStyle(Stoner pet, Mob target) {
		NpcCombatDefinition combatDef = (NpcCombatDefinition) pet.getAttributes().get("PET_COMBAT_DEFINITION");
		if (combatDef == null) return;

		// ADD: Style switching cooldown (5 seconds)
		Long lastStyleChange = (Long) pet.getAttributes().get("PET_LAST_STYLE_CHANGE");
		long currentTime = System.currentTimeMillis();

		if (lastStyleChange != null && (currentTime - lastStyleChange) < 5000) {
			return; // Don't switch too frequently
		}

		try {
			int distance = Math.max(
				Math.abs(pet.getLocation().getX() - target.getLocation().getX()),
				Math.abs(pet.getLocation().getY() - target.getLocation().getY())
			);

			Combat.CombatTypes optimalStyle = selectNpcStyleForDistance(combatDef, distance);

			// Only change if different from current
			if (pet.getCombat().getCombatType() != optimalStyle) {
				pet.getCombat().setCombatType(optimalStyle);
				setupNpcCombatDataDirectly(pet, combatDef, optimalStyle);

				// Store when we last changed style
				pet.getAttributes().set("PET_LAST_STYLE_CHANGE", currentTime);

				System.out.println("Pet " + pet.getUsername() + " switched to " + optimalStyle +
					" at distance " + distance);
			}
		} catch (Exception e) {
			System.out.println("Error in pet combat style selection: " + e.getMessage());
			pet.getCombat().setCombatType(Combat.CombatTypes.MELEE);
		}
	}

	// NPC-style distance selection (no gear limitations)
	private static Combat.CombatTypes selectNpcStyleForDistance(NpcCombatDefinition combatDef, int distance) {
		boolean hasMelee = combatDef.getMelee() != null && combatDef.getMelee().length > 0;
		boolean hasSagittarius = combatDef.getSagittarius() != null && combatDef.getSagittarius().length > 0;
		boolean hasMage = combatDef.getMage() != null && combatDef.getMage().length > 0;

		// Count available styles
		java.util.List<Combat.CombatTypes> availableStyles = new java.util.ArrayList<>();

		// Add styles that are effective at this distance
		if (distance <= 1) {
			// Close range: all styles can work at 1 tile, add variety
			if (hasMelee) availableStyles.add(Combat.CombatTypes.MELEE);
			if (hasSagittarius) availableStyles.add(Combat.CombatTypes.SAGITTARIUS);
			if (hasMage) availableStyles.add(Combat.CombatTypes.MAGE);
		} else if (distance <= 5) {
			// Medium range: sagittarius and mage work well
			if (hasSagittarius) availableStyles.add(Combat.CombatTypes.SAGITTARIUS);
			if (hasMage) availableStyles.add(Combat.CombatTypes.MAGE);
			// Melee can still work but less preferred
			if (hasMelee && distance <= 3) availableStyles.add(Combat.CombatTypes.MELEE);
		} else {
			// Long range: prefer mage, sagittarius as backup
			if (hasMage) availableStyles.add(Combat.CombatTypes.MAGE);
			if (hasSagittarius) availableStyles.add(Combat.CombatTypes.SAGITTARIUS);
		}

		// If multiple styles available, add some randomness for variation
		if (availableStyles.size() > 1) {
			// 70% chance to use random style, 30% chance to use "optimal" style
			if (Math.random() < 0.7) {
				// Random selection for variety
				int randomIndex = (int)(Math.random() * availableStyles.size());
				return availableStyles.get(randomIndex);
			}
		}

		// Fallback to optimal style or first available
		if (!availableStyles.isEmpty()) {
			return availableStyles.get(0);
		}

		// Final fallback
		if (hasMage) return Combat.CombatTypes.MAGE;
		if (hasSagittarius) return Combat.CombatTypes.SAGITTARIUS;
		return Combat.CombatTypes.MELEE;
	}

	// Direct combat setup - bypasses PetCombatHandler to avoid conflicts
	private static void setupNpcCombatDataDirectly(Stoner pet, NpcCombatDefinition combatDef, Combat.CombatTypes style) {
		// Set max hit based on combat type
		int maxHit = getMaxHitForCombatType(combatDef, style);
		pet.getAttributes().set("PET_CURRENT_MAX_HIT", maxHit);

		// Set proper assault delay from combat definition
		int assaultDelay = getAssaultDelayForCombatType(combatDef, style);
		pet.getCombat().setAssaultTimer(assaultDelay);

		// Set up the combat class assault data with proper delays
		setupCombatClassData(pet, combatDef, style);
	}

	private static int getMaxHitForCombatType(NpcCombatDefinition combatDef, Combat.CombatTypes type) {
		switch (type) {
			case MELEE:
				if (combatDef.getMelee() != null && combatDef.getMelee().length > 0) {
					return combatDef.getMelee()[0].getMax();
				}
				break;
			case MAGE:
				if (combatDef.getMage() != null && combatDef.getMage().length > 0) {
					return combatDef.getMage()[0].getMax();
				}
				break;
			case SAGITTARIUS:
				if (combatDef.getSagittarius() != null && combatDef.getSagittarius().length > 0) {
					return combatDef.getSagittarius()[0].getMax();
				}
				break;
		}
		return 25; // Default damage
	}

	private static int getAssaultDelayForCombatType(NpcCombatDefinition combatDef, Combat.CombatTypes type) {
		switch (type) {
			case MELEE:
				if (combatDef.getMelee() != null && combatDef.getMelee().length > 0) {
					return combatDef.getMelee()[0].getAssault().getAssaultDelay();
				}
				break;
			case MAGE:
				if (combatDef.getMage() != null && combatDef.getMage().length > 0) {
					return combatDef.getMage()[0].getAssault().getAssaultDelay();
				}
				break;
			case SAGITTARIUS:
				if (combatDef.getSagittarius() != null && combatDef.getSagittarius().length > 0) {
					return combatDef.getSagittarius()[0].getAssault().getAssaultDelay();
				}
				break;
		}
		return 4; // Default 4 tick delay
	}

	private static void setupCombatClassData(Stoner pet, NpcCombatDefinition combatDef, Combat.CombatTypes combatType) {
		switch (combatType) {
			case MELEE:
				if (combatDef.getMelee() != null && combatDef.getMelee().length > 0) {
					NpcCombatDefinition.Melee meleeAttack = combatDef.getMelee()[0];
					pet.getCombat().getMelee().setAssault(meleeAttack.getAssault(), meleeAttack.getAnimation());
				}
				break;
			case MAGE:
				if (combatDef.getMage() != null && combatDef.getMage().length > 0) {
					NpcCombatDefinition.Mage mageAttack = combatDef.getMage()[0];
					pet.getCombat().getMage().setAssault(
						mageAttack.getAssault(),
						mageAttack.getAnimation(),
						mageAttack.getStart(),
						mageAttack.getEnd(),
						mageAttack.getProjectile()
					);
				}
				break;
			case SAGITTARIUS:
				if (combatDef.getSagittarius() != null && combatDef.getSagittarius().length > 0) {
					NpcCombatDefinition.Sagittarius sagittariusAttack = combatDef.getSagittarius()[0];
					pet.getCombat().getSagittarius().setAssault(
						sagittariusAttack.getAssault(),
						sagittariusAttack.getAnimation(),
						sagittariusAttack.getStart(),
						sagittariusAttack.getEnd(),
						sagittariusAttack.getProjectile()
					);
				}
				break;
		}
	}
}