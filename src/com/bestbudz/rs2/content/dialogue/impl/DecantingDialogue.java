package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.content.profession.thchempistry.PotionDecanting;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class DecantingDialogue extends Dialogue {

  public DecantingDialogue(Stoner stoner) {
    this.stoner = stoner;
  }

  @Override
  public boolean clickButton(int id) {
    switch (id) {
      case DialogueConstants.OPTIONS_3_1:
        stoner.send(new SendRemoveInterfaces());
        setNext(5);
        execute();
        break;

      case DialogueConstants.OPTIONS_3_2:
        if (stoner.getBox().hasItemId(995)) {
          PotionDecanting.decantAll(stoner);
          stoner.send(new SendRemoveInterfaces());
        } else {
          DialogueManager.sendStatement(stoner, "You do not have any bestbucks!");
        }
        break;

      case DialogueConstants.OPTIONS_3_3:
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
            6524,
            Emotion.HAPPY,
            "Hello stoner.",
            "I can decant your potions for 250gp a potion.",
            "What would you like to do?");
        next++;
        break;
      case 1:
        DialogueManager.sendOption(stoner, "What is decanting?", "Decant box.", "Nothing.");
        break;
      case 5:
        DialogueManager.sendNpcChat(
            stoner,
            6524,
            Emotion.HAPPY,
            "Decanting is the method in which a stoner combines",
            "partially full potions of the same kind to produce",
            "one full potion and one partially empty potion.");
        next++;
        break;
      case 6:
        DialogueManager.sendNpcChat(
            stoner,
            6524,
            Emotion.HAPPY,
            "For example, decanting a potion containing 3 doses and",
            "another containing 2 doses yields one full potion (4 doses)",
            "and one partially full potion (1 dose).");
        setNext(1);
        break;
    }
  }
}
