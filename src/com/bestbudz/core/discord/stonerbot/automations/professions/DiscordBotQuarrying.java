package com.bestbudz.core.discord.stonerbot.automations.professions;

import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import static com.bestbudz.core.discord.stonerbot.config.DiscordBotSpeech.START_QUARRYING_MESSAGE;
import com.bestbudz.rs2.content.profession.quarrying.Quarrying;
import com.bestbudz.rs2.entity.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * FIXED: Discord bot quarrying - no duplicate systems, uses existing quarrying
 */
public class DiscordBotQuarrying {

	private final DiscordBotStoner bot;
	private final Random random = new Random();

	// Simple state tracking
	private volatile boolean isQuarrying = false;
	private volatile long quarryingStartTime = 0;
	private volatile long quarryingDuration = 0;
	private volatile boolean movingToRock = false;
	private volatile RSObject targetRock = null;
	private volatile long lastMiningAttempt = 0;
	private volatile long lastExperienceCheck = 0;
	private volatile double lastExperience = 0;

	// Stats
	private volatile int successfulMines = 0;
	private volatile int failedAttempts = 0;

	// Enhanced quarrying objects array
	private static final int[] QUARRY_OBJECTS = {
		// Copper rocks
		13708, 13709, 436, 437, 438, 11936, 11937, 11938,
		// Tin rocks
		13712, 13713, 11933, 11934, 11935,
		// Iron rocks
		13710, 13711, 440, 441, 442, 11954, 11955, 11956,
		// Coal rocks
		13714, 13706, 453, 454, 455, 11930, 11931, 11932,
		// Gold rocks
		13715, 13707, 444, 445, 446, 11951, 11952, 11953,
		// Mithril rocks
		13718, 13719, 447, 448, 449, 11942, 11943, 11944,
		// Adamantite rocks
		13720, 14168, 449, 450, 451, 11939, 11940, 11941,
		// Runite rocks
		14175, 451, 452, 4860, 4861,
		// Essence rocks
		14912, 2491, 7471, 10796,
		// Gem rocks
		14856, 14855, 14854, 2111, 2112, 2113
	};

	public DiscordBotQuarrying(DiscordBotStoner bot) {
		this.bot = bot;
	}

	/**
	 * Start quarrying session
	 */
	public void startQuarrying() {
		if (isQuarrying) {
			return;
		}

		System.out.println("Bot starting quarrying session");

		isQuarrying = true;
		quarryingStartTime = System.currentTimeMillis();
		quarryingDuration = 60000 + random.nextInt(180000); // 1-4 minutes
		movingToRock = false;
		targetRock = null;
		lastMiningAttempt = 0;
		successfulMines = 0;
		failedAttempts = 0;

		// FIXED: Initialize experience tracking for Quarrying skill (ID 14)
		lastExperience = bot.getProfession().getExperience()[14]; // Quarrying is skill 14
		lastExperienceCheck = System.currentTimeMillis();

		bot.getActions().sendAutonomousStatusUpdate(START_QUARRYING_MESSAGE);
	}

	/**
	 * Main update method
	 */
	public void update() {
		if (!isQuarrying) {
			return;
		}

		long currentTime = System.currentTimeMillis();

		// Check if session should end
		if (currentTime - quarryingStartTime >= quarryingDuration) {
			stopQuarrying();
			return;
		}

		// FIXED: Only validate movement if we're actually moving
		if (movingToRock && bot.getBotLocation().isMoving()) {
			bot.getBotLocation().validateMovement();
		}

		// FIXED: Simple mining state check
		updateMiningState(currentTime);

		// Handle movement to rock
		if (movingToRock && targetRock != null) {
			handleMovementToRock(currentTime);
			return;
		}

		// FIXED: Only attempt mining if we should and no quarrying task is running
		if (shouldAttemptMining(currentTime) && !hasActiveQuarryingTask()) {
			attemptMining(currentTime);
		}
	}

	/**
	 * FIXED: Simple mining state tracking
	 */
	private void updateMiningState(long currentTime) {
		// Check if we have an active quarrying task (the real indicator of mining)
		boolean hasQuarryingTask = hasActiveQuarryingTask();

		// If we don't have a task and enough time has passed, we're not mining
		if (!hasQuarryingTask && (currentTime - lastMiningAttempt) > 3000) {
			// Check for experience gain to confirm successful mining (only track quarrying skill 14)
			double currentExperience = bot.getProfession().getExperience()[14]; // Quarrying skill only
			if (currentExperience > lastExperience) {
				double xpGained = currentExperience - lastExperience;
				System.out.println("Bot gained quarrying XP: " + xpGained + " (skill 14 only)");
				successfulMines++;
				lastExperience = currentExperience;

				// Debug: Check if other skills are also gaining XP
				if (currentTime % 10000 < 1000) { // Print occasionally for debugging
					System.out.println("Current XP - Skill 14: " + currentExperience +
						", Skill 20: " + bot.getProfession().getExperience()[20]);
				}
			}
		}
	}

	/**
	 * Handle movement to rock
	 */
	private void handleMovementToRock(long currentTime) {
		if (targetRock == null) {
			movingToRock = false;
			return;
		}

		Location rockLocation = new Location(targetRock.getX(), targetRock.getY(), targetRock.getZ());
		Location botLocation = bot.getLocation();
		int distance = calculateDistance(botLocation, rockLocation);

		// Check if we've reached the rock
		if (distance <= 1) {
			System.out.println("Reached rock, attempting to mine");
			movingToRock = false;

			// FIXED: Use the standard quarrying system - no duplicates
			try {
				boolean success = Quarrying.clickRock(bot, targetRock);
				if (success) {
					lastMiningAttempt = currentTime;
					System.out.println("Started mining rock using standard quarrying system!");
				} else {
					System.out.println("Failed to start mining rock");
					failedAttempts++;
				}
			} catch (Exception e) {
				System.out.println("Error mining rock: " + e.getMessage());
				failedAttempts++;
			}

			targetRock = null;
			return;
		}

		// Check movement progress
		if (!bot.getBotLocation().isMoving()) {
			if (distance > 8) {
				// Too far and movement failed
				System.out.println("Movement failed for distant rock, trying closer rock");
				movingToRock = false;
				targetRock = null;
				lastMiningAttempt = currentTime - 2000;
				return;
			} else {
				// Try again
				System.out.println("Movement stopped, trying again (distance: " + distance + ")");
				bot.getBotLocation().performMove(rockLocation);
			}
		}

		// Timeout check
		if (currentTime - lastMiningAttempt > 15000) {
			System.out.println("Movement timeout, trying different rock");
			movingToRock = false;
			targetRock = null;
			lastMiningAttempt = currentTime - 2000;
		}
	}

	/**
	 * Check if we should attempt mining
	 */
	private boolean shouldAttemptMining(long currentTime) {
		// Don't spam attempts
		if (currentTime - lastMiningAttempt < 3000) {
			return false;
		}

		// Don't attempt if already moving
		if (bot.getBotLocation().isMoving() || movingToRock) {
			return false;
		}

		return true;
	}

	/**
	 * FIXED: Simple mining attempt using existing systems
	 */
	private void attemptMining(long currentTime) {
		RSObject rock = findBestRock();
		if (rock == null) {
			// No rocks found, search wider
			if (currentTime - lastMiningAttempt > 8000) {
				System.out.println("No rocks found, searching wider area");
				bot.getBotLocation().performRandomWalk(6, 2);
				lastMiningAttempt = currentTime;
			}
			return;
		}

		Location rockLocation = new Location(rock.getX(), rock.getY(), rock.getZ());
		Location botLocation = bot.getLocation();
		int distance = calculateDistance(botLocation, rockLocation);

		System.out.println("Found rock at " + rockLocation + ", distance: " + distance);

		// Check if we're close enough to mine directly
		if (distance <= 1) {
			// FIXED: Use standard quarrying system only
			System.out.println("Close enough to mine directly");
			try {
				boolean success = Quarrying.clickRock(bot, rock);
				if (success) {
					lastMiningAttempt = currentTime;
					System.out.println("Started mining rock directly using standard system!");
				} else {
					System.out.println("Failed to mine rock directly");
					failedAttempts++;
					lastMiningAttempt = currentTime;
				}
			} catch (Exception e) {
				System.out.println("Error mining rock: " + e.getMessage());
				failedAttempts++;
				lastMiningAttempt = currentTime;
			}
		} else if (distance <= 8) {
			// Walk to nearby rock
			System.out.println("Walking to nearby rock, distance: " + distance);
			movingToRock = true;
			targetRock = rock;
			lastMiningAttempt = currentTime;
			bot.getBotLocation().performMove(rockLocation);
		} else {
			// Rock too far, move closer to area
			System.out.println("Rock too far, moving closer");
			Location moveTarget = getLocationTowardsRocks(botLocation, rockLocation);
			bot.getBotLocation().performMove(moveTarget);
			lastMiningAttempt = currentTime;
		}
	}

	/**
	 * Find a random rock with better deduplication and debugging
	 */
	private RSObject findBestRock() {
		List<RSObject> allRocks = findNearbyRocks();
		if (allRocks.isEmpty()) {
			System.out.println("No rocks found in area");
			return null;
		}

		// Debug: Print all found rocks
		System.out.println("Found " + allRocks.size() + " rock objects:");
		for (RSObject rock : allRocks) {
			System.out.println("  Rock ID: " + rock.getId() + " at (" + rock.getX() + "," + rock.getY() + ")");
		}

		// Remove duplicate locations (keep only one rock per coordinate)
		List<RSObject> uniqueRocks = deduplicateRocksByLocation(allRocks);

		if (uniqueRocks.isEmpty()) {
			System.out.println("No unique rocks after deduplication");
			return null;
		}

		System.out.println("After deduplication: " + uniqueRocks.size() + " unique rocks");

		Location botLocation = bot.getLocation();

		// Group rocks by distance ranges for weighted selection
		List<RSObject> veryClose = new ArrayList<>();  // 1-2 tiles
		List<RSObject> close = new ArrayList<>();      // 3-5 tiles
		List<RSObject> nearby = new ArrayList<>();     // 6-8 tiles

		for (RSObject rock : uniqueRocks) {
			Location rockLocation = new Location(rock.getX(), rock.getY(), rock.getZ());
			int distance = calculateDistance(botLocation, rockLocation);

			if (distance <= 2) {
				veryClose.add(rock);
			} else if (distance <= 5) {
				close.add(rock);
			} else if (distance <= 8) {
				nearby.add(rock);
			}
		}

		// Weighted random selection with debugging
		RSObject selectedRock = null;

		if (!veryClose.isEmpty() && random.nextFloat() < 0.4f) { // Reduced preference for very close
			selectedRock = veryClose.get(random.nextInt(veryClose.size()));
			System.out.println("Selected very close rock (ID: " + selectedRock.getId() + ")");
		} else if (!close.isEmpty() && random.nextFloat() < 0.5f) {
			selectedRock = close.get(random.nextInt(close.size()));
			System.out.println("Selected close rock (ID: " + selectedRock.getId() + ")");
		} else if (!nearby.isEmpty()) {
			selectedRock = nearby.get(random.nextInt(nearby.size()));
			System.out.println("Selected nearby rock (ID: " + selectedRock.getId() + ")");
		} else if (!veryClose.isEmpty()) {
			selectedRock = veryClose.get(random.nextInt(veryClose.size()));
			System.out.println("Fallback to very close rock (ID: " + selectedRock.getId() + ")");
		}

		if (selectedRock != null) {
			System.out.println("Final selection: Rock ID " + selectedRock.getId() +
				" at (" + selectedRock.getX() + "," + selectedRock.getY() + ")");
		}

		return selectedRock;
	}

	/**
	 * Remove duplicate rocks at the same location, preferring specific rock types
	 */
	private List<RSObject> deduplicateRocksByLocation(List<RSObject> rocks) {
		// Use a map to track best rock at each location
		Map<String, RSObject> locationMap = new HashMap<>();

		for (RSObject rock : rocks) {
			String locationKey = rock.getX() + "," + rock.getY() + "," + rock.getZ();

			RSObject existing = locationMap.get(locationKey);
			if (existing == null) {
				locationMap.put(locationKey, rock);
			} else {
				// Prefer more valuable rock types
				RSObject better = chooseBetterRock(existing, rock);
				locationMap.put(locationKey, better);
			}
		}

		return new ArrayList<>(locationMap.values());
	}

	/**
	 * Choose the better rock between two at the same location
	 */
	private RSObject chooseBetterRock(RSObject rock1, RSObject rock2) {
		// Priority order: Runite > Adamantite > Mithril > Gold > Coal > Iron > Tin > Copper
		int priority1 = getRockPriority(rock1.getId());
		int priority2 = getRockPriority(rock2.getId());

		if (priority1 != priority2) {
			return priority1 > priority2 ? rock1 : rock2;
		}

		// If same priority, randomly choose
		return random.nextBoolean() ? rock1 : rock2;
	}

	/**
	 * Get rock priority for selection (higher = better)
	 */
	private int getRockPriority(int rockId) {
		// Runite rocks
		if (rockId == 14175 || rockId == 451 || rockId == 452 || rockId == 4860 || rockId == 4861) return 8;

		// Adamantite rocks
		if (rockId == 13720 || rockId == 14168 || rockId == 449 || rockId == 450 ||
			rockId == 451 || rockId == 11939 || rockId == 11940 || rockId == 11941) return 7;

		// Mithril rocks
		if (rockId == 13718 || rockId == 13719 || rockId == 447 || rockId == 448 ||
			rockId == 449 || rockId == 11942 || rockId == 11943 || rockId == 11944) return 6;

		// Gold rocks
		if (rockId == 13715 || rockId == 13707 || rockId == 444 || rockId == 445 ||
			rockId == 446 || rockId == 11951 || rockId == 11952 || rockId == 11953) return 5;

		// Coal rocks
		if (rockId == 13714 || rockId == 13706 || rockId == 453 || rockId == 454 ||
			rockId == 455 || rockId == 11930 || rockId == 11931 || rockId == 11932) return 4;

		// Iron rocks
		if (rockId == 13710 || rockId == 13711 || rockId == 440 || rockId == 441 ||
			rockId == 442 || rockId == 11954 || rockId == 11955 || rockId == 11956) return 3;

		// Tin rocks
		if (rockId == 13712 || rockId == 13713 || rockId == 11933 || rockId == 11934 || rockId == 11935) return 2;

		// Copper rocks
		if (rockId == 13708 || rockId == 13709 || rockId == 436 || rockId == 437 ||
			rockId == 438 || rockId == 11936 || rockId == 11937 || rockId == 11938) return 1;

		// Special rocks (Essence, Gem) - medium priority
		if (rockId == 14912 || rockId == 2491 || rockId == 7471 || rockId == 10796 ||
			rockId == 14856 || rockId == 14855 || rockId == 14854 || rockId == 2111 ||
			rockId == 2112 || rockId == 2113) return 4;

		// Unknown rocks
		return 0;
	}

	/**
	 * Find nearby quarrying rocks
	 */
	private List<RSObject> findNearbyRocks() {
		List<RSObject> rocks = new ArrayList<>();
		Location botLocation = bot.getLocation();

		try {
			// Search in a wider radius for more rock options
			for (RSObject object : bot.getBotObjectHandler().findObjectsInRadius(botLocation, 15)) {
				if (object != null && isQuarryObject(object.getId())) {
					rocks.add(object);
				}
			}
		} catch (Exception e) {
			System.out.println("Error finding nearby rocks: " + e.getMessage());
		}

		return rocks;
	}

	/**
	 * Check if object is a quarrying object
	 */
	private boolean isQuarryObject(int objectId) {
		for (int id : QUARRY_OBJECTS) {
			if (id == objectId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get a location that's closer to the rocks
	 */
	private Location getLocationTowardsRocks(Location from, Location rockLocation) {
		int deltaX = rockLocation.getX() - from.getX();
		int deltaY = rockLocation.getY() - from.getY();

		// Move 4 tiles towards the rocks
		int moveDistance = 4;
		double totalDistance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

		if (totalDistance > 0) {
			int moveX = (int) ((deltaX / totalDistance) * moveDistance);
			int moveY = (int) ((deltaY / totalDistance) * moveDistance);

			return new Location(
				from.getX() + moveX,
				from.getY() + moveY,
				from.getZ()
			);
		}

		return rockLocation;
	}

	/**
	 * Calculate distance between locations
	 */
	private int calculateDistance(Location loc1, Location loc2) {
		int deltaX = Math.abs(loc1.getX() - loc2.getX());
		int deltaY = Math.abs(loc1.getY() - loc2.getY());
		return Math.max(deltaX, deltaY);
	}

	/**
	 * FIXED: Check if bot has active quarrying tasks (real indicator)
	 */
	private boolean hasActiveQuarryingTask() {
		try {
			java.util.LinkedList<com.bestbudz.core.task.Task> botTasks = bot.getTasks();
			if (botTasks != null) {
				for (com.bestbudz.core.task.Task task : botTasks) {
					if (task.getTaskId() == com.bestbudz.core.task.impl.TaskIdentifier.CURRENT_ACTION) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// Ignore errors
		}
		return false;
	}

	/**
	 * Stop quarrying session
	 */
	public void stopQuarrying() {
		if (!isQuarrying) {
			return;
		}

		isQuarrying = false;
		movingToRock = false;
		targetRock = null;

		// Move away from current rock to avoid getting stuck
		try {
			System.out.println("Quarrying session ended, moving away from current location");
			bot.getBotLocation().performRandomWalk(3, 5); // Walk 3-5 tiles randomly
		} catch (Exception e) {
			System.out.println("Error moving away after quarrying: " + e.getMessage());
		}

		long sessionDuration = System.currentTimeMillis() - quarryingStartTime;
		System.out.println("Quarrying session completed: " + (sessionDuration / 1000) + "s, " +
			successfulMines + " successful mines, " + failedAttempts + " failed attempts");

}

	// Public getters for status
	public boolean isQuarrying() {
		return isQuarrying;
	}

	public boolean isCurrentlyMining() {
		return hasActiveQuarryingTask() || movingToRock;
	}

	public long getQuarryingTimeRemaining() {
		if (!isQuarrying) {
			return 0;
		}
		long elapsed = System.currentTimeMillis() - quarryingStartTime;
		return Math.max(0, quarryingDuration - elapsed);
	}

	public String getQuarryingStatus() {
		if (!isQuarrying) {
			return "Not quarrying";
		}

		long timeRemaining = getQuarryingTimeRemaining();
		String miningStatus;
		if (movingToRock) {
			miningStatus = "Walking to rock";
		} else if (hasActiveQuarryingTask()) {
			miningStatus = "Mining";
		} else {
			miningStatus = "Searching";
		}
		String stats = " | " + successfulMines + " mined, " + failedAttempts + " failed";

		return "Quarrying: " + (timeRemaining / 1000) + "s | " + miningStatus + stats;
	}
}