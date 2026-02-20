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

public class DiscordBotQuarrying {

	private final DiscordBotStoner bot;
	private final Random random = new Random();

	private volatile boolean isQuarrying = false;
	private volatile long quarryingStartTime = 0;
	private volatile long quarryingDuration = 0;
	private volatile boolean movingToRock = false;
	private volatile RSObject targetRock = null;
	private volatile long lastMiningAttempt = 0;
	private volatile long lastExperienceCheck = 0;
	private volatile double lastExperience = 0;

	private volatile int successfulMines = 0;
	private volatile int failedAttempts = 0;

	private static final int[] QUARRY_OBJECTS = {

		13708, 13709, 436, 437, 438, 11936, 11937, 11938,

		13712, 13713, 11933, 11934, 11935,

		13710, 13711, 440, 441, 442, 11954, 11955, 11956,

		13714, 13706, 453, 454, 455, 11930, 11931, 11932,

		13715, 13707, 444, 445, 446, 11951, 11952, 11953,

		13718, 13719, 447, 448, 449, 11942, 11943, 11944,

		13720, 14168, 449, 450, 451, 11939, 11940, 11941,

		14175, 451, 452, 4860, 4861,

		14912, 2491, 7471, 10796,

		14856, 14855, 14854, 2111, 2112, 2113
	};

	public DiscordBotQuarrying(DiscordBotStoner bot) {
		this.bot = bot;
	}

	public void startQuarrying() {
		if (isQuarrying) {
			return;
		}

		System.out.println("Bot starting quarrying session");

		isQuarrying = true;
		quarryingStartTime = System.currentTimeMillis();
		quarryingDuration = 60000 + random.nextInt(180000);
		movingToRock = false;
		targetRock = null;
		lastMiningAttempt = 0;
		successfulMines = 0;
		failedAttempts = 0;

		lastExperience = bot.getProfession().getExperience()[14];
		lastExperienceCheck = System.currentTimeMillis();

		bot.getActions().sendAutonomousStatusUpdate(START_QUARRYING_MESSAGE);
	}

	public void update() {
		if (!isQuarrying) {
			return;
		}

		long currentTime = System.currentTimeMillis();

		if (currentTime - quarryingStartTime >= quarryingDuration) {
			stopQuarrying();
			return;
		}

		if (movingToRock && bot.getBotLocation().isMoving()) {
			bot.getBotLocation().validateMovement();
		}

		updateMiningState(currentTime);

		if (movingToRock && targetRock != null) {
			handleMovementToRock(currentTime);
			return;
		}

		if (shouldAttemptMining(currentTime) && !hasActiveQuarryingTask()) {
			attemptMining(currentTime);
		}
	}

	private void updateMiningState(long currentTime) {

		boolean hasQuarryingTask = hasActiveQuarryingTask();

		if (!hasQuarryingTask && (currentTime - lastMiningAttempt) > 3000) {

			double currentExperience = bot.getProfession().getExperience()[14];
			if (currentExperience > lastExperience) {
				double xpGained = currentExperience - lastExperience;
				System.out.println("Bot gained quarrying XP: " + xpGained + " (skill 14 only)");
				successfulMines++;
				lastExperience = currentExperience;

				if (currentTime % 10000 < 1000) {
					System.out.println("Current XP - Skill 14: " + currentExperience +
						", Skill 20: " + bot.getProfession().getExperience()[20]);
				}
			}
		}
	}

	private void handleMovementToRock(long currentTime) {
		if (targetRock == null) {
			movingToRock = false;
			return;
		}

		Location rockLocation = new Location(targetRock.getX(), targetRock.getY(), targetRock.getZ());
		Location botLocation = bot.getLocation();
		int distance = calculateDistance(botLocation, rockLocation);

		if (distance <= 1) {
			System.out.println("Reached rock, attempting to mine");
			movingToRock = false;

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

		if (!bot.getBotLocation().isMoving()) {
			if (distance > 8) {

				System.out.println("Movement failed for distant rock, trying closer rock");
				movingToRock = false;
				targetRock = null;
				lastMiningAttempt = currentTime - 2000;
				return;
			} else {

				System.out.println("Movement stopped, trying again (distance: " + distance + ")");
				bot.getBotLocation().performMove(rockLocation);
			}
		}

		if (currentTime - lastMiningAttempt > 15000) {
			System.out.println("Movement timeout, trying different rock");
			movingToRock = false;
			targetRock = null;
			lastMiningAttempt = currentTime - 2000;
		}
	}

	private boolean shouldAttemptMining(long currentTime) {

		if (currentTime - lastMiningAttempt < 3000) {
			return false;
		}

		if (bot.getBotLocation().isMoving() || movingToRock) {
			return false;
		}

		return true;
	}

	private void attemptMining(long currentTime) {
		RSObject rock = findBestRock();
		if (rock == null) {

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

		if (distance <= 1) {

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

			System.out.println("Walking to nearby rock, distance: " + distance);
			movingToRock = true;
			targetRock = rock;
			lastMiningAttempt = currentTime;
			bot.getBotLocation().performMove(rockLocation);
		} else {

			System.out.println("Rock too far, moving closer");
			Location moveTarget = getLocationTowardsRocks(botLocation, rockLocation);
			bot.getBotLocation().performMove(moveTarget);
			lastMiningAttempt = currentTime;
		}
	}

	private RSObject findBestRock() {
		List<RSObject> allRocks = findNearbyRocks();
		if (allRocks.isEmpty()) {
			System.out.println("No rocks found in area");
			return null;
		}

		System.out.println("Found " + allRocks.size() + " rock objects:");
		for (RSObject rock : allRocks) {
			System.out.println("  Rock ID: " + rock.getId() + " at (" + rock.getX() + "," + rock.getY() + ")");
		}

		List<RSObject> uniqueRocks = deduplicateRocksByLocation(allRocks);

		if (uniqueRocks.isEmpty()) {
			System.out.println("No unique rocks after deduplication");
			return null;
		}

		System.out.println("After deduplication: " + uniqueRocks.size() + " unique rocks");

		Location botLocation = bot.getLocation();

		List<RSObject> veryClose = new ArrayList<>();
		List<RSObject> close = new ArrayList<>();
		List<RSObject> nearby = new ArrayList<>();

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

		RSObject selectedRock = null;

		if (!veryClose.isEmpty() && random.nextFloat() < 0.4f) {
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

	private List<RSObject> deduplicateRocksByLocation(List<RSObject> rocks) {

		Map<String, RSObject> locationMap = new HashMap<>();

		for (RSObject rock : rocks) {
			String locationKey = rock.getX() + "," + rock.getY() + "," + rock.getZ();

			RSObject existing = locationMap.get(locationKey);
			if (existing == null) {
				locationMap.put(locationKey, rock);
			} else {

				RSObject better = chooseBetterRock(existing, rock);
				locationMap.put(locationKey, better);
			}
		}

		return new ArrayList<>(locationMap.values());
	}

	private RSObject chooseBetterRock(RSObject rock1, RSObject rock2) {

		int priority1 = getRockPriority(rock1.getId());
		int priority2 = getRockPriority(rock2.getId());

		if (priority1 != priority2) {
			return priority1 > priority2 ? rock1 : rock2;
		}

		return random.nextBoolean() ? rock1 : rock2;
	}

	private int getRockPriority(int rockId) {

		if (rockId == 14175 || rockId == 451 || rockId == 452 || rockId == 4860 || rockId == 4861) return 8;

		if (rockId == 13720 || rockId == 14168 || rockId == 449 || rockId == 450 ||
			rockId == 451 || rockId == 11939 || rockId == 11940 || rockId == 11941) return 7;

		if (rockId == 13718 || rockId == 13719 || rockId == 447 || rockId == 448 ||
			rockId == 449 || rockId == 11942 || rockId == 11943 || rockId == 11944) return 6;

		if (rockId == 13715 || rockId == 13707 || rockId == 444 || rockId == 445 ||
			rockId == 446 || rockId == 11951 || rockId == 11952 || rockId == 11953) return 5;

		if (rockId == 13714 || rockId == 13706 || rockId == 453 || rockId == 454 ||
			rockId == 455 || rockId == 11930 || rockId == 11931 || rockId == 11932) return 4;

		if (rockId == 13710 || rockId == 13711 || rockId == 440 || rockId == 441 ||
			rockId == 442 || rockId == 11954 || rockId == 11955 || rockId == 11956) return 3;

		if (rockId == 13712 || rockId == 13713 || rockId == 11933 || rockId == 11934 || rockId == 11935) return 2;

		if (rockId == 13708 || rockId == 13709 || rockId == 436 || rockId == 437 ||
			rockId == 438 || rockId == 11936 || rockId == 11937 || rockId == 11938) return 1;

		if (rockId == 14912 || rockId == 2491 || rockId == 7471 || rockId == 10796 ||
			rockId == 14856 || rockId == 14855 || rockId == 14854 || rockId == 2111 ||
			rockId == 2112 || rockId == 2113) return 4;

		return 0;
	}

	private List<RSObject> findNearbyRocks() {
		List<RSObject> rocks = new ArrayList<>();
		Location botLocation = bot.getLocation();

		try {

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

	private boolean isQuarryObject(int objectId) {
		for (int id : QUARRY_OBJECTS) {
			if (id == objectId) {
				return true;
			}
		}
		return false;
	}

	private Location getLocationTowardsRocks(Location from, Location rockLocation) {
		int deltaX = rockLocation.getX() - from.getX();
		int deltaY = rockLocation.getY() - from.getY();

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

	private int calculateDistance(Location loc1, Location loc2) {
		int deltaX = Math.abs(loc1.getX() - loc2.getX());
		int deltaY = Math.abs(loc1.getY() - loc2.getY());
		return Math.max(deltaX, deltaY);
	}

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

		}
		return false;
	}

	public void stopQuarrying() {
		if (!isQuarrying) {
			return;
		}

		isQuarrying = false;
		movingToRock = false;
		targetRock = null;

		try {
			System.out.println("Quarrying session ended, moving away from current location");
			bot.getBotLocation().performRandomWalk(3, 5);
		} catch (Exception e) {
			System.out.println("Error moving away after quarrying: " + e.getMessage());
		}

		long sessionDuration = System.currentTimeMillis() - quarryingStartTime;
		System.out.println("Quarrying session completed: " + (sessionDuration / 1000) + "s, " +
			successfulMines + " successful mines, " + failedAttempts + " failed attempts");

}

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
