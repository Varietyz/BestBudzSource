package com.bestbudz.rs2.content.membership;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.CreditTab;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBanner;

public class RankHandler {

  public static void upgrade(Stoner stoner) {

    if (StonerConstants.isStaff(stoner)) {
      DialogueManager.sendStatement(stoner, "You are worth more then a progression rank!.");
      return;
    }

    int rights = 0;
    if (stoner.getTotalAdvances() < 5) rights = 0;
    if (stoner.getTotalAdvances() >= 10) rights = 5;
    if (stoner.getTotalAdvances() >= 20) rights = 6;
    if (stoner.getTotalAdvances() >= 35) rights = 7;
    if (stoner.getTotalAdvances() >= 55) rights = 8;
    if (stoner.getTotalAdvances() >= 80) rights = 9;
    if (stoner.getTotalAdvances() >= 105) rights = 10;

    if (rights != 0 && stoner.getRights() != rights) {
      stoner.setRights(rights);
      stoner.getUpdateFlags().setUpdateRequired(true);
      InterfaceHandler.writeText(new QuestTab(stoner));
      InterfaceHandler.writeText(new CreditTab(stoner));
      stoner.send(
          new SendBanner(
              "You are now ranked as a "
                  + Utility.getAOrAn(stoner.deterquarryRank(stoner))
                  + " "
                  + stoner.deterquarryIcon(stoner)
                  + " "
                  + stoner.deterquarryRank(stoner)
                  + "!",
              0x1C889E));
      DialogueManager.sendStatement(
          stoner,
          "You are now ranked as a "
              + Utility.getAOrAn(stoner.deterquarryRank(stoner))
              + " "
              + stoner.deterquarryIcon(stoner)
              + " "
              + stoner.deterquarryRank(stoner)
              + "!");
    }
  }
}
