package com.bestbudz.rs2.content.profession.cultivation;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.membership.CreditPurchase;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class Hops {

  public static final int GROWING = 0x00;
  public static final int WATERED = 0x01;
  public static final int DISEASED = 0x02;
  public static final int DEAD = 0x03;
  public static final int MAIN_HOPS_CONFIG = 506;
  private static final int START_HARVEST_AMOUNT = 3;
  private static final int END_HARVEST_AMOUNT = 41;
  private static final double WATERING_CHANCE = 0.5;
  private static final double COMPOST_CHANCE = 0.9;
  private static final double SUPERCOMPOST_CHANCE = 0.7;
  private static final double CLEARING_EXPERIENCE = 4;
  private final Stoner stoner;
  public int[] hopsStages = new int[4];
  public int[] hopsSeeds = new int[4];
  public int[] hopsHarvest = new int[4];
  public int[] hopsState = new int[4];
  public long[] hopsTimer = new long[4];
  public double[] diseaseChance = {1, 1, 1, 1};
  public boolean[] hasFullyGrown = {false, false, false, false};
  public boolean[] hopsWatched = {false, false, false, false};

  public Hops(Stoner stoner) {
    this.stoner = stoner;
  }

  public void updateHopsStates() {
    int[] configValues = new int[hopsStages.length];

    int configValue;
    for (int i = 0; i < hopsStages.length; i++) {
      configValues[i] = getConfigValue(hopsStages[i], hopsSeeds[i], hopsState[i], i);
    }

    configValue =
        (configValues[0] << 16)
            + (configValues[1] << 8 << 16)
            + configValues[2]
            + (configValues[3] << 8);
    stoner.send(new SendConfig(MAIN_HOPS_CONFIG, configValue));
  }

  public int getConfigValue(int hopsStage, int seedId, int plantState, int index) {
    HopsData hopsData = HopsData.forId(seedId);
    switch (hopsStage) {
      case 0:
        return 0;
      case 1:
        return 0x01;
      case 2:
        return 0x02;
      case 3:
        return 0x03;
    }
    if (hopsData == null) {
      return -1;
    }
    if (hopsData.getEndingState() == hopsData.getStartingState() + hopsStage - 1) {
      hasFullyGrown[index] = true;
    }

    return (getPlantState(plantState) << 6) + hopsData.getStartingState() + hopsStage - 4;
  }

  public int getPlantState(int plantState) {
    switch (plantState) {
      case 0:
        return GROWING;
      case 1:
        return WATERED;
      case 2:
        return DISEASED;
      case 3:
        return DEAD;
    }
    return -1;
  }

  public void doCalculations() {
    for (int i = 0; i < hopsSeeds.length; i++) {
      if (hopsStages[i] > 0
          && hopsStages[i] <= 3
          && Cultivation.getMinutesCounter(stoner) - hopsTimer[i] >= 5) {
        hopsStages[i]--;
        hopsTimer[i] = Cultivation.getMinutesCounter(stoner);
        updateHopsStates();
        continue;
      }
      HopsData hopsData = HopsData.forId(hopsSeeds[i]);
      if (hopsData == null) {
        continue;
      }

      long difference = Cultivation.getMinutesCounter(stoner) - hopsTimer[i];
      long growth = hopsData.getGrowthTime();
      int nbStates = hopsData.getEndingState() - hopsData.getStartingState();
      int state = (int) (difference * nbStates / growth);
      if (hopsTimer[i] == 0 || hopsState[i] == 3 || state > nbStates) {
        continue;
      }
      if (4 + state != hopsStages[i]) {
        hopsStages[i] = 4 + state;
        if (hopsStages[i] <= 4 + state)
          for (int j = hopsStages[i]; j <= 4 + state; j++) doStateCalculation(i);
        updateHopsStates();
      }
    }
  }

  public void modifyStage(int i) {
    HopsData hopsData = HopsData.forId(hopsSeeds[i]);
    if (hopsData == null) return;
    long difference = Cultivation.getMinutesCounter(stoner) - hopsTimer[i];
    long growth = hopsData.getGrowthTime();
    int nbStates = hopsData.getEndingState() - hopsData.getStartingState();
    int state = (int) (difference * nbStates / growth);
    hopsStages[i] = 4 + state;
    updateHopsStates();
  }

  public void doStateCalculation(int index) {
    if (hopsState[index] == 3) {
      return;
    }
    if (hopsState[index] == 2) {
      if (hopsWatched[index]) {
        hopsState[index] = 0;
        HopsData hopsData = HopsData.forId(hopsSeeds[index]);
        if (hopsData == null) return;
        int difference = hopsData.getEndingState() - hopsData.getStartingState();
        int growth = hopsData.getGrowthTime();
        hopsTimer[index] += (growth / difference);
        modifyStage(index);
      } else {
        hopsState[index] = 3;
      }
    }

    if (hopsState[index] == 1) {
      diseaseChance[index] *= 2;
      hopsState[index] = 0;
    }

    if (hopsState[index] == 5 && hopsStages[index] != 3) {
      hopsState[index] = 0;
    }

    if (hopsState[index] == 0 && hopsStages[index] >= 5 && !hasFullyGrown[index]) {
      HopsData hopsData = HopsData.forId(hopsSeeds[index]);
      if (hopsData == null) {
        return;
      }

      double chance = diseaseChance[index] * hopsData.getDiseaseChance();
      int maxChance = (int) chance * 100;
      if (Utility.random(100) <= maxChance
          && !stoner.isCreditUnlocked(CreditPurchase.DISEASE_IMUNITY)) {
        hopsState[index] = 2;
      }
    }
  }

  public boolean waterPatch(int objectX, int objectY, int itemId) {
    if (stoner.getProfession().locked()) {
      return false;
    }
    final HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(objectX, objectY);
    if (hopsFieldsData == null) {
      return false;
    }
    HopsData hopsData = HopsData.forId(hopsSeeds[hopsFieldsData.getHopsIndex()]);
    if (hopsData == null) {
      return false;
    }
    if (hopsState[hopsFieldsData.getHopsIndex()] == 1
        || hopsStages[hopsFieldsData.getHopsIndex()] <= 1
        || hopsStages[hopsFieldsData.getHopsIndex()]
            == hopsData.getEndingState() - hopsData.getStartingState() + 4) {
      stoner.send(new SendMessage("This patch doesn't need watering."));
      return true;
    }
    stoner.getBox().remove(itemId, 1);
    stoner.getBox().add(itemId == 5333 ? itemId - 2 : itemId - 1, 1);

    if (!stoner.getEquipment().isWearingItem(CultivationConstants.RAKE)) {
      DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to plant seed here.");
      return true;
    }
    stoner.send(new SendMessage("You water the patch."));
    stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.WATERING_CAN_ANIM));

    stoner.getProfession().lock(5);
    Controller controller = stoner.getController();
    stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
    TaskQueue.queue(
        new Task(
            stoner, 5, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
          @Override
          public void execute() {
            diseaseChance[hopsFieldsData.getHopsIndex()] *= WATERING_CHANCE;
            hopsState[hopsFieldsData.getHopsIndex()] = 1;
            stop();
          }

          @Override
          public void onStop() {
            updateHopsStates();
            stoner.getUpdateFlags().sendAnimation(new Animation(65535));
            stoner.setController(controller);
          }
        });
    return true;
  }

  public boolean clearPatch(int objectX, int objectY, int itemId) {
    if (stoner.getProfession().locked()) {
      return false;
    }
    final HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(objectX, objectY);
    int finalAnimation;
    int finalDelay;
    if (hopsFieldsData == null
        || (itemId != CultivationConstants.RAKE && itemId != CultivationConstants.SPADE)) {
      return false;
    }
    if (hopsStages[hopsFieldsData.getHopsIndex()] == 3) {
      return true;
    }
    if (hopsStages[hopsFieldsData.getHopsIndex()] <= 3) {
      if (!stoner.getEquipment().isWearingItem(CultivationConstants.RAKE)) {
        DialogueManager.sendStatement(
            stoner, "You must be wearing a tool ring to clear this path.");
        return true;
      } else {
        finalAnimation = CultivationConstants.RAKING_ANIM;
        finalDelay = 5;
      }
    } else {
      if (!stoner.getEquipment().isWearingItem(CultivationConstants.SPADE)) {
        DialogueManager.sendStatement(
            stoner, "You must be wearing a tool ring to clear this path.");
        return true;
      } else {
        finalAnimation = CultivationConstants.SPADE_ANIM;
        finalDelay = 3;
      }
    }
    final int animation = finalAnimation;
    stoner.getProfession().lock(finalDelay);
    stoner.getUpdateFlags().sendAnimation(new Animation(animation));
    Controller controller = stoner.getController();
    stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
    TaskQueue.queue(
        new Task(
            stoner,
            finalDelay,
            false,
            StackType.NEVER_STACK,
            BreakType.NEVER,
            TaskIdentifier.CULTIVATION) {
          @Override
          public void execute() {
            stoner.getUpdateFlags().sendAnimation(new Animation(animation));
            if (hopsStages[hopsFieldsData.getHopsIndex()] <= 2) {
              hopsStages[hopsFieldsData.getHopsIndex()]++;
              stoner.getBox().add(6055, 1);
            } else {
              hopsStages[hopsFieldsData.getHopsIndex()] = 3;
              stop();
            }
            stoner.getProfession().addExperience(Professions.CULTIVATION, CLEARING_EXPERIENCE);
            hopsTimer[hopsFieldsData.getHopsIndex()] = Cultivation.getMinutesCounter(stoner);
            updateHopsStates();
            if (hopsStages[hopsFieldsData.getHopsIndex()] == 3) {
              stop();
            }
          }

          @Override
          public void onStop() {
            resetHops(hopsFieldsData.getHopsIndex());
            stoner.send(new SendMessage("You clear the patch."));
            stoner.getUpdateFlags().sendAnimation(new Animation(65535));
            stoner.setController(controller);
          }
        });
    return true;
  }

  public boolean plantSeed(int objectX, int objectY, final int seedId) {
    if (stoner.getProfession().locked()) {
      return false;
    }
    final HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(objectX, objectY);
    final HopsData hopsData = HopsData.forId(seedId);
    if (hopsFieldsData == null || hopsData == null) {
      return false;
    }
    if (hopsStages[hopsFieldsData.getHopsIndex()] != 3) {
      stoner.send(new SendMessage("You can't plant a seed here."));
      return false;
    }
    if (hopsData.getGradeRequired() > stoner.getGrades()[Professions.CULTIVATION]) {
      DialogueManager.sendStatement(
          stoner,
          "You need a cultivation grade of "
              + hopsData.getGradeRequired()
              + " to plant this seed.");
      return true;
    }
    if (!stoner.getEquipment().isWearingItem(CultivationConstants.SEED_DIBBER)) {
      DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to plant seed here.");
      return true;
    }
    if (stoner.getBox().getItemAmount(hopsData.getSeedId()) < hopsData.getSeedAmount()) {
      DialogueManager.sendStatement(
          stoner, "You need at least " + hopsData.getSeedAmount() + " seeds to plant here.");
      return true;
    }
    stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.SEED_DIBBING));
    hopsStages[hopsFieldsData.getHopsIndex()] = 4;
    stoner.getBox().remove(seedId, hopsData.getSeedAmount());

    stoner.getProfession().lock(3);
    Controller controller = stoner.getController();
    stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
    TaskQueue.queue(
        new Task(
            stoner, 3, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
          @Override
          public void execute() {
            hopsState[hopsFieldsData.getHopsIndex()] = 0;
            hopsSeeds[hopsFieldsData.getHopsIndex()] = seedId;
            hopsTimer[hopsFieldsData.getHopsIndex()] = Cultivation.getMinutesCounter(stoner);
            stoner.getProfession().addExperience(Professions.CULTIVATION, hopsData.getPlantingXp());
            stop();
          }

          @Override
          public void onStop() {
            updateHopsStates();
            stoner.setController(controller);
          }
        });
    return true;
  }

  @SuppressWarnings("unused")
  private void displayAll() {
    for (int i = 0; i < hopsStages.length; i++) {
      System.out.println("index : " + i);
      System.out.println("state : " + hopsState[i]);
      System.out.println("harvest : " + hopsHarvest[i]);
      System.out.println("seeds : " + hopsSeeds[i]);
      System.out.println("grade : " + hopsStages[i]);
      System.out.println("timer : " + hopsTimer[i]);
      System.out.println("disease chance : " + diseaseChance[i]);
      System.out.println("-----------------------------------------------------------------");
    }
  }

  public boolean harvest(int objectX, int objectY) {
    final HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(objectX, objectY);
    if (hopsFieldsData == null) {
      return false;
    }
    final HopsData hopsData = HopsData.forId(hopsSeeds[hopsFieldsData.getHopsIndex()]);
    if (hopsData == null) {
      return false;
    }
    if (!stoner.getEquipment().isWearingItem(CultivationConstants.SPADE)) {
      DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to harvest here.");
      return true;
    }
    stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.SPADE_ANIM));
    Controller controller = stoner.getController();
    stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
    TaskQueue.queue(
        new Task(
            stoner, 2, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
          @Override
          public void execute() {
            if (hopsHarvest[hopsFieldsData.getHopsIndex()] == 0) {
              hopsHarvest[hopsFieldsData.getHopsIndex()] =
                  1
                      + (START_HARVEST_AMOUNT
                          + Utility.random(END_HARVEST_AMOUNT - START_HARVEST_AMOUNT));
            }
            if (hopsHarvest[hopsFieldsData.getHopsIndex()] == 1) {
              resetHops(hopsFieldsData.getHopsIndex());
              hopsStages[hopsFieldsData.getHopsIndex()] = 3;
              hopsTimer[hopsFieldsData.getHopsIndex()] = Cultivation.getMinutesCounter(stoner);
              stop();
              return;
            }
            if (stoner.getBox().getFreeSlots() <= 0) {
              stop();
              return;
            }
            hopsHarvest[hopsFieldsData.getHopsIndex()]--;
            stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.SPADE_ANIM));
            stoner.send(new SendMessage("You harvest the crop, and get some vegetables."));
            stoner.getBox().add(hopsData.getHarvestId(), 1);
            stoner.getProfession().addExperience(Professions.CULTIVATION, hopsData.getHarvestXp());
          }

          @Override
          public void onStop() {
            updateHopsStates();
            stoner.getUpdateFlags().sendAnimation(new Animation(65535));
            stoner.setController(controller);
          }
        });
    return true;
  }

  public boolean putCompost(int objectX, int objectY, final int itemId) {
    if (stoner.getProfession().locked()) {
      return false;
    }
    if (itemId != 6032 && itemId != 6034) {
      return false;
    }
    final HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(objectX, objectY);
    if (hopsFieldsData == null) {
      return false;
    }
    if (hopsStages[hopsFieldsData.getHopsIndex()] != 3
        || hopsState[hopsFieldsData.getHopsIndex()] == 5) {
      stoner.send(new SendMessage("This patch doesn't need compost."));
      return true;
    }
    stoner.getBox().remove(itemId, 1);
    stoner.getBox().add(1925, 1);

    stoner.send(
        new SendMessage(
            "You pour some " + (itemId == 6034 ? "super" : "") + "compost on the patch."));
    stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.PUTTING_COMPOST));
    stoner
        .getProfession()
        .addExperience(
            Professions.CULTIVATION,
            itemId == 6034 ? Compost.SUPER_COMPOST_EXP_USE : Compost.COMPOST_EXP_USE);

    stoner.getProfession().lock(7);
    Controller controller = stoner.getController();
    stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
    TaskQueue.queue(
        new Task(
            stoner, 7, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
          @Override
          public void execute() {
            diseaseChance[hopsFieldsData.getHopsIndex()] *=
                itemId == 6032 ? COMPOST_CHANCE : SUPERCOMPOST_CHANCE;
            hopsState[hopsFieldsData.getHopsIndex()] = 5;
            stop();
          }

          @Override
          public void onStop() {
            stoner.getUpdateFlags().sendAnimation(new Animation(65535));
            stoner.setController(controller);
          }
        });
    return true;
  }

  public boolean inspect(int objectX, int objectY) {
    if (stoner.getProfession().locked()) {
      return false;
    }
    final HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(objectX, objectY);
    if (hopsFieldsData == null) {
      return false;
    }
    final InspectData inspectData = InspectData.forId(hopsSeeds[hopsFieldsData.getHopsIndex()]);
    final HopsData hopsData = HopsData.forId(hopsSeeds[hopsFieldsData.getHopsIndex()]);
    if (hopsState[hopsFieldsData.getHopsIndex()] == 2) {
      DialogueManager.sendStatement(
          stoner,
          "This plant is diseased. Use a PK 13-14 on it to cure it, ",
          "or clear the patch with a spade.");
      return true;
    } else if (hopsState[hopsFieldsData.getHopsIndex()] == 3) {
      DialogueManager.sendStatement(
          stoner,
          "This plant is dead. You did not cure it while it was diseased.",
          "Clear the patch with a spade.");
      return true;
    }
    if (hopsStages[hopsFieldsData.getHopsIndex()] == 0) {
      DialogueManager.sendStatement(
          stoner,
          "This is a hops patch. The soil has not been treated.",
          "The patch needs weeding.");
    } else if (hopsStages[hopsFieldsData.getHopsIndex()] == 3) {
      DialogueManager.sendStatement(
          stoner,
          "This is a hops patch. The soil has not been treated.",
          "The patch is empty and weeded.");
    } else if (inspectData != null && hopsData != null) {
      stoner.send(new SendMessage("You bend down and start to inspect the patch..."));

      stoner.getUpdateFlags().sendAnimation(new Animation(1331));
      stoner.getProfession().lock(5);

      Controller controller = stoner.getController();
      stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
      TaskQueue.queue(
          new Task(
              stoner,
              5,
              false,
              StackType.NEVER_STACK,
              BreakType.NEVER,
              TaskIdentifier.CULTIVATION) {
            @Override
            public void execute() {
              if (hopsStages[hopsFieldsData.getHopsIndex()] - 4
                  < inspectData.getMessages().length - 2) {
                DialogueManager.sendStatement(
                    stoner,
                    inspectData.getMessages()[hopsStages[hopsFieldsData.getHopsIndex()] - 4]);
              } else if (hopsStages[hopsFieldsData.getHopsIndex()]
                  < hopsData.getEndingState() - hopsData.getStartingState() + 2) {
                DialogueManager.sendStatement(
                    stoner, inspectData.getMessages()[inspectData.getMessages().length - 2]);
              } else {
                DialogueManager.sendStatement(
                    stoner, inspectData.getMessages()[inspectData.getMessages().length - 1]);
              }
              stop();
            }

            @Override
            public void onStop() {
              stoner.getUpdateFlags().sendAnimation(new Animation(1332));
              stoner.setController(controller);
            }
          });
    }
    return true;
  }

  public boolean curePlant(int objectX, int objectY, int itemId) {
    if (stoner.getProfession().locked()) {
      return false;
    }
    final HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(objectX, objectY);
    if (hopsFieldsData == null || itemId != 6036) {
      return false;
    }
    final HopsData hopsData = HopsData.forId(hopsSeeds[hopsFieldsData.getHopsIndex()]);
    if (hopsData == null) {
      return false;
    }
    if (hopsState[hopsFieldsData.getHopsIndex()] != 2) {
      stoner.send(new SendMessage("This plant doesn't need to be cured."));
      return true;
    }
    stoner.getBox().remove(itemId, 1);
    stoner.getBox().add(229, 1);
    stoner.getUpdateFlags().sendAnimation(new Animation(CultivationConstants.CURING_ANIM));
    stoner.getProfession().lock(7);
    hopsState[hopsFieldsData.getHopsIndex()] = 0;

    Controller controller = stoner.getController();
    stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
    TaskQueue.queue(
        new Task(
            stoner, 7, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
          @Override
          public void execute() {
            stoner.send(new SendMessage("You cure the plant with a PK 13-14."));
            stop();
          }

          @Override
          public void onStop() {
            updateHopsStates();
            stoner.getUpdateFlags().sendAnimation(new Animation(65535));
            stoner.setController(controller);
          }
        });
    return true;
  }

  private void resetHops(int index) {
    hopsSeeds[index] = 0;
    hopsState[index] = 0;
    diseaseChance[index] = 1;
    hopsHarvest[index] = 0;
    hasFullyGrown[index] = false;
    hopsWatched[index] = false;
  }

  public boolean checkIfRaked(int objectX, int objectY) {
    final HopsFieldsData hopsFieldsData = HopsFieldsData.forIdPosition(objectX, objectY);
    if (hopsFieldsData == null) return false;
    return hopsStages[hopsFieldsData.getHopsIndex()] == 3;
  }

  public enum HopsData {
    BARLEY(5305, 6006, 4, 3, new int[] {6032, 3}, 40, 0.35, 8.5, 9.5, 0x31, 0x35),
    HAMMERSTONE(5307, 5994, 4, 4, new int[] {6010, 1}, 40, 0.35, 9, 10, 0x04, 0x08),
    ASGARNIAN(5308, 5996, 4, 8, new int[] {5458, 1}, 40, 0.30, 10.5, 12, 0x0b, 0x10),
    JUTE(5306, 5931, 3, 13, new int[] {6008, 6}, 40, 0.30, 13, 14.5, 0x38, 0x3d),
    YANILLIAN(5309, 5998, 4, 16, new int[] {5968, 1}, 40, 0.25, 14.5, 16, 0x13, 0x19),
    KRANDORIAN(5310, 6000, 4, 21, new int[] {5478}, 40, 0.25, 17.5, 19.5, 0x1c, 0x23),
    WILDBLOOD(5311, 6002, 4, 28, new int[] {6012, 1}, 40, 0.20, 23, 26, 0x26, 0x2e),
    ;

    private static final Map<Integer, HopsData> seeds = new HashMap<Integer, HopsData>();

    static {
      for (HopsData data : HopsData.values()) {
        seeds.put(data.seedId, data);
      }
    }

    private final int seedId;
    private final int harvestId;
    private final int seedAmount;
    private final int gradeRequired;
    private final int[] paymentToWatch;
    private final int growthTime;
    private final double diseaseChance;
    private final double plantingXp;
    private final double harvestXp;
    private final int startingState;
    private final int endingState;

    HopsData(
        int seedId,
        int harvestId,
        int seedAmount,
        int gradeRequired,
        int[] paymentToWatch,
        int growthTime,
        double diseaseChance,
        double plantingXp,
        double harvestXp,
        int startingState,
        int endingState) {
      this.seedId = seedId;
      this.harvestId = harvestId;
      this.seedAmount = seedAmount;
      this.gradeRequired = gradeRequired;
      this.paymentToWatch = paymentToWatch;
      this.growthTime = growthTime;
      this.diseaseChance = diseaseChance;
      this.plantingXp = plantingXp;
      this.harvestXp = harvestXp;
      this.startingState = startingState;
      this.endingState = endingState;
    }

    public static HopsData forId(int seedId) {
      return seeds.get(seedId);
    }

    public int getSeedId() {
      return seedId;
    }

    public int getHarvestId() {
      return harvestId;
    }

    public int getSeedAmount() {
      return seedAmount;
    }

    public int getGradeRequired() {
      return gradeRequired;
    }

    public int[] getPaymentToWatch() {
      return paymentToWatch;
    }

    public int getGrowthTime() {
      return growthTime;
    }

    public double getDiseaseChance() {
      return diseaseChance;
    }

    public double getPlantingXp() {
      return plantingXp;
    }

    public double getHarvestXp() {
      return harvestXp;
    }

    public int getStartingState() {
      return startingState;
    }

    public int getEndingState() {
      return endingState;
    }
  }

  public enum HopsFieldsData {
    LUMBRIDGE(0, new Point[] {new Point(3227, 3313), new Point(3231, 3317)}, 2333),
    MCGRUBOR(1, new Point[] {new Point(2664, 3523), new Point(2669, 3528)}, 2334),
    YANILLE(2, new Point[] {new Point(2574, 3103), new Point(2577, 3106)}, 2332),
    ENTRANA(3, new Point[] {new Point(2809, 3335), new Point(2812, 3338)}, 2327);

    private static final Map<Integer, HopsFieldsData> npcsProtecting =
        new HashMap<Integer, HopsFieldsData>();

    static {
      for (HopsFieldsData data : HopsFieldsData.values()) {
        npcsProtecting.put(data.npcId, data);
      }
    }

    private final int hopsIndex;
    private final Point[] hopsPosition;
    private final int npcId;

    HopsFieldsData(int hopsIndex, Point[] hopsPosition, int npcId) {
      this.hopsIndex = hopsIndex;
      this.hopsPosition = hopsPosition;
      this.npcId = npcId;
    }

    public static HopsFieldsData forId(int npcId) {
      return npcsProtecting.get(npcId);
    }

    public static HopsFieldsData forIdPosition(int x, int y) {
      for (HopsFieldsData hopsFieldsData : HopsFieldsData.values()) {
        if (CultivationConstants.inRangeArea(
            hopsFieldsData.getHopsPosition()[0], hopsFieldsData.getHopsPosition()[1], x, y)) {
          return hopsFieldsData;
        }
      }
      return null;
    }

    public int getHopsIndex() {
      return hopsIndex;
    }

    public Point[] getHopsPosition() {
      return hopsPosition;
    }

    public int getNpcId() {
      return npcId;
    }
  }

  public enum InspectData {
    BARLEY(
        5305,
        new String[][] {
          {"The barley seeds have only just been planted."},
          {"Grain heads develop at the upper part of the stalks,", "as the barley grows taller."},
          {"The barley grows taller, the heads weighing", "slightly on the stalks."},
          {"The barley grows taller."},
          {
            "The barley is ready to harvest. The heads of grain",
            "are weighing down heavily on the stalks!"
          }
        }),
    HAMMERSTONE(
        5307,
        new String[][] {
          {"The Hammerstone seeds have only just been planted."},
          {"The Hammerstone hops plant grows a little bit taller."},
          {"The Hammerstone hops plant grows a bit taller."},
          {"The Hammerstone hops plant grows a bit taller."},
          {"The Hammerstone hops plant is ready to harvest."}
        }),
    ASGARNIAN(
        5308,
        new String[][] {
          {"The Asgarnian seeds have only just been planted."},
          {"The Asgarnian hops plant grows a bit taller."},
          {"The Asgarnian hops plant grows a bit taller."},
          {"The Asgarnian hops plant grows a bit taller."},
          {"The upper new leaves appear dark green to the", "rest of the plant."},
          {"The Asgarnian hops plant is ready to harvest."}
        }),
    JUTE(
        5306,
        new String[][] {
          {"The Jute seeds have only just been planted."},
          {"The jute plants grow taller."},
          {"The jute plants grow taller."},
          {"The jute plants grow taller."},
          {"The jute plant grows taller. They are as high", "as the stoner."},
          {"The jute plants are ready to harvest."}
        }),
    YANILLIAN(
        5309,
        new String[][] {
          {"The Yanillian seeds have only just been planted."},
          {"The Yanillian hops plant grows a bit taller."},
          {"The Yanillian hops plant grows a bit taller."},
          {"The Yanillian hops plant grows a bit taller."},
          {"The new leaves on the top of the Yanillian hops", "plant are dark green."},
          {"The new leaves on the top of the Yanillian hops", "plant are dark green."},
          {"The Yanillian hops plant is ready to harvest."}
        }),
    KRANDORIAN(
        5310,
        new String[][] {
          {"The Krandorian seeds have only just been planted."},
          {"The Krandorian plant grows a bit taller."},
          {"The Krandorian plant grows a bit taller."},
          {"The Krandorian plant grows a bit taller."},
          {"The new leaves on top of the Krandorian plant are", "dark green."},
          {"The Krandorian plant grows a bit taller."},
          {"The new leaves on top of the Krandorian plant", "are dark green."},
          {"The Krandorian plant is ready for harvesting."}
        }),
    WILDBLOOD(
        5311,
        new String[][] {
          {"The wildblood seeds have only just been planted."},
          {"The wildblood hops plant grows a bit taller."},
          {"The wildblood hops plant grows a bit taller."},
          {"The wildblood hops plant grows a bit taller."},
          {"The wildblood hops plant grows a bit taller."},
          {"The wildblood hops plant grows a bit taller."},
          {"The wildblood hops plant grows a bit taller."},
          {"The new leaves at the top of the wildblood hops plant", "are dark green."},
          {"The wildblood hops plant is ready to harvest."}
        });

    private static final Map<Integer, InspectData> seeds = new HashMap<Integer, InspectData>();

    static {
      for (InspectData data : InspectData.values()) {
        seeds.put(data.seedId, data);
      }
    }

    private final int seedId;
    private final String[][] messages;

    InspectData(int seedId, String[][] messages) {
      this.seedId = seedId;
      this.messages = messages;
    }

    public static InspectData forId(int seedId) {
      return seeds.get(seedId);
    }

    public int getSeedId() {
      return seedId;
    }

    public String[][] getMessages() {
      return messages;
    }
  }
}
