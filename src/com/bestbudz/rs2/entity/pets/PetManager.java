package com.bestbudz.rs2.entity.pets;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.combat.Combat;
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
	public static boolean retaliatedAttack = false;

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

		if (stoner.getPetMaster() != null) {
			stoner.getPetMaster().onPetDeployed(pet);
		}

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

		if (stoner.getPetMaster() != null) {
			stoner.getPetMaster().onPetPickedUp(pet);
		}

		performPickupAnimation(stoner, pet);
		schedulePetRemoval(stoner, pet);

		return true;
	}

	// Modified handleTradeRequest method in PetManager.java
	public static boolean handleTradeRequest(Stoner stoner, Stoner target) {
		// Check if the target is a pet
		if (!PetUtils.isPetStoner(target)) {
			return false; // Not a pet, continue with normal trade
		}

		// Find the pet object for this pet stoner
		Pet pet = findActivePet(stoner, target);

		// Check if this pet belongs to the requesting player
		if (pet == null) {
			// Pet doesn't belong to this player - RETALIATE!
			retaliatedAttack = true;
			performPetRetaliation(target, stoner);
			retaliatedAttack = false;
			return true; // Handled, don't continue with trade
		}

		// Attempt to pick up the pet (original logic for owner)
		if (pickupPet(stoner, target)) {
			stoner.send(new SendMessage("You pick up your " + PetUtils.formatPetDisplayName(pet.getData()) + "."));
		}

		return true; // Pet pickup was handled, don't continue with normal trade
	}

	/**
	 * Handle pet retaliation against would-be abductors
	 */
	private static void performPetRetaliation(Stoner petStoner, Stoner abductor) {
		// Get pet data for messages
		PetData petData = PetUtils.getPetDataFromUsername(petStoner.getUsername());
		String petDisplayName = petData != null ? PetUtils.formatPetDisplayName(petData) : "Pet";

		// Random overhead messages for pet (immediate)
		String[] petOverheads = {
			"Bad touch! BAD!",
			"Congratulations, you played yourself.",
			"This isn't a petting zoo, freak.",
			"Oh, you thought that'd work? Cute.",
			"Guess what? I'm bitey.",
			"Touch me again and I'll poop in your inventory.",
			"Wow, that was bold. And dumb.",
			"You just got clapped by a sidekick.",
			"Does this look like your pet? Didn't think so.",
			"I've got more fight than your KD ratio.",
			"I don't know who you are, but you're bleeding now.",
			"Nope. Denied. Sit down.",
			"You try to snatch, I throw hands. That's the deal.",
			"You touch, I zap. Simple math.",
			"You just got rejected by a virtual animal."
		};

		// Random overhead messages for abductor (delayed)
		String[] abductorOverheads = {
			"Ow! Bad " + petDisplayName + "!",
			"Ouch! That hurt!",
			"Hey! Stop biting!",
			"Ow! I was just looking!",
			"Geez, so aggressive!",
			"Fine, I don't want you anyway!",
			"Ow! Such attitude!",
			"I didn't mean it!",
			"Sorry, sorry!",
			"Note to self: don't steal pets",
			"Why are you so mean?!",
			"I thought we were friends!",
			"Ow! " + petDisplayName + " bit me!",
			"But... I just wanted you to tickle my pickle!",
			"I just wanted to pet you!",
			"Okay! Okay! Okay!",
			"Aggressive much?!",
			"AAAAUUUUWWWWWCCHHHH WHAT THE FUUUUUUUU!!!!",
			"Bro! This pet rabid?",
			"Note to self: wear armor next time.",
			"I regret my life's decisions!",
			"Can someone call Animal Control?",
			"Bro, your pet is broken.",
			"I just wanted to borrow it forever!",
			"Ouch! Why are the cute ones always violent?",
			"That thing just bit my soul!",
			"Is this pet trained in MMA or what?!",
			"I think your pet just unlocked a new combo move.",
			"I only wanted to sniff it... is that weird?",
			"You call that a pet? That's a mini boss!"
		};

		// Send immediate pet overhead message
		String petOverhead = petOverheads[(int)(Math.random() * petOverheads.length)];
		petStoner.getUpdateFlags().sendForceMessage(petOverhead);

		// Chat messages
		abductor.send(new SendMessage("The " + petDisplayName + " attacks you for trying to steal it!"));

		// Notify owner
		Stoner owner = (Stoner) petStoner.getAttributes().get("PET_OWNER");
		if (owner != null && owner.isActive()) {
			owner.send(new SendMessage("Your " + petDisplayName + " defended itself against " + abductor.getUsername() + "!"));
		}

		// Set up combat engagement around here (Facing the abducter for during the attack)
		petStoner.getCombat().setAssaulting(abductor);
		petStoner.face(abductor);

		// Execute retaliation attack
		executePetRetaliationAttack(petStoner, abductor);

		// Schedule delayed effects
		String abductorOverhead = abductorOverheads[(int)(Math.random() * abductorOverheads.length)];
		com.bestbudz.core.task.TaskQueue.queue(new com.bestbudz.core.task.Task(null, 3) {
			@Override
			public void execute() {
				if (abductor.isActive()) {
					abductor.getUpdateFlags().sendForceMessage(abductorOverhead);
				}
				stop();
			}

			@Override
			public void onStop() {}
		});

		// Schedule hit delivery and combat resolution
		com.bestbudz.core.task.TaskQueue.queue(new com.bestbudz.core.task.Task(null, 6) {
			@Override
			public void execute() {
				if (abductor.isActive()) {
					// Apply damage and set combat states
					long newHP = abductor.getGrades()[3];
					abductor.getGrades()[3] = newHP;

					// Handle death scenario
					if (newHP <= 0) {
						String[] deathTaunts = {
							"LOL, get wrecked noob!",
							"That's what you get, scrub!",
							"HAHAHAHA! Don't mess with me!",
							"Rekt! Try again, kiddo!",
							"Get good, trash!",
							"Ez clap! Too easy!",
							"Owned! Stay down!",
							"LMAO what a noob!",
							"Git gud, peasant!",
							"Sit down, clown!",
							"Destroyed! ROFL!",
							"You got dunked on!",
							"Yikes, that was embarrassing!",
							"Imagine dying to a pet!",
							"XD LMAAAOOOOOO",
							"You suck LOL!",
							"Too easy!",
							"Trash spotted.",
							"Mad?",
							"Try harder.",
							"Cry more.",
							"Clapped!",
							"You died doing what you loved: failing.",
							"Shoulda brought backup... or skill."
						};
						String taunt = deathTaunts[(int)(Math.random() * deathTaunts.length)];
						petStoner.getUpdateFlags().sendForceMessage(taunt);
						abductor.setLastHitSuccess(true);
						abductor.checkForDeath();
					}
					// after attack landed its hind, reface the owner
					if (owner != null && owner.isActive()) {
						petStoner.face(owner);
					}
					petStoner.getCombat().reset();
				}
				stop();
			}

			@Override
			public void onStop() {}
		});
	}

	/**
	 * Execute a single combat attack using pet's animation and graphics systems
	 */
	private static void executePetRetaliationAttack(Stoner petStoner, Stoner abductor) {
		// Randomly select available combat type for visual effects
		java.util.List<String> availableAttackTypes = new java.util.ArrayList<>();
		if (petStoner.getAttributes().get("PET_MELEE_ANIMATION") != null) availableAttackTypes.add("MELEE");
		if (petStoner.getAttributes().get("PET_SAGITTARIUS_ANIMATION") != null) availableAttackTypes.add("SAGITTARIUS");
		if (petStoner.getAttributes().get("PET_MAGE_ANIMATION") != null) availableAttackTypes.add("MAGE");

		// Force random combat type
		String attackType = availableAttackTypes.get((int)(Math.random() * availableAttackTypes.size()));

		// Set combat type and guaranteed damage
		switch (attackType) {
			case "MELEE":
				petStoner.getCombat().setCombatType(Combat.CombatTypes.MELEE);
				petStoner.getCombat().getMelee().setNextDamage(10 + (int)(Math.random() * 40)); // 50-200 guaranteed
				break;
			case "SAGITTARIUS":
				petStoner.getCombat().setCombatType(Combat.CombatTypes.SAGITTARIUS);
				// Sagittarius doesn't have setNextDamage - it uses formula calculation
				break;
			case "MAGE":
				petStoner.getCombat().setCombatType(Combat.CombatTypes.MAGE);
				petStoner.getCombat().getMage().setNextHit(15 + (int)(Math.random() * 60)); // 60-240 guaranteed
				break;
		}

		// Force 100% hit success for retaliation
		petStoner.setLastHitSuccess(true);
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

		// IMMEDIATE: Force all pets to their formation positions right now
		forceAllPetsToFormationPositions(stoner);
	}

	/**
	 * Immediately teleport all pets to their correct formation positions
	 */
	private static void forceAllPetsToFormationPositions(Stoner owner) {
		List<Pet> activePets = owner.getActivePets();

		for (int i = 0; i < activePets.size(); i++) {
			Pet pet = activePets.get(i);

			// Calculate where this pet should be positioned
			Location formationPos = PetFormation.getFormationPosition(owner, i, activePets.size());

			// FORCE the pet to that position immediately
			pet.getPetStoner().teleport(formationPos);

			// ALSO set following offset for future owner movement
			Following following = pet.getPetStoner().getFollowing();
			if (following instanceof StonerFollowing) {
				StonerFollowing stonerFollowing = (StonerFollowing) following;
				int offsetX = formationPos.getX() - owner.getLocation().getX();
				int offsetY = formationPos.getY() - owner.getLocation().getY();
				stonerFollowing.setFormationOffset(offsetX, offsetY);
			}

			// Ensure pet is following owner for future movement
			pet.getPetStoner().getFollowing().setFollow(owner);

			System.out.println("DEBUG: Forced pet " + pet.getPetStoner().getUsername() +
				" to formation position " + formationPos +
				" (offset: " + (formationPos.getX() - owner.getLocation().getX()) +
				", " + (formationPos.getY() - owner.getLocation().getY()) + ")");
		}
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