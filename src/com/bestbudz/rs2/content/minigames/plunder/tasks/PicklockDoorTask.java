package com.bestbudz.rs2.content.minigames.plunder.tasks;

import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.minigames.plunder.PlunderConstants;
import com.bestbudz.rs2.content.minigames.plunder.PlunderConstants.DoorBitPosition;
import com.bestbudz.rs2.content.minigames.plunder.PyramidPlunder;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendAnimateObject;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class PicklockDoorTask extends Task {

  private final RSObject doorObject;
  private final DoorBitPosition door;

  private int ticks;

  public PicklockDoorTask(Stoner stoner, RSObject doorObject, DoorBitPosition door) {
    super(stoner, 1, true, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CURRENT_ACTION);
    this.doorObject = doorObject;
    this.door = door;

    ticks = 0;
    stoner.getProfession().lock(4);
    stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
    stoner.getMovementHandler().setForceMove(true);
  }

  @Override
  public void execute() {
    switch (ticks++) {
      case 1:
        getEntity()
            .getStoner()
            .getAttributes()
            .set(
                PlunderConstants.DOORS_CHEST_SARCOPHAGUS_CONFIG_KEY,
                getEntity()
                        .getStoner()
                        .getAttributes()
                        .getInt(PlunderConstants.DOORS_CHEST_SARCOPHAGUS_CONFIG_KEY)
                    | door.getConfig());
        break;

      case 2:
        getEntity()
            .getStoner()
            .send(
                new SendConfig(
                    PlunderConstants.DOORS_CHEST_SARCOPHAGUS_CONFIG,
                    getEntity()
                        .getStoner()
                        .getAttributes()
                        .getInt(PlunderConstants.DOORS_CHEST_SARCOPHAGUS_CONFIG_KEY)));
        getEntity()
            .getStoner()
            .send(new SendAnimateObject(doorObject, PlunderConstants.DOOR_ANIMATION));
        break;

      case 3:
        int floor = getEntity().getStoner().getAttributes().getInt("PLUNDER_FLOOR");

        if (PyramidPlunder.SINGLETON.isExitDoor(door, floor)) {
          if (PyramidPlunder.SINGLETON.changeFloor(getEntity().getStoner(), floor + 1)) {
            getEntity().getStoner().send(new SendMessage("Floor @dre@" + (floor + 2) + "</col>."));
          } else {
            ((Task) getEntity().getStoner().getAttributes().get("PLUNDER_TASK")).stop();
            getEntity().getStoner().send(new SendMessage("You have completed your run."));
          }
        }
        stop();
        break;
    }

    getEntity().getStoner().getUpdateFlags().sendAnimation(PlunderConstants.SPEAR_TRAP);
  }

  @Override
  public void onStop() {
    getEntity().getStoner().setController(ControllerManager.PLUNDER_CONTROLLER);
    getEntity().getStoner().getMovementHandler().setForceMove(false);
  }
}
