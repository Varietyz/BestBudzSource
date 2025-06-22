package com.bestbudz.rs2.content.profession.petmaster;

import com.bestbudz.rs2.content.profession.petmaster.bond.PetBond;
import com.bestbudz.rs2.content.profession.petmaster.bond.PetBondManager;
import com.bestbudz.rs2.content.profession.petmaster.db.PetDataManager;
import com.bestbudz.rs2.content.profession.petmaster.gain.PetExperienceManager;
import com.bestbudz.rs2.content.profession.petmaster.growth.PetGrowthManager;
import com.bestbudz.rs2.entity.pets.Pet;
import com.bestbudz.rs2.entity.pets.PetData;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * OPTIMIZED: Pet Master profession with reduced processing frequency
 */
public class PetMaster {

	private static final Logger logger = Logger.getLogger(PetMaster.class.getName());

	private final Stoner stoner;
	private final PetExperienceManager experienceManager;
	private final PetBondManager bondManager;
	private final PetGrowthManager growthManager;
	private final PetDataManager dataManager;

	// OPTIMIZATION: Track processing cycles to reduce frequency
	private int processingCycle = 0;
	private static final int PROCESS_EVERY_N_CYCLES = 50; // Only process every 50 game cycles (~30 seconds)

	public PetMaster(Stoner stoner) {
		this.stoner = stoner;
		this.dataManager = new PetDataManager(stoner);
		this.bondManager = new PetBondManager(stoner, dataManager);
		this.experienceManager = new PetExperienceManager(stoner, bondManager);
		this.growthManager = new PetGrowthManager(stoner, bondManager);

		// Set up growth callback to connect bond manager to growth manager
		bondManager.setGrowthCallback((pet, bond) -> growthManager.checkAndExecuteGrowth(pet, bond));

		// Load existing data and initialize bond manager
		Map<PetData, PetBond> loadedBonds = dataManager.loadData();
		bondManager.loadBonds(loadedBonds);

		logger.info("PetMaster initialized for user: " +
			(stoner != null && stoner.getUsername() != null ? stoner.getUsername() : "null"));
	}

	/**
	 * OPTIMIZED: Process passive experience much less frequently
	 */
	public void process() {
		try {
			// OPTIMIZATION: Only process every 50 cycles instead of every cycle
			processingCycle++;
			if (processingCycle >= PROCESS_EVERY_N_CYCLES) {
				experienceManager.processPassiveExperience();
				processingCycle = 0;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error processing PetMaster for " +
				(stoner != null && stoner.getUsername() != null ? stoner.getUsername() : "null"), e);
		}
	}

	/**
	 * Called when pet participates in combat
	 */
	public void onPetCombat(Pet pet) {
		try {
			if (pet == null) return;
			experienceManager.applyCombatBonus(pet);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error applying pet combat bonus for " +
				(stoner != null ? stoner.getUsername() : "null"), e);
		}
	}

	/**
	 * Called when pet is first summoned
	 */
	public void onPetFirstSummoned(PetData petData) {
		try {
			bondManager.recordFirstSummon(petData);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error recording first summon for " +
				(stoner != null ? stoner.getUsername() : "null") +
				" with pet: " + (petData != null ? petData.name() : "null"), e);
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
			logger.log(Level.WARNING, "Error getting stoner grade for " +
				(stoner != null ? stoner.getUsername() : "null"), e);
			return 1; // Default grade
		}
	}

	/**
	 * Get bond grade for display
	 */
	public int getBondGrade(PetData petData) {
		return bondManager.getBondGrade(petData);
	}

	/**
	 * Get stats for display
	 */
	public String getStats() {
		try {
			if (stoner == null) return "Error: No stoner reference";

			StringBuilder sb = new StringBuilder();
			sb.append("Pet Master Grade: ").append(getStonerGrade()).append("\n");
			sb.append("Active Pets: ").append(stoner.getActivePets().size()).append("\n");

			for (Pet pet : stoner.getActivePets()) {
				if (pet != null && pet.getData() != null) {
					int bondGrade = bondManager.getBondGrade(pet.getData());
					sb.append(pet.getName()).append(": Bond Grade ").append(bondGrade).append("\n");
				}
			}

			return sb.toString();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error generating stats for " +
				(stoner != null ? stoner.getUsername() : "null"), e);
			return "Error retrieving stats";
		}
	}

	/**
	 * OPTIMIZED: Save data to database with flush
	 */
	public void save() {
		try {
			// OPTIMIZATION: Force flush any pending saves and save all bond data
			bondManager.forceSaveAllBonds();
			dataManager.saveData(bondManager.getAllBonds());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error saving PetMaster data for " +
				(stoner != null ? stoner.getUsername() : "null"), e);
		}
	}
}