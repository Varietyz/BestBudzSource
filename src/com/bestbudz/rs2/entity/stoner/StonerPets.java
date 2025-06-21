package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.entity.pets.Pet;
import com.bestbudz.rs2.entity.pets.PetManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages pet-related functionality for players
 */
public class StonerPets {
	private final Stoner stoner;

	// Pet system
	private boolean pet = false;
	private List<Pet> activePetsNew = new ArrayList<>();

	public StonerPets(Stoner stoner) {
		this.stoner = stoner;
	}

	/**
	 * Checks if this stoner is a pet
	 */
	public boolean isPet() {
		return pet;
	}

	/**
	 * Checks if this stoner is a pet stoner (pet with valid pet username)
	 */
	public boolean isPetStoner() {
		return isPet() && PetManager.isPetUsername(stoner.getUsername());
	}

	/**
	 * Sets the pet status and configures pet-specific attributes
	 */
	public void setPet(boolean pet) {
		this.pet = pet;
		if (pet) {
			// Set special HP for pets
			stoner.getGrades()[3] = 420000;
			stoner.getMaxGrades()[3] = 420000;
		}
	}

	/**
	 * Gets the list of active pets for this player
	 */
	public List<Pet> getActivePets() {
		return activePetsNew;
	}
}