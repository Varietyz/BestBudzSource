package com.bestbudz.rs2.content.profession.petmaster;

import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.petmaster.bond.PetBond;
import com.bestbudz.rs2.content.profession.petmaster.bond.PetBondManager;
import com.bestbudz.rs2.content.profession.petmaster.growth.PetGrowthManager;
import com.bestbudz.rs2.entity.pets.Pet;
import com.bestbudz.rs2.entity.pets.PetData;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * SIMPLE SOLUTION: Timer-based active time tracking
 * Starts timer on pet deploy, stops on pickup/logout
 */
public class PetMaster {

	private static final Logger logger = Logger.getLogger(PetMaster.class.getName());

	private final Stoner stoner;
	private final PetBondManager bondManager;
	private final PetGrowthManager growthManager;
	private final PetDataManager dataManager;

	// Simple timer system
	private final Map<PetData, Timer> petTimers = new ConcurrentHashMap<>();
	private final Map<PetData, Long> petDeployTimes = new ConcurrentHashMap<>();

	// Update every minute
	private static final long UPDATE_INTERVAL = 60000; // 1 minute

	public PetMaster(Stoner stoner) {
		this.stoner = stoner;
		this.dataManager = new PetDataManager(stoner);
		this.bondManager = new PetBondManager(stoner, dataManager);
		this.growthManager = new PetGrowthManager(stoner, bondManager);

		// Set up growth callback
		bondManager.setGrowthCallback((pet, bond) -> growthManager.checkAndExecuteGrowth(pet, bond));

		// Load existing data
		Map<PetData, PetBond> loadedBonds = dataManager.loadData();
		bondManager.loadBonds(loadedBonds);

		logger.info("Simple PetMaster initialized for: " + stoner.getUsername());
	}

	/**
	 * Start timer when pet is deployed
	 */
	public void onPetDeployed(Pet pet) {
		if (pet == null || pet.getData() == null) return;

		PetData petData = pet.getData();

		// Record deploy time
		long deployTime = System.currentTimeMillis();
		petDeployTimes.put(petData, deployTime);

		// Create and start timer
		Timer timer = new Timer("PetTimer-" + petData.name() + "-" + stoner.getUsername(), true);
		petTimers.put(petData, timer);

		// Schedule repeating task every minute
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					updatePetActiveTime(petData);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Error in pet timer for " + petData.name(), e);
				}
			}
		}, UPDATE_INTERVAL, UPDATE_INTERVAL); // First run after 1 minute, then every minute

		logger.info("Started timer for " + petData.name() + " deployed by " + stoner.getUsername());
	}

	/**
	 * Stop timer when pet is picked up
	 */
	public void onPetPickedUp(Pet pet) {
		if (pet == null || pet.getData() == null) return;

		PetData petData = pet.getData();

		// Final update before stopping
		updatePetActiveTime(petData);

		// Stop and remove timer
		Timer timer = petTimers.remove(petData);
		if (timer != null) {
			timer.cancel();
		}

		// Remove deploy time
		petDeployTimes.remove(petData);

		// Save final state
		PetBond bond = bondManager.getAllBonds().get(petData);
		if (bond != null) {
			dataManager.savePetBond(petData, bond);
			logger.info("Pet picked up: " + petData.name() +
				" - Final active time: " + (bond.getActiveTime() / 60000) + " minutes");
		}
	}

	/**
	 * Update active time for a specific pet
	 */
	private void updatePetActiveTime(PetData petData) {
		try {
			// Add 1 minute of active time
			PetBond bond = bondManager.getOrCreateBond(petData);
			long oneMinute = 60000; // 1 minute in milliseconds

			bond.addActiveTime(oneMinute);
			bond.addExperience(1.0); // Small bond experience bonus

			// FIXED: Award PetMaster profession experience
			double professionExp = calculateProfessionExperience();
			if (stoner.getProfession() != null) {
				stoner.getProfession().addExperience(Professions.PET_MASTER, professionExp);
			}

			// Save to database
			dataManager.savePetBond(petData, bond);

			logger.info("Updated active time for " + petData.name() +
				" (Owner: " + stoner.getUsername() + ") - " +
				"Total: " + (bond.getActiveTime() / 60000) + " minutes, " +
				"Bond Grade: " + bond.getBondGrade() +
				", PetMaster EXP: +" + String.format("%.1f", professionExp));

			// Check for growth
			Pet activePet = findActivePet(petData);
			if (activePet != null) {
				growthManager.checkAndExecuteGrowth(activePet, bond);
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error updating active time for " + petData.name(), e);
		}
	}

	/**
	 * Calculate profession experience based on active pets and bonuses
	 */
	private double calculateProfessionExperience() {
		// Base experience per minute per pet
		double baseExp = 5.0;

		// Number of active pets multiplier
		int activePetCount = stoner.getActivePets().size();
		double petMultiplier = 1.0 + (activePetCount - 1) * 0.5; // 50% bonus per additional pet

		// Movement bonus
		double movementMultiplier = 1.0;
		if (stoner.getMovementHandler() != null && stoner.getMovementHandler().moving()) {
			movementMultiplier = 1.3; // 30% bonus when moving
		}

		// Combat bonus
		double combatMultiplier = 1.0;
		if (stoner.getCombat() != null && stoner.getCombat().inCombat()) {
			combatMultiplier = 2.0; // 100% bonus in combat
		}

		// Calculate final experience
		double totalExp = baseExp * petMultiplier * movementMultiplier * combatMultiplier;

		logger.fine("PetMaster EXP calculation: " + String.format("%.1f", baseExp) +
			" base * " + String.format("%.1f", petMultiplier) + " pets * " +
			String.format("%.1f", movementMultiplier) + " movement * " +
			String.format("%.1f", combatMultiplier) + " combat = " +
			String.format("%.1f", totalExp));

		return totalExp;
	}

	/**
	 * Find active pet by pet data
	 */
	private Pet findActivePet(PetData petData) {
		if (stoner.getActivePets() == null) return null;

		for (Pet pet : stoner.getActivePets()) {
			if (pet != null && pet.getData() == petData) {
				return pet;
			}
		}
		return null;
	}

	/**
	 * Called when pet is first summoned
	 */
	public void onPetFirstSummoned(PetData petData) {
		try {
			bondManager.recordFirstSummon(petData);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error recording first summon", e);
		}
	}

	/**
	 * Called when pet participates in combat
	 */
	public void onPetCombat(Pet pet) {
		try {
			if (pet == null || pet.getData() == null) return;

			// Give immediate bonuses
			PetBond bond = bondManager.getOrCreateBond(pet.getData());
			bond.addExperience(0.5); // Bond experience
			bond.addActiveTime(5000); // 5 seconds active time bonus

			// FIXED: Award immediate PetMaster profession experience for combat
			double combatExp = 2.0; // Immediate combat bonus
			if (stoner.getProfession() != null) {
				stoner.getProfession().addExperience(Professions.PET_MASTER, combatExp);
			}

			logger.fine("Combat bonus for " + pet.getData().name() +
				" - Bond EXP: +0.5, Active time: +5s, PetMaster EXP: +" + combatExp);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error applying combat bonus", e);
		}
	}

	/**
	 * Get stoner grade
	 */
	public int getStonerGrade() {
		try {
			if (stoner == null || stoner.getProfession() == null) return 1;
			return (int) stoner.getProfession().getGrades()[17];
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error getting stoner grade", e);
			return 1;
		}
	}

	/**
	 * Get stats for display
	 */
	public String getStats() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("Pet Master Stats for ").append(stoner.getUsername()).append("\n");
			sb.append("Grade: ").append(getStonerGrade()).append("\n");
			sb.append("Active Pets: ").append(stoner.getActivePets().size()).append("\n");
			sb.append("Running Timers: ").append(petTimers.size()).append("\n\n");

			// Show all bonds (not just active pets)
			Map<PetData, PetBond> allBonds = bondManager.getAllBonds();
			for (Map.Entry<PetData, PetBond> entry : allBonds.entrySet()) {
				PetData petData = entry.getKey();
				PetBond bond = entry.getValue();

				boolean isActive = petTimers.containsKey(petData);
				long activeMinutes = bond.getActiveTime() / 60000;

				sb.append(petData.name()).append(":\n");
				sb.append("  Status: ").append(isActive ? "ACTIVE (timer running)" : "Stored").append("\n");
				sb.append("  Bond Grade: ").append(bond.getBondGrade()).append("\n");
				sb.append("  Experience: ").append(String.format("%.1f", bond.getExperience())).append("\n");
				sb.append("  Active Time: ").append(activeMinutes).append(" minutes\n\n");
			}

			return sb.toString();
		} catch (Exception e) {
			return "Error getting stats: " + e.getMessage();
		}
	}

	/**
	 * Save all data and stop all timers (called on logout)
	 */
	public void save() {
		try {
			// Final update for all active pets
			for (PetData petData : petTimers.keySet()) {
				updatePetActiveTime(petData);
			}

			// Save all bonds
			bondManager.forceSaveAllBonds();
			dataManager.saveData(bondManager.getAllBonds());

			// Stop all timers
			for (Timer timer : petTimers.values()) {
				timer.cancel();
			}
			petTimers.clear();
			petDeployTimes.clear();

			logger.info("PetMaster saved and timers stopped for " + stoner.getUsername());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error saving PetMaster data", e);
		}
	}

	/**
	 * No longer needed - timer handles everything
	 */
	public void process() {
		// Timer system handles all updates - this method can be empty
		// or used for other periodic tasks if needed
	}
}