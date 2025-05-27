package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class BestBudzDialogue extends Dialogue {

  public BestBudzDialogue(Stoner stoner) {
    this.stoner = stoner;
  }

  @Override
  public boolean clickButton(int id) {
    switch (id) {
      case DialogueConstants.OPTIONS_2_1:
        stoner.send(new SendInterface(55000));
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
        DialogueManager.sendNpcChat(stoner, 1558, Emotion.HAPPY_TALK, "High, how hi are you?");
        next++;
        break;

      case 1:
        DialogueManager.sendStonerChat(
            stoner,
            Emotion.CALM,
            "Hi to you too!",
            "Just chillin, professioning, killing.",
            "Getting baked, have a good feelin.");
        next++;
        break;

      case 2:
        DialogueManager.sendNpcChat(
            stoner,
            1558,
            Emotion.HAPPY_TALK,
            "Haha, word up!",
            "What you smoking on fam?",
            "Got some for me?");
        next++;
        break;

      case 3:
        DialogueManager.sendStonerChat(
            stoner,
            Emotion.CALM,
            "A spliff a day keep's the doctor away!",
            "Sure i'll toke with you!",
            "Let me grind some good goods!");
        next++;
        break;

      case 4:
        DialogueManager.sendNpcChat(
            stoner,
            1558,
            Emotion.HAPPY_TALK,
            "Oh yeah bud!",
            "Maybe you smoking on some popular stuff?",
            "Wanne see the weed titles i have in store?");
        next++;
        break;

      case 5:
        DialogueManager.sendOption(stoner, "Deal for weed titles.", "I'm going to be back!");
        break;
    }
  }
}
