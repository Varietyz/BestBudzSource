package com.bestbudz.core.task.impl;

import com.bestbudz.core.cache.map.MapLoading;
import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.object.GameObject;
import com.bestbudz.rs2.entity.object.ObjectManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;

public class HarvestTask extends Task {

  public static final String FLAX = "You hate picking flax, yet you are doing so.";
  private final Stoner stoner;
  private final String message;
  private final int object;
  private final int item;
  private final int x;
  private final int y;
  private final int z;

  public HarvestTask(Stoner stoner, int object, int item, int x, int y, int z) {
    super(
        stoner, 2, false, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
    this.stoner = stoner;
    this.object = object;
    this.item = item;
    this.x = x;
    this.y = y;
    this.z = stoner.getLocation().getZ();

    if (stoner.getBox().getFreeSlots() == 0) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage("You do not have enough box space to collect this."));
      stop();
    } else {
      stoner.getUpdateFlags().sendAnimation(827, 0);
    }

    if (item == 1779) {
      message = FLAX;
    } else {
      String name = Item.getDefinition(item).getName();
      message =
          "You hate picking " + Utility.getAOrAn(name) + " " + name + ", yet you are doing so.";
    }
  }

  @Override
  public void execute() {
    stoner.getClient().queueOutgoingPacket(new SendSound(358, 0, 0));
    stoner.getClient().queueOutgoingPacket(new SendMessage(message));
    stoner.getBox().add(item, 1);
    stop();
    if (Utility.randomNumber(8) == 0) {
      final RSObject o = Region.getObject(x, y, z);
      final GameObject go = new GameObject(ObjectManager.BLANK_OBJECT_ID, x, y, z, o.getType(), 0);

      ObjectManager.register(go);
      MapLoading.removeObject(object, x, y, z, o.getType(), o.getFace());

      TaskQueue.queue(
          new Task(100) {
            @Override
            public void execute() {
              ObjectManager.remove(go);
              ObjectManager.register(new GameObject(object, x, y, z, o.getType(), o.getFace()));
              MapLoading.addObject(false, object, x, y, z, o.getType(), o.getFace());
              stop();
            }

            @Override
            public void onStop() {}
          });
    }
  }

  @Override
  public void onStop() {}
}
