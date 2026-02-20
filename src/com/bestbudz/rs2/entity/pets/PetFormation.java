package com.bestbudz.rs2.entity.pets;

import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.List;
import java.util.ArrayList;

public class PetFormation {

	public static Location getFormationPosition(Stoner owner, int petIndex, int totalPets) {
		Location ownerLoc = owner.getLocation();

		if (totalPets == 1) {

			return new Location(ownerLoc.getX(), ownerLoc.getY() - 1, ownerLoc.getZ());
		}

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

		return new Location(ownerLoc.getX(), ownerLoc.getY() - (petIndex + 1), ownerLoc.getZ());
	}

	private static int[][] getFormationPattern(int petCount) {
		switch (petCount) {
			case 2:
				return new int[][]{{-1, -1}, {1, -1}};
			case 3:
				return new int[][]{{0, -1}, {-1, -1}, {1, -1}};
			case 4:
				return new int[][]{{-1, -1}, {1, -1}, {-1, -2}, {1, -2}};
			case 5:
				return new int[][]{{0, -1}, {-1, -1}, {1, -1}, {-2, -2}, {2, -2}};
			default:

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

	public static boolean isLocationOccupiedByPet(Stoner owner, Location targetLocation, Stoner excludePet) {
		for (Pet pet : owner.getActivePets()) {
			if (pet.getPetStoner() == excludePet) {
				continue;
			}

			Location petLoc = pet.getPetStoner().getLocation();
			if (petLoc.equals(targetLocation)) {
				return true;
			}
		}
		return false;
	}

	public static Location findAvailableFormationPosition(Stoner owner, int petIndex) {
		List<Pet> activePets = owner.getActivePets();
		Location basePosition = getFormationPosition(owner, petIndex, activePets.size());

		if (!isLocationOccupiedByPet(owner, basePosition, null)) {
			return basePosition;
		}

		for (int radius = 1; radius <= 3; radius++) {
			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					if (Math.abs(x) != radius && Math.abs(y) != radius) {
						continue;
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

		return basePosition;
	}
}
