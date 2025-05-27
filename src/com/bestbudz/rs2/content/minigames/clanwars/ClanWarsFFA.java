package com.bestbudz.rs2.content.minigames.clanwars;

import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.mage.MageProfession.TeleportTypes;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;

public class ClanWarsFFA {

  public static boolean clickObject(Stoner stoner, int object) {
    switch (object) {
      case 26645:
        enterGame(stoner);
        break;

      case 26646:
        leaveGame(stoner);
        break;
    }
    return false;
  }

  public static void enterGame(Stoner stoner) {
    if (!stoner.getActivePets().isEmpty()) {
      DialogueManager.sendStatement(stoner, "You can't bring a pet into this game!");
      return;
    }
    stoner.getMage().teleport(ClanWarsConstants.FFA_PORTAL, TeleportTypes.SPELL_BOOK);
    stoner.setController(ControllerManager.CLAN_WARS_FFA_CONTROLLER);
  }

  public static void leaveGame(Stoner stoner) {
    stoner.setController(ControllerManager.DEFAULT_CONTROLLER);
    stoner.getMage().teleport(new Location(3352, 3164, 0), TeleportTypes.SPELL_BOOK);
  }
}
