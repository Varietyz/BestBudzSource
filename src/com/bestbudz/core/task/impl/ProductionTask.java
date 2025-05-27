package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public abstract class ProductionTask extends Task {

  protected Stoner stoner;

  protected boolean started = false;

  protected int cycleCount = 0;

  private int productionCount = 0;

  public ProductionTask(Stoner stoner, int delay) {
    this(
        stoner,
        delay,
        false,
        StackType.NEVER_STACK,
        BreakType.ON_MOVE,
        TaskIdentifier.CURRENT_ACTION);
  }

  public ProductionTask(
      Stoner stoner,
      int delay,
      boolean immediate,
      StackType stackType,
      BreakType breakType,
      TaskIdentifier taskId) {
    super(stoner, delay, immediate, stackType, breakType, taskId);
    this.stoner = stoner;
  }

  public abstract boolean canProduce();

  @Override
  public void execute() {
    if (stoner.getProfession().getGrades()[getProfession()] < getRequiredGrade()) {
      DialogueManager.sendStatement(stoner, getInsufficentGradeMessage());
      stoner.getUpdateFlags().sendAnimation(new Animation(-1));
      this.stop();
      return;
    }
    for (Item productionItem : getConsumedItems()) {
      if (productionItem != null) {
        if (stoner.getBox().getItemAmount(productionItem.getId()) < productionItem.getAmount()) {
          if (noIngredients(productionItem) != null)
            stoner.getClient().queueOutgoingPacket(new SendMessage(noIngredients(productionItem)));
          stoner.getUpdateFlags().sendAnimation(new Animation(-1));
          this.stop();
          return;
        }
      }
    }
    if (!canProduce()) {
      this.stop();
      return;
    }
    if (!started) {
      started = true;
      if (getAnimation() != null) {
        stoner.getUpdateFlags().sendAnimation(getAnimation());
      }
      if (getGraphic() != null) {
        stoner.getUpdateFlags().sendGraphic(getGraphic());
      }

      productionCount = getProductionCount();
      cycleCount = getCycleCount();
      return;
    }

    if (cycleCount > 1) {
      cycleCount--;
    } else {

      if (getAnimation() != null && getAnimation().getId() > 0) {
        stoner.getUpdateFlags().sendAnimation(getAnimation());
      }
      if (getGraphic() != null && getGraphic().getId() > 0) {
        stoner.getUpdateFlags().sendGraphic(getGraphic());
      }

      cycleCount = getCycleCount();

      productionCount--;

      for (Item item : getConsumedItems()) {
        stoner.getBox().remove(item);
      }
      for (Item item : getRewards()) {
        stoner.getBox().add(item);
      }
      stoner.getClient().queueOutgoingPacket(new SendMessage(getSuccessfulProductionMessage()));
      stoner.getProfession().addExperience(getProfession(), getExperience());

      if (productionCount < 1) {
        stoner.getUpdateFlags().sendAnimation(new Animation(-1));
        this.stop();
        return;
      }
      for (Item item : getConsumedItems()) {
        if (stoner.getBox().getItemAmount(item.getId()) < item.getAmount()) {
          stoner.getUpdateFlags().sendAnimation(new Animation(-1));
          this.stop();
          return;
        }
      }
    }
  }

  public abstract Animation getAnimation();

  public abstract Item[] getConsumedItems();

  public abstract int getCycleCount();

  public void setCycleCount(int cycleCount) {
    this.cycleCount = cycleCount;
  }

  public abstract double getExperience();

  public abstract Graphic getGraphic();

  public abstract String getInsufficentGradeMessage();

  public abstract int getProductionCount();

  public void setProductionCount(int productionCount) {
    this.productionCount = productionCount;
  }

  public abstract int getRequiredGrade();

  public abstract Item[] getRewards();

  public abstract int getProfession();

  public abstract String getSuccessfulProductionMessage();

  public boolean isStarted() {
    return started;
  }

  public void setStarted(boolean started) {
    this.started = started;
  }

  public abstract String noIngredients(Item item);
}
