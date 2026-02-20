package com.bestbudz.rs2.entity.pets;

import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PetUtils {

	public static String formatPetDisplayName(PetData petData) {
		if (petData == null) {
			return "Pet Unknown";
		}

		String name = petData.name();
		return Arrays.stream(name.split("_"))
			.map(word -> word.charAt(0) + word.substring(1).toLowerCase())
			.collect(Collectors.joining(" "));
	}

	public static boolean isPetUsername(String username) {
		return username != null && username.startsWith("Pet_");
	}

	public static PetData getPetDataFromUsername(String username) {
		if (!isPetUsername(username)) {
			return null;
		}

		String[] parts = username.split("_");
		if (parts.length >= 3) {
			String petName = String.join("_", Arrays.copyOfRange(parts, 2, parts.length));
			try {
				return PetData.valueOf(petName);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
		return null;
	}

	public static boolean isPetStoner(Stoner stoner) {
		return stoner != null && stoner.isPet() && stoner.getUsername().startsWith("Pet_");
	}
}
