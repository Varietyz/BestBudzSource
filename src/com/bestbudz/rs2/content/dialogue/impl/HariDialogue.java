package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.DropTable;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class HariDialogue extends Dialogue {

  public HariDialogue(Stoner stoner) {
    this.stoner = stoner;
  }

  @Override
  public boolean clickButton(int id) {
    switch (id) {
      case DialogueConstants.OPTIONS_2_1:
        DropTable.open(stoner);
        break;
      case DialogueConstants.OPTIONS_2_2:
        stoner.send(new SendRemoveInterfaces());
        break;
    }
    return false;
  }

  @Override
  public void execute() {
    switch (next) {
      case 0:
        DialogueManager.sendNpcChat(
            stoner,
            1305,
            Emotion.CALM,
            "Hello stoner.",
            "I can show you the drop table for any NPC.");
        next++;
        break;
      case 1:
        DialogueManager.sendOption(stoner, "Open Drop Tables", "Nevermind");
        break;
    }
  }
}
