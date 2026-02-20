package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.pathfinding.SimplePathWalker;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class MobWalkTask extends Task {

  private final Mob mob;
  private final Location destination;
  private final boolean shouldWait;
  private byte wait = 0;

  public MobWalkTask(Mob mob, Location destination, boolean shouldWait) {
    super(mob, 1, true, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
    this.mob = mob;
    this.destination = destination;
    this.shouldWait = shouldWait;
    mob.setForceWalking(true);
  }

  @Override
  public void execute() {
    if (mob.isDead()) {
      stop();
      return;
    }

    if (Utility.getManhattanDistance(destination, mob.getLocation()) == 0) {
      stop();
      return;
    }

    SimplePathWalker.walkToNextTile(mob, destination);

    if (mob.getMovementHandler().getPrimaryDirection() != -1) {
      mob.getCombat().reset();
      return;
    }

    if (shouldWait) {
      if (wait > 0) {
        wait--;
        return;
      }

      for (Stoner stoner : World.getStoners()) {
        if (stoner != null && mob.getLocation().isViewableFrom(stoner.getLocation())) {
          wait = 15;
          return;
        }
      }

      mob.teleport(destination);
    }

    stop();
  }

  @Override
  public void onStop() {
    mob.setForceWalking(false);
  }
}
