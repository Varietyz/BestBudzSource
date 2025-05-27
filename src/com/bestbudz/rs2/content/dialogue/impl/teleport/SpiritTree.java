package com.bestbudz.rs2.content.dialogue.impl.teleport;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class SpiritTree extends Dialogue {
  public static final int NPC_ID = 3636;

  public SpiritTree(Stoner stoner) {
    this.stoner = stoner;
  }

  @Override
  public boolean clickButton(int id) {
    switch (id) {
      case 9157:
        stoner.teleport(new Location(2461, 3434, 0));
        stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
        break;
      case 9158:
        stoner.teleport(new Location(2725, 3491, 0));
        stoner.getClient().queueOutgoingPacket(new SendRemoveInterfaces());
    }

    return false;
  }

  @Override
  public void execute() {
    Emotion e = Emotion.HAPPY_TALK;
    switch (next) {
      case 0:
        DialogueManager.sendNpcChat(stoner, 3636, e, "Where would you like to go?");
        next += 1;
        break;
      case 1:
        DialogueManager.sendOption(stoner, "Gnome Stronghold", "Seers Village");
    }
  }
}
