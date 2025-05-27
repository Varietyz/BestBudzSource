package com.bestbudz.rs2.content.profession.forging;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class Smelting extends Task {

  public static final Animation SMELTING_ANIMATION = new Animation(899, 0);
  public static final String A = "You smelt ";
  public static final String B = ".";
  public static final String IRON_FAILURE = "You fail to refine the iron ore.";
  private final Stoner stoner;
  private final SmeltingData data;
  private final int amount;
  private final String name;
  private int smelted = 0;

  public Smelting(Stoner stoner, int amount, SmeltingData data) {
    super(
        stoner,
        2,
        true,
        Task.StackType.NEVER_STACK,
        Task.BreakType.ON_MOVE,
        TaskIdentifier.CURRENT_ACTION);
    this.stoner = stoner;
    this.data = data;
    this.amount = amount;
    name = data.getResult().getDefinition().getName();

    stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());

    if (!canSmelt(stoner, data, false)) {
      stop();
    }
  }

  public boolean canSmelt(Stoner stoner, SmeltingData data, boolean taskRunning) {
    if (stoner.getMaxGrades()[13] < data.getGradeRequired()) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "You need a Forging grade of "
                      + data.getGradeRequired()
                      + " to smelt this bar."));
      return false;
    }

    for (Item i : data.getRequiredOres()) {
      if (!stoner.getBox().hasItemAmount(i.getId(), i.getAmount())) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    taskRunning
                        ? "You have run out of " + i.getDefinition().getName() + "."
                        : "You don't not have any "
                            + i.getDefinition().getName().toLowerCase()
                            + " to smelt."));
        return false;
      }
    }

    return true;
  }

  @Override
  public void execute() {
    if (!canSmelt(stoner, data, true)) {
      stop();
      return;
    }

    stoner.getUpdateFlags().sendAnimation(SMELTING_ANIMATION);

    stoner.getBox().remove(data.getRequiredOres(), false);

    if (data == SmeltingData.IRON_BAR) {
      if (Professions.isSuccess(stoner, 13, data.getGradeRequired())) {
        stoner.getBox().add(data.getResult(), false);
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage("You smelt " + Utility.getAOrAn(name) + " " + name + "."));
      } else {
        stoner.getClient().queueOutgoingPacket(new SendMessage("You fail to refine the iron ore."));
      }
    } else {
      stoner.getBox().add(data.getResult(), false);
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("You smelt " + Utility.getAOrAn(name) + " " + name + "."));
    }

    stoner.getBox().update();

    stoner.getProfession().addExperience(13, data.getExp());

    if (++smelted == amount) stop();
  }

  @Override
  public void onStop() {}

  public boolean isSuccess(Stoner stoner, SmeltingData data) {
    return Professions.isSuccess(stoner, 13, data.gradeRequired);
  }
}
