package com.bestbudz.rs2.entity.movement;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.Walking;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class MobMovementHandler extends MovementHandler {
	private final Mob mob;

	public MobMovementHandler(Mob mob) {
		super(mob);
		this.mob = mob;
	}

	@Override
	public boolean canMoveTo(int direction) {
		return Walking.canMoveTo(mob, direction);
	}

	@Override
	public boolean canMoveTo(int x, int y, int size, int direction) {
		return Walking.canMoveTo(mob, x, y, direction, size);
	}

	@Override
	public boolean moving() {
		return mob.getFollowing().isFollowing();
	}

	/**
	 * ENHANCED: Smart movement processing with collision avoidance and spacing
	 */
	@Override
	public void process() {
		if ((mob.isDead())
			|| (mob.isFrozen())
			|| (mob.isStunned())
			|| (mob.getCombat().getAssaulting() != null)) {
			return;
		}

		Point walkPoint = waypoints.poll();

		if ((walkPoint != null) && (walkPoint.getDirection() != -1)) {
			int nextX = mob.getLocation().getX() + GameConstants.DIR[walkPoint.getDirection()][0];
			int nextY = mob.getLocation().getY() + GameConstants.DIR[walkPoint.getDirection()][1];
			int z = mob.getLocation().getZ();

			Region region = Region.getRegion(mob.getLocation());

			boolean hardBlocked = !region.canMove(mob.getLocation(), walkPoint.getDirection());
			boolean softBlocked = region.isNpcOnTile(nextX, nextY, z);

			// ENHANCED: Smart collision handling
			if (hardBlocked) {
				// Record collision for learning
				mob.recordCollision(new Location(nextX, nextY, z));

				// Try to find alternative path around obstacle
				int alternativeDir = findQuickAlternative(walkPoint.getDirection());
				if (alternativeDir != -1) {
					// Update the walkPoint to use alternative direction
					walkPoint.setDirection(alternativeDir);
					nextX = mob.getLocation().getX() + GameConstants.DIR[alternativeDir][0];
					nextY = mob.getLocation().getY() + GameConstants.DIR[alternativeDir][1];

					// Re-check if alternative is valid
					if (region.canMove(mob.getLocation(), alternativeDir) &&
						!region.isNpcOnTile(nextX, nextY, z)) {
						executeMovement(alternativeDir);
						return;
					}
				}

				// If no alternative found, stop and clear path
				reset();
				return;
			}

			// ENHANCED: Handle soft blocking (other NPCs) - allow clustering in combat
			if (softBlocked) {
				// During combat/following, allow some clustering for exciting encounters
				if (mob.getCombat().inCombat() || mob.getFollowing().isFollowing()) {
					// In combat: try a quick alternative but don't break up the pack
					int alternativeDir = findQuickAlternative(walkPoint.getDirection());
					if (alternativeDir != -1) {
						executeMovement(alternativeDir);
						return;
					}

					// Accept being blocked sometimes for cluster combat
					if (Utility.randomNumber(2) == 0) {
						reset(); // 50% chance to wait, 50% to try again
					}
					return;
				} else {
					// Out of combat: use spacing to prevent aimless clustering
					int alternativeDir = findSpacedDirection(walkPoint.getDirection());
					if (alternativeDir != -1) {
						executeMovement(alternativeDir);
						return;
					}
					reset();
					return;
				}
			}

			// Normal movement
			executeMovement(walkPoint.getDirection());
		}
	}

	/**
	 * ENHANCED: Execute movement with proper state management
	 */
	private void executeMovement(int direction) {
		mob.getMovementHandler().getLastLocation().setAs(mob.getLocation());
		mob.getLocation().move(
			GameConstants.DIR[direction][0],
			GameConstants.DIR[direction][1]);
		primaryDirection = direction;
		flag = true;
	}

	/**
	 * ENHANCED: Quick alternative for combat situations - less complex than full pathfinding
	 */
	private int findQuickAlternative(int blockedDirection) {
		// Just try adjacent directions quickly
		int adjacent1 = (blockedDirection + 1) % 8;
		int adjacent2 = (blockedDirection + 7) % 8;

		if (canMoveTo(adjacent1)) {
			return adjacent1;
		}
		if (canMoveTo(adjacent2)) {
			return adjacent2;
		}

		return -1; // No quick alternative
	}

	/**
	 * ENHANCED: Find direction that maintains spacing from other NPCs
	 */
	private int findSpacedDirection(int preferredDirection) {
		Location current = mob.getLocation();

		// Check directions for NPC density
		int[] spacingScore = new int[8];

		for (int dir = 0; dir < 8; dir++) {
			if (!canMoveTo(dir)) {
				spacingScore[dir] = 999; // Block invalid directions
				continue;
			}

			int testX = current.getX() + GameConstants.DIR[dir][0];
			int testY = current.getY() + GameConstants.DIR[dir][1];

			// Count nearby NPCs (2x2 area around target position)
			int nearbyNpcs = 0;
			for (int offsetX = -1; offsetX <= 1; offsetX++) {
				for (int offsetY = -1; offsetY <= 1; offsetY++) {
					int checkX = testX + offsetX;
					int checkY = testY + offsetY;

					for (Mob otherMob : World.getNpcs()) {
						if (otherMob != null && otherMob != mob && otherMob.isActive()) {
							if (otherMob.getLocation().getX() == checkX &&
								otherMob.getLocation().getY() == checkY &&
								otherMob.getLocation().getZ() == current.getZ()) {
								nearbyNpcs++;
							}
						}
					}
				}
			}

			spacingScore[dir] = nearbyNpcs;
		}

		// Find direction with best spacing (lowest NPC count)
		int bestScore = 999;
		int bestDirection = -1;

		// First, try to use preferred direction if it has good spacing
		if (preferredDirection >= 0 && preferredDirection < 8 && spacingScore[preferredDirection] <= 1) {
			return preferredDirection;
		}

		// Otherwise, find direction with best spacing
		for (int dir = 0; dir < 8; dir++) {
			if (spacingScore[dir] < bestScore) {
				bestScore = spacingScore[dir];
				bestDirection = dir;
			}
		}

		return (bestScore <= 2) ? bestDirection : -1; // Only return if spacing is reasonable
	}

	/**
	 * ENHANCED: Get directions adjacent to a given direction for smart pathfinding
	 */
	private int[] getAdjacentDirections(int direction) {
		// Return the two directions adjacent to the given direction
		int[] adjacent = new int[2];
		adjacent[0] = (direction + 1) % 8;
		adjacent[1] = (direction + 7) % 8; // equivalent to (direction - 1 + 8) % 8
		return adjacent;
	}
}