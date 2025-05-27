package com.bestbudz.rs2.content.profiles;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerProfilerIndex;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class StonerProfiler {

  public static void search(Stoner stoner, String string) {
    stoner.send(
        new SendMessage(
            "@dre@Searching Bongbase '"
                + Utility.capitalizeFirstLetter(string)
                + "' for stoner..."));
    Stoner viewing = World.getStonerByName(string);
    stoner.viewing = string;

    if (stoner == viewing && !StonerConstants.isOwner(stoner)) {
      myProfile(stoner);
      return;
    }

    if (viewing == null) {
      stoner.send(
          new SendMessage(
              "@dre@"
                  + Utility.capitalizeFirstLetter(string)
                  + " either has not arrived yet or is asleep!"));
      return;
    }

    if (viewing.getProfilePrivacy()) {
      stoner.send(
          new SendMessage(
              "@dre@"
                  + Utility.capitalizeFirstLetter(viewing.getUsername())
                  + " has disabled stoner viewing."));
      return;
    }

    if (stoner.getProfilePrivacy()) {
      stoner.send(new SendMessage("@dre@You cannot view stoners whilst your in the bushes!"));
      return;
    }

    int deltaX = viewing.getLocation().getX() - (stoner.getCurrentRegion().getRegionX() << 3);
    int deltaY = viewing.getLocation().getY() - (stoner.getCurrentRegion().getRegionY() << 3);

    if ((deltaX < 16) || (deltaX >= 88) || (deltaY < 16) || (deltaY > 88)) {
      stoner.send(
          new SendMessage("@dre@Viewing character models is disabled while not in same region."));
      displayProfile(stoner, viewing, false);
    } else {
      displayProfile(stoner, viewing, true);
    }
  }

  public static void displayProfile(Stoner stoner, Stoner viewing, boolean inRegion) {
    viewing.send(
        new SendMessage(
            "@dre@" + Utility.capitalizeFirstLetter(stoner.getUsername()) + " is viewing you!"));
    AchievementHandler.activateAchievement(stoner, AchievementList.VIEW_15_STONER_PROFILES, 1);
    viewing.setProfileViews(+1);

    if (inRegion) {
      stoner.send(new SendStonerProfilerIndex(viewing.getIndex()));
    } else {
      stoner.send(new SendStonerProfilerIndex(-1));
    }

    stoner.send(new SendString("", 51802));

    for (int i = 0; i < 20; i++) {
      stoner.send(
          new SendString(
              Utility.capitalizeFirstLetter(Professions.PROFESSION_NAMES[i])
                  + " lv: "
                  + viewing.getProfession().getGrades()[i]
                  + "/"
                  + viewing
                      .getProfession()
                      .getGradeForExperience(i, viewing.getProfession().getExperience()[i])
                  + "\\nAdvance lv: "
                  + viewing.getProfessionAdvances()[i],
              51832 + i));
    }

    stoner.send(
        new SendString(
            "</col>Stoner: @gre@" + Utility.capitalizeFirstLetter(viewing.getUsername()), 51807));
    stoner.send(
        new SendString(
            "</col>Rank: @gre@"
                + viewing.deterquarryIcon(viewing)
                + " "
                + viewing.deterquarryRank(viewing),
            51808));
    stoner.send(
        new SendString("</col>Combat: @gre@" + viewing.getProfession().getCombatGrade(), 51809));

    String[] STRINGS = {
      "",
      "",
      "",
      "</col>Views: @whi@" + viewing.getProfileViews(),
      "</col>CannaCredits: @whi@" + viewing.getCredits(),
      "</col>Achievements Completed: @whi@" + viewing.getPA().achievementCompleted(),
      "</col>Achievement Points: @whi@" + viewing.getAchievementsPoints(),
      "</col>Total Advances: @whi@" + viewing.getTotalAdvances(),
      "</col>Advance Points: @whi@" + viewing.getAdvancePoints(),
      "</col>Task: @whi@"
          + viewing.getMercenary().getTask()
          + "</col>(@whi@"
          + viewing.getMercenary().getAmount()
          + "</col>)",
      "</col>Rogue Kills: @whi@" + viewing.getRogueKills(),
      "</col>Rogue Record: @whi@" + viewing.getRogueRecord(),
      "</col>Hunter Kills: @whi@" + viewing.getHunterKills(),
      "</col>Hunter Record: @whi@" + viewing.getHunterRecord(),
      "</col>Deaths: @whi@" + viewing.getDeaths(),
      "</col>Bounty Points: @whi@" + viewing.getBountyPoints(),
      "</col>PC Points: @whi@" + viewing.getPestPoints(),
      "</col>Mercenary Points: @whi@" + viewing.getMercenaryPoints(),
      "</col>Mage Arena Points: @whi@" + viewing.getArenaPoints(),
      "</col>WG Points: @whi@" + viewing.getWeaponPoints(),
    };

    for (int i = 0; i < STRINGS.length; i++) {
      stoner.send(new SendString(STRINGS[i], 51881 + i));
    }

    stoner.send(new SendInterface(51800));
  }

  public static void myProfile(Stoner stoner) {

    stoner.send(new SendMessage("@dre@This is you."));

    stoner.send(new SendString("", 51602));

    for (int i = 0; i < 20; i++) {
      stoner.send(
          new SendString(
              Utility.capitalizeFirstLetter(Professions.PROFESSION_NAMES[i])
                  + " lv: @dre@"
                  + stoner.getProfession().getGrades()[i]
                  + "@bla@/@dre@"
                  + stoner
                      .getProfession()
                      .getGradeForExperience(i, stoner.getProfession().getExperience()[i])
                  + "\\nAdvance lv: @dre@"
                  + stoner.getProfessionAdvances()[i],
              51632 + i));
    }

    String[] STRINGS = {
      "",
      "",
      "",
      "</col>Views: @whi@" + stoner.getProfileViews(),
      "</col>CannaCredits: @whi@" + stoner.getCredits(),
      "</col>Achievements Completed: @whi@" + stoner.getPA().achievementCompleted(),
      "</col>Achievement Points: @whi@" + stoner.getAchievementsPoints(),
      "</col>Total Advances: @whi@" + stoner.getTotalAdvances(),
      "</col>Advance Points: @whi@" + stoner.getAdvancePoints(),
      "</col>Task: @whi@"
          + stoner.getMercenary().getTask()
          + "</col>(@whi@"
          + stoner.getMercenary().getAmount()
          + "</col>)",
      "</col>Rogue Kills: @whi@" + stoner.getRogueKills(),
      "</col>Rogue Record: @whi@" + stoner.getRogueRecord(),
      "</col>Hunter Kills: @whi@" + stoner.getHunterKills(),
      "</col>Hunter Record: @whi@" + stoner.getHunterRecord(),
      "</col>Deaths: @whi@" + stoner.getDeaths(),
      "</col>Bounty Points: @whi@" + stoner.getBountyPoints(),
      "</col>PC Points: @whi@" + stoner.getPestPoints(),
      "</col>Mercenary Points: @whi@" + stoner.getMercenaryPoints(),
      "</col>Mage Arena Points: @whi@" + stoner.getArenaPoints(),
      "</col>WG Points: @whi@" + stoner.getWeaponPoints(),
    };

    for (int i = 0; i < STRINGS.length; i++) {
      stoner.send(new SendString(STRINGS[i], 51681 + i));
    }

    stoner.send(
        new SendString(
            "</col>Stoner: @gre@" + Utility.capitalizeFirstLetter(stoner.getUsername()), 51607));
    stoner.send(
        new SendString(
            "</col>Rank: @gre@"
                + stoner.deterquarryIcon(stoner)
                + " "
                + stoner.deterquarryRank(stoner),
            51608));
    stoner.send(
        new SendString("</col>Combat: @gre@" + stoner.getProfession().getCombatGrade(), 51609));

    stoner.send(new SendInterface(51600));
  }
}
