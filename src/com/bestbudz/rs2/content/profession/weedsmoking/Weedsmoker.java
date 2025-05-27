package com.bestbudz.rs2.content.profession.weedsmoking;

import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.HashMap;
import java.util.Map;

public class Weedsmoker {

  public static final String SMOKE_WITH_BUD_KEY = "Smokeweedwithbud";

  public static boolean burnin(Stoner stoner, int id, int experience, int time) {
    Weed weed = Weed.forId(id);

    if (weed == null) {
      return false;
    }

    if (!stoner.getEquipment().isWearingItem(6575)) {
      DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to spark the pipe!", 6575);
      return false;
    }

    if (!stoner.getEquipment().isWearingItem(1511)) {
      DialogueManager.sendItem1(stoner, "You must be wearing wood to keep the fire going!", 1511);
      return false;
    }

    if (!stoner.getBox().hasItemId(1785)) {
      DialogueManager.sendItem1(stoner, "You must have a weed pipe in your box to smoke!", 1785);
      return false;
    }

    if (System.currentTimeMillis() - stoner.getCurrentStunDelay() < stoner.getSetStunDelay()) {
      return false;
    }

    if (stoner.getProfession().locked()) {
      return true;
    }
    stoner.setCurrentStunDelay(System.currentTimeMillis() + weed.getStunTime() * 1000L);
    stoner.getUpdateFlags().sendGraphic(new Graphic(354, true));
    stoner.getUpdateFlags().sendAnimation(new Animation(884));
    stoner.getBox().remove(weed.getId(), 1);
    stoner.getBox().add(995, 100);
    stoner.getProfession().addExperience(16, weed.experience);
    AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_100G_WEED, 1);
    AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_1000G_WEED, 1);
    AchievementHandler.activateAchievement(stoner, AchievementList.SMOKE_10000G_WEED, 1);
    return false;
  }

  public enum Weed {
    KUSH(249, 520.0D, 4),
    HAZE(251, 650.0D, 4),
    OG_KUSH(253, 600.0D, 4),
    POWERPLANT(255, 520.0D, 4),
    CHEESE_HAZE(257, 810.0D, 4),
    BUBBA_KUSH(2998, 650.0D, 4),
    CHOCOLOPE(259, 760.0D, 4),
    GORILLA_GLUE(261, 915.0D, 4),
    JACK_HERER(263, 520.0D, 4),
    DURBAN_POISON(3000, 780.0D, 4),
    AMNESIA(265, 700.0D, 4),
    SUPER_SILVER_HAZE(2481, 950.0D, 4),
    GIRL_SCOUT_COOKIES(267, 1005.0D, 4),
    KHALIFA_KUSH(269, 1105.0D, 4);

    private static final Map<Integer, Weed> weed = new HashMap<Integer, Weed>();
    private final int id;
    private final double experience;
    int stunTime;

    Weed(int id, double experience, int time) {
      this.id = id;
      this.experience = experience;
      stunTime = time;
    }

    public static final void declare() {
      for (Weed w : values()) weed.put(Integer.valueOf(w.getId()), w);
    }

    public static Weed forId(int id) {
      return weed.get(Integer.valueOf(id));
    }

    public int getStunTime() {
      return stunTime;
    }

    public double getExperience() {
      return experience;
    }

    public int getId() {
      return id;
    }
  }
}
