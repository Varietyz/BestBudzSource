package com.bestbudz.rs2.entity.pets;

import com.bestbudz.core.definitions.NpcCombatDefinition;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerAnimations;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ChangeAppearancePacket;
import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import java.util.Random;

public class Pet {
	private final Stoner owner;
	private final PetData data;
	private final Stoner petStoner;

	public Pet(Stoner owner, PetData data) {
		this.owner = owner;
		this.data = data;
		this.petStoner = createPetStoner(owner, data);

		if (owner.getPetMaster() != null) {
			owner.getPetMaster().onPetFirstSummoned(data);
		}
	}

private static Stoner createPetStoner(Stoner owner, PetData data) {

	Stoner pet = new Stoner(new PetIsolatedClient(owner));

	if (pet.getClient() instanceof PetIsolatedClient) {
		((PetIsolatedClient) pet.getClient()).setPetStoner(pet);
	}

	ChangeAppearancePacket.setToDefault(pet);

	long randomId = Math.abs(new Random().nextLong() % 1000000000000L);

	String uniqueUsername = "Pet_" + randomId +  data.name() + "_" + owner.getUsername();

	pet.setUsername(uniqueUsername);
	pet.setPassword("");

	String formattedDisplayName = PetUtils.formatPetDisplayName(data);
	String ownerName = owner.getUsername() + "'s ";
	pet.setDisplay(ownerName + formattedDisplayName);

	pet.getLocation().setAs(owner.getLocation());
	pet.setNpcAppearanceId((short) data.npcID);
	pet.getFollowing().setIgnoreDistance(true);
	pet.getFollowing().setFollow(owner);
	pet.setPet(true);
	int petSize = PetDefinition.getPetSize(data.npcID);
	pet.setSize(petSize);

	StonerAnimations petAnimations = PetDefinition.createPetAnimations(data.npcID);
	pet.getAnimations().set(petAnimations);

	setPetGrades(pet, data.npcID);
	pet.setRetaliate(true);
	pet.updateCombatType();
	pet.setNpc(true);
	pet.setActive(true);

	pet.getRunEnergy().setRunning(true);
	pet.getRunEnergy().setEnergy(100.0);
	pet.getRunEnergy().setAllowed(false);

	if (pet.getClient() instanceof PetIsolatedClient) {
		((PetIsolatedClient) pet.getClient()).simulateLogin();
	}

	int petIndex = World.register(pet);
	if (petIndex == -1) {
		System.err.println("Failed to register pet in world!");
		return null;
	}

	pet.getUpdateFlags().setUpdateRequired(true);
	pet.setAppearanceUpdateRequired(true);
	pet.setNeedsPlacement(true);

	PetCombatUtils.initializePetCombat(pet, data, owner);
	setupPetCombatAnimations(pet, data);
	return pet;
}

	private static void setupPetCombatAnimations(Stoner pet, PetData data) {

		NpcCombatDefinition combatDef = GameDefinitionLoader.getNpcCombatDefinition(data.npcID);

		if (combatDef != null) {

			pet.getAttributes().set("PET_COMBAT_DEFINITION", combatDef);

			if (combatDef.getMelee() != null && combatDef.getMelee().length > 0) {
				NpcCombatDefinition.Melee meleeAttack = combatDef.getMelee()[0];
				if (meleeAttack.getAssault() != null && meleeAttack.getAnimation() != null) {
					pet.getAttributes().set("PET_MELEE_ANIMATION", meleeAttack.getAnimation());
					pet.getAttributes().set("PET_MELEE_ASSAULT", meleeAttack.getAssault());
					pet.getAttributes().set("PET_MELEE_MAX_HIT", meleeAttack.getMax());
					System.out.println("  Stored melee animation: " + meleeAttack.getAnimation().getId());
				}
			}

			if (combatDef.getMage() != null && combatDef.getMage().length > 0) {
				NpcCombatDefinition.Mage mageAttack = combatDef.getMage()[0];
				if (mageAttack.getAssault() != null && mageAttack.getAnimation() != null) {
					pet.getAttributes().set("PET_MAGE_ANIMATION", mageAttack.getAnimation());
					pet.getAttributes().set("PET_MAGE_ASSAULT", mageAttack.getAssault());
					pet.getAttributes().set("PET_MAGE_MAX_HIT", mageAttack.getMax());
					pet.getAttributes().set("PET_MAGE_START", mageAttack.getStart());
					pet.getAttributes().set("PET_MAGE_PROJECTILE", mageAttack.getProjectile());
					pet.getAttributes().set("PET_MAGE_END", mageAttack.getEnd());
					System.out.println("  Stored mage animation: " + mageAttack.getAnimation().getId());
				}
			}

			if (combatDef.getSagittarius() != null && combatDef.getSagittarius().length > 0) {
				NpcCombatDefinition.Sagittarius sagittariusAttack = combatDef.getSagittarius()[0];
				if (sagittariusAttack.getAssault() != null && sagittariusAttack.getAnimation() != null) {
					pet.getAttributes().set("PET_SAGITTARIUS_ANIMATION", sagittariusAttack.getAnimation());
					pet.getAttributes().set("PET_SAGITTARIUS_ASSAULT", sagittariusAttack.getAssault());
					pet.getAttributes().set("PET_SAGITTARIUS_MAX_HIT", sagittariusAttack.getMax());
					pet.getAttributes().set("PET_SAGITTARIUS_START", sagittariusAttack.getStart());
					pet.getAttributes().set("PET_SAGITTARIUS_PROJECTILE", sagittariusAttack.getProjectile());
					pet.getAttributes().set("PET_SAGITTARIUS_END", sagittariusAttack.getEnd());
					System.out.println("  Stored sagittarius animation: " + sagittariusAttack.getAnimation().getId());
				}
			}

			pet.getAttributes().set("PET_COMBAT_TYPE", combatDef.getCombatType());
			if (combatDef.getBlock() != null) {
				pet.getAttributes().set("PET_BLOCK_ANIMATION", combatDef.getBlock());
			}
			if (combatDef.getDeath() != null) {
				pet.getAttributes().set("PET_DEATH_ANIMATION", combatDef.getDeath());
			}

			System.out.println("DEBUG: Pet " + pet.getUsername() + " combat animations configured:");
			System.out.println("  NPC ID: " + data.npcID + ", Combat type: " + combatDef.getCombatType());
			System.out.println("  Has melee: " + (combatDef.getMelee() != null));
			System.out.println("  Has mage: " + (combatDef.getMage() != null));
			System.out.println("  Has sagittarius: " + (combatDef.getSagittarius() != null));
		} else {
			System.err.println("ERROR: No combat definition found for NPC ID: " + data.npcID);
		}
	}

	private static void setPetGrades(Stoner pet, int npcID) {
		NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npcID);

		if (npcDef == null) {

			long[] defaultGrades = {
				1000,
				1000,
				1000,
				5000,
				1000,
				1000,
				1000
			};
			pet.setGrades(defaultGrades.clone());
			pet.setMaxGrades(defaultGrades.clone());
			return;
		}

		int baseGrade = Math.max(50, npcDef.getGrade());

		long attackGrade = baseGrade * 8L;
		long strengthGrade = baseGrade * 8L;
		long defenceGrade = baseGrade * 6L;
		long rangedGrade = baseGrade * 6L;
		long prayerGrade = baseGrade * 4L;
		long magicGrade = baseGrade * 6L;

		int petHP = Math.max(2000, PetDefinition.getPetHP(npcID));

		long[] petGrades = {
			attackGrade,
			strengthGrade,
			defenceGrade,
			petHP,
			rangedGrade,
			prayerGrade,
			magicGrade
		};

		long[] petMaxGrades = petGrades.clone();

		pet.setGrades(petGrades);
		pet.setMaxGrades(petMaxGrades);

		System.out.println("DEBUG: Pet " + pet.getUsername() + " grades set:");
		System.out.println("  Attack: " + attackGrade + ", Strength: " + strengthGrade);
		System.out.println("  Defence: " + defenceGrade + ", HP: " + petHP);
	}

	public Stoner getOwner() {
		return owner;
	}

	public PetData getData() {
		return data;
	}

	public Stoner getPetStoner() {
		return petStoner;
	}

	public String getName() {
		return data.getName();
	}

	public void remove() {
		System.out.println("Removing pet: " + petStoner.getUsername());
		petStoner.setVisible(false);
		petStoner.setActive(false);

		if (petStoner.getClient() instanceof PetIsolatedClient) {
			((PetIsolatedClient) petStoner.getClient()).simulateLogout();
		}

		World.unregister(petStoner);
	}
}
