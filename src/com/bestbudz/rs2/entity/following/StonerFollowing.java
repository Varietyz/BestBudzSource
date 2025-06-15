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

public class StonerFollowing extends Following {
	private final Stoner stoner;
	private int formationOffsetX = 0;
	private int formationOffsetY = 0;

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
		// CRITICAL FIX: For pets, adjust target location based on formation
		Location targetLocation = location;

		if (stoner.isPetStoner() && following != null) {
			// Apply formation offset for pets
			targetLocation = new Location(
				location.getX() + formationOffsetX,
				location.getY() + formationOffsetY,
				location.getZ()
			);

			// Check if target location is occupied by another pet
			Stoner owner = null;
			// Find the owner (you might need to store this reference in the pet)
			for (Stoner s : com.bestbudz.rs2.entity.World.getStoners()) {
				if (s != null && s.isActive()) {
					for (com.bestbudz.rs2.entity.pets.Pet pet : s.getActivePets()) {
						if (pet.getPetStoner() == stoner) {
							owner = s;
							break;
						}
					}
					if (owner != null) break;
				}
			}

			if (owner != null && PetFormation.isLocationOccupiedByPet(owner, targetLocation, stoner)) {
				// Find alternative position
				int petIndex = owner.getActivePets().indexOf(
					owner.getActivePets().stream()
						.filter(p -> p.getPetStoner() == stoner)
						.findFirst()
						.orElse(null)
				);

				if (petIndex >= 0) {
					targetLocation = PetFormation.findAvailableFormationPosition(owner, petIndex);
				}
			}
		}

		if (type == Following.FollowType.COMBAT) {
			if (stoner.getCombat().getCombatType() == CombatTypes.MELEE)
				RS317PathFinder.findRoute(stoner, targetLocation.getX(), targetLocation.getY(), false, 0, 0);
			else RS317PathFinder.findRoute(stoner, targetLocation.getX(), targetLocation.getY(), true, 16, 16);
		} else {
			RS317PathFinder.findRoute(stoner, targetLocation.getX(), targetLocation.getY(), true, 16, 16);
		}
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
				&& (stoner
				.getCombat()
				.withinDistanceForAssault(stoner.getCombat().getCombatType(), true));
		}

		// CRITICAL FIX: For pets, pause when close enough to formation position
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

			return distance <= 1; // Stop when within 1 tile of formation position
		}

		return false;
	}
}