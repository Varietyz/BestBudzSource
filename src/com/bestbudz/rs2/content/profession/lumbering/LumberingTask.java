package com.bestbudz.rs2.content.profession.lumbering;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.object.GameObject;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSound;

public class LumberingTask extends Task {

  private static final int[] AXES = {6575};
  private final Stoner stoner;
  private final GameObject object;
  private final LumberingAxeData axe;
  private final LumberingTreeData tree;
  private final int treeId;
  private final int[] NORMAL_TREES = {11684, 11685};
  private int animationCycle;
  private int pos;

  public LumberingTask(
      Stoner stoner, int treeId, LumberingTreeData tree, GameObject object, LumberingAxeData axe) {
    super(
        stoner,
        1,
        false,
        Task.StackType.NEVER_STACK,
        Task.BreakType.ON_MOVE,
        TaskIdentifier.CURRENT_ACTION);
    this.stoner = stoner;
    this.object = object;
    this.tree = tree;
    this.axe = axe;
    this.treeId = treeId;
  }

  public static void attemptLumbering(Stoner stoner, int objectId, int x, int y) {
    GameObject object = new GameObject(objectId, x, y, stoner.getLocation().getZ(), 10, 0);
    LumberingTreeData tree = LumberingTreeData.forId(object.getId());
    if (tree == null) {
      return;
    }

    if (!meetsRequirements(stoner, tree, object)) {
      return;
    }

    LumberingAxeData[] axes = new LumberingAxeData[15];

    int d = 0;

    for (int s : AXES) {
      if (stoner.getEquipment().getItems()[12] != null
          && stoner.getEquipment().getItems()[12].getId() == s) {
        axes[d] = LumberingAxeData.forId(s);
        d++;
        break;
      }
    }
    if (d == 0) {
      for (Item i : stoner.getEquipment().getItems()) {
        if (i != null) {
          for (int c : AXES) {
            if (i.getId() == c) {
              axes[d] = LumberingAxeData.forId(c);
              d++;
              break;
            }
          }
        }
      }
    }

    int professionGrade = 0;
    int anyGradeAxe = 0;
    int index = -1;
    int indexb = 0;

    for (LumberingAxeData i : axes) {
      if (i != null) {
        if (meetsAxeRequirements(stoner, i)) {
          if (index == -1 || i.getGradeRequired() > professionGrade) {
            index = indexb;
            professionGrade = i.getGradeRequired();
          }
        }

        anyGradeAxe = i.getGradeRequired();
      }

      indexb++;
    }

    if (index == -1) {
      if (anyGradeAxe != 0) {
        stoner
            .getClient()
            .queueOutgoingPacket(
                new SendMessage(
                    "You need a lumbering grade of " + anyGradeAxe + " to use this axe."));
      } else {
        DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to do this!", 6575);
      }
      return;
    }

    LumberingAxeData axe = axes[index];

    stoner
        .getClient()
        .queueOutgoingPacket(
            new SendMessage("You generated an axe and started chopping the tree."));
    stoner.getUpdateFlags().sendAnimation(axe.getAnimation());
    stoner.getUpdateFlags().sendFaceToDirection(object.getLocation());
    TaskQueue.queue(new LumberingTask(stoner, objectId, tree, object, axe));
  }

  private static boolean meetsAxeRequirements(Stoner stoner, LumberingAxeData data) {
    if (data == null) {
      DialogueManager.sendItem1(stoner, "You must be wearing a tool ring to do this!", 6575);
      return false;
    }
    return stoner.getProfession().getGrades()[8] >= data.getGradeRequired();
  }

  private static boolean meetsRequirements(
      Stoner stoner, LumberingTreeData data, GameObject object) {
    if (stoner.getProfession().getGrades()[Professions.LUMBERING] < data.getGradeRequired()) {
      stoner
          .getClient()
          .queueOutgoingPacket(
              new SendMessage(
                  "You need a lumbering grade of "
                      + data.getGradeRequired()
                      + " to cut this tree."));
      return false;
    }
    if (!Region.objectExists(
        object.getId(),
        object.getLocation().getX(),
        object.getLocation().getY(),
        object.getLocation().getZ())) {
      return false;
    }
    if (stoner.getBox().getFreeSlots() == 0) {
      stoner.getUpdateFlags().sendAnimation(-1, 0);
      stoner
          .getClient()
          .queueOutgoingPacket(new SendMessage("You don't have enough box space to cut this."));
      return false;
    }
    return true;
  }

  private void animate() {
    stoner.getClient().queueOutgoingPacket(new SendSound(472, 0, 0));

    if (++animationCycle == 1) {
      stoner.getUpdateFlags().sendAnimation(axe.getAnimation());
      animationCycle = 0;
    }
  }

  @Override
  public void execute() {
    if (!meetsRequirements(stoner, tree, object)) {
      stop();
      return;
    }

    if (pos == 3) {
      if ((successfulAttemptChance()) && (handleTreeChopping())) {
        stop();
        return;
      }

      pos = 0;
    } else {
      pos += 1;
    }

    animate();
  }

  private void handleGivingLogs() {
    stoner.getBox().add(new Item(tree.getReward(), 1));
    stoner.getProfession().addExperience(Professions.LUMBERING, tree.getExperience());
    AchievementHandler.activateAchievement(stoner, AchievementList.CHOP_1000_WOOD, 1);
    AchievementHandler.activateAchievement(stoner, AchievementList.CHOP_4000_WOOD, 1);
  }

  private boolean handleTreeChopping() {
    if (isNormalTree()) {
      successfulAttempt();
      return true;
    }

    if (Utility.randomNumber(420 + 4200) == 1) {
      successfulAttempt();
      return true;
    }

    handleGivingLogs();

    return false;
  }

  private boolean isNormalTree() {
    for (int i : NORMAL_TREES) {
      if (i == treeId) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void onStop() {}

  private void successfulAttempt() {
    stoner.getClient().queueOutgoingPacket(new SendSound(1312, 5, 0));
    stoner
        .getClient()
        .queueOutgoingPacket(new SendMessage("Your arm grew tired of wacking the tree."));
    stoner.getBox().add(new Item(tree.getReward(), 1));
    stoner.getProfession().addExperience(Professions.LUMBERING, tree.getExperience());
    stoner.getUpdateFlags().sendAnimation(new Animation(65535));
  }

  private boolean successfulAttemptChance() {
    return Professions.isSuccess(
        stoner, Professions.LUMBERING, tree.getGradeRequired(), axe.getGradeRequired());
  }
}
