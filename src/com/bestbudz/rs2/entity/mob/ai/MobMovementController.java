package com.bestbudz.rs2.entity.mob.ai;

import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.RandomMobChatting;
import com.bestbudz.rs2.entity.mob.Walking;
import com.bestbudz.rs2.entity.movement.MovementHandler;
import com.bestbudz.rs2.entity.movement.MobMovementHandler;
import com.bestbudz.rs2.entity.following.MobFollowing;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.MobWalkTask;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.minigames.godwars.GodWarsData;

/**
 * Simplified 317-style movement controller
 */
public class MobMovementController {
	private final Mob mob;
	private final MovementHandler movementHandler;
	private final MobFollowing following;
	private final Location spawnLocation;
	private boolean forceWalking = false;
	private final boolean walks;

	public MobMovementController(Mob mob, Location spawnLocation, boolean walks) {
		this.mob = mob;
		this.spawnLocation = new Location(spawnLocation);
		this.walks = walks;
		this.movementHandler = new MobMovementHandler(mob);
		this.following = new MobFollowing(mob);
		this.movementHandler.resetMoveDirections();
	}

	/**
	 * Simple movement processing - just follow or random walk
	 */
	public void process() {
		if (mob.isDead()) {
			return;
		}

		if (forceWalking) {
			// Let force walking complete
			return;
		}

		// Priority 1: Following
		if (following.isFollowing()) {
			following.process();
			return;
		}

		// Priority 2: Random walking for non-assaultable mobs
		if (!mob.isAssaultable() && walks) {
			if (Utility.randomNumber(15) == 0) { // Simple random walk
				Walking.randomWalk(mob);
			}
		}
	}

	/**
	 * Simple random walk handler
	 */
	public void handleRandomWalk() {
		if (!mob.isDead() &&
			mob.getCombat().getAssaulting() == null &&
			!following.isFollowing() &&
			walks &&
			!forceWalking) {

			RandomMobChatting.handleRandomMobChatting(mob);
			if (Utility.randomNumber(15) == 0) {
				Walking.randomWalk(mob);
			}
		}
	}

	/**
	 * God Wars movement (unchanged)
	 */
	public void handleGodWarsMovement() {
		GodWarsData.GodWarsNpc npc = GodWarsData.forId(mob.getId());
		if (npc != null && !mob.getCombat().inCombat()) {
			for (Mob i : com.bestbudz.rs2.entity.World.getNpcs()) {
				if (i == null) continue;

				GodWarsData.GodWarsNpc other = GodWarsData.forId(i.getId());
				if (other == null) continue;

				if (i.getCombat().getAssaulting() == null && i.getCombatDefinition() != null) {
					if (npc.getAllegiance() != other.getAllegiance() && !i.getCombat().inCombat()) {
						int distance = Math.abs(mob.getX() - i.getX()) + Math.abs(mob.getY() - i.getY());
						int combatDistance = 4 + com.bestbudz.rs2.content.combat.CombatConstants
							.getDistanceForCombatType(i.getCombat().getCombatType());

						if (distance <= combatDistance) {
							i.getCombat().setAssault(mob);
						}
					}
				}
			}
		}
	}

	/**
	 * Simple distance check for walking home
	 */
	public boolean isWalkToHome() {
		// God Wars bosses don't walk home
		if (GodWarsData.forId(mob.getId()) != null && GodWarsData.bossNpc(GodWarsData.forId(mob.getId()))) {
			return false;
		}

		// Owned mobs with ignore distance don't walk home
		if (following.isIgnoreDistance() || mob.getOwner() != null) {
			return false;
		}

		// Simple distance calculation
		int distance = Math.abs(mob.getLocation().getX() - spawnLocation.getX()) +
			Math.abs(mob.getLocation().getY() - spawnLocation.getY());

		if (mob.inWilderness()) {
			return distance > mob.getSize() + 2;
		}

		if (mob.isAssaultable()) {
			return distance > mob.getSize() * 2 + 6;
		}

		return distance > 2;
	}

	/**
	 * Simple teleport
	 */
	public void teleport(Location p) {
		Walking.setNpcOnTile(mob, false);
		movementHandler.getLastLocation().setAs(new Location(p.getX(), p.getY() + 1));
		mob.getLocation().setAs(p);
		Walking.setNpcOnTile(mob, true);
		mob.getStateManager().setPlacement(true);
		movementHandler.resetMoveDirections();
	}

	/**
	 * Simple retreat - just move away
	 */
	public void retreat() {
		if (mob.getCombat().getAssaulting() != null) {
			forceWalking = true;
			mob.getCombat().reset();
			// Simple retreat - move 5 tiles away
			int newX = mob.getX() + (Utility.randomNumber(2) == 0 ? 5 : -5);
			int newY = mob.getY() + (Utility.randomNumber(2) == 0 ? 5 : -5);
			TaskQueue.queue(new MobWalkTask(mob, new Location(newX, newY), false));
		}
	}

	/**
	 * Simple walk distance check
	 */
	public boolean withinMobWalkDistance(Entity e) {
		if (following.isIgnoreDistance() || mob.getOwner() != null) {
			return true;
		}

		int distance = Math.abs(e.getLocation().getX() - spawnLocation.getX()) +
			Math.abs(e.getLocation().getY() - spawnLocation.getY());

		return distance < mob.getSize() * 2 + 6;
	}

	public void processMovement() {
		// Override in subclasses for custom movement behavior
	}

	// Getters and setters
	public MovementHandler getMovementHandler() { return movementHandler; }
	public MobFollowing getFollowing() { return following; }
	public Location getSpawnLocation() { return spawnLocation; }
	public Location getNextSpawnLocation() { return spawnLocation; }
	public boolean isForceWalking() { return forceWalking; }
	public void setForceWalking(boolean forceWalking) { this.forceWalking = forceWalking; }
	public boolean isWalks() { return walks; }
}