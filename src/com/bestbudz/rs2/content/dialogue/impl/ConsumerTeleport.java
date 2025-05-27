package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TeleOtherTask;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class ConsumerTeleport extends Dialogue {

  final Mob mob;

  public ConsumerTeleport(Stoner stoner, Mob mob) {
    this.stoner = stoner;
    this.mob = mob;
  }

  @Override
  public boolean clickButton(int id) {
    if (id == 9158) {
      stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
      TaskQueue.queue(new TeleOtherTask(mob, stoner, new Location(2923, 4819)));
      return true;
    }
    return false;
  }

  @Override
  public void execute() {
    switch (next) {
      case 0:
        DialogueManager.sendNpcChat(
            stoner,
            mob.getId(),
            Emotion.DEFAULT,
            "Hello " + Utility.formatStonerName(stoner.getUsername()) + ".",
            "I can teleport you to a Consumer training area.",
            "Where would you like to go?");
        next++;
        break;
      case 1:
        DialogueManager.sendOption(stoner, "Essence quarry");
        break;
    }
  }
}
