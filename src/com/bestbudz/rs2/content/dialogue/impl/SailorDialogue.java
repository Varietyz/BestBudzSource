package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.content.profession.mage.MageProfession.TeleportTypes;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class SailorDialogue extends Dialogue {

  public SailorDialogue(Stoner stoner) {
    this.stoner = stoner;
  }

  public boolean can() {
	  return false;
  }

  @Override
  public boolean clickButton(int id) {
	  return false;
  }

  @Override
  public void execute() {
	  if (next == 0)
	  {
		  DialogueManager.sendNpcChat(
			  stoner,
			  3936,
			  Emotion.HAPPY_TALK,
			  "YOU GOT DEM CHEESEBURGERS MAN!?");
		  next++;
	  }
  }
}
