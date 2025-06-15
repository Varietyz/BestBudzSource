package com.bestbudz.rs2.content;

import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class GenieReset {

  public static boolean handle(Stoner stoner, int buttonId) {
    GenieData genie = GenieData.forId(buttonId);
    if (genie == null) {
      return false;
    }
    stoner.send(new SendRemoveInterfaces());
    if (genie.getProfessionId() == 3) {
      stoner.getGrades()[genie.getProfessionId()] = (3);
      stoner.getMaxGrades()[genie.getProfessionId()] = (3);
      stoner.getProfession().getExperience()[genie.getProfessionId()] =
          stoner.getProfession().getXPForGrade(genie.getProfessionId(), 3);
      stoner.getProfession().update(genie.getProfessionId());
    } else {
      stoner.getGrades()[genie.getProfessionId()] = (1);
      stoner.getMaxGrades()[genie.getProfessionId()] = (1);
      stoner.getProfession().getExperience()[genie.getProfessionId()] =
          stoner.getProfession().getXPForGrade(genie.getProfessionId(), 1);
      stoner.getProfession().update(genie.getProfessionId());
    }
    stoner.getProfession().update();
    DialogueManager.sendNpcChat(
        stoner,
        326,
        Emotion.HAPPY,
        "You have successfully reset your "
            + genie.getProfessionName()
            + " to "
            + stoner.getProfession().getGrades()[genie.getProfessionId()]
            + "!");
    stoner.getDialogue().setNext(-1);
    AchievementHandler.activateAchievement(stoner, AchievementList.RESET_5_STATISTICS, 1);
    stoner.send(new SendMessage("Yes"));
    return true;
  }

  public enum GenieData {
    ASSAULT(232114, "Assault", 0),
    VIGOUR(232117, "Vigour", 2),
    AEGIS(232120, "Aegis", 1),
    RANGE(232123, "Sagittarius", 4),
    MAGE(232126, "Mage", 6),
    RESONANCE(232129, "Resonance", 5),
    LIFE(232132, "Life", 3);

    private final int buttonId;
    private final int professionId;
    private final String professionName;

    GenieData(int buttonId, String professionName, int professionId) {
      this.buttonId = buttonId;
      this.professionName = professionName;
      this.professionId = professionId;
    }

    public static GenieData forId(int buttonId) {
      for (GenieData data : GenieData.values()) if (data.buttonId == buttonId) return data;
      return null;
    }

    public int getButton() {
      return buttonId;
    }

    public String getProfessionName() {
      return professionName;
    }

    public int getProfessionId() {
      return professionId;
    }
  }
}
