package com.bestbudz.rs2.content;

import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.HashMap;

public class ProfessionsChat {

  public static String[] professionName = {
    "Assault",
    "Aegis",
    "Vigour",
    "Life",
    "Sagittarius",
    "Necromance",
    "Mage",
    "Foodie",
    "Lumbering",
    "Woodcarving",
    "Fisher",
    "Pyromaniac",
    "Handiness",
    "Forging",
    "Quarrying",
    "THC-hempistry",
    "Weedsmoking",
    "Accomplisher",
    "Mercenary",
    "Cultivation",
    "Consumer"
  };

  public static String getProfessionName(int i) {
    return professionName[i];
  }

  public static boolean handle(Stoner stoner, int buttonId) {
    ChatData chat = ChatData.chat.get(buttonId);
    if (chat == null) {
      return false;
    }

    if (chat.getButton() == 94144) {
      stoner
          .getUpdateFlags()
          .sendForceMessage(
              "Grades total is "
                  + (stoner.getProfession().getTotalGrade())
                  + ", Profession advances "
                  + stoner.getTotalAdvances()
                  + ".");
      return false;
    }

    stoner
        .getUpdateFlags()
        .sendForceMessage(
            getProfessionName(chat.profession)
                + " grade is "
                + stoner.getMaxGrades()[chat.profession]
                + ", advanced "
                + stoner.getProfessionAdvances()[chat.profession]
                + " times.");
    return true;
  }

  public enum ChatData {
    ASSAULT(94147, 0),
    VIGOUR(94150, 2),
    RANGE(94156, 4),
    MAGE(94162, 6),
    AEGIS(94153, 1),
    LIFE(94148, 3),
    NECROMANCE(94159, 5),
    WEEDSMOKER(94151, 16),
    THCHEMPISTRY(94154, 15),
    ACCOMPLISHER(94157, 17),
    HANDINESS(94160, 12),
    CONSUMER(94165, 20),
    MERCENARY(94166, 18),
    CULTIVATION(94167, 19),
    QUARRYING(94149, 14),
    FORGING(94152, 13),
    FISHER(94155, 10),
    FOODIE(94158, 7),
    PYROMANIAC(94161, 11),
    LUMBERING(94164, 8),
    WOODCARVING(94163, 9),
    TOTAL_GRADE(94144, 69);

    public static HashMap<Integer, ChatData> chat = new HashMap<Integer, ChatData>();

    static {
      for (final ChatData chat : ChatData.values()) {
        ChatData.chat.put(chat.button, chat);
      }
    }

    public int profession, button;

    ChatData(int button, int profession) {
      this.profession = profession;
      this.button = button;
    }

    public static ChatData forprofession(int id) {
      for (ChatData data : ChatData.values()) if (data.button == id) return data;
      return null;
    }

    public int getProfession() {
      return profession;
    }

    public int getButton() {
      return button;
    }
  }
}
