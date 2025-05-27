package com.bestbudz.rs2.content.minigames.warriorsguild;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItems;

public final class CyclopsRoom {

  public static final int[] DEFENDERS = {8844, 8845, 8846, 8847, 8848, 8849, 8850, 12954};

  public static boolean handleDoor(Stoner stoner, int x, int y) {
    if (stoner.getX() == 2847) {
      if (stoner.getAttributes().get("warrguildtokentask") != null) {
        ((Task) stoner.getAttributes().get("warrguildtokentask")).stop();
      }
      stoner.getAttributes().remove("cyclopsdefenderdrop");
      stoner.getAttributes().remove("warrguildtokentask");
      stoner.teleport(new Location(stoner.getX() - 1, stoner.getY(), stoner.getZ()));
      stoner.getUpdateFlags().sendFaceToDirection(x, y);
      return true;
    } else if (stoner.getX() == 2846) {
      if (stoner.getBox().getItemAmount(8851) < 100) {
        DialogueManager.sendStatement(
            stoner,
            "100 tokens are required to enter! You have "
                + stoner.getBox().getItemAmount(8851)
                + ".");
        return true;
      }
      stoner.getBox().remove(8851, 100);
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("You pay 100 tokens to enter the cyclops arena."));
      executeTimer(stoner);
      int defender = DEFENDERS[getDefenderIndex(stoner)];
      stoner.getAttributes().set("cyclopsdefenderdrop", Integer.valueOf(defender));
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "@dre@The cyclops are now dropping: "
                      + Item.getDefinition(defender).getName()
                      + "."));
      stoner.getAttributes().set("warrguildtokensused", 0);
      stoner.teleport(new Location(stoner.getX() + 1, stoner.getY(), stoner.getZ()));
      stoner.getUpdateFlags().sendFaceToDirection(x, y);
      updateInterface(stoner);
      return true;
    }
    return false;
  }

  public static void dropDefender(Stoner stoner, Mob mob) {
    if (Utility.randomNumber(10) != 0) {
      return;
    }

    if (stoner.getAttributes().get("cyclopsdefenderdrop") == null) {
      return;
    }

    stoner
        .getAttributes()
        .set("cyclopsdefenderdrop", Integer.valueOf(DEFENDERS[getDefenderIndex(stoner)]));
    stoner
        .getClient()
        .queueOutgoingPacket(
            new SendMessage(
                "@dre@The cyclops are now dropping: "
                    + Item.getDefinition(DEFENDERS[getDefenderIndex(stoner)]).getName()
                    + "."));
    updateInterface(stoner);
  }

  public static void updateInterface(Stoner stoner) {
    int defender = DEFENDERS[getDefenderIndex(stoner)];
    stoner.send(new SendUpdateItems(51203, new Item[] {new Item(defender)}));
    stoner.send(
        new SendString(
            "</col>Tokens Used: @red@ " + stoner.getAttributes().getInt("warrguildtokensused"),
            51205));
    stoner.send(
        new SendString(
            "</col>Cyclops Killed: @red@ "
                + (stoner.getAttributes().getInt("CYCLOPS_KILLED") == -1
                    ? 0
                    : stoner.getAttributes().getInt("CYCLOPS_KILLED")),
            51206));
  }

  public static void executeTimer(Stoner stoner) {
    Task task = new TokenTask(stoner, 100);
    stoner.getAttributes().set("warrguildtokentask", task);
    TaskQueue.queue(task);
  }

  public static int getDefenderIndex(Stoner stoner) {
    int currentDefender = -1;

    Item shield = stoner.getEquipment().getItems()[EquipmentConstants.SHIELD_SLOT];

    for (int i = 0; i < DEFENDERS.length; i++) {
      if (stoner.getBank().hasItemId(DEFENDERS[i])
          || stoner.getBox().hasItemId(DEFENDERS[i])
          || (shield != null && shield.getId() == DEFENDERS[i])) {
        currentDefender = i;
      }
    }

    if ((currentDefender + 1 >= 0) && (currentDefender + 1 < DEFENDERS.length)) {
      currentDefender++;
    }

    return currentDefender;
  }
}
