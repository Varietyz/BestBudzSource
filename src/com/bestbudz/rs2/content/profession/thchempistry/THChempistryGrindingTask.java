package com.bestbudz.rs2.content.profession.thchempistry;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class THChempistryGrindingTask extends Task {
  private final Stoner stoner;
  private final GrindingData data;

  public THChempistryGrindingTask(Stoner stoner, GrindingData data) {
    super(
        stoner,
        1,
        true,
        Task.StackType.NEVER_STACK,
        Task.BreakType.ON_MOVE,
        TaskIdentifier.CURRENT_ACTION);
    this.stoner = stoner;
    this.data = data;
  }

  public static void handleGrindingIngredients(Stoner stoner, Item used, Item usedWith) {
    int itemId = used.getId() != 233 ? used.getId() : usedWith.getId();
    GrindingData data = GrindingData.forId(itemId);
    if (data == null) return;
    stoner.getUpdateFlags().sendAnimation(new Animation(364));
    TaskQueue.queue(new THChempistryGrindingTask(stoner, data));
  }

  private void createGroundItem() {
    stoner.getBox().remove(data.getItemId(), 1);
    stoner.getBox().add(new Item(data.getGroundId(), 1));
  }

  @Override
  public void execute() {
    createGroundItem();
    stop();
  }

  @Override
  public void onStop() {}
}
