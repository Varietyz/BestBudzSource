package com.bestbudz.rs2.content.achievements;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBanner;

public class AchievementInterface {

  public static void sendInterfaceForAchievement(final Stoner stoner, AchievementList achievement) {
    String difficulty = Utility.formatStonerName(achievement.getDifficulty().name().toLowerCase());
    int completed = stoner.getStonerAchievements().get(achievement);
    int progress = (int) (completed * 100 / (double) achievement.getCompleteAmount());
    DialogueManager.sendInformationBox(
        stoner,
        "<u>Achievement System",
        "@dre@" + achievement.getName(),
        "@bla@Difficulty: @blu@" + difficulty,
        "@bla@Point(s): @blu@" + achievement.getReward(),
        "Progress: @blu@"
            + completed
            + "@bla@/@blu@"
            + achievement.getCompleteAmount()
            + " @bla@(@blu@"
            + progress
            + "%@bla@)");
  }

  public static void sendCompleteInterface(final Stoner stoner, final AchievementList achievement) {
    int color = 0;

    switch (achievement.getDifficulty()) {
      case EASY:
        color = 0x1C889E;
        break;
      case MEDIUM:
        color = 0xD9750B;
        break;
      case HARD:
        color = 0xC41414;
        break;
    }

    stoner.send(new SendBanner("Achievement complete: '" + achievement.getName() + "'", color));
  }
}
