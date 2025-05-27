package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class AntifireTask extends Task {

  private final Stoner stoner;
  private final boolean isSuper;
  private int cycles;
  private boolean success;

  public AntifireTask(Stoner stoner, boolean isSuper) {
    super(stoner, 1, false, StackType.STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
    this.cycles = 600;
    this.stoner = stoner;
    this.isSuper = isSuper;
    this.success = true;

    if (stoner.getAttributes().get("fire_potion_task") != null) {
      ((AntifireTask) stoner.getAttributes().get("fire_potion_task")).cycles = 600;
      success = false;
      return;
    }
    stoner.getAttributes().set("fire_resist", Boolean.FALSE);
    stoner.getAttributes().set("super_fire_resist", Boolean.FALSE);
    stoner.getAttributes().set("fire_potion_task", this);

    stoner.getAttributes().set(isSuper ? "super_fire_resist" : "fire_resist", Boolean.TRUE);
  }

  @Override
  public void execute() {
    if (stoner.isDead() || !success) {
      this.stop();
      return;
    }

    if ((!isSuper && !stoner.getAttributes().is("fire_resist"))
        || (isSuper && !stoner.getAttributes().is("super_fire_resist"))) {
      this.stop();
      return;
    }
    if (cycles > 0) {
      cycles--;

      if (cycles == 100) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage("@red@Your resistance to dragonfire is about to run out."));
      }

      if (cycles == 0) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage("@red@Your resistance to dragonfire has run out."));
        this.stop();
      }
    }
  }

  @Override
  public void onStop() {
    if (success) {
      stoner.getAttributes().set(isSuper ? "super_fire_resist" : "fire_resist", Boolean.FALSE);
      stoner.getAttributes().remove("fire_potion_task");
    }
  }
}
