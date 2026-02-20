package com.bestbudz.rs2.entity.pets;

import com.bestbudz.core.definitions.NpcCombatDefinition;
import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.pets.abilities.PetAbility;

public class PetCombatSystem {

	public static void processPetCombat(Stoner pet) {
		if (!canEngageInCombat(pet)) {
			return;
		}

		Mob target = findTargetForPet(pet);
		if (target != null) {

			selectAndSetPetCombatStyle(pet, target);

			pet.getCombat().setAssault(target);

			Entity actualTarget = pet.getCombat().getAssaulting();
			if (actualTarget != null) {

				Pet petObject = findPetObjectFromStoner(pet);
				if (petObject != null) {
					onPetCombatHit(petObject);
				}
			}
		}

		Entity currentTarget = pet.getCombat().getAssaulting();
		if (currentTarget != null && !currentTarget.isDead()) {
			processPetAbilities(pet, currentTarget);
		}
	}

	public static Pet findPetObjectFromStoner(Stoner petStoner) {

		Stoner owner = (Stoner) petStoner.getAttributes().get("PET_OWNER");
		if (owner == null || !owner.isActive()) {
			return null;
		}

		for (Pet pet : owner.getActivePets()) {
			if (pet.getPetStoner() == petStoner) {
				return pet;
			}
		}

		return null;
	}

	public static void onPetCombatHit(Pet pet) {
		Stoner owner = pet.getOwner();
		if (owner != null && owner.getPetMaster() != null) {
			owner.getPetMaster().onPetCombat(pet);
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

	private static void selectAndSetPetCombatStyle(Stoner pet, Mob target) {
		NpcCombatDefinition combatDef = (NpcCombatDefinition) pet.getAttributes().get("PET_COMBAT_DEFINITION");
		if (combatDef == null) return;

		Long lastStyleChange = (Long) pet.getAttributes().get("PET_LAST_STYLE_CHANGE");
		long currentTime = System.currentTimeMillis();

		if (lastStyleChange != null && (currentTime - lastStyleChange) < 5000) {
			return;
		}

		try {
			int distance = Math.max(
				Math.abs(pet.getLocation().getX() - target.getLocation().getX()),
				Math.abs(pet.getLocation().getY() - target.getLocation().getY())
			);

			Combat.CombatTypes optimalStyle = selectNpcStyleForDistance(combatDef, distance);

			if (pet.getCombat().getCombatType() != optimalStyle) {
				pet.getCombat().setCombatType(optimalStyle);
				setupNpcCombatDataDirectly(pet, combatDef, optimalStyle);

				pet.getAttributes().set("PET_LAST_STYLE_CHANGE", currentTime);

				System.out.println("Pet " + pet.getUsername() + " switched to " + optimalStyle +
					" at distance " + distance);
			}
		} catch (Exception e) {
			System.out.println("Error in pet combat style selection: " + e.getMessage());
			pet.getCombat().setCombatType(Combat.CombatTypes.MELEE);
		}
	}

	private static Combat.CombatTypes selectNpcStyleForDistance(NpcCombatDefinition combatDef, int distance) {
		boolean hasMelee = combatDef.getMelee() != null && combatDef.getMelee().length > 0;
		boolean hasSagittarius = combatDef.getSagittarius() != null && combatDef.getSagittarius().length > 0;
		boolean hasMage = combatDef.getMage() != null && combatDef.getMage().length > 0;

		java.util.List<Combat.CombatTypes> availableStyles = new java.util.ArrayList<>();

		if (distance <= 1) {

			if (hasMelee) availableStyles.add(Combat.CombatTypes.MELEE);
			if (hasSagittarius) availableStyles.add(Combat.CombatTypes.SAGITTARIUS);
			if (hasMage) availableStyles.add(Combat.CombatTypes.MAGE);
		} else if (distance <= 5) {

			if (hasSagittarius) availableStyles.add(Combat.CombatTypes.SAGITTARIUS);
			if (hasMage) availableStyles.add(Combat.CombatTypes.MAGE);

			if (hasMelee && distance <= 3) availableStyles.add(Combat.CombatTypes.MELEE);
		} else {

			if (hasMage) availableStyles.add(Combat.CombatTypes.MAGE);
			if (hasSagittarius) availableStyles.add(Combat.CombatTypes.SAGITTARIUS);
		}

		if (availableStyles.size() > 1) {

			if (Math.random() < 0.7) {

				int randomIndex = (int)(Math.random() * availableStyles.size());
				return availableStyles.get(randomIndex);
			}
		}

		if (!availableStyles.isEmpty()) {
			return availableStyles.get(0);
		}

		if (hasMage) return Combat.CombatTypes.MAGE;
		if (hasSagittarius) return Combat.CombatTypes.SAGITTARIUS;
		return Combat.CombatTypes.MELEE;
	}

	private static void setupNpcCombatDataDirectly(Stoner pet, NpcCombatDefinition combatDef, Combat.CombatTypes style) {

		int maxHit = getMaxHitForCombatType(combatDef, style);
		pet.getAttributes().set("PET_CURRENT_MAX_HIT", maxHit);

		int assaultDelay = getAssaultDelayForCombatType(combatDef, style);
		pet.getCombat().setAssaultTimer(assaultDelay);

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
		return 25;
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
		return 4;
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
