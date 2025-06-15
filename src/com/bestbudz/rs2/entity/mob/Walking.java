package com.bestbudz.rs2.entity.mob;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.content.profession.summoning.FamiliarMob;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.pathfinding.SimplePathWalker;

public class Walking {

	private static Region region = null;

	public static boolean canMoveTo(Mob mob, int direction) {
		if (direction == -1) {
			return false;
		}

		int x = mob.getLocation().getX();
		int y = mob.getLocation().getY();
		int z = mob.getLocation().getZ() > 3 ? mob.getLocation().getZ() % 4 : mob.getLocation().getZ();

		boolean virtual = mob.inVirtualRegion();
		VirtualMobRegion region = mob.getVirtualRegion();

		int x5 = mob.getLocation().getX() + GameConstants.DIR[direction][0];
		int y5 = mob.getLocation().getY() + GameConstants.DIR[direction][1];

		int size = mob.getSize();

		boolean familiar = mob instanceof FamiliarMob;

		for (int i = 1; i < size + 1; i++) {
			for (int k = 0; k < GameConstants.SIZES[i].length; k++) {
				int x3 = x + GameConstants.SIZES[i][k][0];
				int y3 = y + GameConstants.SIZES[i][k][1];

				int x2 = x5 + GameConstants.SIZES[i][k][0];
				int y2 = y5 + GameConstants.SIZES[i][k][1];

				if (GameConstants.withinBlock(x, y, size, x2, y2)) {
					continue;
				}

				if ((familiar) && (mob.getOwner().getX() == x2) && (mob.getOwner().getY() == y2)) {
					return false;
				}

				Region r = getRegion(x3, y3);

				if (r == null) {
					mob.remove();
					return false;
				}

				if (!r.canMove(x3, y3, z, direction)) {
					return false;
				}

				if (!virtual) {
					if (getRegion(x2, y2).isNpcOnTile(x2, y2, z)) return false;
				} else {
					if (region.isMobOnTile(x2, y2, z)) return false;
				}

				for (int j = 0; j < 8; j++) {
					if ((GameConstants.withinBlock(
						x5, y5, size, x2 + GameConstants.DIR[j][0], x2 + GameConstants.DIR[j][1]))
						&& (!Region.getRegion(x2, x2).canMove(x2, x2, z, j))) {
						return false;
					}
				}
			}
		}

		if (GameConstants.DIR[direction][0] != 0 && GameConstants.DIR[direction][1] != 0) {
			return canMoveTo(mob, GameConstants.getDirection(GameConstants.DIR[direction][0], 0))
				&& canMoveTo(mob, GameConstants.getDirection(0, GameConstants.DIR[direction][1]));
		}

		return true;
	}

	public static boolean canMoveTo(Mob mob, int x, int y, int direction, int size) {
		if (direction == -1) {
			return false;
		}

		int z = mob.getLocation().getZ() > 3 ? mob.getLocation().getZ() % 4 : mob.getLocation().getZ();

		int x5 = mob.getLocation().getX() + GameConstants.DIR[direction][0];
		int y5 = mob.getLocation().getY() + GameConstants.DIR[direction][1];

		int x4 = 0;
		int y4 = 0;

		for (int i = 1; i < size + 1; i++) {
			for (int k = 0; k < GameConstants.SIZES[i].length; k++) {
				int x3 = x + GameConstants.SIZES[i][k][0];
				int y3 = y + GameConstants.SIZES[i][k][1];

				int x2 = x5 + GameConstants.SIZES[i][k][0];
				int y2 = y5 + GameConstants.SIZES[i][k][1];

				if (!GameConstants.withinBlock(x, y, size, x2, y2)) {
					if (!getRegion(x3, y3).canMove(x3, y3, z, direction)) {
						return false;
					}

					if ((x2 == x4) && (y2 == y4)) {
						return false;
					}

					for (int j = 0; j < 8; j++) {
						int x6 = x3 + GameConstants.DIR[j][0];
						int y6 = y3 + GameConstants.DIR[j][1];

						if ((GameConstants.withinBlock(x5, y5, size, x6, y6))
							&& (!getRegion(x3, y3).canMove(x3, y3, z, j))) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	public static int getRandomDirection(Mob mob) {
		int count = 0;

		byte[] walkable = new byte[8];

		for (byte i = 0; i < 8; i = (byte) (i + 1)) {
			if ((withinMaxDistance(mob, i)) && (mob.getMovementHandler().canMoveTo(i))) {
				walkable[count] = i;
				count++;
			}
		}

		if (count > 0) {
			return walkable[Utility.randomNumber(count)];
		}

		return -1;
	}

	/**
	 * ENHANCED: Gets direction toward spawn location for patrol behavior
	 */
	public static int getDirectionTowardSpawn(Mob mob) {
		Location spawn = mob.getSpawnLocation();
		Location current = mob.getLocation();

		// If already at spawn, pick a random direction
		if (current.equals(spawn)) {
			return getRandomDirection(mob);
		}

		// Calculate direction toward spawn
		int deltaX = Integer.compare(spawn.getX(), current.getX());
		int deltaY = Integer.compare(spawn.getY(), current.getY());

		// Find the best direction toward spawn
		int[] preferredDirections = new int[3];
		int dirCount = 0;

		// Primary direction (diagonal if both deltas exist)
		if (deltaX != 0 && deltaY != 0) {
			int primaryDir = GameConstants.getDirection(current.getX(), current.getY(),
				current.getX() + deltaX, current.getY() + deltaY);
			if (primaryDir != -1 && withinMaxDistance(mob, primaryDir) && mob.getMovementHandler().canMoveTo(primaryDir)) {
				preferredDirections[dirCount++] = primaryDir;
			}
		}

		// Secondary directions (horizontal and vertical)
		if (deltaX != 0) {
			int horizontalDir = GameConstants.getDirection(current.getX(), current.getY(),
				current.getX() + deltaX, current.getY());
			if (horizontalDir != -1 && withinMaxDistance(mob, horizontalDir) && mob.getMovementHandler().canMoveTo(horizontalDir)) {
				preferredDirections[dirCount++] = horizontalDir;
			}
		}

		if (deltaY != 0) {
			int verticalDir = GameConstants.getDirection(current.getX(), current.getY(),
				current.getX(), current.getY() + deltaY);
			if (verticalDir != -1 && withinMaxDistance(mob, verticalDir) && mob.getMovementHandler().canMoveTo(verticalDir)) {
				preferredDirections[dirCount++] = verticalDir;
			}
		}

		// Return best available direction, or random if none work
		if (dirCount > 0) {
			return preferredDirections[Utility.randomNumber(dirCount)];
		}

		return getRandomDirection(mob);
	}

	/**
	 * ENHANCED: Gets direction away from crowded areas to prevent clustering
	 */
	public static int getDirectionAwayFromCrowd(Mob mob) {
		Location current = mob.getLocation();
		int[] crowdDensity = new int[8]; // Density in each direction

		// Check each direction for nearby mobs
		for (int dir = 0; dir < 8; dir++) {
			if (!withinMaxDistance(mob, dir) || !mob.getMovementHandler().canMoveTo(dir)) {
				crowdDensity[dir] = 999; // Block unusable directions
				continue;
			}

			int checkX = current.getX() + GameConstants.DIR[dir][0];
			int checkY = current.getY() + GameConstants.DIR[dir][1];

			// Count nearby mobs in a 3x3 area around this direction
			int nearbyMobs = 0;
			for (int offsetX = -1; offsetX <= 1; offsetX++) {
				for (int offsetY = -1; offsetY <= 1; offsetY++) {
					int scanX = checkX + offsetX;
					int scanY = checkY + offsetY;

					// Count mobs at this location
					for (Mob otherMob : World.getNpcs()) {
						if (otherMob != null && otherMob != mob && otherMob.isActive()) {
							if (otherMob.getLocation().getX() == scanX &&
								otherMob.getLocation().getY() == scanY &&
								otherMob.getLocation().getZ() == current.getZ()) {
								nearbyMobs++;
							}
						}
					}
				}
			}

			crowdDensity[dir] = nearbyMobs;
		}

		// Find direction(s) with lowest crowd density
		int minDensity = 999;
		for (int density : crowdDensity) {
			if (density < minDensity) {
				minDensity = density;
			}
		}

		// Collect all directions with minimum density
		byte[] bestDirections = new byte[8];
		int bestCount = 0;
		for (int dir = 0; dir < 8; dir++) {
			if (crowdDensity[dir] == minDensity) {
				bestDirections[bestCount++] = (byte) dir;
			}
		}

		// Return random direction from the least crowded options
		if (bestCount > 0) {
			return bestDirections[Utility.randomNumber(bestCount)];
		}

		return getRandomDirection(mob);
	}

	public static Region getRegion(int x, int y) {
		if ((region == null) || (!region.withinRegion(x, y))) {
			region = Region.getRegion(x, y);
		}

		return region;
	}

	/**
	 * ENHANCED: Smart walking with purposeful movement patterns
	 */
	public static void randomWalk(Mob mob) {
		// Special behavior for specific mob types with increased frequency
		if (mob.getId() == 6064
			|| mob.getId() == 6063
			|| mob.getId() == 1035
			|| mob.getId() == 1034
			|| mob.getId() == 1033
			|| mob.getId() == 1032
			|| mob.getId() == 1031
			|| mob.getId() == 1030
			|| mob.getId() == 1029
			|| mob.getId() == 1028) {
			if (Utility.randomNumber(2) == 1) { // Much more frequent for testing
				int dir = getRandomDirection(mob);
				if (dir != -1)
					SimplePathWalker.walkToNextTile(
						mob,
						new Location(
							mob.getX() + GameConstants.DIR[dir][0], mob.getY() + GameConstants.DIR[dir][1]));
			}
			return;
		}

		// ENHANCED: Much more frequent smart walking for testing visibility
		// Increased from 1/25 to 1/3 chance to see the intelligence clearly
		if (Utility.randomNumber(3) == 0) {

			// Determine movement behavior based on weighted probabilities:
			// 50% - Patrol toward spawn area (territorial behavior)
			// 30% - Avoid crowded areas (prevent clustering)
			// 20% - Random exploration

			int behavior = Utility.randomNumber(100);
			int dir = -1;

			if (behavior < 50) {
				// Patrol behavior - move toward spawn area
				dir = getDirectionTowardSpawn(mob);
			} else if (behavior < 80) {
				// Anti-clustering behavior - move away from crowds
				dir = getDirectionAwayFromCrowd(mob);
			} else {
				// Random exploration - maintain some unpredictability
				dir = getRandomDirection(mob);
			}

			// Execute the movement
			if (dir != -1) {
				SimplePathWalker.walkToNextTile(
					mob,
					new Location(
						mob.getX() + GameConstants.DIR[dir][0],
						mob.getY() + GameConstants.DIR[dir][1]));
			}
		}
	}

	public static void setNpcOnTile(Mob mob, boolean set) {
		int x = mob.getLocation().getX();
		int y = mob.getLocation().getY();
		int z = mob.getLocation().getZ() > 3 ? mob.getLocation().getZ() % 4 : mob.getLocation().getZ();

		boolean virtual = mob.inVirtualRegion();
		VirtualMobRegion region = mob.getVirtualRegion();

		int size = mob.getSize();

		for (int i = 1; i < size + 1; i++)
			for (int k = 0; k < GameConstants.SIZES[i].length; k++) {
				int x2 = x + GameConstants.SIZES[i][k][0];
				int y2 = y + GameConstants.SIZES[i][k][1];
				if (!virtual) {
					Region r = getRegion(x2, y2);

					if (r == null) {
						return;
					}

					r.setNpcOnTile(set, x2, y2, z);
				} else {
					region.setMobOnTile(x2, y2, z, set);
				}
			}
	}

	public static void walk(Mob mob, int dir) {
		if ((dir == -1)
			|| (mob.isPlacement())
			|| (mob.isFrozen())
			|| (mob.isStunned())
			|| (mob.getMovementHandler().getPrimaryDirection() != -1)) {
			return;
		}

		setNpcOnTile(mob, false);

		mob.getMovementHandler().setPrimaryDirection(dir);

		mob.getMovementHandler().getLastLocation().setAs(mob.getLocation());

		mob.getLocation().move(GameConstants.DIR[dir][0], GameConstants.DIR[dir][1]);

		setNpcOnTile(mob, true);

		mob.getUpdateFlags().setUpdateRequired(true);

		mob.getMovementHandler().setPrimaryDirection(dir);
	}

	public static boolean withinMaxDistance(Mob mob, int direction) {
		int yMax;
		int xMax;
		int yMin;
		int xMin;

		// ENHANCED: More reasonable movement ranges for intelligent behavior
		if (mob.getId() == 6064
			|| mob.getId() == 6063
			|| mob.getId() == 1035
			|| mob.getId() == 1034
			|| mob.getId() == 1033
			|| mob.getId() == 1032
			|| mob.getId() == 1031
			|| mob.getId() == 1030
			|| mob.getId() == 1029
			|| mob.getId() == 1028) {
			// Special mobs keep their large range
			xMax = mob.getSpawnLocation().getX() + 15;
			yMax = mob.getSpawnLocation().getY() + 15;
			xMin = mob.getSpawnLocation().getX() - 15;
			yMin = mob.getSpawnLocation().getY() - 15;
		} else {
			// INCREASED: Regular mobs now get 10-tile radius for very visible patrol behavior
			int range = 10;
			xMax = mob.getSpawnLocation().getX() + range;
			yMax = mob.getSpawnLocation().getY() + range;
			xMin = mob.getSpawnLocation().getX() - range;
			yMin = mob.getSpawnLocation().getY() - range;
		}

		int newX = mob.getLocation().getX() + GameConstants.DIR[direction][0];
		int newY = mob.getLocation().getY() + GameConstants.DIR[direction][1];
		return (newX <= xMax) && (newY <= yMax) && (newX >= xMin) && (newY >= yMin);
	}
}