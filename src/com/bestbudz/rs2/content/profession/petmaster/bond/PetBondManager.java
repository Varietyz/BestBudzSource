package com.bestbudz.rs2.content.profession.petmaster.bond;

import com.bestbudz.rs2.content.profession.petmaster.db.PetDataManager;
import com.bestbudz.rs2.entity.pets.Pet;
import com.bestbudz.rs2.entity.pets.PetData;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * OPTIMIZED: Async bond manager with reduced database calls
 */
public class PetBondManager {

	private static final Logger logger = Logger.getLogger(PetBondManager.class.getName());

	private final Stoner stoner;
	private final PetDataManager dataManager;

	// OPTIMIZATION: Use ConcurrentHashMap for thread safety
	private final Map<PetData, PetBond> petBonds = new ConcurrentHashMap<>();

	// Callback for growth checking
	private GrowthCheckCallback growthCallback;

	// OPTIMIZATION: Track when bonds were last saved to database
	private volatile long lastDatabaseSave = System.currentTimeMillis();

	public PetBondManager(Stoner stoner, PetDataManager dataManager) {
		this.stoner = stoner;
		this.dataManager = dataManager;
	}

	/**
	 * Set growth callback for when bonds need growth checking
	 */
	public void setGrowthCallback(GrowthCheckCallback callback) {
		this.growthCallback = callback;
	}

	/**
	 * OPTIMIZED: Update individual pet bond with reduced database calls
	 */
	public void updatePetBond(Pet pet, double experience) {
		try {
			if (pet == null || pet.getData() == null) return;

			PetData petData = pet.getData();
			PetBond bond = getOrCreateBond(petData);

			int oldGrade = bond.getBondGrade();
			bond.addExperience(experience);
			bond.addActiveTime(300000); // 5 minutes instead of 1 minute

			// Check for grade up
			if (bond.getBondGrade() > oldGrade) {
				handleBondGradeUp(pet, bond);
			}

			// Check for growth after bond update
			checkForGrowth(pet, bond);

			// OPTIMIZATION: Only save to database every 10 minutes or on significant events
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastDatabaseSave > 600000 || bond.getBondGrade() > oldGrade) {
				dataManager.savePetBond(petData, bond);
				lastDatabaseSave = currentTime;
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error updating pet bond for " + stoner.getUsername() +
				" with pet: " + (pet != null && pet.getData() != null ? pet.getData().name() : "null"), e);
		}
	}

	/**
	 * Get or create pet bond for given pet data
	 */
	private PetBond getOrCreateBond(PetData petData) {
		return petBonds.computeIfAbsent(petData, key -> {
			PetBond bond = new PetBond();
			bond.setFirstSummoned(System.currentTimeMillis());
			logger.info("Created new pet bond for " + stoner.getUsername() +
				" with pet: " + petData.name());
			return bond;
		});
	}

	/**
	 * Check for growth and trigger if ready
	 */
	private void checkForGrowth(Pet pet, PetBond bond) {
		if (growthCallback != null) {
			growthCallback.checkGrowth(pet, bond);
		}
	}

	/**
	 * Interface for growth checking callback
	 */
	public interface GrowthCheckCallback {
		void checkGrowth(Pet pet, PetBond bond);
	}

	/**
	 * Handle bond grade increase
	 */
	private void handleBondGradeUp(Pet pet, PetBond bond) {
		try {
			if (pet == null || pet.getData() == null) return;

			stoner.send(new SendMessage("Your bond with " + pet.getData().name() +
				" reached grade " + bond.getBondGrade() + "!"));

			// Send overhead message to the pet
			if (pet.getPetStoner() != null && pet.getPetStoner().getUpdateFlags() != null) {
				pet.getPetStoner().getUpdateFlags().sendForceMessage("*feels closer to master*");
			}

			logger.info("Bond grade increased for " + stoner.getUsername() +
				" with pet " + pet.getData().name() + " to grade " + bond.getBondGrade());

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error handling bond grade up for " + stoner.getUsername(), e);
		}
	}

	/**
	 * Record first summon for a pet
	 */
	public void recordFirstSummon(PetData petData) {
		if (petData == null) return;

		PetBond bond = petBonds.computeIfAbsent(petData, key -> new PetBond());

		if (bond.getFirstSummoned() == 0) {
			bond.setFirstSummoned(System.currentTimeMillis());
			logger.info("First summon recorded for " + stoner.getUsername() +
				" with pet: " + petData.name());
		}
	}

	/**
	 * Get bond grade for display
	 */
	public int getBondGrade(PetData petData) {
		try {
			PetBond bond = petBonds.get(petData);
			return bond != null ? bond.getBondGrade() : 1;
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error getting bond grade for " +
				(stoner != null ? stoner.getUsername() : "null") +
				" with pet: " + petData.name(), e);
			return 1;
		}
	}

	/**
	 * Get pet bond for specific pet data
	 */
	public PetBond getPetBond(PetData petData) {
		return petBonds.get(petData);
	}

	/**
	 * Transfer bond data during growth
	 */
	public void transferBond(PetData fromPet, PetData toPet, PetBond fromBond) {
		// Create new bond for evolved pet
		PetBond newBond = new PetBond();
		newBond.setExperience(fromBond.getExperience() * 0.9); // Keep 90% experience
		newBond.setActiveTime(fromBond.getActiveTime());
		newBond.setFirstSummoned(fromBond.getFirstSummoned());

		// Update bond mapping - remove old, add new
		petBonds.remove(fromPet);
		petBonds.put(toPet, newBond);
	}

	/**
	 * Get all pet bonds for saving
	 */
	public Map<PetData, PetBond> getAllBonds() {
		return new HashMap<>(petBonds);
	}

	/**
	 * Load bonds from data manager
	 */
	public void loadBonds(Map<PetData, PetBond> bonds) {
		petBonds.clear();
		petBonds.putAll(bonds);
	}

	/**
	 * OPTIMIZATION: Force save all bonds to database
	 */
	public void forceSaveAllBonds() {
		try {
			for (Map.Entry<PetData, PetBond> entry : petBonds.entrySet()) {
				dataManager.savePetBond(entry.getKey(), entry.getValue());
			}
			lastDatabaseSave = System.currentTimeMillis();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error force saving all bonds for " + stoner.getUsername(), e);
		}
	}
}