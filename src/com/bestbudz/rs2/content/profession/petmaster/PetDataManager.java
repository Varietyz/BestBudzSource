package com.bestbudz.rs2.content.profession.petmaster;

import com.bestbudz.rs2.content.profession.petmaster.bond.PetBond;
import com.bestbudz.rs2.entity.pets.PetData;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PetDataManager {

	private static final String SAVE_DIRECTORY = "./data/profession/petmaster/";
	private static final String FILE_EXTENSION = ".properties";

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

		String filename = SAVE_DIRECTORY + stoner.getUsername().toLowerCase() + FILE_EXTENSION;
		Properties props = new Properties();

		try {

			Path filePath = Paths.get(filename);
			if (Files.exists(filePath)) {
				try (FileInputStream fis = new FileInputStream(filename)) {
					props.load(fis);
				}
			}

			String petPrefix = "pet.bond." + petData.name() + ".";
			props.setProperty(petPrefix + "experience", String.valueOf(bond.getExperience()));
			props.setProperty(petPrefix + "bondGrade", String.valueOf(bond.getBondGrade()));
			props.setProperty(petPrefix + "activeTime", String.valueOf(bond.getActiveTime()));
			props.setProperty(petPrefix + "firstSummoned", String.valueOf(bond.getFirstSummoned()));

			try (FileOutputStream fos = new FileOutputStream(filename)) {
				props.store(fos, "PetMaster Profession Data for " + stoner.getUsername());
			}

		} catch (IOException e) {
			System.err.println("Failed to save pet bond for " + stoner.getUsername() +
				" with pet " + petData.name() + ": " + e.getMessage());
		}
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

		Properties props = new Properties();

		try (FileInputStream fis = new FileInputStream(filename)) {
			props.load(fis);

			for (PetData petData : PetData.values()) {
				String petPrefix = "pet.bond." + petData.name() + ".";

				if (props.getProperty(petPrefix + "experience") != null) {
					PetBond bond = new PetBond();

					try {
						double experience = Double.parseDouble(props.getProperty(petPrefix + "experience", "0.0"));
						int bondGrade = Integer.parseInt(props.getProperty(petPrefix + "bondGrade", "1"));
						long activeTime = Long.parseLong(props.getProperty(petPrefix + "activeTime", "0"));
						long firstSummoned = Long.parseLong(props.getProperty(petPrefix + "firstSummoned", "0"));

						bond.setExperience(experience);
						bond.setBondGrade(bondGrade);
						bond.setActiveTime(activeTime);
						bond.setFirstSummoned(firstSummoned);

						bonds.put(petData, bond);

					} catch (NumberFormatException e) {
						System.err.println("Invalid pet bond data for " + petData.name() + " for user " + stoner.getUsername());
					}
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
		Properties props = new Properties();

		try {

			for (Map.Entry<PetData, PetBond> entry : bonds.entrySet()) {
				PetData petData = entry.getKey();
				PetBond bond = entry.getValue();

				if (petData != null && bond != null) {
					String petPrefix = "pet.bond." + petData.name() + ".";
					props.setProperty(petPrefix + "experience", String.valueOf(bond.getExperience()));
					props.setProperty(petPrefix + "bondGrade", String.valueOf(bond.getBondGrade()));
					props.setProperty(petPrefix + "activeTime", String.valueOf(bond.getActiveTime()));
					props.setProperty(petPrefix + "firstSummoned", String.valueOf(bond.getFirstSummoned()));
				}
			}

			try (FileOutputStream fos = new FileOutputStream(filename)) {
				props.store(fos, "PetMaster Profession Data for " + stoner.getUsername());
			}

		} catch (IOException e) {
			System.err.println("Failed to batch save petmaster data for " + stoner.getUsername() + ": " + e.getMessage());
		}
	}

	public void deletePetBond(PetData petData) {
		if (stoner == null || stoner.getUsername() == null || petData == null) return;

		String filename = SAVE_DIRECTORY + stoner.getUsername().toLowerCase() + FILE_EXTENSION;
		Properties props = new Properties();

		try {

			Path filePath = Paths.get(filename);
			if (Files.exists(filePath)) {
				try (FileInputStream fis = new FileInputStream(filename)) {
					props.load(fis);
				}
			}

			String petPrefix = "pet.bond." + petData.name() + ".";
			props.remove(petPrefix + "experience");
			props.remove(petPrefix + "bondGrade");
			props.remove(petPrefix + "activeTime");
			props.remove(petPrefix + "firstSummoned");

			try (FileOutputStream fos = new FileOutputStream(filename)) {
				props.store(fos, "PetMaster Profession Data for " + stoner.getUsername());
			}

		} catch (IOException e) {
			System.err.println("Failed to delete pet bond for " + stoner.getUsername() +
				" with pet " + petData.name() + ": " + e.getMessage());
		}
	}
}
