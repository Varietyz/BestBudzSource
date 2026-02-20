package com.bestbudz.rs2.auto.combat.handlers;

import com.bestbudz.core.discord.stonerbot.client.DiscordBotIsolatedClient;
import com.bestbudz.rs2.auto.combat.config.AutoCombatConfig;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.pathfinding.StraightPathFinder;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.List;

public class TargetFinder {

	private final Stoner stoner;

	public TargetFinder(Stoner stoner) {
		this.stoner = stoner;
	}

	public Mob findNearestTarget() {
		if (stoner.isPetStoner()) {
			return findNearestTargetForPet();
		}

		if (stoner.getCombat().inCombat()) {
			Entity currentTarget = stoner.getCombat().getAssaulting();

			if (currentTarget == null) {

				Mob attacker = findAttacker();
        if (attacker != null && isTargetReachable(attacker)) {

					engageTargetWithPathfinding(attacker);
					return attacker;
				}
			}

			else if (currentTarget.isDead() || !currentTarget.isActive() ||
				(currentTarget.isNpc() && (!isTargetReachable((Mob)currentTarget) ||
					getDistanceToTarget((Mob)currentTarget) > AutoCombatConfig.DEFAULT_RADIUS))) {

				Mob attacker = findAttacker();
				if (attacker != null && isTargetReachable(attacker)) {

					engageTargetWithPathfinding(attacker);
					return attacker;
				}
			}
		}

		Mob closest = null;
		int closestDistance = Integer.MAX_VALUE;

		List<Mob> clientNpcs;

		if (stoner.getClient() instanceof DiscordBotIsolatedClient) {
			((DiscordBotIsolatedClient) stoner.getClient()).forceRefreshNpcs();
			clientNpcs = stoner.getClient().getNpcs();
		} else {
			clientNpcs = stoner.getClient().getNpcs();
		}

		if (clientNpcs == null || clientNpcs.isEmpty()) {

			return null;
		}

		int totalNpcs = 0;
		int validNpcs = 0;
		int reachableNpcs = 0;

		for (Mob npc : clientNpcs) {
			if (npc == null) continue;
			totalNpcs++;

			int distance = getDistanceToTarget(npc);

			if (!isValidTarget(npc)) {
				continue;
			}
			validNpcs++;

			if (distance <= AutoCombatConfig.DEFAULT_RADIUS && isTargetReachable(npc)) {
				reachableNpcs++;
				if (distance < closestDistance) {
					closestDistance = distance;
					closest = npc;
				}
			}
		}

		return closest;
	}

	private Mob findNearestTargetForPet() {
		try {

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

	private boolean isTargetReachable(Mob target) {
		if (target == null || target.getLocation() == null) {
			return false;
		}

		try {
			Location playerLoc = stoner.getLocation();
			Location targetLoc = target.getLocation();

			int distance = getDistanceToTarget(target);
			if (distance > AutoCombatConfig.DEFAULT_RADIUS) {
				return false;
			}

			boolean pathClear = false;

			Location[] playerEdges = com.bestbudz.rs2.GameConstants.getEdges(
				playerLoc.getX(), playerLoc.getY(), stoner.getSize());

			for (Location edge : playerEdges) {

				if (StraightPathFinder.isInteractionPathClear(edge, targetLoc)) {
					pathClear = true;
					break;
				}

				if (StraightPathFinder.isInteractionPathClear(targetLoc, edge)) {
					pathClear = true;
					break;
				}
			}

			if (!pathClear) {

				return false;
			}

			return true;

		} catch (Exception e) {
			System.out.println("Error checking target reachability: " + e.getMessage());
			return false;
		}
	}

	private void engageTargetWithPathfinding(Mob target) {
		if (target == null) return;

		try {

			int distance = getDistanceToTarget(target);

			if (distance > 1) {

				com.bestbudz.rs2.entity.pathfinding.RS317PathFinder.findRoute(
					stoner, target.getLocation().getX(), target.getLocation().getY(), true, 1, 1);

			}

			stoner.getCombat().setAssault(target);

		} catch (Exception e) {
			System.out.println("Error engaging target with pathfinding: " + e.getMessage());

			stoner.getCombat().setAssault(target);
		}
	}

	private Mob findAttacker() {
		List<Mob> clientNpcs;

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

			if (npc.getCombat().inCombat() && npc.getCombat().getAssaulting() == stoner) {
				return npc;
			}
		}

		return null;
	}

	public int getDistanceToTarget(Mob target) {
		int deltaX = Math.abs(stoner.getLocation().getX() - target.getLocation().getX());
		int deltaY = Math.abs(stoner.getLocation().getY() - target.getLocation().getY());

		int chebyshevDistance = Math.max(deltaX, deltaY);

		return chebyshevDistance;
	}

	public boolean isValidTarget(Mob npc) {
		if (npc == null || npc.isDead() || !npc.isActive()) return false;
		if (npc.getLocation().getZ() != stoner.getLocation().getZ()) return false;
		if (npc.getCombatDefinition() == null) return false;
		if (stoner.isPet() || stoner.isPetStoner() || npc.isWalkToHome()) return false;
		if (!stoner.inMultiArea()) return false;
		return true;
	}
}
