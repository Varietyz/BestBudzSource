package com.bestbudz.core.discord.stonerbot.automations.professions;

import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.content.profession.lumbering.LumberingTask;
import com.bestbudz.rs2.entity.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DiscordBotLumbering {

	private final DiscordBotStoner bot;
	private final Random random = new Random();

	private volatile boolean isLumbering = false;
	private volatile long lumberingStartTime = 0;
	private volatile long lumberingDuration = 0;
	private volatile boolean movingToTree = false;
	private volatile RSObject targetTree = null;
	private volatile long lastChoppingAttempt = 0;
	private volatile long lastExperienceCheck = 0;
	private volatile double lastExperience = 0;

	private volatile int successfulChops = 0;
	private volatile int failedAttempts = 0;

	private static final int[] TREE_OBJECTS = {

		1276, 1277, 1278, 1279, 1280, 1330, 1331, 1332, 5902, 5903, 5904,
		11684, 11685, 10041, 10042, 10043,

		1281, 10820, 14308, 14309, 37477, 37478,
		11756,

		1308, 5551, 5552, 5553, 14308, 25016, 25017, 25018,
		11759,

		1307, 4674, 4675, 7394, 40754,
		11762,

		1309, 7402, 7403, 36984,
		11758,

		1306, 7401, 37823,
		11764,

		1318, 1319, 1315, 1316
	};

	public DiscordBotLumbering(DiscordBotStoner bot) {
		this.bot = bot;
	}

	public void startLumbering() {
		if (isLumbering) {
			return;
		}

		System.out.println("Bot starting lumbering session");

		isLumbering = true;
		lumberingStartTime = System.currentTimeMillis();
		lumberingDuration = 60000 + random.nextInt(180000);
		movingToTree = false;
		targetTree = null;
		lastChoppingAttempt = 0;
		successfulChops = 0;
		failedAttempts = 0;

		lastExperience = bot.getProfession().getExperience()[8];
		lastExperienceCheck = System.currentTimeMillis();

		bot.getActions().sendAutonomousStatusUpdate("Chop chop choppity chop chop time!");
	}

	public void update() {
		if (!isLumbering) {
			return;
		}

		long currentTime = System.currentTimeMillis();

		if (currentTime - lumberingStartTime >= lumberingDuration) {
			stopLumbering();
			return;
		}

		if (movingToTree && bot.getBotLocation().isMoving()) {
			bot.getBotLocation().validateMovement();
		}

		updateChoppingState(currentTime);

		if (movingToTree && targetTree != null) {
			handleMovementToTree(currentTime);
			return;
		}

		if (shouldAttemptChopping(currentTime) && !hasActiveLumberingTask()) {
			attemptChopping(currentTime);
		}
	}

	private void updateChoppingState(long currentTime) {

		boolean hasLumberingTask = hasActiveLumberingTask();

		if (!hasLumberingTask && (currentTime - lastChoppingAttempt) > 3000) {

			double currentExperience = bot.getProfession().getExperience()[8];
			if (currentExperience > lastExperience) {
				System.out.println("Bot gained lumbering XP: " + (currentExperience - lastExperience));
				successfulChops++;
				lastExperience = currentExperience;
			}
		}
	}

	private void handleMovementToTree(long currentTime) {
		if (targetTree == null) {
			movingToTree = false;
			return;
		}

		Location treeLocation = new Location(targetTree.getX(), targetTree.getY(), targetTree.getZ());
		Location botLocation = bot.getLocation();
		int distance = calculateDistance(botLocation, treeLocation);

		if (distance <= 1) {
			System.out.println("Reached tree, attempting to chop");
			movingToTree = false;

			try {
				LumberingTask.attemptLumbering(bot, targetTree.getId(),
					targetTree.getX(), targetTree.getY());
				lastChoppingAttempt = currentTime;
				System.out.println("Started chopping tree using standard lumbering system!");
			} catch (Exception e) {
				System.out.println("Error chopping tree: " + e.getMessage());
				failedAttempts++;
				lastChoppingAttempt = currentTime;
			}

			targetTree = null;
			return;
		}

		if (!bot.getBotLocation().isMoving()) {
			if (distance > 8) {

				System.out.println("Movement failed for distant tree, trying closer tree");
				movingToTree = false;
				targetTree = null;
				lastChoppingAttempt = currentTime - 2000;
				return;
			} else {

				System.out.println("Movement stopped, trying again (distance: " + distance + ")");
				bot.getBotLocation().performMove(treeLocation);
			}
		}

		if (currentTime - lastChoppingAttempt > 15000) {
			System.out.println("Movement timeout, trying different tree");
			movingToTree = false;
			targetTree = null;
			lastChoppingAttempt = currentTime - 2000;
		}
	}

	private boolean shouldAttemptChopping(long currentTime) {

		if (currentTime - lastChoppingAttempt < 3000) {
			return false;
		}

		if (bot.getBotLocation().isMoving() || movingToTree) {
			return false;
		}

		return true;
	}

	private void attemptChopping(long currentTime) {
		RSObject tree = findBestTree();
		if (tree == null) {

			if (currentTime - lastChoppingAttempt > 8000) {
				System.out.println("No trees found, searching wider area");
				bot.getBotLocation().performRandomWalk(6, 2);
				lastChoppingAttempt = currentTime;
			}
			return;
		}

		Location treeLocation = new Location(tree.getX(), tree.getY(), tree.getZ());
		Location botLocation = bot.getLocation();
		int distance = calculateDistance(botLocation, treeLocation);

		System.out.println("Found tree at " + treeLocation + ", distance: " + distance);

		if (distance <= 1) {

			System.out.println("Close enough to chop directly");
			try {
				LumberingTask.attemptLumbering(bot, tree.getId(), tree.getX(), tree.getY());
				lastChoppingAttempt = currentTime;
				System.out.println("Started chopping tree directly using standard system!");
			} catch (Exception e) {
				System.out.println("Error chopping tree: " + e.getMessage());
				failedAttempts++;
				lastChoppingAttempt = currentTime;
			}
		} else if (distance <= 8) {

			System.out.println("Walking to nearby tree, distance: " + distance);
			movingToTree = true;
			targetTree = tree;
			lastChoppingAttempt = currentTime;
			bot.getBotLocation().performMove(treeLocation);
		} else {

			System.out.println("Tree too far, moving closer");
			Location moveTarget = getLocationTowardsTrees(botLocation, treeLocation);
			bot.getBotLocation().performMove(moveTarget);
			lastChoppingAttempt = currentTime;
		}
	}

	private RSObject findBestTree() {
		List<RSObject> allTrees = findNearbyTrees();
		if (allTrees.isEmpty()) {
			System.out.println("No trees found in area");
			return null;
		}

		System.out.println("Found " + allTrees.size() + " tree objects:");
		for (RSObject tree : allTrees) {
			System.out.println("  Tree ID: " + tree.getId() + " at (" + tree.getX() + "," + tree.getY() + ")");
		}

		List<RSObject> uniqueTrees = deduplicateTreesByLocation(allTrees);

		if (uniqueTrees.isEmpty()) {
			System.out.println("No unique trees after deduplication");
			return null;
		}

		System.out.println("After deduplication: " + uniqueTrees.size() + " unique trees");

		Location botLocation = bot.getLocation();

		List<RSObject> veryClose = new ArrayList<>();
		List<RSObject> close = new ArrayList<>();
		List<RSObject> nearby = new ArrayList<>();

		for (RSObject tree : uniqueTrees) {
			Location treeLocation = new Location(tree.getX(), tree.getY(), tree.getZ());
			int distance = calculateDistance(botLocation, treeLocation);

			if (distance <= 2) {
				veryClose.add(tree);
			} else if (distance <= 5) {
				close.add(tree);
			} else if (distance <= 8) {
				nearby.add(tree);
			}
		}

		RSObject selectedTree = null;

		if (!veryClose.isEmpty() && random.nextFloat() < 0.4f) {
			selectedTree = veryClose.get(random.nextInt(veryClose.size()));
			System.out.println("Selected very close tree (ID: " + selectedTree.getId() + ")");
		} else if (!close.isEmpty() && random.nextFloat() < 0.5f) {
			selectedTree = close.get(random.nextInt(close.size()));
			System.out.println("Selected close tree (ID: " + selectedTree.getId() + ")");
		} else if (!nearby.isEmpty()) {
			selectedTree = nearby.get(random.nextInt(nearby.size()));
			System.out.println("Selected nearby tree (ID: " + selectedTree.getId() + ")");
		} else if (!veryClose.isEmpty()) {
			selectedTree = veryClose.get(random.nextInt(veryClose.size()));
			System.out.println("Fallback to very close tree (ID: " + selectedTree.getId() + ")");
		}

		if (selectedTree != null) {
			System.out.println("Final selection: Tree ID " + selectedTree.getId() +
				" at (" + selectedTree.getX() + "," + selectedTree.getY() + ")");
		}

		return selectedTree;
	}

	private List<RSObject> deduplicateTreesByLocation(List<RSObject> trees) {

		Map<String, RSObject> locationMap = new HashMap<>();

		for (RSObject tree : trees) {
			String locationKey = tree.getX() + "," + tree.getY() + "," + tree.getZ();

			RSObject existing = locationMap.get(locationKey);
			if (existing == null) {
				locationMap.put(locationKey, tree);
			} else {

				RSObject better = chooseBetterTree(existing, tree);
				locationMap.put(locationKey, better);
			}
		}

		return new ArrayList<>(locationMap.values());
	}

	private RSObject chooseBetterTree(RSObject tree1, RSObject tree2) {

		int priority1 = getTreePriority(tree1.getId());
		int priority2 = getTreePriority(tree2.getId());

		if (priority1 != priority2) {
			return priority1 > priority2 ? tree1 : tree2;
		}

		return random.nextBoolean() ? tree1 : tree2;
	}

	private int getTreePriority(int treeId) {

		if (treeId == 1306 || treeId == 7401 || treeId == 37823 || treeId == 11764) return 6;

		if (treeId == 1309 || treeId == 7402 || treeId == 7403 || treeId == 36984 || treeId == 11758) return 5;

		if (treeId == 1307 || treeId == 4674 || treeId == 4675 || treeId == 7394 || treeId == 40754 || treeId == 11762) return 4;

		if (treeId == 1308 || treeId == 5551 || treeId == 5552 || treeId == 5553 ||
			treeId == 14308 || treeId == 25016 || treeId == 25017 || treeId == 25018 || treeId == 11759) return 3;

		if (treeId == 1281 || treeId == 10820 || treeId == 14308 || treeId == 14309 ||
			treeId == 37477 || treeId == 37478 || treeId == 11756) return 2;

		return 1;
	}

	private List<RSObject> findNearbyTrees() {
		List<RSObject> trees = new ArrayList<>();
		Location botLocation = bot.getLocation();

		try {

			for (RSObject object : bot.getBotObjectHandler().findObjectsInRadius(botLocation, 15)) {
				if (object != null && isTreeObject(object.getId())) {
					trees.add(object);
				}
			}
		} catch (Exception e) {
			System.out.println("Error finding nearby trees: " + e.getMessage());
		}

		return trees;
	}

	private boolean isTreeObject(int objectId) {
		for (int id : TREE_OBJECTS) {
			if (id == objectId) {
				return true;
			}
		}
		return false;
	}

	private Location getLocationTowardsTrees(Location from, Location treeLocation) {
		int deltaX = treeLocation.getX() - from.getX();
		int deltaY = treeLocation.getY() - from.getY();

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

		return treeLocation;
	}

	private int calculateDistance(Location loc1, Location loc2) {
		int deltaX = Math.abs(loc1.getX() - loc2.getX());
		int deltaY = Math.abs(loc1.getY() - loc2.getY());
		return Math.max(deltaX, deltaY);
	}

	private boolean hasActiveLumberingTask() {
		try {
			java.util.LinkedList<com.bestbudz.core.task.Task> botTasks = bot.getTasks();
			if (botTasks != null) {
				for (com.bestbudz.core.task.Task task : botTasks) {
					if (task instanceof LumberingTask) {
						return true;
					}
				}
			}
		} catch (Exception e) {

		}
		return false;
	}

	public void stopLumbering() {
		if (!isLumbering) {
			return;
		}

		isLumbering = false;
		movingToTree = false;
		targetTree = null;

		try {
			System.out.println("Lumbering session ended, moving away from current location");
			bot.getBotLocation().performRandomWalk(3, 5);
		} catch (Exception e) {
			System.out.println("Error moving away after lumbering: " + e.getMessage());
		}

		long sessionDuration = System.currentTimeMillis() - lumberingStartTime;
		System.out.println("Lumbering session completed: " + (sessionDuration / 1000) + "s, " +
			successfulChops + " successful chops, " + failedAttempts + " failed attempts");

		bot.getActions().sendAutonomousStatusUpdate("Lumbering complete! " + successfulChops + " trees chopped.");
	}

	public boolean isLumbering() {
		return isLumbering;
	}

	public boolean isCurrentlyChopping() {
		return hasActiveLumberingTask() || movingToTree;
	}

	public long getLumberingTimeRemaining() {
		if (!isLumbering) {
			return 0;
		}
		long elapsed = System.currentTimeMillis() - lumberingStartTime;
		return Math.max(0, lumberingDuration - elapsed);
	}

	public String getLumberingStatus() {
		if (!isLumbering) {
			return "Not lumbering";
		}

		long timeRemaining = getLumberingTimeRemaining();
		String choppingStatus;
		if (movingToTree) {
			choppingStatus = "Walking to tree";
		} else if (hasActiveLumberingTask()) {
			choppingStatus = "Chopping";
		} else {
			choppingStatus = "Searching";
		}
		String stats = " | " + successfulChops + " chopped, " + failedAttempts + " failed";

		return "Lumbering: " + (timeRemaining / 1000) + "s | " + choppingStatus + stats;
	}
}
