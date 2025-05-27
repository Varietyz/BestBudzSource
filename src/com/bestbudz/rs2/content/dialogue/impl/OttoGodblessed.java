package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class OttoGodblessed extends Dialogue {

  private final int ZAMORAKIAN_SPEAR = 11824;
  private final int ZAMORAKIAN_HASTA = 11889;
  private final int CREATION_COST = 3_000_000;

  public OttoGodblessed(Stoner stoner) {
    this.stoner = stoner;
  }

  @Override
  public boolean clickButton(int id) {
    switch (id) {
      case DialogueConstants.OPTIONS_3_1:
        if (stoner.getBox().hasItemId(ZAMORAKIAN_SPEAR)) {
          if (stoner.getBox().hasItemAmount(995, CREATION_COST)) {
            stoner.getBox().remove(995, CREATION_COST);
            stoner.getBox().remove(ZAMORAKIAN_SPEAR, 1);
            stoner.getBox().add(ZAMORAKIAN_HASTA, 1);
            DialogueManager.sendItem1(
                stoner, "Otto has given you a @dre@Zamorakian hasta</col>!", ZAMORAKIAN_HASTA);
            setNext(-1);
          } else {
            DialogueManager.sendNpcChat(
                stoner,
                2914,
                Emotion.ANNOYED,
                "You need " + Utility.format(CREATION_COST) + " bestbucks to do this!");
            setNext(-1);
          }
        } else {
          DialogueManager.sendNpcChat(
              stoner, 2914, Emotion.ANNOYED, "You need a Zamorakian spear to do this!");
          setNext(-1);
        }
        break;
      case DialogueConstants.OPTIONS_3_2:
        if (stoner.getBox().hasItemId(ZAMORAKIAN_HASTA)) {
          stoner.getBox().remove(ZAMORAKIAN_HASTA, 1);
          stoner.getBox().add(ZAMORAKIAN_SPEAR, 1);
          DialogueManager.sendItem1(
              stoner, "Otto has given you a @dre@Zamorakian spear</col>!", ZAMORAKIAN_SPEAR);
          setNext(-1);

        } else {
          DialogueManager.sendNpcChat(
              stoner, 2914, Emotion.ANNOYED, "You need a Zamorakian hasta to do this!");
          setNext(-1);
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
            2914,
            Emotion.HAPPY_TALK,
            "Hello young warrior!",
            "You have quite the body on you I may say.",
            "I can offer you some services.");
        next++;
        break;

      case 1:
        DialogueManager.sendOption(
            stoner, "Make Zamorakian hasta", "Revert Zamorakian spear", "Nothing");
        break;
    }
  }
}
