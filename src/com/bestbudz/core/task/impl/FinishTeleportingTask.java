package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class FinishTeleportingTask extends Task {

  private final Stoner stoner;

  public FinishTeleportingTask(Stoner stoner, int ticks) {
    super(
        stoner,
        ticks,
        false,
        StackType.NEVER_STACK,
        BreakType.NEVER,
        TaskIdentifier.CURRENT_ACTION);
    this.stoner = stoner;
  }

  @Override
  public void execute() {
    stoner.setTakeDamage(true);
    stop();
  }

  @Override
  public void onStop() {}
}
