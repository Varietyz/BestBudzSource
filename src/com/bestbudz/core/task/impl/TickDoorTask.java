package com.bestbudz.core.task.impl;

import com.bestbudz.core.cache.map.Door;
import com.bestbudz.core.task.Task;

public class TickDoorTask extends Task {

  public TickDoorTask(Door door) {
    super(null, 1);
    if (door.original()) {
      stop();
    }
  }

  @Override
  public void execute() {

    stop();
  }

  @Override
  public void onStop() {}
}
