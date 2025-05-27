package com.bestbudz.core.task.impl;

import com.bestbudz.core.task.Task;
import com.bestbudz.rs2.content.cluescroll.ClueScrollManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;

public class DigTask extends Task {

  private final Stoner stoner;
  private int time = 0;

  public DigTask(Stoner stoner) {
    super(stoner, 1, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.CURRENT_ACTION);
    this.stoner = stoner;
  }

  @Override
  public void execute() {
    if (++time == 1) {
      stoner.getUpdateFlags().sendAnimation(830, 0);
      stoner.getClient().queueOutgoingPacket(new SendMessage("You dig.."));
      return;
    }

    if (++time != 3) {
      return;
    }

    stoner.getClient().queueOutgoingPacket(new SendSound(380, 10, 0));

    if (ClueScrollManager.SINGLETON.dig(stoner)) {
      stop();
      return;
    }

    stoner.getClient().queueOutgoingPacket(new SendMessage("You find nothing of interest."));
    stop();
  }

  @Override
  public void onStop() {
    stoner.getUpdateFlags().sendAnimation(65535, 0);
  }
}
