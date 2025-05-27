package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.OptionDialogue;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendEnterString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class ShopExchangeDialogue2 extends Dialogue {

  public ShopExchangeDialogue2(Stoner stoner) {
    this.stoner = stoner;
  }

  @Override
  public boolean clickButton(int button) {
    switch (button) {
      case DialogueConstants.OPTIONS_3_1:
        stoner.getShopping().open(stoner);
        break;
      case DialogueConstants.OPTIONS_3_2:
        if (stoner.getCredits() < 0) {
          DialogueManager.sendStatement(stoner, "You do not have enough cannacredits to do this!");
          return false;
        }
        stoner.setEnterXInterfaceId(55776);
        stoner.getClient().queueOutgoingPacket(new SendEnterString());
        break;
      case DialogueConstants.OPTIONS_3_3:
        if (stoner.getCredits() < 0) {
          DialogueManager.sendStatement(stoner, "You do not have enough cannacredits to do this!");
          return false;
        }
        stoner.start(
            new OptionDialogue(
                "Red",
                p -> {
                  stoner.setShopColor("@red@");
                  stoner.setCredits(stoner.getCredits());
                  stoner.send(new SendRemoveInterfaces());
                  DialogueManager.sendInformationBox(
                      stoner,
                      "Stoner Owned Shops Exchange",
                      "You have successfully changed your shop color.",
                      "It's now Red",
                      "",
                      "");
                },
                "Blue",
                p -> {
                  stoner.setShopColor("@blu@");
                  stoner.setCredits(stoner.getCredits());
                  stoner.send(new SendRemoveInterfaces());
                  DialogueManager.sendInformationBox(
                      stoner,
                      "Stoner Owned Shops Exchange",
                      "You have successfully changed your shop color.",
                      "It's now Blue",
                      "",
                      "");
                },
                "Green",
                p -> {
                  stoner.setShopColor("@gre@");
                  stoner.setCredits(stoner.getCredits());
                  stoner.send(new SendRemoveInterfaces());
                  DialogueManager.sendInformationBox(
                      stoner,
                      "Stoner Owned Shops Exchange",
                      "You have successfully changed your shop color.",
                      "It's now Green",
                      "",
                      "");
                },
                "Cyan",
                p -> {
                  stoner.setShopColor("@cya@");
                  stoner.setCredits(stoner.getCredits());
                  stoner.send(new SendRemoveInterfaces());
                  DialogueManager.sendInformationBox(
                      stoner,
                      "Stoner Owned Shops Exchange",
                      "You have successfully changed your shop color.",
                      "It's now Cyan",
                      "",
                      "");
                },
                "Default",
                p -> {
                  stoner.setShopColor("</col>");
                  stoner.setCredits(stoner.getCredits());
                  stoner.send(new SendRemoveInterfaces());
                  DialogueManager.sendInformationBox(
                      stoner,
                      "Stoner Owned Shops Exchange",
                      "You have successfully changed your shop color.",
                      "It's now Default	",
                      "",
                      "");
                }));
        break;
    }

    return false;
  }

  @Override
  public void execute() {
    switch (next) {
      case 0:
        DialogueManager.sendOption(stoner, "Edit shop", "Edit shop motto", "Edit shop color");
        break;
    }
  }
}
