package com.bestbudz.rs2.content.minigames.plunder.tasks;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.minigames.plunder.PlunderConstants;
import com.bestbudz.rs2.content.minigames.plunder.PlunderConstants.UrnBitPosition;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class LootUrnTask extends Task {

  private final UrnBitPosition urn;
  private int ticks;

  public LootUrnTask(Stoner stoner, UrnBitPosition urn) {
    super(
        stoner, 1, false, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
    this.urn = urn;

    ticks = 0;
  }

  @Override
  public void execute() {
    getEntity().getStoner().send(new SendMessage(ticks));
    switch (ticks++) {
      case 2:
        getEntity().getStoner().getUpdateFlags().sendAnimation(PlunderConstants.SUCCESSFUL_LOOT);
        getEntity()
            .getStoner()
            .getAttributes()
            .set(
                PlunderConstants.URNS_CONFIG_KEY,
                getEntity().getStoner().getAttributes().getInt(PlunderConstants.URNS_CONFIG_KEY)
                    | urn.getConfig(0));
        break;
      case 3:
        stop();
        break;
    }
  }

  @Override
  public void onStop() {
    getEntity()
        .getStoner()
        .send(
            new SendConfig(
                PlunderConstants.URNS_CONFIG,
                getEntity().getStoner().getAttributes().getInt(PlunderConstants.URNS_CONFIG_KEY)));
  }

  @Override
  public void onStart() {
    getEntity().getStoner().getUpdateFlags().sendAnimation(PlunderConstants.ATTEMPT_LOOT);
    getEntity().getStoner().getProfession().lock(4);
  }
}
