package com.bestbudz.rs2.content.profession.petmaster.gain;

import com.bestbudz.rs2.content.profession.petmaster.bond.PetBondManager;
import com.bestbudz.rs2.entity.pets.Pet;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.logging.Logger;

/**
 * OPTIMIZED: Reduced frequency experience processing
 */
public class PetExperienceManager {

	private static final Logger logger = Logger.getLogger(PetExperienceManager.class.getName());

	private final Stoner stoner;
	private final PetBondManager bondManager;
	private long lastProcessTime = System.currentTimeMillis();

	// OPTIMIZATION: Reduce processing frequency from 1 minute to 5 minutes
	private static final long PROCESS_INTERVAL = 300000; // 5 minutes instead of 1 minute

	public PetExperienceManager(Stoner stoner, PetBondManager bondManager) {
		this.stoner = stoner;
		this.bondManager = bondManager;
	}

	/**
	 * OPTIMIZED: Process passive experience every 5 minutes instead of 1 minute
	 */
	public void processPassiveExperience() {
		if (stoner == null || stoner.getActivePets() == null || stoner.getActivePets().isEmpty()) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		if (currentTime - lastProcessTime >= PROCESS_INTERVAL) {

			// OPTIMIZATION: Calculate experience for 5 minutes at once
			double baseExp = calculateBaseExperience() * 5; // 5x since it's 5 minutes

			// Award profession experience safely
			if (stoner.getProfession() != null) {
				stoner.getProfession().addExperience(17, baseExp);
			}

			// OPTIMIZATION: Batch update pet bonds instead of individual calls
			Pet[] activePets = stoner.getActivePets().toArray(new Pet[0]);
			for (Pet pet : activePets) {
				if (pet != null && pet.getData() != null) {
					bondManager.updatePetBond(pet, baseExp * 0.1);
				}
			}

			lastProcessTime = currentTime;

			logger.fine("PetMaster processed for " + stoner.getUsername() +
				" - awarded " + baseExp + " experience (5min batch)");
		}
	}

	/**
	 * Calculate base experience based on current conditions
	 */
	private double calculateBaseExperience() {
		// Base experience per minute with active pets
		double baseExp = 2.0;

		// Bonus for multiple pets
		if (stoner.getActivePets().size() > 1) {
			baseExp *= 1.3;
		}

		// Movement bonus
		if (stoner.getMovementHandler() != null && stoner.getMovementHandler().moving()) {
			baseExp *= 1.5;
		}

		// Combat bonus
		if (stoner.getCombat() != null && stoner.getCombat().inCombat()) {
			baseExp *= 2.0;
		}

		return baseExp;
	}

	/**
	 * Apply combat bonus to pet
	 */
	public void applyCombatBonus(Pet pet) {
		if (pet == null) return;

		bondManager.updatePetBond(pet, 3.0); // Bonus for combat
		logger.fine("Pet combat bonus applied for " + stoner.getUsername() +
			" with pet: " + pet.getData().name());
	}
}