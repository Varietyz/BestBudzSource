package com.bestbudz.rs2.content;

import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.io.sqlite.SaveCache;
import com.bestbudz.rs2.content.io.sqlite.SaveWorker;
import com.bestbudz.rs2.content.membership.AdvancementBonds;
import com.bestbudz.rs2.content.membership.RankHandler;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.HashMap;

public class Advance {

  private static final String ADVANCE_COLOR = "<col=CC0066>";

  private static final int MAX_ADVANCES = 5;
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

  public static boolean handleActionButtons(Stoner stoner, int buttonId) {
    AdvanceData data = AdvanceData.advance.get(buttonId);

    if (data == null) {
      return false;
    }

    if (stoner.getInterfaceManager().main != 51000) {
      stoner.send(new SendRemoveInterfaces());
      stoner.send(new SendMessage("That interface does not exist!"));
      return false;
    }

    if (advanceProfession(stoner, data.getProfession())) {
		SaveCache.markDirty(stoner);
      stoner.getBox().add(new Item(995, data.getMoney()));
    }
    return true;
  }

  public static boolean canAdvance(Stoner stoner, int professionId) {
    if (stoner.getMaxGrades()[professionId] < 420) {
      stoner.send(new SendMessage(getProfessionName(professionId) + " is not grade 420 yet!"));
      return false;
    }
    if (stoner.getProfessionAdvances()[professionId] >= MAX_ADVANCES) {
      stoner.send(
          new SendMessage("You reached advance " + MAX_ADVANCES + " already, grind to 1B exp."));
      return false;
    }
    /* existing inventory test becomes milestone-aware */
    if (stoner.getBox().getFreeSlots() < 1) {
      int nextTotal = stoner.getTotalAdvances() + 1;
      for (AdvancementBonds.BondData b : AdvancementBonds.BondData.values())
        if (nextTotal >= b.advancementGrades // will cross a threshold
            && stoner.getRights() < 5 + b.ordinal()) {
          stoner.send(
              new SendMessage(
                  "Leave one free box slot before advancing – " + "a " + b.getName() + " awaits!"));
          return false;
        }
    }

    return true;
  }

  public static boolean advanceProfession(Stoner stoner, int professionId) {
    if (!canAdvance(stoner, professionId)) {
      /* give milestone bond (also upgrades rights) */
      AdvancementBonds.BondData given = AdvancementBonds.rewardIfThresholdReached(stoner);
      if (given != null) {
        RankHandler.upgrade(stoner); // sets rights to 5-10 as appropriate
      }

      return false;
    }

    if (professionId == 3) {
      stoner.getGrades()[professionId] = (3);
      stoner.getMaxGrades()[professionId] = (3);
      stoner.getProfession().getExperience()[professionId] =
          stoner.getProfession().getXPForGrade(professionId, 3);
      stoner.getProfession().update(professionId);
    } else {
      stoner.getGrades()[professionId] = (1);
      stoner.getMaxGrades()[professionId] = (1);
      stoner.getProfession().getExperience()[professionId] =
          stoner.getProfession().getXPForGrade(professionId, 1);
      stoner.getProfession().update(professionId);
    }

    stoner.getProfessionAdvances()[professionId] += 1;
    stoner.setTotalAdvances(stoner.getTotalAdvances() + 1);
    stoner.setAdvancePoints(stoner.getAdvancePoints() + 1);
    stoner.send(
        new SendMessage(
            "Advanced "
                + ADVANCE_COLOR
                + getProfessionName(professionId)
                + " to "
                + stoner.getProfessionAdvances()[professionId]
                + "</col>!"));
    World.sendGlobalMessage(
        "<img=8> "
            + ADVANCE_COLOR
            + stoner.getUsername()
            + " </col>advanced "
            + ADVANCE_COLOR
            + getProfessionName(professionId)
            + "</col>  to "
            + ADVANCE_COLOR
            + stoner.getProfessionAdvances()[professionId]
            + "</col>, SMOKE SESH!");
    AchievementHandler.activateAchievement(stoner, AchievementList.ADVANCE_105_TIMES, 1);
    stoner.getProfession().restore();
    update(stoner);
    return true;
  }

  public static void update(Stoner stoner) {
    stoner.send(
        new SendString(
            "@gre@" + stoner.deterquarryIcon(stoner) + "  " + stoner.getUsername(), 51007));
    stoner.send(new SendString("</col>Total Advances: @gre@" + stoner.getTotalAdvances(), 51008));
    stoner.send(new SendString("</col>Advance Points: @gre@" + stoner.getAdvancePoints(), 51009));
    for (int i = 0; i < stoner.getProfessionAdvances().length; i++) {
      AdvanceData data = AdvanceData.forProfession(i);
      if (data == null) {
        continue;
      }
    }
  }

  public static String getProfessionName(int i) {
    return professionName[i];
  }

  public static int professionTierColor(Stoner stoner, int professionId) {
    switch (stoner.getProfessionAdvances()[professionId]) {
      case 1:
        return 0xE100FF;
      case 2:
        return 0xFF6A00;
      case 3:
        return 0x11BF0B;
      case 4:
        return 0x0D96D1;
      case 5:
        return 0xED0909;
      default:
        return 0x070707;
    }
  }

  public enum AdvanceData {
    ASSAULT(199088, " ", 0, 51010, 6_000_000),
    AEGIS(199094, " ", 1, 51012, 6_000_000),
    VIGOUR(199091, " ", 2, 51011, 6_000_000),
    LIFE(199109, " ", 3, 51017, 6_000_000),
    SAGITTARIUS(199097, " ", 4, 51013, 6_000_000),
    NECROMANCE(199100, " ", 5, 51014, 6_000_000),
    MAGE(199103, " ", 6, 51015, 6_000_000),
    FOODIE(199139, " ", 7, 51027, 10_500_000),
    LUMBERING(199145, " ", 8, 51029, 10_500_000),
    WOODCARVING(199124, " ", 9, 51022, 10_500_000),
    FISHER(199136, " ", 10, 51026, 10_500_000),
    PYROMANIAC(199142, " ", 11, 51028, 10_500_000),
    HANDINESS(199121, " ", 12, 51021, 10_500_000),
    FORGING(199133, " ", 13, 51025, 10_500_000),
    QUARRYING(199130, " ", 14, 51024, 10_500_000),
    THCHEMPISTRY(199115, " ", 15, 51019, 20_500_000),
    WEEDSMOKING(199112, " ", 16, 51018, 50_000_000),
    ACCOMPLISHER(199118, " ", 17, 51020, 10_500_000),
    MERCENARY(199127, " ", 18, 51023, 10_500_000),
    CULTIVATION(199148, " ", 19, 51030, 20_500_000),
    CONSUMER(199106, " ", 20, 51016, 10_500_000);

    public static HashMap<Integer, AdvanceData> advance = new HashMap<Integer, AdvanceData>();

    static {
      for (final AdvanceData advance : AdvanceData.values()) {
        AdvanceData.advance.put(advance.buttonId, advance);
      }
    }

    String name;
    int buttonId, profession, frame, money;

    AdvanceData(int buttonId, String name, int profession, int frame, int money) {
      this.buttonId = buttonId;
      this.name = name;
      this.profession = profession;
      this.frame = frame;
      this.money = money;
    }

    public static AdvanceData forProfession(int id) {
      for (AdvanceData data : AdvanceData.values()) if (data.profession == id) return data;
      return null;
    }

    public String getName() {
      return name;
    }

    public int getButton() {
      return buttonId;
    }

    public int getProfession() {
      return profession;
    }

    public int getFrame() {
      return frame;
    }

    public int getMoney() {
      return money;
    }
  }
}
