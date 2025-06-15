package com.bestbudz.core.discord.stonerbot.handling.decisions;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.core.discord.stonerbot.handling.decisions.DecisionState;
import com.bestbudz.rs2.entity.Location;
import java.util.Random;

/**
 * Handles combat-related decision making and operations
 */
public class DecisionCombatManager {

	private final DiscordBotStoner bot;
	private final DecisionState state;
	private final Random random = new Random();

	// Combat areas for variety
	private static final Location[] COMBAT_AREAS = {
		new Location(3417, 2923, 0), // Primary combat area
		new Location(3362, 2889, 0), // Secondary area
		new Location(3400, 2950, 0), // Third area
		new Location(3380, 2900, 0), // Fourth area
	};

	public DecisionCombatManager(DiscordBotStoner bot, DecisionState state) {
		this.bot = bot;
		this.state = state;
	}

	/**
	 * Check if bot should seek combat (with limitations)
	 */
	public boolean shouldSeekCombat(long currentTime) {
		// Don't seek combat if we just returned home or haven't been away long enough
		if (currentTime - state.getLastHomeReturnTime() < 120000) { // Wait 2 minutes after returning home
			return false;
		}

		// Don't seek combat if we've had too many sessions without returning
		if (state.getCombatSessionsWithoutReturn() >= 2) {
			return false;
		}

		// Normal combat seeking logic
		if (currentTime - state.getLastCombatSession() > 240000) { // 4 minutes
			if (Math.random() < 0.15) { // 15% chance
				return true;
			}
		}

		return false;
	}

	/**
	 * Check if should move towards combat area
	 */
	public boolean shouldMoveTowardsCombatArea(long currentTime) {
		// Only if auto-combat is enabled
		if (bot.getAutoCombat() == null || !bot.getAutoCombat().isEnabled()) {
			return false;
		}

		// Don't interrupt other activities
		if (bot.getBotQuarrying().isQuarrying() || bot.getBotLumbering().isLumbering()) {
			return false;
		}

		// Don't move too frequently to combat area
		if (currentTime - state.getLastCombatAreaMoveTime() < 200000) {
			return false;
		}

		// Check if we're far from any combat area
		Location currentLoc = bot.getLocation();
		int distanceToNearestCombatArea = getDistanceToNearestCombatArea(currentLoc);

		// Move to combat area if far from all of them
		if (distanceToNearestCombatArea > 12) {
			boolean hasNearbyNPCs = hasNearbyTargets();
			if (!hasNearbyNPCs) {
				System.out.println("No nearby NPCs found, distance to nearest combat area: " + distanceToNearestCombatArea);
				return true;
			}
		}

		return false;
	}

	/**
	 * Start a combat session
	 */
	public void startCombatSession() {
		if (!state.isCombatSessionActive()) {
			state.setCombatSessionActive(true);
			bot.getAutoCombat().setEnabled(true);
			System.out.println("Discord bot: Combat session STARTED - auto-combat ON");
		}
	}

	/**
	 * End combat session
	 */
	public void endCombatSession() {
		if (state.isCombatSessionActive()) {
			state.setCombatSessionActive(false);
			bot.getAutoCombat().setEnabled(false);

			// Force stop any active combat
			if (bot.getCombat().inCombat()) {
				bot.getCombat().reset();
			}

			// Reset face direction when ending combat session
			bot.setFaceDirection(-1);
			bot.getUpdateFlags().faceEntity(-1);
			bot.getUpdateFlags().faceDirection(-1);
			bot.getUpdateFlags().setUpdateRequired(true);

			System.out.println("Discord bot: Combat session ENDED - auto-combat OFF, face direction reset");
		}
	}

	/**
	 * Check if combat session should end
	 */
	public boolean shouldEndCombatSession(long currentTime) {
		if (!state.isCombatSessionActive()) {
			return false;
		}

		// End combat if:
		// 1. Bot has been out of combat for 10+ seconds
		// 2. Starting a skill activity
		// 3. Manual timeout after 5 minutes

		boolean outOfCombatTooLong = !bot.getCombat().inCombat() &&
			(currentTime - state.getLastCombatSession()) > 10000; // 10 seconds

		boolean skillActivityStarting = bot.getBotQuarrying().isQuarrying() ||
			bot.getBotLumbering().isLumbering();

		boolean combatTimeout = (currentTime - state.getLastCombatSession()) > 300000; // 5 minutes

		return outOfCombatTooLong || skillActivityStarting || combatTimeout;
	}

	/**
	 * Seek combat opportunities with area variety
	 */
	public void seekCombatOpportunities() {
		try {
			Location currentLoc = bot.getLocation();

			// Choose a random combat area instead of always going to the closest
			Location targetArea = COMBAT_AREAS[random.nextInt(COMBAT_AREAS.length)];
			// Update Z coordinate to match current location
			targetArea = new Location(targetArea.getX(), targetArea.getY(), currentLoc.getZ());

			// Calculate distance to chosen area
			int distance = Math.max(
				Math.abs(currentLoc.getX() - targetArea.getX()),
				Math.abs(currentLoc.getY() - targetArea.getY())
			);

			// Move towards the chosen combat area
			bot.getBotLocation().performMove(targetArea);
			System.out.println("Bot seeking combat at random area: " + targetArea +
				" (distance: " + distance + ")");

			// Send status update
			bot.getActions().sendAutonomousStatusUpdate("Looking for worthy opponents!");

		} catch (Exception e) {
			System.out.println("Error seeking combat: " + e.getMessage());
			// Fallback to area wander
			bot.getBotLocation().performAreaWander();
		}
	}

	/**
	 * Move towards combat area with better pathfinding
	 */
	public void moveTowardsCombatArea() {
		try {
			Location currentLoc = bot.getLocation();

			// Choose a random combat area
			Location combatArea = COMBAT_AREAS[random.nextInt(Math.min(2, COMBAT_AREAS.length))]; // Use first 2 areas
			combatArea = new Location(combatArea.getX(), combatArea.getY(), currentLoc.getZ());

			// Calculate direction towards combat area
			int deltaX = combatArea.getX() - currentLoc.getX();
			int deltaY = combatArea.getY() - currentLoc.getY();

			int distance = Math.max(Math.abs(deltaX), Math.abs(deltaY));

			// If we're very far, take bigger steps
			int maxMove = distance > 20 ? 10 : (distance > 10 ? 8 : 6);

			// Calculate movement direction
			int moveX = 0;
			int moveY = 0;

			if (deltaX != 0) {
				moveX = Math.max(-maxMove, Math.min(maxMove, deltaX));
			}
			if (deltaY != 0) {
				moveY = Math.max(-maxMove, Math.min(maxMove, deltaY));
			}

			// If we're very close, add some randomness to avoid exact positioning
			if (Math.abs(deltaX) <= 5 && Math.abs(deltaY) <= 5) {
				moveX += (int)(Math.random() * 6) - 3; // -3 to +3 random offset
				moveY += (int)(Math.random() * 6) - 3;
			}

			Location targetLoc = new Location(
				currentLoc.getX() + moveX,
				currentLoc.getY() + moveY,
				currentLoc.getZ()
			);

			// Use the bot's movement system to go there
			bot.getBotLocation().performMove(targetLoc);

			System.out.println("Bot moving towards combat area: from " + currentLoc +
				" towards " + targetLoc + " (target: " + combatArea + ")");

		} catch (Exception e) {
			System.out.println("Error moving towards combat area: " + e.getMessage());
			// Fallback to random movement
			bot.getBotLocation().performRandomWalk(6, 2);
		}
	}

	/**
	 * Check for nearby combat targets
	 */
	public boolean hasNearbyTargets() {
		try {
			java.util.List<com.bestbudz.rs2.entity.mob.Mob> clientNpcs = bot.getClient().getNpcs();

			if (clientNpcs == null || clientNpcs.isEmpty()) {
				System.out.println("Client NPCs is empty - client population failed");
				return false;
			}

			int targetsFound = 0;
			Location botLocation = bot.getLocation();

			for (com.bestbudz.rs2.entity.mob.Mob npc : clientNpcs) {
				if (npc == null || npc.isDead() || !npc.isActive()) continue;
				if (npc.getLocation().getZ() != botLocation.getZ()) continue;
				if (npc.getCombatDefinition() == null) continue;
				if (npc.isWalkToHome()) continue;

				int distance = Math.abs(botLocation.getX() - npc.getLocation().getX()) +
					Math.abs(botLocation.getY() - npc.getLocation().getY());

				// Check within auto-combat range (DEFAULT_RADIUS = 10)
				if (distance <= 10) {
					targetsFound++;
					System.out.println("Found combat target: NPC " + npc.getId() +
						" at " + npc.getLocation() + " (distance: " + distance + ")");
				}
			}

			System.out.println("hasNearbyTargets: checked " + clientNpcs.size() +
				" client NPCs, found " + targetsFound + " combat targets");
			return targetsFound > 0;

		} catch (Exception e) {
			System.out.println("Error checking nearby targets: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Get distance to nearest combat area
	 */
	private int getDistanceToNearestCombatArea(Location currentLoc) {
		int minDistance = Integer.MAX_VALUE;

		for (Location area : COMBAT_AREAS) {
			int distance = Math.max(
				Math.abs(currentLoc.getX() - area.getX()),
				Math.abs(currentLoc.getY() - area.getY())
			);
			minDistance = Math.min(minDistance, distance);
		}

		return minDistance;
	}

	/**
	 * Debug NPC visibility
	 */
	public void debugNpcVisibility() {
		try {
			System.out.println("=== Discord Bot NPC Debug ===");

			// Check client NPCs
			com.bestbudz.rs2.entity.mob.Mob[] clientNpcs = bot.getClient().getNpcs().toArray(new com.bestbudz.rs2.entity.mob.Mob[0]);
			System.out.println("Client NPCs array length: " + (clientNpcs != null ? clientNpcs.length : "NULL"));

			int clientNpcCount = 0;
			if (clientNpcs != null) {
				for (com.bestbudz.rs2.entity.mob.Mob npc : clientNpcs) {
					if (npc != null && npc.isActive()) {
						clientNpcCount++;
					}
				}
			}
			System.out.println("Active NPCs in client: " + clientNpcCount);

			// Check world NPCs
			com.bestbudz.rs2.entity.mob.Mob[] worldNpcs = com.bestbudz.rs2.entity.World.getNpcs();
			System.out.println("World NPCs array length: " + (worldNpcs != null ? worldNpcs.length : "NULL"));

			int worldNpcCount = 0;
			int nearbyWorldNpcs = 0;
			Location botLoc = bot.getLocation();

			if (worldNpcs != null) {
				for (com.bestbudz.rs2.entity.mob.Mob npc : worldNpcs) {
					if (npc != null && npc.isActive()) {
						worldNpcCount++;

						// Check if nearby
						int distance = Math.max(
							Math.abs(botLoc.getX() - npc.getLocation().getX()),
							Math.abs(botLoc.getY() - npc.getLocation().getY())
						);

						if (distance <= 15) {
							nearbyWorldNpcs++;
							System.out.println("Nearby NPC: " + npc.getId() +
								" at " + npc.getLocation() +
								" (distance: " + distance + ")" +
								" (combat def: " + (npc.getCombatDefinition() != null) + ")");
						}
					}
				}
			}

			System.out.println("Active NPCs in world: " + worldNpcCount);
			System.out.println("Nearby NPCs in world (within 15 tiles): " + nearbyWorldNpcs);
			System.out.println("Bot location: " + botLoc);
			System.out.println("Distance from home: " + bot.getBotLocation().getDistanceFromHome());
			System.out.println("Combat sessions without return: " + state.getCombatSessionsWithoutReturn());
			System.out.println("Time since last home return: " + (System.currentTimeMillis() - state.getLastHomeReturnTime()) / 1000 + "s");
			System.out.println("=============================");

		} catch (Exception e) {
			System.out.println("Error in NPC debug: " + e.getMessage());
			e.printStackTrace();
		}
	}
}