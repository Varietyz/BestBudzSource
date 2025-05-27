package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.content.achievements.AchievementList;

public class StonerAssistant {

  private final Stoner stoner;

  public StonerAssistant(Stoner stoner) {
    this.stoner = stoner;
  }

  public int achievementCompleted() {
    int completed = 0;
    for (AchievementList achievement : stoner.getStonerAchievements().keySet()) {
      if (achievement != null
          && stoner.getStonerAchievements().get(achievement) == achievement.getCompleteAmount()) {
        completed++;
      }
    }
    return completed;
  }
}
