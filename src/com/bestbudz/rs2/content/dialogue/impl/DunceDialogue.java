package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.StonerTitle;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class DunceDialogue extends Dialogue {

  public DunceDialogue(Stoner stoner) {
    this.stoner = stoner;
  }

  private void title(String title, int color) {
    stoner.setStonerTitle(StonerTitle.create(title, color, false));
    stoner.send(new SendMessage("Special title has been set!"));
    stoner.setAppearanceUpdateRequired(true);
    stoner.send(new SendRemoveInterfaces());
  }

  @Override
  public boolean clickButton(int id) {
    switch (id) {
      case DialogueConstants.OPTIONS_4_1:
        if (StonerConstants.isStaff(stoner)
            || stoner.getRights() == 5
            || stoner.getRights() == 6
            || stoner.getRights() == 7
            || stoner.getRights() == 8) {
          title("Member", 0xCC0000);
        } else {
          DialogueManager.sendNpcChat(
              stoner,
              6749,
              Emotion.SAD,
              "You need to at least be a <img=5> <col=0x015E7D>Member</col>!");
        }
        break;

      case DialogueConstants.OPTIONS_4_2:
        if (StonerConstants.isStaff(stoner)
            || stoner.getRights() == 6
            || stoner.getRights() == 7
            || stoner.getRights() == 8) {
          title("Super", 0x3366CC);
        } else {
          DialogueManager.sendNpcChat(
              stoner,
              6749,
              Emotion.SAD,
              "You need to at least be a <img=6> <col=0x015E7D>Super Member</col>!");
        }
        break;

      case DialogueConstants.OPTIONS_4_3:
        if (StonerConstants.isStaff(stoner) || stoner.getRights() == 7 || stoner.getRights() == 8) {
          title("Extreme", 0x244700);
        } else {
          DialogueManager.sendNpcChat(
              stoner,
              6749,
              Emotion.SAD,
              "You need to at least be a <img=7> <col=0x158A08>Extreme Member</col>!");
        }
        break;

      case DialogueConstants.OPTIONS_4_4:
        if (StonerConstants.isStaff(stoner) || stoner.getRights() == 8) {
          title("Elite", 0x9900FF);
        } else {
          DialogueManager.sendNpcChat(
              stoner,
              6749,
              Emotion.SAD,
              "You need to at least be a <img=8> <col=0x7D088A>Elite Member</col>!");
        }
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
            6749,
            Emotion.HAPPY_TALK,
            "Hello " + stoner.getUsername() + "!",
            "I can give you a special title.",
            "You must be privilaged enough of course!");
        next++;
        break;

      case 1:
        DialogueManager.sendOption(stoner, "Member", "Super", "Extreme", "Elite");
        break;
    }
  }
}
