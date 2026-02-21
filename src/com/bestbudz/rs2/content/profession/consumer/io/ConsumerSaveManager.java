package com.bestbudz.rs2.content.profession.consumer.io;

import com.bestbudz.rs2.content.profession.consumer.allergies.AllergySystem;
import com.bestbudz.rs2.entity.stoner.Stoner;
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

public class ConsumerSaveManager {

	private static final String SAVE_DIRECTORY = "./data/profession/consumer/";
	private static final String FILE_EXTENSION = ".json";
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

		try {
			Map<String, Integer> data = new HashMap<>();

			AllergySystem allergySystem = stoner.getAllergySystem();
			if (allergySystem != null) {
				Map<AllergySystem.AllergyType, Integer> resistance = allergySystem.getResistance();

				for (Map.Entry<AllergySystem.AllergyType, Integer> entry : resistance.entrySet()) {
					data.put(entry.getKey().name(), entry.getValue());
				}
			}

			try (FileWriter writer = new FileWriter(filename)) {
				gson.toJson(data, writer);
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

		try (FileReader reader = new FileReader(filename)) {
			Type type = new TypeToken<Map<String, Integer>>() {}.getType();
			Map<String, Integer> data = gson.fromJson(reader, type);

			if (data == null) return;

			AllergySystem allergySystem = stoner.getAllergySystem();
			if (allergySystem != null) {
				Map<AllergySystem.AllergyType, Integer> resistance = new HashMap<>();

				for (AllergySystem.AllergyType allergyType : AllergySystem.AllergyType.values()) {
					Integer value = data.get(allergyType.name());
					if (value != null) {
						resistance.put(allergyType, value);
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
