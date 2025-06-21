package com.bestbudz.rs2.entity.following;

import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.pathfinding.RS317PathFinder;
import com.bestbudz.rs2.entity.pets.PetFormation;
import com.bestbudz.rs2.entity.pets.PetManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.Random;

public class StonerFollowing extends Following {
	private final Stoner stoner;
	private int formationOffsetX = 0;
	private int formationOffsetY = 0;

	// Radius-based following parameters
	private static final int MIN_FOLLOW_DISTANCE = 1; // Minimum tiles to maintain
	private static final int MAX_FOLLOW_DISTANCE = 2; // Maximum tiles before moving
	private static final int PREFERRED_FOLLOW_DISTANCE = 1; // Preferred distance
	private static final Random random = new Random();

	// Tracking for more natural movement
	private Location lastTargetPosition = null;
	private int ticksSinceLastMove = 0;
	private Location preferredPosition = null;
	private int positionStability = 0;

	public StonerFollowing(Stoner stoner) {
		super(stoner);
		this.stoner = stoner;
	}

	public void setFormationOffset(int offsetX, int offsetY) {
		this.formationOffsetX = offsetX;
		this.formationOffsetY = offsetY;
	}

	@Override
	public void findPath(Location location) {
		Location targetLocation = location;

		// Handle pet formation logic
		if (stoner.getCombat().inCombat()) return; // TEST

		if (stoner.isPetStoner() && following != null) {
			targetLocation = handlePetFormation(location);
		} else if (type == FollowType.DEFAULT) {
			// Apply radius-based following for regular stoners
			targetLocation = getRadiusBasedTarget(location);
		}

		// Use pathfinding based on combat type
		if (type == Following.FollowType.COMBAT) {
			if (stoner.getCombat().getCombatType() == CombatTypes.MELEE)
				RS317PathFinder.findRoute(stoner, targetLocation.getX(), targetLocation.getY(), false, 0, 0);
			else
				RS317PathFinder.findRoute(stoner, targetLocation.getX(), targetLocation.getY(), true, 16, 16);
		} else {
			RS317PathFinder.findRoute(stoner, targetLocation.getX(), targetLocation.getY(), true, 16, 16);
		}
	}

	/**
	 * Handle pet formation positioning
	 */
	private Location handlePetFormation(Location location) {
		Location targetLocation = new Location(
			location.getX() + formationOffsetX,
			location.getY() + formationOffsetY,
			location.getZ()
		);

		// Check if target location is occupied by another pet
		Stoner owner = findPetOwner();
		if (owner != null && PetFormation.isLocationOccupiedByPet(owner, targetLocation, stoner)) {
			int petIndex = getPetIndex(owner);
			if (petIndex >= 0) {
				targetLocation = PetFormation.findAvailableFormationPosition(owner, petIndex);
			}
		}

		return targetLocation;
	}

	/**
	 * Get radius-based target location for natural following
	 */
	private Location getRadiusBasedTarget(Location targetLocation) {
		if (following == null) return targetLocation;

		Location currentPos = stoner.getLocation();
		Location followingPos = following.getLocation();

		// Calculate current distance to target
		double currentDistance = Math.sqrt(
			Math.pow(currentPos.getX() - followingPos.getX(), 2) +
				Math.pow(currentPos.getY() - followingPos.getY(), 2)
		);

		// Track target movement
		boolean targetMoved = lastTargetPosition == null ||
			!lastTargetPosition.equals(followingPos);

		if (targetMoved) {
			lastTargetPosition = new Location(followingPos);
			ticksSinceLastMove = 0;
		} else {
			ticksSinceLastMove++;
		}

		// If we're within acceptable range and target hasn't moved much, don't move
		if (currentDistance >= MIN_FOLLOW_DISTANCE &&
			currentDistance <= MAX_FOLLOW_DISTANCE &&
			ticksSinceLastMove > 2) {

			// Stay in current position
			return currentPos;
		}

		// If too far or target moved significantly, find new position
		if (currentDistance > MAX_FOLLOW_DISTANCE || targetMoved) {
			return findNaturalFollowPosition(followingPos);
		}

		// Default to current behavior
		return targetLocation;
	}

	/**
	 * Find a natural-looking position to follow from
	 */
	private Location findNaturalFollowPosition(Location targetPos) {
		// If we have a stable preferred position and it's still good, keep using it
		if (preferredPosition != null && positionStability > 0) {
			double prefDistance = Math.sqrt(
				Math.pow(preferredPosition.getX() - targetPos.getX(), 2) +
					Math.pow(preferredPosition.getY() - targetPos.getY(), 2)
			);

			if (prefDistance >= MIN_FOLLOW_DISTANCE && prefDistance <= MAX_FOLLOW_DISTANCE) {
				positionStability--;
				return preferredPosition;
			}
		}

		// Find a new preferred position
		Location newPos = generateFollowPosition(targetPos);
		preferredPosition = newPos;
		positionStability = 15 + random.nextInt(20); // Stay with this position for 5-15 ticks

		return newPos;
	}

	/**
	 * Generate a position within follow radius that looks natural
	 */
	private Location generateFollowPosition(Location targetPos) {
		// Try several positions and pick the best one
		Location bestPos = null;
		double bestScore = Double.MAX_VALUE;

		for (int attempts = 0; attempts < 8; attempts++) {
			// Generate angle (prefer positions behind and to sides)
			double angle = random.nextDouble() * 2 * Math.PI;

			// Bias towards positions behind the target (180-360 degrees relative to movement)
			if (random.nextDouble() < 0.6) {
				angle = Math.PI + (random.nextDouble() - 0.5) * Math.PI; // 90-270 degrees
			}

			// Random distance within preferred range
			double distance = MIN_FOLLOW_DISTANCE +
				random.nextDouble() * (PREFERRED_FOLLOW_DISTANCE - MIN_FOLLOW_DISTANCE);

			int newX = (int) (targetPos.getX() + Math.cos(angle) * distance);
			int newY = (int) (targetPos.getY() + Math.sin(angle) * distance);

			Location candidatePos = new Location(newX, newY, targetPos.getZ());

			// Score this position
			double score = scoreFollowPosition(candidatePos, targetPos);

			if (score < bestScore) {
				bestScore = score;
				bestPos = candidatePos;
			}
		}

		return bestPos != null ? bestPos :
			new Location(targetPos.getX() - 1, targetPos.getY() - 1, targetPos.getZ());
	}

	/**
	 * Score a potential follow position (lower is better)
	 */
	private double scoreFollowPosition(Location pos, Location targetPos) {
		double score = 0;

		// Distance from target (prefer PREFERRED_FOLLOW_DISTANCE)
		double distance = Math.sqrt(
			Math.pow(pos.getX() - targetPos.getX(), 2) +
				Math.pow(pos.getY() - targetPos.getY(), 2)
		);
		score += Math.abs(distance - PREFERRED_FOLLOW_DISTANCE) * 2;

		// Distance from current position (prefer closer moves)
		double moveDistance = Math.sqrt(
			Math.pow(pos.getX() - stoner.getLocation().getX(), 2) +
				Math.pow(pos.getY() - stoner.getLocation().getY(), 2)
		);
		score += moveDistance;

		// Prefer positions that are stable (away from other entities)
		// This would require checking for other entities at the position
		// score += checkPositionStability(pos);

		return score;
	}

	@Override
	public boolean pause() {
		if (type == Following.FollowType.COMBAT) {
			if (GameConstants.withinBlock(
				following.getLocation().getX(),
				following.getLocation().getY(),
				following.getSize(),
				stoner.getLocation().getX(),
				stoner.getLocation().getY())) {
				return false;
			}

			if (following.isNpc()) {
				CombatTypes c = stoner.getCombat().getCombatType();

				if ((c == CombatTypes.MAGE) || (c == CombatTypes.SAGITTARIUS)) {
					Mob mob = com.bestbudz.rs2.entity.World.getNpcs()[following.getIndex()];

					if (mob == null) {
						return false;
					}

					if (!mob.withinMobWalkDistance(stoner)) {
						return false;
					}
				}
			}

			return (!stoner.getLocation().equals(following.getLocation()))
				&& (stoner.getCombat().withinDistanceForAssault(stoner.getCombat().getCombatType(), true));
		}

		// Enhanced pause logic for pets
		if (stoner.isPetStoner() && following != null) {
			Location targetLocation = new Location(
				following.getLocation().getX() + formationOffsetX,
				following.getLocation().getY() + formationOffsetY,
				following.getLocation().getZ()
			);

			int distance = Math.max(
				Math.abs(stoner.getLocation().getX() - targetLocation.getX()),
				Math.abs(stoner.getLocation().getY() - targetLocation.getY())
			);

			return distance <= 1;
		}

		// Enhanced pause logic for radius-based following
		if (type == FollowType.DEFAULT && following != null) {
			double distance = Math.sqrt(
				Math.pow(stoner.getLocation().getX() - following.getLocation().getX(), 2) +
					Math.pow(stoner.getLocation().getY() - following.getLocation().getY(), 2)
			);

			// Pause if within acceptable follow range
			return distance >= MIN_FOLLOW_DISTANCE && distance <= MAX_FOLLOW_DISTANCE;
		}

		return false;
	}

	@Override
	public void onCannotReach() {
		reset();

		if (type == Following.FollowType.COMBAT) {
			stoner.getCombat().reset();
		}

		// Don't spam "can't reach" messages for pets
		if (!stoner.isPetStoner()) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("I can't reach that!"));
		}
	}

	// Helper methods for pet management
	private Stoner findPetOwner() {
		for (Stoner s : com.bestbudz.rs2.entity.World.getStoners()) {
			if (s != null && s.isActive()) {
				for (com.bestbudz.rs2.entity.pets.Pet pet : s.getActivePets()) {
					if (pet.getPetStoner() == stoner) {
						return s;
					}
				}
			}
		}
		return null;
	}

	private int getPetIndex(Stoner owner) {
		for (int i = 0; i < owner.getActivePets().size(); i++) {
			if (owner.getActivePets().get(i).getPetStoner() == stoner) {
				return i;
			}
		}
		return -1;
	}
}