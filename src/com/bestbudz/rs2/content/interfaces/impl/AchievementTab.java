package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementHandler.AchievementDifficulty;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class AchievementTab extends InterfaceHandler {

  private final String[] text = new String[AchievementList.values().length + 8];

  public AchievementTab(Stoner stoner) {
    super(stoner);
    int shift = 0;
    text[shift++] = "";
    text[shift++] = "@lre@ Total achievements: @gre@" + AchievementList.values().length;
    text[shift++] = "@lre@ Completed: @gre@" + stoner.getPA().achievementCompleted();
    text[shift++] = "@lre@ Points: @gre@" + stoner.getAchievementsPoints();
    text[shift++] = "";
    AchievementDifficulty last = null;
    for (AchievementList achievement : AchievementList.values()) {
      if (last != achievement.getDifficulty()) {
        last = achievement.getDifficulty();
        text[shift++] = "[ [ [@lre@ " + Utility.capitalize(last.name().toLowerCase()) + " ] ] ]";
      }
      int completed = stoner.getStonerAchievements().get(achievement);
      if (completed > achievement.getCompleteAmount()) {
        completed = achievement.getCompleteAmount();
      }

		String tag =
			completed == achievement.getCompleteAmount()
				? "@gre@" // green = complete
				: completed > 0 ? "@yel@" // yellow = in progress
				: "@red@"; // red = not started

		text[shift++] = tag + " " + achievement.getName();

	}
  }

  @Override
  protected String[] text() {
    return text;
  }

  @Override
  protected int startingLine() {
    return 31006;
  }
}
