package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class OziachDialogue extends Dialogue {

  public OziachDialogue(Stoner stoner) {
    this.stoner = stoner;
  }

  public void makeShield() {
    if (!stoner.getBox().hasItemAmount(995, 15_000_000)) {
      DialogueManager.sendNpcChat(
          stoner,
          822,
          Emotion.DEFAULT,
          "You need 15,000,000 bestbucks to do this!",
          "You have " + stoner.getBox().getItemAmount(995) + ".");
      return;
    }
    if (!stoner.getBox().hasItemAmount(11286, 1)) {
      DialogueManager.sendNpcChat(
          stoner,
          822,
          Emotion.DEFAULT,
          "You need a " + GameDefinitionLoader.getItemDef(11286).getName() + ".");
      return;
    }
    stoner.getBox().remove(995, 15_000_000);
    stoner.getBox().remove(11286, 1);
    stoner.getBox().add(11283, 1);
    DialogueManager.sendNpcChat(stoner, 822, Emotion.DEFAULT, "Have fun with your new shield!");
  }

  @Override
  public boolean clickButton(int id) {
    switch (id) {
      case DialogueConstants.OPTIONS_2_1:
        setNext(2);
        execute();
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
            stoner, 822, Emotion.DEFAULT, "Hello there!", "How may I help you?");
        next++;
        break;
      case 1:
        DialogueManager.sendOption(stoner, "Can make me a dragonfire shield?", "Nothing.");
        break;
      case 2:
        DialogueManager.sendNpcChat(
            stoner,
            822,
            Emotion.DEFAULT,
            "Yes of course!",
            "It will cost 15m bestbucks.",
            "You also need a Dragonic visage.");
        next++;
        break;
      case 3:
        makeShield();
        break;
    }
  }
}
