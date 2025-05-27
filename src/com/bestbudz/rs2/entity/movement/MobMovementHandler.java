package com.bestbudz.rs2.entity.movement;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.rs2.GameConstants;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.Walking;
import com.bestbudz.rs2.entity.stoner.Stoner;

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

  @Override
  public void process() {
    if ((mob.isDead())
        || (mob.isFrozen())
        || (mob.isStunned())
        || (mob.getCombat().getAssaulting() != null)) {
      return;
    }

    Point walkPoint = waypoints.poll();

    if ((walkPoint != null) && (walkPoint.getDirection() != -1)) {
      int nextX = mob.getLocation().getX() + GameConstants.DIR[walkPoint.getDirection()][0];
      int nextY = mob.getLocation().getY() + GameConstants.DIR[walkPoint.getDirection()][1];
      int z = mob.getLocation().getZ();

		Region region = Region.getRegion(mob.getLocation());

		boolean hardBlocked = !region.canMove(mob.getLocation(), walkPoint.getDirection());
		boolean softBlocked = region.isNpcOnTile(nextX, nextY, z);

		if (hardBlocked && !softBlocked) {
			Stoner.recordCollision(new Location(nextX, nextY, z));
		}

		if (hardBlocked) {
			reset();
			return;
		}


		mob.getMovementHandler().getLastLocation().setAs(mob.getLocation());
      mob.getLocation()
          .move(
              GameConstants.DIR[walkPoint.getDirection()][0],
              GameConstants.DIR[walkPoint.getDirection()][1]);
      primaryDirection = walkPoint.getDirection();
      flag = true;
    }
  }
}
