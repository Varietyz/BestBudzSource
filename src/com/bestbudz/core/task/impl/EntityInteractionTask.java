package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract class EntityInteractionTask extends Task {

  public EntityInteractionTask(Stoner entity, int ticks) {
    super(entity, ticks);
  }

  public abstract Item[] getConsumedItems();

  public abstract String getInsufficentGradeMessage();

  public abstract Mob getInteractingMob();

  public abstract String getInteractionMessage();

  public abstract short getRequiredGrade();

  public abstract Item[] getRewards();

  public abstract String getSuccessfulInteractionMessage();

  public abstract String getUnsuccessfulInteractionMessage();
}
