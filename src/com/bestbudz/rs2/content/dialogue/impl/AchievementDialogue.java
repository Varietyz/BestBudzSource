package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class AchievementDialogue extends Dialogue {

  public AchievementDialogue(Stoner stoner) {
    this.stoner = stoner;
  }

  @Override
  public boolean clickButton(int id) {
    return false;
  }

  @Override
  public void execute() {
	  if (next == 0)
	  {
		  boolean completed = true;
		  for (AchievementList achievement : stoner.getStonerAchievements().keySet())
		  {
			  if (achievement != null
				  && stoner.getStonerAchievements().get(achievement)
				  != achievement.getCompleteAmount())
			  {
				  completed = false;
				  break;
			  }
		  }

		  if (completed)
		  {
			  stoner.getBox().addOrCreateGroundItem(13069, 1, true);
			  stoner.getBox().addOrCreateGroundItem(13070, 1, true);
			  stoner.send(new SendMessage("[ACHIEVEMENTS] You have been given an achievement cape and hood."));
		  }
		  else
		  {
			  stoner.send(new SendMessage("[ACHIEVEMENTS] You have not completed all the achievements!"));
		  }
	  }
  }
}
