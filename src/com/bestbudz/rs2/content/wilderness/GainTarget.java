package com.bestbudz.rs2.content.wilderness;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class GainTarget extends Task {

  private final Stoner stoner;

  public GainTarget(Stoner stoner, byte delay) {
    super(delay);
    this.stoner = stoner;
  }

  @Override
  public void execute() {
    if (!stoner.inWilderness() || TargetSystem.getInstance().stonerHasTarget(stoner)) {
      stop();
      return;
    }
    if (stoner.inWilderness()) {
      TargetSystem.getInstance().assignTarget(stoner);
      stop();
    }
  }

  @Override
  public void onStop() {
    stoner.getAttributes().remove("gainTarget");
  }
}
