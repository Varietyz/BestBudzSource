package com.bestbudz.rs2.content.profession.petmaster.bond;

import com.bestbudz.rs2.content.profession.petmaster.PetDataManager;
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
 * SIMPLE: Bond manager for timer-based system
 */
public class PetBondManager {

	private static final Logger logger = Logger.getLogger(PetBondManager.class.getName());

	private final Stoner stoner;
	private final PetDataManager dataManager;
	private final Map<PetData, PetBond> petBonds = new ConcurrentHashMap<>();
	private GrowthCheckCallback growthCallback;

	public PetBondManager(Stoner stoner, PetDataManager dataManager) {
		this.stoner = stoner;
		this.dataManager = dataManager;
	}

	/**
	 * Set growth callback
	 */
	public void setGrowthCallback(GrowthCheckCallback callback) {
		this.growthCallback = callback;
	}

	/**
	 * Get or create pet bond - PUBLIC method for timer system
	 */
	public PetBond getOrCreateBond(PetData petData) {
		return petBonds.computeIfAbsent(petData, key -> {
			PetBond bond = new PetBond();
			bond.setFirstSummoned(System.currentTimeMillis());
			logger.info("Created new bond for " + stoner.getUsername() + " - " + petData.name());
			return bond;
		});
	}

	/**
	 * Update pet bond (legacy method for compatibility)
	 */
	public void updatePetBond(Pet pet, double experience, long actualTimeElapsed) {
		try {
			if (pet == null || pet.getData() == null) return;

			PetData petData = pet.getData();
			PetBond bond = getOrCreateBond(petData);

			int oldGrade = bond.getBondGrade();
			bond.addExperience(experience);
			bond.addActiveTime(actualTimeElapsed);

			// Check for grade up
			if (bond.getBondGrade() > oldGrade) {
				handleBondGradeUp(pet, bond);
			}

			// Check for growth
			checkForGrowth(pet, bond);

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error updating pet bond", e);
		}
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

			logger.info("Bond grade up: " + stoner.getUsername() + " - " + pet.getData().name() +
				" - Grade " + bond.getBondGrade());

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error handling bond grade up", e);
		}
	}

	/**
	 * Record first summon
	 */
	public void recordFirstSummon(PetData petData) {
		if (petData == null) return;

		PetBond bond = petBonds.get(petData);

		if (bond == null) {
			// Truly first summon
			bond = new PetBond();
			bond.setFirstSummoned(System.currentTimeMillis());
			petBonds.put(petData, bond);
			logger.info("First summon: " + stoner.getUsername() + " - " + petData.name());
		} else {
			// Bond exists from save data
			if (bond.getFirstSummoned() == 0) {
				bond.setFirstSummoned(System.currentTimeMillis());
			}
			logger.info("Restored bond: " + stoner.getUsername() + " - " + petData.name() +
				" - Grade " + bond.getBondGrade() +
				" - " + (bond.getActiveTime() / 60000) + " minutes");
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
			logger.log(Level.WARNING, "Error getting bond grade", e);
			return 1;
		}
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

		logger.info("Transferring bond: " + fromPet.name() + " -> " + toPet.name() +
			" - " + (newBond.getActiveTime() / 60000) + " minutes");

		// Update bond mapping
		petBonds.remove(fromPet);
		dataManager.deletePetBond(fromPet);
		petBonds.put(toPet, newBond);
	}

	/**
	 * Get all pet bonds
	 */
	public Map<PetData, PetBond> getAllBonds() {
		return new HashMap<>(petBonds);
	}

	/**
	 * Load bonds from data manager
	 */
	public void loadBonds(Map<PetData, PetBond> bonds) {
		logger.info("Loading " + bonds.size() + " bonds for " + stoner.getUsername());
		petBonds.clear();
		petBonds.putAll(bonds);

		for (Map.Entry<PetData, PetBond> entry : petBonds.entrySet()) {
			PetBond bond = entry.getValue();
			logger.info("Loaded: " + entry.getKey().name() +
				" - Grade " + bond.getBondGrade() +
				" - " + (bond.getActiveTime() / 60000) + " minutes");
		}
	}

	/**
	 * Force save all bonds
	 */
	public void forceSaveAllBonds() {
		try {
			for (Map.Entry<PetData, PetBond> entry : petBonds.entrySet()) {
				dataManager.savePetBond(entry.getKey(), entry.getValue());
			}
			logger.info("Saved all " + petBonds.size() + " bonds for " + stoner.getUsername());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error saving all bonds", e);
		}
	}
}