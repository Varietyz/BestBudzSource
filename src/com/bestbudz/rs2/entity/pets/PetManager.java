package com.bestbudz.rs2.entity.pets;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.following.Following;
import com.bestbudz.rs2.entity.following.StonerFollowing;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Main pet management system - handles spawning, pickup, and lifecycle events
 */
public class PetManager {

	private static final int MAX_ACTIVE_PETS = 5;
	private static final int SPAWN_ANIMATION = 451;
	private static final int PICKUP_ANIMATION = 827;

	public static boolean spawnPet(Stoner stoner, int itemID, boolean fromLoot) {
		PetData data = PetData.forItem(itemID);

		if (data == null) {
			return false;
		}

		if (!canSpawnPet(stoner)) {
			stoner.send(new SendMessage("Lets not make everyone jealous!"));
			return true;
		}

		// NEW: Check if stoner already has this type of pet active
		Pet existingPet = getActivePetOfType(stoner, data);
		if (existingPet != null) {
			String refusalMessage = generateStonedRefusalMessage(data);

			// Send message to chat
			stoner.send(new SendMessage(refusalMessage));

			// Make the existing pet "speak" the refusal overhead
			existingPet.getPetStoner().getUpdateFlags().sendForceMessage(refusalMessage);

			return true; // Don't consume the item, just refuse to spawn
		}

		if (!removeItemFromInventory(stoner, itemID)) {
			return false;
		}

		Pet pet = new Pet(stoner, data);
		addPetToStoner(stoner, pet);

		performSpawnAnimation(stoner, pet);
		handleSpawnAchievements(stoner, fromLoot, pet);

		return true;
	}

	/**
	 * NEW: Check if the stoner already has a pet of this type active
	 * Returns the existing pet if found, null otherwise
	 */
	private static Pet getActivePetOfType(Stoner stoner, PetData petData) {
		for (Pet pet : getActivePets(stoner)) {
			if (pet.getData() == petData) {
				return pet;
			}
		}
		return null;
	}

	/**
	 * NEW: Generate a funny stoner-culture message for why the pet won't come out
	 */
	private static String generateStonedRefusalMessage(PetData petData) {
		String petName = PetUtils.formatPetDisplayName(petData);

		String[] messages = {
			"Too stoned for another " + petName + ", man...",
			"Not enough room for two of us, dude!",
			"Having an existential crisis here...",
			"Don't want to harsh my own vibe, bro.",
			"Would create a bad trip, man!",
			"Space-time continuum can't handle it!",
			"Got beef with my own species, dude.",
			"There can only be one, man.",
			"Maximum " + petName + "-ness reached!",
			"Not sharing the spotlight, bro!",
			"Would cause cosmic interference...",
			"Don't want to cramp my style!",
			"Too weird meeting myself, dude.",
			"Would glitch the matrix, man!"
		};

		// Pick a random message
		return messages[(int) (Math.random() * messages.length)];
	}

	public static boolean pickupPet(Stoner stoner, Stoner petStoner) {
		Pet pet = findActivePet(stoner, petStoner);

		if (pet == null) {
			return false;
		}

		if (!validatePetOwnership(stoner, pet)) {
			stoner.send(new SendMessage(pet.getName() + " is not your pet!"));
			return true;
		}

		if (!storePetItem(stoner, pet.getData())) {
			stoner.send(new SendMessage("You must free some box space to pick up your " + pet.getName()));
			return false;
		}

		performPickupAnimation(stoner, pet);
		schedulePetRemoval(stoner, pet);

		return true;
	}

	/**
	 * Handle trade/deal request - checks if target is a pet for pickup
	 * Returns true if the request was handled as a pet pickup, false if it should continue as normal trade
	 */
	public static boolean handleTradeRequest(Stoner stoner, Stoner target) {
		// Check if the target is a pet
		if (!PetUtils.isPetStoner(target)) {
			return false; // Not a pet, continue with normal trade
		}

		// Find the pet object for this pet stoner
		Pet pet = findActivePet(stoner, target);

		// Check if this pet belongs to the requesting player
		if (pet == null) {
			stoner.send(new SendMessage("That is not your pet!"));
			return true; // Handled (even though failed), don't continue with trade
		}

		// Attempt to pick up the pet
		if (pickupPet(stoner, target)) {
			stoner.send(new SendMessage("You pick up your " + PetUtils.formatPetDisplayName(pet.getData()) + "."));
		}

		return true; // Pet pickup was handled, don't continue with normal trade
	}

	public static void handleLogout(Stoner stoner) {
		// Create a copy of the list to avoid ConcurrentModificationException
		List<Pet> activePets = new ArrayList<>(getActivePets(stoner));

		for (Pet pet : activePets) {
			Item petItem = new Item(pet.getData().getItem());

			if (stoner.getBox().hasSpaceFor(petItem)) {
				stoner.getBox().add(petItem);
			} else if (stoner.getBank().hasSpaceFor(petItem)) {
				stoner.getBank().add(petItem);
				stoner.send(new SendMessage("Your " + pet.getName() + " was added to your bank."));
			} else {
				stoner.send(new SendMessage("You have no space to save your pet. It was dismissed."));
			}

			removePetFromStoner(stoner, pet);
		}
	}

	public static void handleDeath(Stoner stoner) {
		// Create a copy of the list to avoid ConcurrentModificationException
		List<Pet> activePets = new ArrayList<>(getActivePets(stoner));

		for (Pet pet : activePets) {
			removePetFromStoner(stoner, pet);
		}

		if (!activePets.isEmpty()) {
			stoner.send(new SendMessage("You got yourself and your pet killed, irresponsible douch!"));
		}
	}

	// Helper methods
	private static boolean canSpawnPet(Stoner stoner) {
		return getActivePets(stoner).size() < MAX_ACTIVE_PETS;
	}

	private static boolean removeItemFromInventory(Stoner stoner, int itemID) {
		Item item = new Item(itemID, 1);
		if (stoner.getBox().contains(item)) {
			stoner.getBox().remove(item);
			return true;
		}
		return false;
	}

	private static void addPetToStoner(Stoner stoner, Pet pet) {
		// Add to the new pet list
		if (!stoner.getActivePets().contains(pet)) {
			stoner.getActivePets().add(pet);
		}

		// CRITICAL FIX: Update pet formation positions
		updatePetFormations(stoner);

		// REMOVED: Don't use setBossID anymore - it conflicts with multiple pets
		// stoner.setBossID(pet.getData().npcID);
	}

	/**
	 * FIXED: Update all pet positions to maintain formation
	 */
	private static void updatePetFormations(Stoner owner) {
		List<Pet> activePets = owner.getActivePets();

		for (int i = 0; i < activePets.size(); i++) {
			Pet pet = activePets.get(i);
			Location formationPos = PetFormation.getFormationPosition(owner, i, activePets.size());

			// FIXED: Cast to StonerFollowing to access the setFormationOffset method
			Following following = pet.getPetStoner().getFollowing();
			if (following instanceof StonerFollowing) {
				StonerFollowing stonerFollowing = (StonerFollowing) following;
				stonerFollowing.setFormationOffset(
					formationPos.getX() - owner.getLocation().getX(),
					formationPos.getY() - owner.getLocation().getY()
				);
			}
		}
	}

	private static void removePetFromStoner(Stoner stoner, Pet pet) {
		pet.remove();
		stoner.getActivePets().remove(pet);

		// CRITICAL FIX: Update formations after removing a pet
		updatePetFormations(stoner);
	}

	private static void performSpawnAnimation(Stoner stoner, Pet pet) {
		stoner.getUpdateFlags().sendAnimation(new Animation(SPAWN_ANIMATION));
		stoner.face(pet.getPetStoner());
		stoner.getUpdateFlags().sendForceMessage("Go " + pet.getName().toUpperCase() + "!!!!!!!!");
	}

	private static void performPickupAnimation(Stoner stoner, Pet pet) {
		stoner.getUpdateFlags().sendAnimation(new Animation(PICKUP_ANIMATION));
		stoner.face(pet.getPetStoner());
	}

	private static void handleSpawnAchievements(Stoner stoner, boolean fromLoot, Pet pet) {
		if (fromLoot) {
			AchievementHandler.activateAchievement(stoner, AchievementList.OBTAIN_1_BOSS_PET, 1);
			AchievementHandler.activateAchievement(stoner, AchievementList.OBTAIN_10_BOSS_PET, 1);
		} else {
			stoner.send(new SendMessage("You took out " + pet.getName() + " for a walk."));
		}
	}

	private static Pet findActivePet(Stoner stoner, Stoner petStoner) {
		if (petStoner == null) {
			return null;
		}

		for (Pet pet : getActivePets(stoner)) {
			if (pet.getPetStoner() == petStoner) {
				return pet;
			}
		}

		return null;
	}

	private static boolean validatePetOwnership(Stoner stoner, Pet pet) {
		return pet.getOwner() == stoner && getActivePets(stoner).contains(pet);
	}

	private static boolean storePetItem(Stoner stoner, PetData data) {
		Item petItem = new Item(data.getItem());

		if (stoner.getBox().hasSpaceFor(petItem)) {
			stoner.getBox().add(petItem);
			return true;
		} else if (stoner.getBank().hasSpaceFor(petItem)) {
			stoner.getBank().add(petItem);
			stoner.send(new SendMessage(data.getName() + " has been added to your bank."));
			return true;
		}

		return false;
	}

	private static void schedulePetRemoval(Stoner stoner, Pet pet) {
		TaskQueue.queue(new Task(stoner, 1, true) {
			@Override
			public void execute() {
				removePetFromStoner(stoner, pet);
				stop();
			}

			@Override
			public void onStop() {
				stoner.send(new SendMessage("You have picked up your " + pet.getName()));
			}
		});
	}

	public static List<Pet> getActivePets(Stoner stoner) {
		// This uses the new pet list that you'll need to add to Stoner class
		return stoner.getActivePets();
	}

	// Legacy compatibility methods - delegate to PetUtils
	public static String formatPetDisplayName(PetData petData) {
		return PetUtils.formatPetDisplayName(petData);
	}

	public static String generatePetUsername(Stoner owner, PetData petData) {
		return PetUtils.generatePetUsername(owner, petData);
	}

	public static boolean isPetUsername(String username) {
		return PetUtils.isPetUsername(username);
	}

	public static PetData getPetDataFromUsername(String username) {
		return PetUtils.getPetDataFromUsername(username);
	}

	public static boolean isPetStoner(Stoner stoner) {
		return PetUtils.isPetStoner(stoner);
	}
}