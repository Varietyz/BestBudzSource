package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.entity.Entity;

public class RandomTalkTask extends Task {

  private final Entity entity;

  private final String message;

  public RandomTalkTask(Entity entity, int delay, String message) {
    super(entity, delay);
    this.entity = entity;
    this.message = message;
  }

  @Override
  public void execute() {
    entity.getUpdateFlags().sendForceMessage(message);
    stop();
  }

  @Override
  public void onStop() {}
}
