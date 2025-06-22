package com.bestbudz.rs2.content.profession.petmaster.growth;

import com.bestbudz.rs2.content.profession.petmaster.bond.PetBond;
import com.bestbudz.rs2.entity.pets.Pet;

/**
 * Interface for growth checking callbacks
 * Allows the bond manager to trigger growth checks without tight coupling
 */
public interface GrowthCheckCallback {
	/**
	 * Check if the given pet should grow based on its bond
	 * @param pet The pet to check for growth
	 * @param bond The current bond state
	 */
	void checkGrowth(Pet pet, PetBond bond);
}