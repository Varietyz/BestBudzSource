package com.bestbudz.rs2.content.profession.forging;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;

public class ForgingTask extends Task {
  private final Stoner stoner;
  private final Item forger;
  private final Item bar;
  private final int amount;
  private int loop = 0;

  public ForgingTask(Stoner stoner, Item forger, Item bar, int amount) {
    super(
        stoner,
        2,
        true,
        Task.StackType.NEVER_STACK,
        Task.BreakType.ON_MOVE,
        TaskIdentifier.CURRENT_ACTION);
    this.stoner = stoner;
    this.forger = forger;
    this.bar = bar;
    this.amount = amount;

    int lvl = ForgingConstants.getGrade(forger.getId());

    if (stoner.getMaxGrades()[13] < lvl) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("You need a Forging grade of " + lvl + " to make that."));
      stop();
    } else if (!stoner.getBox().hasItemAmount(new Item(bar))) {
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("You do not have enough bars to make that."));
      stop();
    } else if (!stoner.getEquipment().isWearingItem(6575)) {
      DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to do this!", 6575);
      stop();
    } else {
      stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
    }
  }

  public static void start(Stoner p, int item, int amount, int interfaceId, int slot) {
    String check = Item.getDefinition(item).getName().substring(0, 3);

    int bar = -1;

    int make = 1;

    if (Item.getDefinition(item).isStackable()) {
      make = 15;
    }

    switch (check) {
      case "Bro":
        bar = ForgingConstants.BARS[0];
        break;
      case "Iro":
        bar = ForgingConstants.BARS[1];
        break;
      case "Ste":
        bar = ForgingConstants.BARS[2];
        break;
      case "Mit":
        bar = ForgingConstants.BARS[3];
        break;
      case "Ada":
        bar = ForgingConstants.BARS[4];
        break;
      case "Run":
        bar = ForgingConstants.BARS[5];
        break;
    }

    TaskQueue.queue(
        new ForgingTask(
            p,
            new Item(item, make),
            new Item(bar, ForgingConstants.getBarAmount(interfaceId, slot)),
            amount));
  }

  @Override
  public void execute() {
    if (!hasRequirements()) {
      stop();
      return;
    }

    stoner.getProfession().addExperience(13, getExperience());

    stoner.getBox().remove(new Item(bar), false);
    stoner.getBox().add(new Item(forger), true);

    stoner.getUpdateFlags().sendAnimation(898, 0);
    stoner.getClient().queueOutgoingPacket(new SendSound(468, 10, 10));

    if (forger.getAmount() == 1)
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "You make "
                      + Utility.getAOrAn(forger.getDefinition().getName())
                      + " "
                      + forger.getDefinition().getName()
                      + "."));
    else {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "You make "
                      + forger.getAmount()
                      + " "
                      + forger.getDefinition().getName()
                      + (!forger.getDefinition().getName().endsWith("s") ? "s" : "")
                      + "."));
    }

    if (++loop == amount) stop();
  }

  @Override
  public void onStop() {}

  public double getExperience() {
    switch (bar.getId()) {
      case 2349:
        return 12.5D * bar.getAmount();
      case 2351:
        return 25 * bar.getAmount();
      case 2353:
        return 37.5D * bar.getAmount();
      case 2359:
        return 50 * bar.getAmount();
      case 2361:
        return 62.5D * bar.getAmount();
      case 2363:
        return 75 * bar.getAmount();
      case 2350:
      case 2352:
      case 2354:
      case 2355:
      case 2356:
      case 2357:
      case 2358:
      case 2360:
      case 2362:
    }
    return 0.0D;
  }

  public boolean hasRequirements() {
    if (!stoner.getBox().hasItemAmount(new Item(bar))) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("You have run out of " + bar.getDefinition().getName() + "s."));
      return false;
    }
    return stoner.getEquipment().isWearingItem(6575);
  }
}
