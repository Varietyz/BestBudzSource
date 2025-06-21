package com.bestbudz.rs2.auto.combat.handlers;

import com.bestbudz.core.discord.stonerbot.client.DiscordBotIsolatedClient;
import com.bestbudz.rs2.auto.combat.config.AutoCombatConfig;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.pathfinding.StraightPathFinder;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.List;

/**
 * FIXED: Handles target finding with proper pathfinding validation
 * Now checks for obstructions and uses pathfinding like regular players
 */
public class TargetFinder {

	private final Stoner stoner;

	public TargetFinder(Stoner stoner) {
		this.stoner = stoner;
	}

	public Mob findNearestTarget() {
		if (stoner.isPetStoner()) {
			return findNearestTargetForPet();
		}
		// Check if we're being attacked but not fighting back effectively
		if (stoner.getCombat().inCombat()) {
			Entity currentTarget = stoner.getCombat().getAssaulting();

			// Case 1: No target at all while in combat
			if (currentTarget == null) {
				//System.out.println("DEBUG: Player is being attacked but has no target - finding attacker");
				Mob attacker = findAttacker();
        if (attacker != null && isTargetReachable(attacker)) {
					//System.out.println("DEBUG: Found reachable attacker NPC " + attacker.getId() + " at " + attacker.getLocation());
					// FIXED: Use proper combat engagement with pathfinding
					engageTargetWithPathfinding(attacker);
					return attacker;
				}
			}
			// Case 2: Has target but it's unreachable/invalid
			else if (currentTarget.isDead() || !currentTarget.isActive() ||
				(currentTarget.isNpc() && (!isTargetReachable((Mob)currentTarget) ||
					getDistanceToTarget((Mob)currentTarget) > AutoCombatConfig.DEFAULT_RADIUS))) {
				//System.out.println("DEBUG: Current target is unreachable/invalid - finding new attacker");
				Mob attacker = findAttacker();
				if (attacker != null && isTargetReachable(attacker)) {
					//System.out.println("DEBUG: Found new reachable attacker NPC " + attacker.getId() + " at " + attacker.getLocation());
					// FIXED: Use proper combat engagement with pathfinding
					engageTargetWithPathfinding(attacker);
					return attacker;
				}
			}
		}

		Mob closest = null;
		int closestDistance = Integer.MAX_VALUE;

		List<Mob> clientNpcs;

		// Handle different client types
		if (stoner.getClient() instanceof DiscordBotIsolatedClient) {
			((DiscordBotIsolatedClient) stoner.getClient()).forceRefreshNpcs();
			clientNpcs = stoner.getClient().getNpcs();
		} else {
			clientNpcs = stoner.getClient().getNpcs();
		}

		if (clientNpcs == null || clientNpcs.isEmpty()) {
			//System.out.println("DEBUG: No NPCs found in client list");
			return null;
		}

		int totalNpcs = 0;
		int validNpcs = 0;
		int reachableNpcs = 0; // NEW: Track reachable NPCs

		for (Mob npc : clientNpcs) {
			if (npc == null) continue;
			totalNpcs++;

			int distance = getDistanceToTarget(npc);

			if (!isValidTarget(npc)) {
				continue;
			}
			validNpcs++;

			// CRITICAL FIX: Check if target is reachable via pathfinding
			if (distance <= AutoCombatConfig.DEFAULT_RADIUS && isTargetReachable(npc)) {
				reachableNpcs++;
				if (distance < closestDistance) {
					closestDistance = distance;
					closest = npc;
				}
			}
		}

		/*if (closest != null) {
			System.out.println("DEBUG: Found reachable target NPC " + closest.getId() +
				" at distance " + closestDistance + " (Total: " + totalNpcs +
				", Valid: " + validNpcs + ", Reachable: " + reachableNpcs + ")");
		}*/

		return closest;
	}

	private Mob findNearestTargetForPet() {
		try {
			// FIXED: Get owner's NPC list instead of pet's empty list
			Stoner owner = (Stoner) stoner.getAttributes().get("PET_OWNER");
			if (owner == null) {
				System.out.println("Pet has no owner reference");
				return null;
			}

			java.util.List<com.bestbudz.rs2.entity.mob.Mob> clientNpcs = owner.getClient().getNpcs();

			if (clientNpcs == null || clientNpcs.isEmpty()) {
				System.out.println("Owner client NPCs is empty - client population failed");
				return null;
			}

			Mob closestTarget = null;
			int closestDistance = Integer.MAX_VALUE;
			Location petLocation = stoner.getLocation();

			for (com.bestbudz.rs2.entity.mob.Mob npc : clientNpcs) {
				if (npc == null || npc.isDead() || !npc.isActive()) continue;
				if (npc.getLocation().getZ() != petLocation.getZ()) continue;
				if (npc.getCombatDefinition() == null) continue;
				if (npc.isWalkToHome()) continue;

				int distance = Math.abs(petLocation.getX() - npc.getLocation().getX()) +
					Math.abs(petLocation.getY() - npc.getLocation().getY());

				// Check within auto-combat range (DEFAULT_RADIUS = 10)
				if (distance <= 10 && distance < closestDistance) {
					closestDistance = distance;
					closestTarget = npc;
					System.out.println("Pet found combat target: NPC " + npc.getId() +
						" at " + npc.getLocation() + " (distance: " + distance + ")");
				}
			}

			if (closestTarget != null) {
				System.out.println("Pet selected closest target: NPC " + closestTarget.getId() +
					" at distance " + closestDistance);
			} else {
				System.out.println("Pet found no valid targets in range");
			}

			return closestTarget;

		} catch (Exception e) {
			System.out.println("Error finding pet targets: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * CRITICAL FIX: Check if target is reachable using proper pathfinding
	 * This uses the same logic as regular player combat
	 */
	private boolean isTargetReachable(Mob target) {
		if (target == null || target.getLocation() == null) {
			return false;
		}

		try {
			Location playerLoc = stoner.getLocation();
			Location targetLoc = target.getLocation();

			// Quick distance check first
			int distance = getDistanceToTarget(target);
			if (distance > AutoCombatConfig.DEFAULT_RADIUS) {
				return false;
			}

			// CRITICAL FIX: Use the same pathfinding logic as Combat.withinDistanceForAssault()
			// This is the exact same logic used for regular players!

			// For melee combat, check interaction path
			boolean pathClear = false;

			// Get edges of player location (handles multi-tile entities)
			Location[] playerEdges = com.bestbudz.rs2.GameConstants.getEdges(
				playerLoc.getX(), playerLoc.getY(), stoner.getSize());

			// Check if any edge of the player can reach the target
			for (Location edge : playerEdges) {
				// Use interaction path for melee (like regular players do)
				if (StraightPathFinder.isInteractionPathClear(edge, targetLoc)) {
					pathClear = true;
					break;
				}
				// Also check reverse path (target to player)
				if (StraightPathFinder.isInteractionPathClear(targetLoc, edge)) {
					pathClear = true;
					break;
				}
			}

			if (!pathClear) {
//				System.out.println("DEBUG: Target NPC " + target.getId() + " at " + targetLoc +
//					" is blocked by obstacles (distance: " + distance + ")");
				return false;
			}

			return true;

		} catch (Exception e) {
			System.out.println("Error checking target reachability: " + e.getMessage());
			return false;
		}
	}

	/**
	 * FIXED: Properly engage target using pathfinding like regular players
	 */
	private void engageTargetWithPathfinding(Mob target) {
		if (target == null) return;

		try {
			// CRITICAL FIX: Use the same engagement method as regular players
			// This will trigger proper pathfinding if the target is not in direct range

			// First, check if we need to move closer
			int distance = getDistanceToTarget(target);

			if (distance > 1) { // Not adjacent
				// FIXED: Use pathfinding to move towards target (like regular players)
				com.bestbudz.rs2.entity.pathfinding.RS317PathFinder.findRoute(
					stoner, target.getLocation().getX(), target.getLocation().getY(), true, 1, 1);

				//System.out.println("DEBUG: Discord bot pathfinding to NPC " + target.getId() +
				//	" at " + target.getLocation() + " (distance: " + distance + ")");
			}

			// Set the combat target (this will be processed by Combat.assault())
			stoner.getCombat().setAssault(target);

		} catch (Exception e) {
			System.out.println("Error engaging target with pathfinding: " + e.getMessage());
			// Fallback: just set assault without pathfinding
			stoner.getCombat().setAssault(target);
		}
	}

	/**
	 * Find the NPC that is attacking the player
	 */
	private Mob findAttacker() {
		List<Mob> clientNpcs;

		// Handle different client types
		if (stoner.getClient() instanceof DiscordBotIsolatedClient) {
			((DiscordBotIsolatedClient) stoner.getClient()).forceRefreshNpcs();
			clientNpcs = stoner.getClient().getNpcs();
		} else {
			clientNpcs = stoner.getClient().getNpcs();
		}

		if (clientNpcs == null || clientNpcs.isEmpty()) {
			return null;
		}

		for (Mob npc : clientNpcs) {
			if (npc == null || npc.isDead() || !npc.isActive()) continue;

			// Check if this NPC is targeting the player
			if (npc.getCombat().inCombat() && npc.getCombat().getAssaulting() == stoner) {
				return npc;
			}
		}

		return null;
	}

	// Improved distance calculation method
	public int getDistanceToTarget(Mob target) {
		int deltaX = Math.abs(stoner.getLocation().getX() - target.getLocation().getX());
		int deltaY = Math.abs(stoner.getLocation().getY() - target.getLocation().getY());

		// Use Chebyshev distance (proper tile distance for RSPS)
		int chebyshevDistance = Math.max(deltaX, deltaY);

		return chebyshevDistance;
	}

	/**
	 * Check if a mob is a valid target
	 */
	public boolean isValidTarget(Mob npc) {
		if (npc == null || npc.isDead() || !npc.isActive()) return false;
		if (npc.getLocation().getZ() != stoner.getLocation().getZ()) return false;
		if (npc.getCombatDefinition() == null) return false;
		if (stoner.isPet() || stoner.isPetStoner() || npc.isWalkToHome()) return false;
		if (!stoner.inMultiArea()) return false;
		return true;
	}
}