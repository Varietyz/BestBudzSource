package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.profession.thchempistry.PotionDecanting;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class DecantingDialogue extends Dialogue {

  public DecantingDialogue(Stoner stoner) {
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
		  PotionDecanting.decantAll(stoner);
	  }
  }
}
