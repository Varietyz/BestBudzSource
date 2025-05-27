package com.bestbudz.rs2.content.cluescroll;

import com.bestbudz.rs2.content.cluescroll.Clue.ClueType;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public interface ClueScroll {

  boolean execute(Stoner stoner);

  boolean meetsRequirements(Stoner stoner);

  boolean inEndArea(Location location);

  Clue getClue();

  int getScrollId();

  ClueDifficulty getDifficulty();

  default void displayClue(Stoner stoner) {
    getClue().display(stoner);
  }

  default ClueType getClueType() {
    return getClue().getClueType();
  }

  default void reward(Stoner stoner, String acquirement) {
    Item item;
    boolean nextClue = false;

    switch (getDifficulty()) {
      case EASY:
        nextClue = stoner.getCluesCompleted()[0] < 5 && Math.random() > 0.5;

        if (nextClue) {
          stoner.setCluesCompleted(0, stoner.getCluesCompleted()[0] + 1);
        } else {
          stoner.setCluesCompleted(0, 0);
        }
        break;

      case MEDIUM:
        nextClue = stoner.getCluesCompleted()[1] < 6 && Math.random() > 0.5;

        if (nextClue) {
          stoner.setCluesCompleted(1, stoner.getCluesCompleted()[1] + 1);
        } else {
          stoner.setCluesCompleted(1, 0);
        }
        break;

      case HARD:
        nextClue = stoner.getCluesCompleted()[2] < 8 && Math.random() > 0.5;

        if (nextClue) {
          stoner.setCluesCompleted(2, stoner.getCluesCompleted()[2] + 1);
        } else {
          stoner.setCluesCompleted(2, 0);
        }
        break;
    }

    if (!nextClue) {
      DialogueManager.sendItem1(stoner, acquirement + " a casket!", ClueScrollManager.CASKET_HARD);
      stoner.send(new SendMessage("Well done, you've completed the Treasure Trail!"));
      item =
          new Item(
              getDifficulty().equals(ClueDifficulty.EASY)
                  ? ClueScrollManager.CASKET_EASY
                  : getDifficulty().equals(ClueDifficulty.MEDIUM)
                      ? ClueScrollManager.CASKET_MEDIUM
                      : ClueScrollManager.CASKET_HARD);
    } else {
      item = ClueScrollManager.getRandomClue(stoner, getDifficulty());
      if (item == null) {
        DialogueManager.sendItem1(
            stoner, acquirement + " a casket!", ClueScrollManager.CASKET_HARD);
        stoner.send(new SendMessage("Well done, you've completed the Treasure Trail!"));
        item =
            new Item(
                getDifficulty().equals(ClueDifficulty.EASY)
                    ? ClueScrollManager.CASKET_EASY
                    : getDifficulty().equals(ClueDifficulty.MEDIUM)
                        ? ClueScrollManager.CASKET_MEDIUM
                        : ClueScrollManager.CASKET_HARD);
      } else {
        DialogueManager.sendItem1(stoner, acquirement + " another clue!", item.getId());
      }
    }

    stoner.getBox().remove(getScrollId());
    stoner.getBox().add(item);
  }
}
