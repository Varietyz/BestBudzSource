package com.bestbudz.rs2.entity.movement;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.Walking;

public class MobMovementHandler extends MovementHandler {
	private final Mob mob;

	public MobMovementHandler(Mob mob) {
		super(mob);
		this.mob = mob;
	}

	@Override
	public boolean canMoveTo(int direction) {
		return Walking.canMoveTo(mob, direction);
	}

	@Override
	public boolean canMoveTo(int x, int y, int size, int direction) {
		return Walking.canMoveTo(mob, x, y, direction, size);
	}

	@Override
	public boolean moving() {
		return mob.getFollowing().isFollowing();
	}

	/**
	 * SIMPLIFIED: Basic movement processing without complex collision handling
	 */
	@Override
	public void process() {
		if (mob.isDead() || mob.isFrozen() || mob.isStunned()) {
			return;
		}

		Point walkPoint = waypoints.poll();
		if (walkPoint != null && walkPoint.getDirection() != -1) {
			int direction = walkPoint.getDirection();

			// Check if movement is possible
			Region region = Region.getRegion(mob.getLocation());
			if (region.canMove(mob.getLocation(), direction)) {
				// Execute movement
				mob.getLocation().move(
					GameConstants.DIR[direction][0],
					GameConstants.DIR[direction][1]);
				primaryDirection = direction;
				flag = true;
				mob.getMovementHandler().getLastLocation().setAs(mob.getLocation());
			} else {
				// If blocked, just stop - no complex pathfinding
				reset();
			}
		}
	}
}