package com.bestbudz.rs2.entity.movement;

import com.bestbudz.BestbudzConstants;
import com.bestbudz.core.cache.map.Region;
import com.bestbudz.rs2.content.minigames.duelarena.DuelingConstants;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMultiInterface;

public class StonerMovementHandler extends MovementHandler {

  private final Stoner stoner;

  public StonerMovementHandler(Stoner stoner) {
    super(stoner);
    this.stoner = stoner;
  }

  @Override
  public boolean canMoveTo(int direction) {
    int x = stoner.getLocation().getX();
    int y = stoner.getLocation().getY();
    int z = stoner.getLocation().getZ();

    return Region.getRegion(x, y).canMove(x, y, z, direction);
  }

  @Override
  public boolean canMoveTo(int x, int y, int size, int direction) {
    int z = stoner.getLocation().getZ();

    return Region.getRegion(x, y).canMove(x, y, z, direction);
  }

  @Override
  public void process() {
    if ((stoner.isDead())
        || (stoner.isFrozen())
        || (stoner.isStunned())
        || (stoner.getMage().isTeleporting())
        || ((stoner.getDueling().isDueling())
            && stoner.getDueling().getRuleToggle()[DuelingConstants.NO_MOVEMENT])) {
      reset();
      return;
    }

    Point walkPoint = waypoints.poll();

    if ((walkPoint != null) && (walkPoint.getDirection() != -1)) {
      if (stoner.getRunEnergy().isResting()) {
        stoner.getRunEnergy().toggleResting();
      }

      if ((!forceMove)
          && (!BestbudzConstants.WALK_CHECK)
          && (!Region.getRegion(stoner.getLocation())
              .canMove(stoner.getLocation(), walkPoint.getDirection()))) {
        reset();
        return;
      }

      stoner.getMovementHandler().getLastLocation().setAs(stoner.getLocation());
      stoner
          .getLocation()
          .move(
              com.bestbudz.rs2.GameConstants.DIR[walkPoint.getDirection()][0],
              com.bestbudz.rs2.GameConstants.DIR[walkPoint.getDirection()][1]);
      primaryDirection = walkPoint.getDirection();
      flag = true;
    } else {
      if (flag) {
        flag = false;
      }
      return;
    }

    if (stoner.getRunEnergy().isRunning()) {
      if (stoner.getRunEnergy().canRun()) {
        Point runPoint = waypoints.poll();

        if ((runPoint != null) && (runPoint.getDirection() != -1)) {
          if ((!forceMove)
              && (!BestbudzConstants.WALK_CHECK)
              && (!Region.getRegion(stoner.getLocation())
                  .canMove(stoner.getLocation(), runPoint.getDirection()))) {

            reset();
            return;
          }

          stoner.getMovementHandler().getLastLocation().setAs(stoner.getLocation());
          stoner
              .getLocation()
              .move(
                  com.bestbudz.rs2.GameConstants.DIR[runPoint.getDirection()][0],
                  com.bestbudz.rs2.GameConstants.DIR[runPoint.getDirection()][1]);
          secondaryDirection = runPoint.getDirection();

          stoner.getRunEnergy().onRun();
        }
      } else {
        stoner
            .getClient()
            .queueOutgoingPacket(new SendMessage("You don't have enough run energy to do that."));
        stoner.getRunEnergy().reset();
        stoner
            .getClient()
            .queueOutgoingPacket(new SendConfig(173, stoner.getRunEnergy().isRunning() ? 1 : 0));
      }
    }

    ControllerManager.setControllerOnWalk(stoner);

    if (stoner.inMultiArea()) stoner.getClient().queueOutgoingPacket(new SendMultiInterface(true));
    else {
      stoner.getClient().queueOutgoingPacket(new SendMultiInterface(false));
    }

    stoner.checkForRegionChange();

    if ((forceMove) && (waypoints.size() == 0)) forceMove = false;
  }
}
