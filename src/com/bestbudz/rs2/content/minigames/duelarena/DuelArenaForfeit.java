package com.bestbudz.rs2.content.minigames.duelarena;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class DuelArenaForfeit extends Dialogue {

  public DuelArenaForfeit(Stoner stoner) {
    setStoner(stoner);
  }

  @Override
  public boolean clickButton(int id) {
    switch (id) {
      case 9157:
        if (!getStoner().getDueling().isDueling()) {
          return true;
        }
        getStoner().getDueling().onDuelEnd(true, false);
        getStoner().getClient().queueOutgoingPacket(new SendRemoveInterfaces());
        return true;
      case 9158:
        getStoner().getClient().queueOutgoingPacket(new SendRemoveInterfaces());
        return true;
    }

    return false;
  }

  @Override
  public void execute() {
    switch (getNext()) {
      case 0:
        DialogueManager.sendStatement(getStoner(), "Are you sure you would like to forfeit?");
        setNext(1);
        break;
      case 1:
        DialogueManager.sendOption(getStoner(), "Yes", "No");
        end();
    }
  }
}
