package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.impl.GroundItem;
import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract class WalkToTask extends Task {

  private final int minX;
  private final int maxX;
  private final int minY;
  private final int maxY;
  private final Location location;

  public WalkToTask(Stoner stoner, GroundItem ground) {
    this(stoner, ground.getLocation().getX(), ground.getLocation().getY(), 1, 1);
  }

  public WalkToTask(Stoner stoner, int x, int y, int xLength, int yLength) {
    super(stoner, 1, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
    location = stoner.getLocation();
    minX = x - 1;
    maxX = minX + xLength + 1;
    minY = y - 1;
    maxY = minY + yLength + 1;
  }

  public WalkToTask(Stoner stoner, Stoner other) {
    this(stoner, other.getLocation().getX(), other.getLocation().getY(), 1, 1);
  }

  @Override
  public void execute() {
    int pX = location.getX();
    int pY = location.getY();

    if (pX >= minX && pX <= maxX && pY >= minY && pY <= maxY) {
      onDestination();
      stop();
    }
  }

  @Override
  public void onStop() {}

  public abstract void onDestination();
}
