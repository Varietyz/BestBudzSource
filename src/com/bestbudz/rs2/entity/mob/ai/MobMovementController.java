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

	public void process() {
		if (mob.isDead()) {
			return;
		}

		if (forceWalking) {

			return;
		}

		if (following.isFollowing()) {
			following.process();
			return;
		}

		if (!mob.isAssaultable() && walks) {
			if (Utility.randomNumber(15) == 0) {
				Walking.randomWalk(mob);
			}
		}
	}

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

	public boolean isWalkToHome() {

		if (GodWarsData.forId(mob.getId()) != null && GodWarsData.bossNpc(GodWarsData.forId(mob.getId()))) {
			return false;
		}

		if (following.isIgnoreDistance() || mob.getOwner() != null) {
			return false;
		}

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

	public void teleport(Location p) {
		Walking.setNpcOnTile(mob, false);
		movementHandler.getLastLocation().setAs(new Location(p.getX(), p.getY() + 1));
		mob.getLocation().setAs(p);
		Walking.setNpcOnTile(mob, true);
		mob.getStateManager().setPlacement(true);
		movementHandler.resetMoveDirections();
	}

	public void retreat() {
		if (mob.getCombat().getAssaulting() != null) {
			forceWalking = true;
			mob.getCombat().reset();

			int newX = mob.getX() + (Utility.randomNumber(2) == 0 ? 5 : -5);
			int newY = mob.getY() + (Utility.randomNumber(2) == 0 ? 5 : -5);
			TaskQueue.queue(new MobWalkTask(mob, new Location(newX, newY), false));
		}
	}

	public boolean withinMobWalkDistance(Entity e) {
		if (following.isIgnoreDistance() || mob.getOwner() != null) {
			return true;
		}

		int distance = Math.abs(e.getLocation().getX() - spawnLocation.getX()) +
			Math.abs(e.getLocation().getY() - spawnLocation.getY());

		return distance < mob.getSize() * 2 + 6;
	}

	public void processMovement() {

	}

	public MovementHandler getMovementHandler() { return movementHandler; }
	public MobFollowing getFollowing() { return following; }
	public Location getSpawnLocation() { return spawnLocation; }
	public Location getNextSpawnLocation() { return spawnLocation; }
	public boolean isForceWalking() { return forceWalking; }
	public void setForceWalking(boolean forceWalking) { this.forceWalking = forceWalking; }
	public boolean isWalks() { return walks; }
}
