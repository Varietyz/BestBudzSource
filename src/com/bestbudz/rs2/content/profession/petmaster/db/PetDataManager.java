package com.bestbudz.rs2.content.profession.petmaster.db;

import com.bestbudz.rs2.content.profession.petmaster.bond.PetBond;
import com.bestbudz.rs2.entity.pets.PetData;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * OPTIMIZED: Batched data manager with async processing
 */
public class PetDataManager {
	private final PetMasterDatabase database;
	private final com.bestbudz.rs2.entity.stoner.Stoner stoner;

	public PetDataManager(com.bestbudz.rs2.entity.stoner.Stoner stoner) {
		this.stoner = stoner;
		this.database = PetMasterDatabase.getInstance();
	}

	/**
	 * Save individual pet bond - called by PetBondManager
	 */
	public void savePetBond(PetData petData, PetBond bond) {
		if (stoner == null || stoner.getUsername() == null) return;

		int stonerId = database.getStonerId(stoner.getUsername());
		if (stonerId != -1) {
			database.savePetBond(stonerId, petData, bond);
		}
	}

	/**
	 * Load all data for this stoner
	 */
	public Map<PetData, PetBond> loadData() {
		Map<PetData, PetBond> bonds = new HashMap<>();

		if (stoner == null || stoner.getUsername() == null) {
			return bonds;
		}

		int stonerId = database.getStonerId(stoner.getUsername());
		if (stonerId != -1) {
			for (PetData petData : PetData.values()) {
				PetBond bond = database.loadPetBond(stonerId, petData);
				if (bond.getExperience() > 0) {
					bonds.put(petData, bond);
				}
			}
		}

		return bonds;
	}

	/**
	 * Batch save data
	 */
	public void saveData(Map<PetData, PetBond> bonds) {
		if (stoner == null || stoner.getUsername() == null) return;

		int stonerId = database.getStonerId(stoner.getUsername());
		if (stonerId != -1) {
			database.batchSave(stonerId, bonds, new HashMap<>());
		}
	}

	/**
	 * Load individual pet bond
	 */
	public PetBond loadPetBond(PetData petData) {
		if (stoner == null || stoner.getUsername() == null) {
			return new PetBond();
		}

		int stonerId = database.getStonerId(stoner.getUsername());
		if (stonerId != -1) {
			return database.loadPetBond(stonerId, petData);
		}

		return new PetBond();
	}

	/**
	 * Compatibility methods for existing code
	 */
	public Map<String, Object> getStonerStats() {
		return database.getStonerStats(database.getStonerId(stoner.getUsername()));
	}

	public void logGrowth(PetData fromPet, PetData toPet, int bondGrade, int stonerGrade) {
		if (stoner != null && stoner.getUsername() != null) {
			int stonerId = database.getStonerId(stoner.getUsername());
			if (stonerId != -1) {
				database.logGrowth(stonerId, fromPet, toPet, bondGrade, stonerGrade);
			}
		}
	}

	public void logActivity(PetData petData, String activityType, String activityData, double expGained) {
		if (stoner != null && stoner.getUsername() != null) {
			int stonerId = database.getStonerId(stoner.getUsername());
			if (stonerId != -1) {
				database.logActivity(stonerId, petData, activityType, activityData, expGained);
			}
		}
	}
}