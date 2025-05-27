package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.gambling.FlowerGame;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class FlowerGameDialogue extends Dialogue {

  public FlowerGameDialogue(Stoner stoner) {
    this.stoner = stoner;
  }

  @Override
  public boolean clickButton(int id) {

    switch (id) {
      case DialogueConstants.OPTIONS_5_1:
        DialogueManager.sendStatement(stoner, "Coming soon!");
        break;

      case DialogueConstants.OPTIONS_5_2:
        FlowerGame.play(stoner, 100_000);
        break;

      case DialogueConstants.OPTIONS_5_3:
        FlowerGame.play(stoner, 500_000);
        break;

      case DialogueConstants.OPTIONS_5_4:
        FlowerGame.play(stoner, 1_000_000);
        break;

      case DialogueConstants.OPTIONS_5_5:
        stoner.start(new GamblerDialogue(stoner));
        break;
    }

    return false;
  }

  @Override
  public void execute() {

    switch (next) {
      case 0:
        DialogueManager.sendOption(stoner, "Guide", "Bet 100K", "Bet 500K", "Bet 1M", "Nevermind");
        next++;
        break;
    }
  }
}
