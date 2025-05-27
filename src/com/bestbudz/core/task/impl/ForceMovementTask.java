package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;

public abstract class ForceMovementTask extends Task {

  protected final Stoner stoner;
  protected final Controller start;
  protected final Controller to;

  protected final Location dest;
  protected final int xMod;
  protected final int yMod;

  public ForceMovementTask(Stoner stoner, Location dest, Controller to) {
    super(stoner, 1);
    this.stoner = stoner;
    this.dest = dest;
    this.to = to;
    start = stoner.getController();
    stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);

    int xDiff = stoner.getLocation().getX() - dest.getX();
    int yDiff = stoner.getLocation().getY() - dest.getY();

    if (xDiff != 0) xMod = (xDiff < 0 ? 1 : -1);
    else xMod = 0;
    if (yDiff != 0) yMod = (yDiff < 0 ? 1 : -1);
    else yMod = 0;
    if (xDiff != 0 && yDiff != 0) {
      stop();
      stoner.setController(start);
    } else {
      stoner.getMovementHandler().reset();
    }
    stoner.getCombat().reset();
  }

  @Override
  public void execute() {
    stoner.getMovementHandler().setForceMove(true);
    stoner.getMovementHandler().walkTo(xMod, yMod);
    if (stoner.getLocation().getX() + xMod == dest.getX()
        && stoner.getLocation().getY() + yMod == dest.getY()) {
      onDestination();
      stop();
    }
  }

  @Override
  public void onStop() {
    stoner.setController(to);
  }

  public abstract void onDestination();
}
