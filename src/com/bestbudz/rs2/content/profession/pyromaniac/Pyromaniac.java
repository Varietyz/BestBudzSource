package com.bestbudz.rs2.content.profession.pyromaniac;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.HashMap;
import java.util.Map;

public class Pyromaniac {

  public static final String USING_ON_FIRE_KEY = "woodonfirekey";
  public static final int BURNING_ANIMATION = 4975;

  public static boolean burnin(Stoner stoner, int id, int experience, int time) {
    Wood wood = Wood.forId(id);

    if (wood == null) {
      return false;
    }

    if (!stoner.getEquipment().isWearingItem(6575)) {
      DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to do this!", 6575);
      return false;
    }

    if (System.currentTimeMillis() - stoner.getCurrentStunDelay() < stoner.getSetStunDelay()) {
      return false;
    }

    if (stoner.getProfession().locked()) {
      return true;
    }

    stoner.freeze(5, 5);
    stoner.setCurrentStunDelay(System.currentTimeMillis() + wood.getStunTime() * 1000L);
    stoner.getProfession().lock(3);
    stoner.getUpdateFlags().sendAnimation(new Animation(4975));
    stoner.getUpdateFlags().sendGraphic(new Graphic(831));
    stoner.getBox().remove(wood.getId(), 1);
    stoner.getBox().add(995, 1000);
    stoner.getProfession().addExperience(11, wood.experience);
    AchievementHandler.activateAchievement(stoner, AchievementList.BURN_1500_WOOD, 1);
    AchievementHandler.activateAchievement(stoner, AchievementList.BURN_12500_WOOD, 1);

    return true;
  }

  public static void finishOnAltar(Stoner stoner, int amount) {
    if (stoner.getAttributes().get("woodonfirekey") == null) {
      return;
    }

    int item = stoner.getAttributes().getInt("woodonfirekey");

    Wood wood = Wood.forId(item);

    if (wood == null) {
      return;
    }

    int invAmount = stoner.getBox().getItemAmount(item);

    if (invAmount == 0) return;
    if (invAmount < amount) {
      amount = invAmount;
    }

    stoner.getProfession().lock(5);

    stoner.getUpdateFlags().sendAnimation(new Animation(4975));
    stoner.getUpdateFlags().sendGraphic(new Graphic(831));
    stoner.getBox().remove(new Item(item, amount));
    stoner.getProfession().addExperience(11, (wood.experience) * amount);
    stoner.getBox().add(995, 100);
    AchievementHandler.activateAchievement(stoner, AchievementList.BURN_1500_WOOD, 1);
    AchievementHandler.activateAchievement(stoner, AchievementList.BURN_12500_WOOD, 1);
  }

  public static boolean useWoodOnAltar(Stoner p, int item, int object) {
    if (object == 5249) {
      Wood wood = Wood.forId(item);

      if (wood == null) {
        return false;
      }
      if (!p.getEquipment().isWearingItem(6575)) {
        DialogueManager.sendItem1(p, "You must be wearing a tool ring to do this!", 6575);
        return false;
      }

      TaskQueue.queue(
          new Task(
              p, 3, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.WOOD_ON_FIRE) {

            @Override
            public void execute() {
              if (!p.getBox().hasItemId(item)) {
                stop();
                return;
              }

              if (p.getProfession().locked()) {
                return;
              }

              p.getProfession().lock(5);

              p.getUpdateFlags().sendAnimation(new Animation(2286));
              p.getBox().remove(item);
              p.getBox().add(995, 100);
              p.getProfession().addExperience(11, wood.experience / 4.0);
            }

            @Override
            public void onStop() {}
          });

      return true;
    }

    return false;
  }

  public enum Wood {
    NORMAL_LOG(1511, 140.0D, 5),
    ACHEY_LOG(2862, 140.0D, 5),
    OAK_LOG(1521, 160.0D, 5),
    WILLOW_LOG(1519, 190.0D, 5),
    TEAK_LOG(6333, 1105.0D, 5),
    ARCTIC_PINE_LOG(10810, 1125.0D, 5),
    MAPLE_LOG(1517, 1135.0D, 5),
    MOHOGANY_LOG(6332, 1157.5D, 5),
    EUCALYPTUS_LOG(12581, 1193.5D, 5),
    YEW_LOG(1515, 1202.5D, 5),
    MAGE_LOG(1513, 1460.5D, 5);

    private static final Map<Integer, Wood> wood = new HashMap<Integer, Wood>();
    private final int id;
    private final double experience;
    int stunTime;

    Wood(int id, double experience, int time) {
      this.id = id;
      this.experience = experience;
      stunTime = time;
    }

    public static final void declare() {
      for (Wood w : values()) wood.put(Integer.valueOf(w.getId()), w);
    }

    public static Wood forId(int id) {
      return wood.get(Integer.valueOf(id));
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
