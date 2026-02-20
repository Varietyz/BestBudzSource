package com.bestbudz.rs2.entity.following;

import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.pathfinding.RS317PathFinder;
import com.bestbudz.rs2.entity.pets.PetFormation;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.Random;

public class StonerFollowing extends Following {
	private final Stoner stoner;
	private int formationOffsetX = 0;
	private int formationOffsetY = 0;

	private static final int MIN_FOLLOW_DISTANCE = 1;
	private static final int MAX_FOLLOW_DISTANCE = 2;
	private static final int PREFERRED_FOLLOW_DISTANCE = 1;
	private static final Random random = new Random();

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

		if (stoner.getCombat().inCombat()) return;

		if (stoner.isPetStoner() && following != null) {
			targetLocation = handlePetFormation(location);
		} else if (type == FollowType.DEFAULT) {

			targetLocation = getRadiusBasedTarget(location);
		}

		if (type == Following.FollowType.COMBAT) {
			if (stoner.getCombat().getCombatType() == CombatTypes.MELEE)
				RS317PathFinder.findRoute(stoner, targetLocation.getX(), targetLocation.getY(), false, 0, 0);
			else
				RS317PathFinder.findRoute(stoner, targetLocation.getX(), targetLocation.getY(), true, 16, 16);
		} else {
			RS317PathFinder.findRoute(stoner, targetLocation.getX(), targetLocation.getY(), true, 16, 16);
		}
	}

	private Location handlePetFormation(Location location) {
		Location targetLocation = new Location(
			location.getX() + formationOffsetX,
			location.getY() + formationOffsetY,
			location.getZ()
		);

		Stoner owner = findPetOwner();
		if (owner != null && PetFormation.isLocationOccupiedByPet(owner, targetLocation, stoner)) {
			int petIndex = getPetIndex(owner);
			if (petIndex >= 0) {
				targetLocation = PetFormation.findAvailableFormationPosition(owner, petIndex);
			}
		}

		return targetLocation;
	}

	private Location getRadiusBasedTarget(Location targetLocation) {
		if (following == null) return targetLocation;

		Location currentPos = stoner.getLocation();
		Location followingPos = following.getLocation();

		double currentDistance = Math.sqrt(
			Math.pow(currentPos.getX() - followingPos.getX(), 2) +
				Math.pow(currentPos.getY() - followingPos.getY(), 2)
		);

		boolean targetMoved = lastTargetPosition == null ||
			!lastTargetPosition.equals(followingPos);

		if (targetMoved) {
			lastTargetPosition = new Location(followingPos);
			ticksSinceLastMove = 0;
		} else {
			ticksSinceLastMove++;
		}

		if (currentDistance >= MIN_FOLLOW_DISTANCE &&
			currentDistance <= MAX_FOLLOW_DISTANCE &&
			ticksSinceLastMove > 2) {

			return currentPos;
		}

		if (currentDistance > MAX_FOLLOW_DISTANCE || targetMoved) {
			return findNaturalFollowPosition(followingPos);
		}

		return targetLocation;
	}

	private Location findNaturalFollowPosition(Location targetPos) {

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

		Location newPos = generateFollowPosition(targetPos);
		preferredPosition = newPos;
		positionStability = 15 + random.nextInt(20);

		return newPos;
	}

	private Location generateFollowPosition(Location targetPos) {

		Location bestPos = null;
		double bestScore = Double.MAX_VALUE;

		for (int attempts = 0; attempts < 8; attempts++) {

			double angle = random.nextDouble() * 2 * Math.PI;

			if (random.nextDouble() < 0.6) {
				angle = Math.PI + (random.nextDouble() - 0.5) * Math.PI;
			}

			double distance = MIN_FOLLOW_DISTANCE +
				random.nextDouble() * (PREFERRED_FOLLOW_DISTANCE - MIN_FOLLOW_DISTANCE);

			int newX = (int) (targetPos.getX() + Math.cos(angle) * distance);
			int newY = (int) (targetPos.getY() + Math.sin(angle) * distance);

			Location candidatePos = new Location(newX, newY, targetPos.getZ());

			double score = scoreFollowPosition(candidatePos, targetPos);

			if (score < bestScore) {
				bestScore = score;
				bestPos = candidatePos;
			}
		}

		return bestPos != null ? bestPos :
			new Location(targetPos.getX() - 1, targetPos.getY() - 1, targetPos.getZ());
	}

	private double scoreFollowPosition(Location pos, Location targetPos) {
		double score = 0;

		double distance = Math.sqrt(
			Math.pow(pos.getX() - targetPos.getX(), 2) +
				Math.pow(pos.getY() - targetPos.getY(), 2)
		);
		score += Math.abs(distance - PREFERRED_FOLLOW_DISTANCE) * 2;

		double moveDistance = Math.sqrt(
			Math.pow(pos.getX() - stoner.getLocation().getX(), 2) +
				Math.pow(pos.getY() - stoner.getLocation().getY(), 2)
		);
		score += moveDistance;

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

		if (type == FollowType.DEFAULT && following != null) {
			double distance = Math.sqrt(
				Math.pow(stoner.getLocation().getX() - following.getLocation().getX(), 2) +
					Math.pow(stoner.getLocation().getY() - following.getLocation().getY(), 2)
			);

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

		if (!stoner.isPetStoner()) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("I can't reach that!"));
		}
	}

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
