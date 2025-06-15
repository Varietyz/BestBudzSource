package com.bestbudz.rs2.entity.pets;

import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.List;
import java.util.ArrayList;

/**
 * Handles pet formation and positioning to prevent overlap
 */
public class PetFormation {

	/**
	 * Get formation position for a pet based on owner location and pet index
	 */
	public static Location getFormationPosition(Stoner owner, int petIndex, int totalPets) {
		Location ownerLoc = owner.getLocation();

		if (totalPets == 1) {
			// Single pet - follow directly behind
			return new Location(ownerLoc.getX(), ownerLoc.getY() - 1, ownerLoc.getZ());
		}

		// Multiple pets - arrange in formation
		int[][] formations = getFormationPattern(totalPets);
		if (petIndex < formations.length) {
			int offsetX = formations[petIndex][0];
			int offsetY = formations[petIndex][1];

			return new Location(
				ownerLoc.getX() + offsetX,
				ownerLoc.getY() + offsetY,
				ownerLoc.getZ()
			);
		}

		// Fallback - arrange in a line behind owner
		return new Location(ownerLoc.getX(), ownerLoc.getY() - (petIndex + 1), ownerLoc.getZ());
	}

	/**
	 * Get formation patterns for different numbers of pets
	 */
	private static int[][] getFormationPattern(int petCount) {
		switch (petCount) {
			case 2:
				return new int[][]{{-1, -1}, {1, -1}}; // Left-back, Right-back
			case 3:
				return new int[][]{{0, -1}, {-1, -1}, {1, -1}}; // Behind, Left-back, Right-back
			case 4:
				return new int[][]{{-1, -1}, {1, -1}, {-1, -2}, {1, -2}}; // 2x2 formation
			case 5:
				return new int[][]{{0, -1}, {-1, -1}, {1, -1}, {-2, -2}, {2, -2}}; // V formation
			default:
				// For more pets, arrange in rows
				List<int[]> positions = new ArrayList<>();
				int row = 1;
				int col = 0;
				int maxCols = 3;

				for (int i = 0; i < petCount; i++) {
					positions.add(new int[]{col - 1, -row});
					col++;
					if (col >= maxCols) {
						col = 0;
						row++;
					}
				}

				return positions.toArray(new int[0][]);
		}
	}

	/**
	 * Check if a location is occupied by another pet belonging to the same owner
	 */
	public static boolean isLocationOccupiedByPet(Stoner owner, Location targetLocation, Stoner excludePet) {
		for (Pet pet : owner.getActivePets()) {
			if (pet.getPetStoner() == excludePet) {
				continue; // Skip the pet we're checking for
			}

			Location petLoc = pet.getPetStoner().getLocation();
			if (petLoc.equals(targetLocation)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Find the nearest available formation position
	 */
	public static Location findAvailableFormationPosition(Stoner owner, int petIndex) {
		List<Pet> activePets = owner.getActivePets();
		Location basePosition = getFormationPosition(owner, petIndex, activePets.size());

		// Check if base position is available
		if (!isLocationOccupiedByPet(owner, basePosition, null)) {
			return basePosition;
		}

		// Find alternative position in a spiral pattern
		for (int radius = 1; radius <= 3; radius++) {
			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					if (Math.abs(x) != radius && Math.abs(y) != radius) {
						continue; // Only check perimeter
					}

					Location altPosition = new Location(
						basePosition.getX() + x,
						basePosition.getY() + y,
						basePosition.getZ()
					);

					if (!isLocationOccupiedByPet(owner, altPosition, null)) {
						return altPosition;
					}
				}
			}
		}

		return basePosition; // Fallback to base position
	}
}