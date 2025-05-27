package com.bestbudz.rs2.content.profession.necromance;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;
import java.util.HashMap;
import java.util.Map;

public class BoneBurying {

  public static final String USING_ON_ALTAR_KEY = "boneonaltarkey";
  public static final int BURYING_ANIMATION = 827;

  public static boolean bury(Stoner stoner, int id, int amount) {
    Bones bones = Bones.forId(id);

    if (bones == null) {
      return false;
    }

    if (stoner.getProfession().locked()) {
      return true;
    }

    stoner.getProfession().lock(4);
    stoner.getCombat().reset();
    stoner.getUpdateFlags().sendAnimation(2780, 0);
    stoner
        .getClient()
        .queueOutgoingPacket(
            new SendMessage(
                Item.getDefinition(bones.id).getName() + " have been fed to your pet fish."));
    stoner.getBox().remove(bones.getId(), 1);
    stoner.getProfession().addExperience(5, bones.experience);
    AchievementHandler.activateAchievement(stoner, AchievementList.BURY_150_BONES, 1);
    AchievementHandler.activateAchievement(stoner, AchievementList.BURY_1000_BONES, 1);

    return true;
  }

  public static void finishOnAltar(Stoner stoner, int amount) {
    if (stoner.getAttributes().get("boneonaltarkey") == null) {
      return;
    }

    int item = stoner.getAttributes().getInt("boneonaltarkey");

    Bones bones = Bones.forId(item);

    if (bones == null) {
      return;
    }

    int invAmount = stoner.getBox().getItemAmount(item);

    if (invAmount == 0) return;
    if (invAmount < amount) {
      amount = invAmount;
    }

    stoner.getProfession().lock(2);

    stoner
        .getClient()
        .queueOutgoingPacket(
            new SendMessage(
                "You sacrifice the " + Item.getDefinition(bones.id).getName() + " at the altar."));

    stoner.getUpdateFlags().sendAnimation(645, 5);
    stoner.getBox().remove(new Item(item, amount));
    stoner.getProfession().addExperience(5, (bones.experience * 2.0D) * amount);
    AchievementHandler.activateAchievement(stoner, AchievementList.BURY_150_BONES, 1);
    AchievementHandler.activateAchievement(stoner, AchievementList.BURY_1000_BONES, 1);
  }

  public static boolean useBonesOnAltar(Stoner p, int item, int object) {
    if (object == 2640 || object == 409) {
      Bones bones = Bones.forId(item);

      if (bones == null) {
        return false;
      }

      TaskQueue.queue(
          new Task(
              p, 3, true, StackType.NEVER_STACK, BreakType.ON_MOVE, TaskIdentifier.BONES_ON_ALTER) {
            @Override
            public void execute() {
              if (!p.getBox().hasItemId(item)) {
                stop();
                return;
              }

              if (p.getProfession().locked()) {
                return;
              }

              p.getProfession().lock(2);

              p.getClient().queueOutgoingPacket(new SendSound(442, 1, 0));
              p.getClient()
                  .queueOutgoingPacket(
                      new SendMessage(
                          "You sacrifice the "
                              + Item.getDefinition(bones.id).getName()
                              + " at the altar."));

              p.getUpdateFlags().sendAnimation(645, 5);
              p.getBox().remove(item);
              p.getProfession().addExperience(5, bones.experience * 2.0);
            }

            @Override
            public void onStop() {}
          });

      return true;
    }

    return false;
  }

  public enum Bones {
    NORMAL_BONES(526, 4.5D),
    WOLF_BONES(2859, 4.5D),
    BAT_BONES(530, 4.5D),
    BIG_BONES(532, 18.0D),
    BABYDRAGON_BONES(534, 30.0D),
    DRAGON_BONES(536, 72.0D),
    DAGG_BONES(6729, 75.0D),
    OURG_BONES(4834, 100.0D),
    LONG_BONE(10976, 100.0D),
    FISH_BONES(6904, 90.0D),
    SKELETAL_WYVERN_BONES(6812, 95.0D),
    LAVA_DRAGON_BONES(11943, 185.0D);

    private static final Map<Integer, Bones> bones = new HashMap<Integer, Bones>();
    private final int id;
    private final double experience;

    Bones(int id, double experience) {
      this.id = id;
      this.experience = experience;
    }

    public static final void declare() {
      for (Bones b : values()) bones.put(Integer.valueOf(b.getId()), b);
    }

    public static Bones forId(int id) {
      return bones.get(Integer.valueOf(id));
    }

    public int getId() {
      return id;
    }
  }
}
