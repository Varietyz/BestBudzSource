package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.content.shopping.ShopConstants;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
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



    }
    return false;
  }

  @Override
  public void execute() {
    switch (next) {
      case 0:
		  if (stoner.getBox().hasItemId(ZAMORAKIAN_HASTA)) {
			  stoner.getBox().remove(ZAMORAKIAN_HASTA, 1);
			  stoner.getBox().add(ZAMORAKIAN_SPEAR, 1);
			  stoner
				  .getClient()
				  .queueOutgoingPacket(
					  new SendMessage(
						  "Otto has given you a Zamorakian spear."));

		  }else if (stoner.getBox().hasItemId(ZAMORAKIAN_SPEAR)) {
			  if (stoner.getBox().hasItemAmount(995, CREATION_COST)) {
				  stoner.getBox().remove(995, CREATION_COST);
				  stoner.getBox().remove(ZAMORAKIAN_SPEAR, 1);
				  stoner.getBox().add(ZAMORAKIAN_HASTA, 1);
				  stoner
					  .getClient()
					  .queueOutgoingPacket(
						  new SendMessage(
							  "Otto has given you a Zamorakian hasta."));
		}
		  break;
		  }
    }
  }
}
