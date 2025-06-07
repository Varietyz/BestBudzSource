package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.util.Cooldown;

public class BestBudzDialogue extends Dialogue {

	private int cooldownSeconds = 3;

  public BestBudzDialogue(Stoner stoner) {
    this.stoner = stoner;
  }

  @Override
  public boolean clickButton(int id) {
    return false;
  }

  @Override
  public void execute() {
	  if (next == 0)
	  {
		  DialogueManager.sendNpcChat(stoner, 1558, Emotion.HAPPY_TALK, "High, how hi are you?");

		  Cooldown.cooldown(cooldownSeconds);

		  DialogueManager.sendStonerChat(
			  stoner,
			  Emotion.CALM,
			  "Hi to you too!",
			  "Just chillin, professioning, killing.",
			  "Getting baked, have a good feelin.");

		  Cooldown.cooldown(cooldownSeconds);

		  DialogueManager.sendNpcChat(
			  stoner,
			  1558,
			  Emotion.HAPPY_TALK,
			  "Haha, word up!",
			  "What you smoking on fam?",
			  "Got some for me?");

		  Cooldown.cooldown(cooldownSeconds);

		  DialogueManager.sendStonerChat(
			  stoner,
			  Emotion.CALM,
			  "A spliff a day keep's the doctor away!",
			  "Sure i'll toke with you!",
			  "Let me grind some good goods!");

		  Cooldown.cooldown(cooldownSeconds);

		  DialogueManager.sendNpcChat(
			  stoner,
			  1558,
			  Emotion.HAPPY_TALK,
			  "Oh yeah bud!",
			  "Maybe you smoking on some popular stuff?",
			  "Check out these weed titles i have in store.");

		  Cooldown.cooldown(cooldownSeconds);

		  stoner.send(new SendInterface(55000));
		  next++;
	  }
  }
}
