package com.bestbudz.rs2.content.profession.consumer.io;

import com.bestbudz.rs2.content.profession.consumer.allergies.AllergySystem;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConsumerSaveManager {

	private static final String SAVE_DIRECTORY = "./data/profession/consumer/";
	private static final String FILE_EXTENSION = ".properties";

	static {

		try {
			Files.createDirectories(Paths.get(SAVE_DIRECTORY));
		} catch (IOException e) {
			System.err.println("Failed to create consumer save directory: " + e.getMessage());
		}
	}

	public static void saveConsumerData(Stoner stoner) {
		if (stoner.getUsername() == null) return;

		String filename = SAVE_DIRECTORY + stoner.getUsername().toLowerCase() + FILE_EXTENSION;
		Properties props = new Properties();

		try {

			AllergySystem allergySystem = stoner.getAllergySystem();
			if (allergySystem != null) {
				Map<AllergySystem.AllergyType, Integer> resistance = allergySystem.getResistance();

				for (Map.Entry<AllergySystem.AllergyType, Integer> entry : resistance.entrySet()) {
					props.setProperty("allergy.resistance." + entry.getKey().name(),
						entry.getValue().toString());
				}
			}

			try (FileOutputStream fos = new FileOutputStream(filename)) {
				props.store(fos, "Consumer Profession Data for " + stoner.getUsername());
			}

		} catch (IOException e) {
			System.err.println("Failed to save consumer data for " + stoner.getUsername() + ": " + e.getMessage());
		}
	}

	public static void loadConsumerData(Stoner stoner) {
		if (stoner.getUsername() == null) return;

		String filename = SAVE_DIRECTORY + stoner.getUsername().toLowerCase() + FILE_EXTENSION;
		Path filePath = Paths.get(filename);

		if (!Files.exists(filePath)) {

			return;
		}

		Properties props = new Properties();

		try (FileInputStream fis = new FileInputStream(filename)) {
			props.load(fis);

			AllergySystem allergySystem = stoner.getAllergySystem();
			if (allergySystem != null) {
				Map<AllergySystem.AllergyType, Integer> resistance = new HashMap<>();

				for (AllergySystem.AllergyType allergyType : AllergySystem.AllergyType.values()) {
					String key = "allergy.resistance." + allergyType.name();
					String value = props.getProperty(key);

					if (value != null) {
						try {
							resistance.put(allergyType, Integer.parseInt(value));
						} catch (NumberFormatException e) {
							System.err.println("Invalid resistance value for " + allergyType + ": " + value);
						}
					}
				}

				allergySystem.setResistance(resistance);
			}

		} catch (IOException e) {
			System.err.println("Failed to load consumer data for " + stoner.getUsername() + ": " + e.getMessage());
		}
	}

	public static void deleteConsumerData(String username) {
		if (username == null) return;

		String filename = SAVE_DIRECTORY + username.toLowerCase() + FILE_EXTENSION;
		Path filePath = Paths.get(filename);

		try {
			Files.deleteIfExists(filePath);
		} catch (IOException e) {
			System.err.println("Failed to delete consumer data for " + username + ": " + e.getMessage());
		}
	}

	public static boolean hasConsumerSaveData(String username) {
		if (username == null) return false;

		String filename = SAVE_DIRECTORY + username.toLowerCase() + FILE_EXTENSION;
		return Files.exists(Paths.get(filename));
	}
}
