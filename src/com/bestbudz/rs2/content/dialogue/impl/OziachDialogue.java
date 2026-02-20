package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class OziachDialogue extends Dialogue {

  public OziachDialogue(Stoner stoner) {
    this.stoner = stoner;
  }

  public void makeShield() {
    if (!stoner.getBox().hasItemAmount(995, 15_000_000)) {
		stoner
			.getClient()
			.queueOutgoingPacket(
				new SendMessage(
					"You need 15,000,000 bestbucks to do this! You have " + stoner.getBox().getItemAmount(995) + "."));
      return;
    }
    if (!stoner.getBox().hasItemAmount(11286, 1)) {
		stoner
			.getClient()
			.queueOutgoingPacket(
				new SendMessage("You need a " + GameDefinitionLoader.getItemDef(11286).getName() + "."));
      return;
    }
    stoner.getBox().remove(995, 15_000_000);
    stoner.getBox().remove(11286, 1);
    stoner.getBox().add(11283, 1);
  }

  @Override
  public boolean clickButton(int id) {
    return false;
  }

  @Override
  public void execute() {
    switch (next) {
      case 0:
        makeShield();
        break;
    }
  }
}
