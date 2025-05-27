package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class AchievementDialogue extends Dialogue {

  public AchievementDialogue(Stoner stoner) {
    this.stoner = stoner;
  }

  @Override
  public boolean clickButton(int id) {
    switch (id) {
      case DialogueConstants.OPTIONS_3_1:
        boolean completed = true;
        for (AchievementList achievement : stoner.getStonerAchievements().keySet()) {
          if (achievement != null
              && stoner.getStonerAchievements().get(achievement)
                  != achievement.getCompleteAmount()) {
            completed = false;
            break;
          }
        }

        if (completed) {
          stoner.getBox().addOrCreateGroundItem(13069, 1, true);
          stoner.getBox().addOrCreateGroundItem(13070, 1, true);
          stoner.send(new SendMessage("You have been given an achievement cape and hood."));
        } else {
          stoner.send(new SendMessage("You have not completed all the achievements!"));
        }
        stoner.send(new SendRemoveInterfaces());

        break;
      case DialogueConstants.OPTIONS_3_2:
        if (StonerConstants.isDeveloper(stoner)) {
          stoner.getShopping().open(89);
        } else {
          DialogueManager.sendStatement(stoner, "Coming soon!");
        }
        break;
      case DialogueConstants.OPTIONS_3_3:
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
            5527,
            Emotion.HAPPY,
            "Hello " + stoner.getUsername() + ".",
            "How may I help you?");
        next++;
        break;
      case 1:
        DialogueManager.sendStonerChat(stoner, Emotion.CALM, "What are you doing here?");
        next++;
        break;
      case 2:
        DialogueManager.sendNpcChat(
            stoner, 5527, Emotion.HAPPY, "I'm looking for the very best of course!");
        next++;
        break;
      case 3:
        DialogueManager.sendStonerChat(stoner, Emotion.CALM, "The very best?");
        next++;
        break;
      case 4:
        DialogueManager.sendNpcChat(
            stoner,
            5527,
            Emotion.HAPPY,
            "Yes!",
            "Stoners that have completed all the achievements,",
            "will be rewarded with my cape.");
        next++;
        break;
      case 5:
        DialogueManager.sendOption(stoner, "Obtain cape", "Trade", "Nothing.");
        break;
    }
  }
}
