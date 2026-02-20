package com.bestbudz.core.discord.stonerbot.handling.decisions;

import com.bestbudz.core.discord.stonerbot.DiscordBotStoner;
import com.bestbudz.core.discord.stonerbot.handling.decisions.DecisionState;
import com.bestbudz.rs2.entity.Location;
import java.util.Random;

public class DecisionCombatManager {

	private final DiscordBotStoner bot;
	private final DecisionState state;
	private final Random random = new Random();

	private static final Location[] COMBAT_AREAS = {
		new Location(3417, 2923, 0),
		new Location(3362, 2889, 0),
		new Location(3400, 2950, 0),
		new Location(3380, 2900, 0),
	};

	public DecisionCombatManager(DiscordBotStoner bot, DecisionState state) {
		this.bot = bot;
		this.state = state;
	}

	public boolean shouldSeekCombat(long currentTime) {

		if (currentTime - state.getLastHomeReturnTime() < 120000) {
			return false;
		}

		if (state.getCombatSessionsWithoutReturn() >= 2) {
			return false;
		}

		if (currentTime - state.getLastCombatSession() > 240000) {
			if (Math.random() < 0.15) {
				return true;
			}
		}

		return false;
	}

	public boolean shouldMoveTowardsCombatArea(long currentTime) {

		if (bot.getAutoCombat() == null || !bot.getAutoCombat().isEnabled()) {
			return false;
		}

		if (bot.getBotQuarrying().isQuarrying() || bot.getBotLumbering().isLumbering()) {
			return false;
		}

		if (currentTime - state.getLastCombatAreaMoveTime() < 200000) {
			return false;
		}

		Location currentLoc = bot.getLocation();
		int distanceToNearestCombatArea = getDistanceToNearestCombatArea(currentLoc);

		if (distanceToNearestCombatArea > 12) {
			boolean hasNearbyNPCs = hasNearbyTargets();
			if (!hasNearbyNPCs) {
				System.out.println("No nearby NPCs found, distance to nearest combat area: " + distanceToNearestCombatArea);
				return true;
			}
		}

		return false;
	}

	public void startCombatSession() {
		if (!state.isCombatSessionActive()) {
			state.setCombatSessionActive(true);
			bot.getAutoCombat().setEnabled(true);
			System.out.println("Discord bot: Combat session STARTED - auto-combat ON");
		}
	}

	public void endCombatSession() {
		if (state.isCombatSessionActive()) {
			state.setCombatSessionActive(false);
			bot.getAutoCombat().setEnabled(false);

			if (bot.getCombat().inCombat()) {
				bot.getCombat().reset();
			}

			bot.setFaceDirection(-1);
			bot.getUpdateFlags().faceEntity(-1);
			bot.getUpdateFlags().faceDirection(-1);
			bot.getUpdateFlags().setUpdateRequired(true);

			System.out.println("Discord bot: Combat session ENDED - auto-combat OFF, face direction reset");
		}
	}

	public boolean shouldEndCombatSession(long currentTime) {
		if (!state.isCombatSessionActive()) {
			return false;
		}

		boolean outOfCombatTooLong = !bot.getCombat().inCombat() &&
			(currentTime - state.getLastCombatSession()) > 10000;

		boolean skillActivityStarting = bot.getBotQuarrying().isQuarrying() ||
			bot.getBotLumbering().isLumbering();

		boolean combatTimeout = (currentTime - state.getLastCombatSession()) > 300000;

		return outOfCombatTooLong || skillActivityStarting || combatTimeout;
	}

	public void seekCombatOpportunities() {
		try {
			Location currentLoc = bot.getLocation();

			Location targetArea = COMBAT_AREAS[random.nextInt(COMBAT_AREAS.length)];

			targetArea = new Location(targetArea.getX(), targetArea.getY(), currentLoc.getZ());

			int distance = Math.max(
				Math.abs(currentLoc.getX() - targetArea.getX()),
				Math.abs(currentLoc.getY() - targetArea.getY())
			);

			bot.getBotLocation().performMove(targetArea);
			System.out.println("Bot seeking combat at random area: " + targetArea +
				" (distance: " + distance + ")");

			bot.getActions().sendAutonomousStatusUpdate("Looking for worthy opponents!");

		} catch (Exception e) {
			System.out.println("Error seeking combat: " + e.getMessage());

			bot.getBotLocation().performAreaWander();
		}
	}

	public void moveTowardsCombatArea() {
		try {
			Location currentLoc = bot.getLocation();

			Location combatArea = COMBAT_AREAS[random.nextInt(Math.min(2, COMBAT_AREAS.length))];
			combatArea = new Location(combatArea.getX(), combatArea.getY(), currentLoc.getZ());

			int deltaX = combatArea.getX() - currentLoc.getX();
			int deltaY = combatArea.getY() - currentLoc.getY();

			int distance = Math.max(Math.abs(deltaX), Math.abs(deltaY));

			int maxMove = distance > 20 ? 10 : (distance > 10 ? 8 : 6);

			int moveX = 0;
			int moveY = 0;

			if (deltaX != 0) {
				moveX = Math.max(-maxMove, Math.min(maxMove, deltaX));
			}
			if (deltaY != 0) {
				moveY = Math.max(-maxMove, Math.min(maxMove, deltaY));
			}

			if (Math.abs(deltaX) <= 5 && Math.abs(deltaY) <= 5) {
				moveX += (int)(Math.random() * 6) - 3;
				moveY += (int)(Math.random() * 6) - 3;
			}

			Location targetLoc = new Location(
				currentLoc.getX() + moveX,
				currentLoc.getY() + moveY,
				currentLoc.getZ()
			);

			bot.getBotLocation().performMove(targetLoc);

			System.out.println("Bot moving towards combat area: from " + currentLoc +
				" towards " + targetLoc + " (target: " + combatArea + ")");

		} catch (Exception e) {
			System.out.println("Error moving towards combat area: " + e.getMessage());

			bot.getBotLocation().performRandomWalk(6, 2);
		}
	}

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

	public void debugNpcVisibility() {
		try {
			System.out.println("=== Discord Bot NPC Debug ===");

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

			com.bestbudz.rs2.entity.mob.Mob[] worldNpcs = com.bestbudz.rs2.entity.World.getNpcs();
			System.out.println("World NPCs array length: " + (worldNpcs != null ? worldNpcs.length : "NULL"));

			int worldNpcCount = 0;
			int nearbyWorldNpcs = 0;
			Location botLoc = bot.getLocation();

			if (worldNpcs != null) {
				for (com.bestbudz.rs2.entity.mob.Mob npc : worldNpcs) {
					if (npc != null && npc.isActive()) {
						worldNpcCount++;

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
