package com.bestbudz.core.discord.stonerbot.state;

import com.bestbudz.core.discord.stonerbot.config.DiscordBotDefaults;
import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.pathfinding.RS317PathFinder;

import java.util.Random;
import java.util.logging.Logger;

public class DiscordBotLocation {

	private static final Logger logger = Logger.getLogger(DiscordBotLocation.class.getSimpleName());

	private final DiscordBotStoner bot;
	private final Random random = new Random();

	private volatile Location lastAttemptedLocation = null;
	private volatile long lastMovementCheck = 0;
	private volatile int stuckCounter = 0;

	public DiscordBotLocation(DiscordBotStoner bot) {
		this.bot = bot;
	}

	public void setInitialLocation() {
		bot.getLocation().setAs(DiscordBotDefaults.DEFAULT_LOCATION);
	}

	public void performMove(Location destination) {
		if (destination == null) {
			return;
		}

		if (!isValidLocation(destination)) {
			System.err.println("Cannot move to location with no loaded region: " + destination);
			return;
		}

		try {
			Location currentLocation = bot.getLocation();

			if (currentLocation.equals(destination)) {
				return;
			}

			RS317PathFinder.findRoute(bot, destination.getX(), destination.getY(), true, 1, 1);

			lastAttemptedLocation = new Location(destination);
			lastMovementCheck = System.currentTimeMillis();

		} catch (Exception e) {
			logger.warning("Error moving bot: " + e.getMessage());
		}
	}

	public void validateMovement() {
		long currentTime = System.currentTimeMillis();

		if (currentTime - lastMovementCheck < 5000) {
			return;
		}

		if (isMoving() && lastAttemptedLocation != null) {
			Location currentLocation = bot.getLocation();

			if (currentLocation.equals(lastAttemptedLocation)) {
				stuckCounter++;

				if (stuckCounter >= 2) {
					handleStuckMovement();
					stuckCounter = 0;
				}
			} else {

				stuckCounter = 0;
			}
		}

		lastMovementCheck = currentTime;
	}

	private void handleStuckMovement() {
		try {
			logger.info("Bot appears stuck, resetting movement");

			bot.getMovementHandler().reset();
			stuckCounter = 0;
			lastAttemptedLocation = null;

		} catch (Exception e) {
			logger.warning("Error handling stuck movement: " + e.getMessage());
		}
	}

	public void performRandomWalk(int maxDistance, int minDistance) {
		Location currentLocation = bot.getLocation();
		if (currentLocation == null) {
			return;
		}

		if (!isValidLocation(currentLocation)) {
			System.err.println("Bot in invalid region, cannot perform random walk");
			return;
		}

		Location homeBase = DiscordBotDefaults.DEFAULT_LOCATION;

		for (int attempts = 0; attempts < 15; attempts++) {
			int distance = minDistance + random.nextInt(maxDistance - minDistance + 1);
			int deltaX = random.nextInt(distance * 2 + 1) - distance;
			int deltaY = random.nextInt(distance * 2 + 1) - distance;

			if (Math.abs(deltaX) < minDistance && Math.abs(deltaY) < minDistance) {
				deltaX = minDistance * (random.nextBoolean() ? -1 : 1);
				deltaY = minDistance * (random.nextBoolean() ? -1 : 1);
			}

			Location candidate = new Location(
				currentLocation.getX() + deltaX,
				currentLocation.getY() + deltaY,
				currentLocation.getZ()
			);

			int distanceFromHome = calculateDistance(candidate, homeBase);
			if (distanceFromHome <= 35 && isValidLocation(candidate)) {
				performMove(candidate);
				return;
			}
		}

		System.out.println("Could not find valid random walk destination");
	}

	public void performRandomWalk() {
		performRandomWalk(8, 2);
	}

	public void performAreaWander() {
		try {
			Location currentLocation = bot.getLocation();
			Location homeBase = DiscordBotDefaults.DEFAULT_LOCATION;

			double angle = Math.random() * 2 * Math.PI;
			int distance = 8 + (int)(Math.random() * 12);

			int deltaX = (int) Math.round(Math.cos(angle) * distance);
			int deltaY = (int) Math.round(Math.sin(angle) * distance);

			Location destination = new Location(
				currentLocation.getX() + deltaX,
				currentLocation.getY() + deltaY,
				currentLocation.getZ()
			);

			int distanceFromHome = calculateDistance(destination, homeBase);
			if (distanceFromHome <= 40 && isValidLocation(destination)) {
				performMove(destination);
			} else {

				performRandomWalk(6, 2);
			}

		} catch (Exception e) {
			logger.warning("Error performing area wander: " + e.getMessage());
			performRandomWalk();
		}
	}

	public boolean isTooFarFromHome() {
		Location currentLocation = bot.getLocation();
		Location homeBase = DiscordBotDefaults.DEFAULT_LOCATION;
		return calculateDistance(currentLocation, homeBase) > 40;
	}

	public void moveTowardsHome() {
		try {
			Location homeBase = DiscordBotDefaults.DEFAULT_LOCATION;
			performMove(homeBase);
		} catch (Exception e) {
			logger.warning("Error moving towards home: " + e.getMessage());
		}
	}

	public int getDistanceFromHome() {
		Location currentLocation = bot.getLocation();
		Location homeBase = DiscordBotDefaults.DEFAULT_LOCATION;
		return calculateDistance(currentLocation, homeBase);
	}

	public boolean isMoving() {
		return bot.getMovementHandler().moving();
	}

	public void stopMovement() {
		try {
			bot.getMovementHandler().reset();
			stuckCounter = 0;
			lastAttemptedLocation = null;
		} catch (Exception e) {
			logger.warning("Error stopping movement: " + e.getMessage());
		}
	}

	private int calculateDistance(Location loc1, Location loc2) {
		int deltaX = Math.abs(loc1.getX() - loc2.getX());
		int deltaY = Math.abs(loc1.getY() - loc2.getY());
		return Math.max(deltaX, deltaY);
	}

	public void setNeedsPlacement() {
		bot.setNeedsPlacement(true);
		bot.getUpdateFlags().setUpdateRequired(true);
	}

	public void moveToCombatArea() {
		try {
			Location combatArea = new Location(3415, 2934, bot.getLocation().getZ());
			performMove(combatArea);
			System.out.println("Bot moving to combat area: " + combatArea);
		} catch (Exception e) {
			logger.warning("Error moving to combat area: " + e.getMessage());

			performAreaWander();
		}
	}

	public boolean isNearCombatArea() {
		Location currentLoc = bot.getLocation();
		Location combatArea = new Location(3415, 2934, currentLoc.getZ());

		int distance = Math.max(
			Math.abs(currentLoc.getX() - combatArea.getX()),
			Math.abs(currentLoc.getY() - combatArea.getY())
		);

		return distance <= 10;
	}

	private boolean isValidLocation(Location location) {
		if (location == null) {
			return false;
		}

		try {

			com.bestbudz.core.cache.map.Region region =
				com.bestbudz.core.cache.map.Region.getRegion(location.getX(), location.getY());

			return region != null;
		} catch (Exception e) {
			return false;
		}
	}
}
