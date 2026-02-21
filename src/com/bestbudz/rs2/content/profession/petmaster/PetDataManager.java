package com.bestbudz.rs2.content.profession.petmaster;

import com.bestbudz.rs2.content.profession.petmaster.bond.PetBond;
import com.bestbudz.rs2.entity.pets.PetData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class PetDataManager {

	private static final String SAVE_DIRECTORY = "./data/profession/petmaster/";
	private static final String FILE_EXTENSION = ".json";
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private final com.bestbudz.rs2.entity.stoner.Stoner stoner;

	static {

		try {
			Files.createDirectories(Paths.get(SAVE_DIRECTORY));
		} catch (IOException e) {
			System.err.println("Failed to create petmaster save directory: " + e.getMessage());
		}
	}

	public PetDataManager(com.bestbudz.rs2.entity.stoner.Stoner stoner) {
		this.stoner = stoner;
	}

	public void savePetBond(PetData petData, PetBond bond) {
		if (stoner == null || stoner.getUsername() == null || petData == null || bond == null) return;

		Map<PetData, PetBond> bonds = loadData();
		bonds.put(petData, bond);
		saveData(bonds);
	}

	public Map<PetData, PetBond> loadData() {
		Map<PetData, PetBond> bonds = new HashMap<>();

		if (stoner == null || stoner.getUsername() == null) {
			return bonds;
		}

		String filename = SAVE_DIRECTORY + stoner.getUsername().toLowerCase() + FILE_EXTENSION;
		Path filePath = Paths.get(filename);

		if (!Files.exists(filePath)) {

			return bonds;
		}

		try (FileReader reader = new FileReader(filename)) {
			Type type = new TypeToken<Map<String, PetBondData>>() {}.getType();
			Map<String, PetBondData> data = gson.fromJson(reader, type);

			if (data == null) return bonds;

			for (PetData pet : PetData.values()) {
				PetBondData bondData = data.get(pet.name());
				if (bondData != null) {
					PetBond bond = new PetBond();
					bond.setExperience(bondData.experience);
					bond.setBondGrade(bondData.bondGrade);
					bond.setActiveTime(bondData.activeTime);
					bond.setFirstSummoned(bondData.firstSummoned);
					bonds.put(pet, bond);
				}
			}

		} catch (IOException e) {
			System.err.println("Failed to load petmaster data for " + stoner.getUsername() + ": " + e.getMessage());
		}

		return bonds;
	}

	public void saveData(Map<PetData, PetBond> bonds) {
		if (stoner == null || stoner.getUsername() == null || bonds == null) return;

		String filename = SAVE_DIRECTORY + stoner.getUsername().toLowerCase() + FILE_EXTENSION;

		try {
			Map<String, PetBondData> data = new HashMap<>();

			for (Map.Entry<PetData, PetBond> entry : bonds.entrySet()) {
				PetData petData = entry.getKey();
				PetBond bond = entry.getValue();

				if (petData != null && bond != null) {
					PetBondData bondData = new PetBondData();
					bondData.experience = bond.getExperience();
					bondData.bondGrade = bond.getBondGrade();
					bondData.activeTime = bond.getActiveTime();
					bondData.firstSummoned = bond.getFirstSummoned();
					data.put(petData.name(), bondData);
				}
			}

			try (FileWriter writer = new FileWriter(filename)) {
				gson.toJson(data, writer);
			}

		} catch (IOException e) {
			System.err.println("Failed to batch save petmaster data for " + stoner.getUsername() + ": " + e.getMessage());
		}
	}

	public void deletePetBond(PetData petData) {
		if (stoner == null || stoner.getUsername() == null || petData == null) return;

		Map<PetData, PetBond> bonds = loadData();
		bonds.remove(petData);
		saveData(bonds);
	}

	private static class PetBondData {
		double experience;
		int bondGrade;
		long activeTime;
		long firstSummoned;
	}
}
