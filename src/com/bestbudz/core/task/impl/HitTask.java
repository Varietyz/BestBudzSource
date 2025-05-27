package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.entity.Entity;

public class HitTask extends Task {

  private final Hit hit;
  private final Entity entity;

  public HitTask(int delay, boolean immediate, Hit hit, Entity entity) {
    super(
        entity, delay, immediate, StackType.STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
    this.hit = hit;
    this.entity = entity;
    if (delay <= 1) {
      sendBlockAnimation();
    } else {
      final Task t = this;

      TaskQueue.queue(
          new Task(delay - 1) {

            @Override
            public void execute() {
              if (t.stopped()) {
                stop();
                return;
              }

              sendBlockAnimation();
              stop();
            }

            @Override
            public void onStop() {}
          });
    }
  }

  @Override
  public void execute() {
    entity.hit(hit);
    stop();
  }

  @Override
  public Entity getEntity() {
    return entity;
  }

  @Override
  public void onStop() {}

  public void sendBlockAnimation() {
    if (hit.getAssaulter() != null
        && entity.getCombat().getBlockAnimation() != null
        && !entity.isDead()) {
      int a = entity.getCombat().getAssaultTimer();
      if (a != entity.getCombat().getAssaultCooldown()) {
        entity.getUpdateFlags().sendAnimation(entity.getCombat().getBlockAnimation());
      }
    }
  }
}
