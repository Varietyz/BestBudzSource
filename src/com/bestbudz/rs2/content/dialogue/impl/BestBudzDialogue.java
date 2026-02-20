package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;

public class BestBudzDialogue extends Dialogue {

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
		  stoner.send(new SendInterface(55000));
		  next++;
	  }
  }
}
