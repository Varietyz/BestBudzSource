package com.bestbudz.rs2.content.dialogue.impl.teleport;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.mage.MageProfession;
import com.bestbudz.rs2.content.profession.mage.MageProfession.TeleportTypes;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class WildernessLever extends Dialogue {

  public WildernessLever(Stoner stoner) {
    this.stoner = stoner;
  }

  @Override
  public boolean clickButton(int id) {
    if (!stoner.getStoner().getMage().canTeleport(TeleportTypes.SPELL_BOOK)) {
      stoner.getDialogue().end();
      return false;
    }
    switch (id) {
      case 9178:
        getStoner().getMage().teleport(3153, 3923, 0, MageProfession.TeleportTypes.SPELL_BOOK);
        stoner.getDialogue().end();
        break;
      case 9179:
        getStoner().getMage().teleport(3158, 3670, 0, MageProfession.TeleportTypes.SPELL_BOOK);
        stoner.getDialogue().end();
        break;
      case 9180:
        getStoner().getMage().teleport(3361, 3687, 0, MageProfession.TeleportTypes.SPELL_BOOK);
        stoner.getDialogue().end();
        break;
      case 9181:
        getStoner().getMage().teleport(3091, 3476, 0, MageProfession.TeleportTypes.SPELL_BOOK);
        stoner.getDialogue().end();
        break;
    }
    return false;
  }

  @Override
  public void execute() {
    DialogueManager.sendOption(
        stoner, "Deserted Keep", "Graveyard", "East Dragons", "West Dragons");
  }
}
