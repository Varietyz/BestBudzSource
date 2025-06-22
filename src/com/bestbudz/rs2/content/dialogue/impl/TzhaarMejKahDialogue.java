package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.pets.PetData;
import com.bestbudz.rs2.entity.pets.PetManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class TzhaarMejKahDialogue extends Dialogue {

  public TzhaarMejKahDialogue(Stoner stoner) {
    this.stoner = stoner;
  }

  public void handlePet() {
    PetData petDrop = PetData.forItem(13225);

    if (petDrop != null) {
      if (stoner.getActivePets().size() < 5) {
		  PetManager.spawnPet(stoner, petDrop.getItem(), true);
        stoner.send(
            new SendMessage(
                "You feel a pressence following you; "
                    + Utility.formatStonerName(
                        GameDefinitionLoader.getNpcDefinition(petDrop.getNPC()).getName())
                    + " starts to follow you."));
      } else {
        stoner.getBank().depositFromNoting(petDrop.getItem(), 1, 0, false);
        stoner.send(new SendMessage("You feel a pressence added to your bank."));
      }
    }
  }

  @Override
  public boolean clickButton(int id) {
    return false;
  }

  @Override
  public void execute() {
	  if (next == 0)
	  {
		  if (!stoner.getBox().hasItemId(6570))
		  {
			  DialogueManager.sendItem1(stoner, "You don't have a Firecape to do this!", 6570);
			  setNext(2);
			  return;
		  }
		  stoner.getBox().remove(6570, 1);
		  if (Utility.random(200) == 0)
		  {
			  handlePet();
		  }
		  else
		  {
			  stoner.send(new SendMessage("@red@You have sacrificed a Fire cape... Nothing happens."));
		  }
	  }
  }
}
