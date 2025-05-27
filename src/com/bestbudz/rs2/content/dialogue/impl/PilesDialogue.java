package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class PilesDialogue extends Dialogue {

  public int[][] ITEMS = {
    {451, 452},
    {11934, 11935},
    {440, 441},
    {453, 454},
    {444, 445},
    {447, 448},
    {449, 450},
    {1515, 1516},
    {1513, 1514}
  };

  public PilesDialogue(Stoner stoner) {
    this.stoner = stoner;
  }

  @Override
  public boolean clickButton(int id) {
    switch (id) {
      case DialogueConstants.OPTIONS_2_1:
        stoner.send(new SendRemoveInterfaces());

        for (int i = 0; i < ITEMS.length; i++) {
          if (stoner.getBox().hasItemId(new Item(ITEMS[i][0]))) {
            int amount = stoner.getBox().getItemAmount(ITEMS[i][0]);
            int payment = stoner.getBox().getItemAmount(ITEMS[i][0]) * 50;

            if (!stoner.getBox().hasItemId(new Item(995, payment))) {
              DialogueManager.sendStatement(
                  stoner,
                  Utility.format(payment)
                      + " bestbucks is required to do this; which you do not have!");
              break;
            }
            stoner.getBox().remove(new Item(ITEMS[i][0], amount));
            stoner.getBox().add(new Item(ITEMS[i][1], amount));
            DialogueManager.sendInformationBox(
                stoner,
                "Piles",
                "You have noted:",
                "@blu@" + amount + " </col>items",
                "You have paid:",
                "@blu@" + Utility.format(payment) + " </col>bestbucks");
            break;
          } else {
            DialogueManager.sendStatement(
                stoner, "You do not contain any items that are allowed to be noted!");
          }
        }

        break;

      case DialogueConstants.OPTIONS_2_2:
        stoner.send(new SendRemoveInterfaces());
        break;
    }
    return false;
  }

  @Override
  public void execute() {
    switch (next) {
      case 0:
        DialogueManager.sendNpcChat(
            stoner,
            13,
            Emotion.HAPPY,
            "I can note any items obtained from the resource",
            "arena for 50 bestbucks an item.");
        next++;
        break;
      case 1:
        DialogueManager.sendOption(stoner, "Note items", "Nevermind");
        break;
    }
  }
}
