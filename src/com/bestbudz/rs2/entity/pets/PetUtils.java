package com.bestbudz.rs2.entity.pets;

import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utility methods for pet operations
 */
public class PetUtils {

	/**
	 * Format pet name for display (e.g. "GIANT_EAGLE" -> "Pet Giant Eagle")
	 */
	public static String formatPetDisplayName(PetData petData) {
		if (petData == null) {
			return "Pet Unknown";
		}

		String name = petData.name();
		return Arrays.stream(name.split("_"))
			.map(word -> word.charAt(0) + word.substring(1).toLowerCase())
			.collect(Collectors.joining(" "));
	}

	/**
	 * Generate unique pet username for internal systems
	 */
	public static String generatePetUsername(Stoner owner, PetData petData) {
		return "Pet_" + owner.getUsernameToLong() + "_" + petData.name();
	}

	/**
	 * Check if a username belongs to a pet
	 */
	public static boolean isPetUsername(String username) {
		return username != null && username.startsWith("Pet_");
	}

	/**
	 * Extract pet data from username
	 */
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

	/**
	 * Check if a Stoner is a pet
	 */
	public static boolean isPetStoner(Stoner stoner) {
		return stoner != null && stoner.isPet() && stoner.getUsername().startsWith("Pet_");
	}
}