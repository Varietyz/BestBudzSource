package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.FinishTeleportingTask;
import com.bestbudz.rs2.content.RunEnergy;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.movement.MovementHandler;
import com.bestbudz.rs2.entity.movement.StonerMovementHandler;
import com.bestbudz.rs2.entity.following.Following;
import com.bestbudz.rs2.entity.following.StonerFollowing;
import com.bestbudz.rs2.entity.pets.PetFormation;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMapRegion;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendWalkableInterface;
import com.bestbudz.rs2.entity.pets.Pet;
import com.bestbudz.rs2.entity.pets.PetData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles all movement-related functionality including walking, running, teleporting, and following
 */
public class StonerMovement {
	private final Stoner stoner;
	private final MovementHandler movementHandler;
	private final Following following;
	private final RunEnergy runEnergy;

	// Current region tracking
	private Location currentRegion = new Location(0, 0, 0);

	// Path memory for collision avoidance
	public static final Map<Location, Integer> pathMemory = new HashMap<>();

	// Teleport state
	public boolean homeTeleporting;
	private int teleportTo;

	public StonerMovement(Stoner stoner) {
		this.stoner = stoner;
		this.movementHandler = new StonerMovementHandler(stoner);
		this.following = new StonerFollowing(stoner);
		this.runEnergy = new RunEnergy(stoner);
	}

	public void process() {
		following.process();
	}

	public void reset() {
		following.updateWaypoint();
		movementHandler.resetMoveDirections();
	}

	/**
	 * Teleports the player to a new location, handling pets and region changes
	 */
	public void teleport(Location location) {
		boolean zChange = location.getZ() != stoner.getLocation().getZ();

		// Handle pet teleportation - store pet data before teleporting
		List<PetData> activePetData = new ArrayList<>();
		if (stoner.getActivePets() != null && !stoner.getActivePets().isEmpty()) {
			for (Pet pet : stoner.getActivePets()) {
				activePetData.add(pet.getData());
			}

			// Remove existing pets (they'll be recreated at new location)
			List<Pet> petsToRemove = new ArrayList<>(stoner.getActivePets());
			for (Pet pet : petsToRemove) {
				pet.remove();
				stoner.getActivePets().remove(pet);
			}
		}

		stoner.getLocation().setAs(location);
		stoner.getSession().setResetMovementQueue(true);
		stoner.getSession().setNeedsPlacement(true);
		stoner.getBankStanding().forceStop();
		movementHandler
			.getLastLocation()
			.setAs(new Location(stoner.getLocation().getX(), stoner.getLocation().getY() + 1));
		stoner.getAttributes().remove("combatsongdelay");

		stoner.clearAnimationLock();

		stoner.send(new SendRemoveInterfaces());
		stoner.send(new SendWalkableInterface(-1));

		ControllerManager.setControllerOnWalk(stoner);

		TaskQueue.cancelHitsOnEntity(stoner);
		TaskQueue.queue(new FinishTeleportingTask(stoner, 5));

		movementHandler.reset();

		if (!stoner.inClanWarsFFA()) {
			if (zChange) {
				stoner.send(new SendMapRegion(stoner));
			} else {
				checkForRegionChange();
			}
		}

		if (stoner.getTrade().trading()) {
			stoner.getTrade().end(false);
		} else if (stoner.getDueling().isStaking()) {
			stoner.getDueling().decline();
		}

		TaskQueue.onMovement(stoner);

		// Recreate pets at new location after teleport completes
		if (!activePetData.isEmpty()) {
			final Stoner teleportedStoner = stoner;
			TaskQueue.queue(new Task(stoner, 2, true) {
				@Override
				public void execute() {
					// FIXED: Recreate pets with proper formation
					for (int i = 0; i < activePetData.size(); i++) {
						PetData petData = activePetData.get(i);
						Pet newPet = new Pet(teleportedStoner, petData);
						teleportedStoner.getActivePets().add(newPet);

						// CRITICAL FIX: Set formation position immediately
						Location formationPos = PetFormation.getFormationPosition(
							teleportedStoner, i, activePetData.size());

						// Teleport pet to formation position
						newPet.getPetStoner().teleport(formationPos);

						// IMPORTANT: Set formation offset in the following system
						Following following = newPet.getPetStoner().getFollowing();
						if (following instanceof StonerFollowing) {
							StonerFollowing stonerFollowing = (StonerFollowing) following;
							int offsetX = formationPos.getX() - teleportedStoner.getLocation().getX();
							int offsetY = formationPos.getY() - teleportedStoner.getLocation().getY();
							stonerFollowing.setFormationOffset(offsetX, offsetY);
						}
					}
					stop();
				}

				@Override
				public void onStop() {
					// Optional: Send message about pets following
					if (activePetData.size() == 1) {
						teleportedStoner.send(new SendMessage("Your pet follows you to the new location."));
					} else if (activePetData.size() > 1) {
						teleportedStoner.send(new SendMessage("Your pets follow you to the new location."));
					}
				}
			});
		}
	}

	/**
	 * Changes the player's Z coordinate (height level)
	 */
	public void changeZ(int z) {
		stoner.getLocation().setZ(z);
		stoner.getSession().setNeedsPlacement(true);

		stoner.getObjects().onRegionChange();
		stoner.getGroundItems().onRegionChange();

		movementHandler.reset();

		stoner.send(new SendMapRegion(stoner));
	}

	/**
	 * Checks if the player has moved far enough to require a region update
	 */
	public void checkForRegionChange() {
		int deltaX = stoner.getLocation().getX() - getCurrentRegion().getRegionX() * 8;
		int deltaY = stoner.getLocation().getY() - getCurrentRegion().getRegionY() * 8;

		if ((deltaX < 16) || (deltaX >= 88) || (deltaY < 16) || (deltaY > 88)) {
			stoner.send(new SendMapRegion(stoner));
		}
	}

	/**
	 * Checks if a location is within the current region
	 */
	public boolean withinRegion(Location other) {
		int deltaX = other.getX() - currentRegion.getRegionX() * 8;
		int deltaY = other.getY() - currentRegion.getRegionY() * 8;

		return (deltaX >= 2) && (deltaX <= 110) && (deltaY >= 2) && (deltaY <= 110);
	}

	// Getters and setters
	public MovementHandler getMovementHandler() { return movementHandler; }
	public Following getFollowing() { return following; }
	public RunEnergy getRunEnergy() { return runEnergy; }

	public Location getCurrentRegion() { return currentRegion; }
	public void setCurrentRegion(Location currentRegion) { this.currentRegion = currentRegion; }

	public boolean isHomeTeleporting() { return homeTeleporting; }
	public void setHomeTeleporting(boolean homeTeleporting) { this.homeTeleporting = homeTeleporting; }

	public int getTeleportTo() { return teleportTo; }
	public void setTeleportTo(int teleportTo) { this.teleportTo = teleportTo; }
}