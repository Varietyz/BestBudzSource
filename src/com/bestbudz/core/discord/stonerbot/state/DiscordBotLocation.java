package com.bestbudz.core.discord.stonerbot.state;

import com.bestbudz.core.discord.stonerbot.config.DiscordBotDefaults;
import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.pathfinding.RS317PathFinder;

import java.util.Random;
import java.util.logging.Logger;

/**
 * FIXED: Discord bot location using proper pathfinding
 */
public class DiscordBotLocation {

	private static final Logger logger = Logger.getLogger(DiscordBotLocation.class.getSimpleName());

	private final DiscordBotStoner bot; // FIXED: Use DiscordBotStoner reference
	private final Random random = new Random(); // FIXED: Add missing Random

	// Movement validation
	private volatile Location lastAttemptedLocation = null;
	private volatile long lastMovementCheck = 0;
	private volatile int stuckCounter = 0;

	public DiscordBotLocation(DiscordBotStoner bot) { // FIXED: Correct parameter type
		this.bot = bot;
	}

	public void setInitialLocation() {
		bot.getLocation().setAs(DiscordBotDefaults.DEFAULT_LOCATION);
	}

	/**
	 * FIXED: Use proper pathfinding like regular players
	 */
	public void performMove(Location destination) {
		if (destination == null) {
			return;
		}

		// FIXED: Add region validation before movement
		if (!isValidLocation(destination)) {
			System.err.println("Cannot move to location with no loaded region: " + destination);
			return;
		}

		try {
			Location currentLocation = bot.getLocation();

			// Check if already at destination
			if (currentLocation.equals(destination)) {
				return;
			}

			// CRITICAL FIX: Use the same pathfinding system as regular players
			RS317PathFinder.findRoute(bot, destination.getX(), destination.getY(), true, 1, 1);

			// Store attempted location for validation
			lastAttemptedLocation = new Location(destination);
			lastMovementCheck = System.currentTimeMillis();

		} catch (Exception e) {
			logger.warning("Error moving bot: " + e.getMessage());
		}
	}

	/**
	 * SIMPLIFIED: Check if movement is working
	 */
	public void validateMovement() {
		long currentTime = System.currentTimeMillis();

		// Only check every 5 seconds
		if (currentTime - lastMovementCheck < 5000) {
			return;
		}

		// If bot hasn't moved in a while and should be moving
		if (isMoving() && lastAttemptedLocation != null) {
			Location currentLocation = bot.getLocation();

			// Check if we're stuck (not making progress)
			if (currentLocation.equals(lastAttemptedLocation)) {
				stuckCounter++;

				if (stuckCounter >= 2) {
					handleStuckMovement();
					stuckCounter = 0;
				}
			} else {
				// Movement is working, reset counter
				stuckCounter = 0;
			}
		}

		lastMovementCheck = currentTime;
	}

	/**
	 * SIMPLIFIED: Handle when bot gets stuck
	 */
	private void handleStuckMovement() {
		try {
			logger.info("Bot appears stuck, resetting movement");

			// Stop current movement
			bot.getMovementHandler().reset();
			stuckCounter = 0;
			lastAttemptedLocation = null;

		} catch (Exception e) {
			logger.warning("Error handling stuck movement: " + e.getMessage());
		}
	}

	/**
	 * FIXED: Safe random walk using existing region data
	 */
	public void performRandomWalk(int maxDistance, int minDistance) {
		Location currentLocation = bot.getLocation();
		if (currentLocation == null) {
			return;
		}

		// If current location is invalid, don't move
		if (!isValidLocation(currentLocation)) {
			System.err.println("Bot in invalid region, cannot perform random walk");
			return;
		}

		Location homeBase = DiscordBotDefaults.DEFAULT_LOCATION;

		for (int attempts = 0; attempts < 15; attempts++) {
			int distance = minDistance + random.nextInt(maxDistance - minDistance + 1);
			int deltaX = random.nextInt(distance * 2 + 1) - distance;
			int deltaY = random.nextInt(distance * 2 + 1) - distance;

			// Ensure minimum distance
			if (Math.abs(deltaX) < minDistance && Math.abs(deltaY) < minDistance) {
				deltaX = minDistance * (random.nextBoolean() ? -1 : 1);
				deltaY = minDistance * (random.nextBoolean() ? -1 : 1);
			}

			Location candidate = new Location(
				currentLocation.getX() + deltaX,
				currentLocation.getY() + deltaY,
				currentLocation.getZ()
			);

			// Check if destination is reasonable distance from home
			int distanceFromHome = calculateDistance(candidate, homeBase);
			if (distanceFromHome <= 35 && isValidLocation(candidate)) {
				performMove(candidate);
				return;
			}
		}

		System.out.println("Could not find valid random walk destination");
	}

	/**
	 * Default random walk
	 */
	public void performRandomWalk() {
		performRandomWalk(8, 2);
	}

	/**
	 * FIXED: Use pathfinding for area wander
	 */
	public void performAreaWander() {
		try {
			Location currentLocation = bot.getLocation();
			Location homeBase = DiscordBotDefaults.DEFAULT_LOCATION;

			// Generate random direction and distance
			double angle = Math.random() * 2 * Math.PI;
			int distance = 8 + (int)(Math.random() * 12); // 8-19 tiles

			int deltaX = (int) Math.round(Math.cos(angle) * distance);
			int deltaY = (int) Math.round(Math.sin(angle) * distance);

			Location destination = new Location(
				currentLocation.getX() + deltaX,
				currentLocation.getY() + deltaY,
				currentLocation.getZ()
			);

			// Check if destination is reasonable
			int distanceFromHome = calculateDistance(destination, homeBase);
			if (distanceFromHome <= 40 && isValidLocation(destination)) {
				performMove(destination);
			} else {
				// If too far, try a shorter distance
				performRandomWalk(6, 2);
			}

		} catch (Exception e) {
			logger.warning("Error performing area wander: " + e.getMessage());
			performRandomWalk(); // Fallback
		}
	}

	/**
	 * Check if bot is too far from home
	 */
	public boolean isTooFarFromHome() {
		Location currentLocation = bot.getLocation();
		Location homeBase = DiscordBotDefaults.DEFAULT_LOCATION;
		return calculateDistance(currentLocation, homeBase) > 40;
	}

	/**
	 * FIXED: Move towards home using pathfinding
	 */
	public void moveTowardsHome() {
		try {
			Location homeBase = DiscordBotDefaults.DEFAULT_LOCATION;
			performMove(homeBase);
		} catch (Exception e) {
			logger.warning("Error moving towards home: " + e.getMessage());
		}
	}

	/**
	 * Get distance from home base
	 */
	public int getDistanceFromHome() {
		Location currentLocation = bot.getLocation();
		Location homeBase = DiscordBotDefaults.DEFAULT_LOCATION;
		return calculateDistance(currentLocation, homeBase);
	}

	/**
	 * Check if the bot is currently moving
	 */
	public boolean isMoving() {
		return bot.getMovementHandler().moving();
	}

	/**
	 * Stop current movement
	 */
	public void stopMovement() {
		try {
			bot.getMovementHandler().reset();
			stuckCounter = 0;
			lastAttemptedLocation = null;
		} catch (Exception e) {
			logger.warning("Error stopping movement: " + e.getMessage());
		}
	}

	/**
	 * Calculate distance between two locations
	 */
	private int calculateDistance(Location loc1, Location loc2) {
		int deltaX = Math.abs(loc1.getX() - loc2.getX());
		int deltaY = Math.abs(loc1.getY() - loc2.getY());
		return Math.max(deltaX, deltaY); // Chebyshev distance
	}

	public void setNeedsPlacement() {
		bot.setNeedsPlacement(true);
		bot.getUpdateFlags().setUpdateRequired(true);
	}

	/**
	 * NEW: Move towards a specific combat area
	 */
	public void moveToCombatArea() {
		try {
			Location combatArea = new Location(3415, 2934, bot.getLocation().getZ());
			performMove(combatArea);
			System.out.println("Bot moving to combat area: " + combatArea);
		} catch (Exception e) {
			logger.warning("Error moving to combat area: " + e.getMessage());
			// Fallback to area wander
			performAreaWander();
		}
	}

	/**
	 * NEW: Check if bot is in or near the combat area
	 */
	public boolean isNearCombatArea() {
		Location currentLoc = bot.getLocation();
		Location combatArea = new Location(3415, 2934, currentLoc.getZ());

		int distance = Math.max(
			Math.abs(currentLoc.getX() - combatArea.getX()),
			Math.abs(currentLoc.getY() - combatArea.getY())
		);

		return distance <= 10; // Within 10 tiles of combat area
	}

	/**
	 * FIXED: Simple region validation using existing loaded regions
	 */
	private boolean isValidLocation(Location location) {
		if (location == null) {
			return false;
		}

		try {
			// Use existing region system - if region is null, it's not loaded/valid
			com.bestbudz.core.cache.map.Region region =
				com.bestbudz.core.cache.map.Region.getRegion(location.getX(), location.getY());

			return region != null;
		} catch (Exception e) {
			return false;
		}
	}
}