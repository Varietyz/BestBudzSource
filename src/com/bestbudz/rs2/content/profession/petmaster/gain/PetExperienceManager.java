package com.bestbudz.rs2.content.profession.petmaster.gain;

import com.bestbudz.rs2.content.profession.petmaster.bond.PetBondManager;
import com.bestbudz.rs2.entity.pets.Pet;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.logging.Logger;

/**
 * FIXED: Realistic experience processing with proper timing
 */
public class PetExperienceManager {

	private static final Logger logger = Logger.getLogger(PetExperienceManager.class.getName());

	private final Stoner stoner;
	private final PetBondManager bondManager;
	private long lastProcessTime = System.currentTimeMillis();

	// FIXED: Process every 60 seconds (1 minute) for realistic progression
	private static final long PROCESS_INTERVAL = 60000; // 1 minute

	public PetExperienceManager(Stoner stoner, PetBondManager bondManager) {
		this.stoner = stoner;
		this.bondManager = bondManager;
	}

	/**
	 * FIXED: Process experience based on ACTUAL time elapsed, not arbitrary multipliers
	 */
	public void processPassiveExperience() {
		if (stoner == null || stoner.getActivePets() == null || stoner.getActivePets().isEmpty()) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		long timeElapsed = currentTime - lastProcessTime;

		// Only process if at least 1 minute has passed
		if (timeElapsed >= PROCESS_INTERVAL) {

			// FIXED: Calculate experience based on ACTUAL time elapsed, not fixed multipliers
			double actualMinutesElapsed = timeElapsed / 60000.0; // Convert to minutes
			double baseExpPerMinute = calculateBaseExperiencePerMinute();
			double totalExp = baseExpPerMinute * actualMinutesElapsed;

			// Award profession experience safely
			if (stoner.getProfession() != null) {
				stoner.getProfession().addExperience(17, totalExp);
			}

			// FIXED: Update pet bonds with ACTUAL time elapsed and realistic experience
			Pet[] activePets = stoner.getActivePets().toArray(new Pet[0]);
			for (Pet pet : activePets) {
				if (pet != null && pet.getData() != null) {
					// Add actual time elapsed (not arbitrary 5 minutes)
					// Bond experience is much smaller than profession experience
					bondManager.updatePetBond(pet, totalExp * 0.05, timeElapsed);
				}
			}

			lastProcessTime = currentTime;

			logger.fine("PetMaster processed for " + stoner.getUsername() +
				" - " + String.format("%.1f", actualMinutesElapsed) + " minutes elapsed, " +
				"awarded " + String.format("%.1f", totalExp) + " experience");
		}
	}

	/**
	 * FIXED: Realistic base experience per minute (much lower values)
	 */
	private double calculateBaseExperiencePerMinute() {
		// FIXED: Much more realistic base experience values
		double baseExp = 1.0; // 1 exp per minute base

		// Small bonus for multiple pets
		if (stoner.getActivePets().size() > 1) {
			baseExp *= 1.2; // 20% bonus instead of 30%
		}

		// Small movement bonus
		if (stoner.getMovementHandler() != null && stoner.getMovementHandler().moving()) {
			baseExp *= 1.3; // 30% bonus instead of 50%
		}

		// Combat bonus
		if (stoner.getCombat() != null && stoner.getCombat().inCombat()) {
			baseExp *= 1.8; // 80% bonus instead of 100%
		}

		return baseExp;
	}

	/**
	 * FIXED: Realistic combat bonus
	 */
	public void applyCombatBonus(Pet pet) {
		if (pet == null) return;

		// FIXED: Much smaller combat bonus
		bondManager.updatePetBond(pet, 0.5, 0); // Small experience bonus, no time manipulation
		logger.fine("Pet combat bonus applied for " + stoner.getUsername() +
			" with pet: " + pet.getData().name());
	}
}