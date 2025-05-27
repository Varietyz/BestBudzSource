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

public class Weeds {

  public static final int GROWING = 0x00;
  public static final int MAIN_WEED_LOCATION_CONFIG = 515;
  private static final int START_HARVEST_AMOUNT = 3;
  private static final int END_HARVEST_AMOUNT = 18;
  private static final double COMPOST_CHANCE = 0.9;
  private static final double SUPERCOMPOST_CHANCE = 0.7;
  private static final double CLEARING_EXPERIENCE = 4;
  private final Stoner stoner;
  public int[] weedStages = new int[4];
  public int[] weedSeeds = new int[4];
  public int[] weedHarvest = new int[4];
  public int[] weedState = new int[4];
  public long[] weedTimer = new long[4];
  public double[] diseaseChance = {1, 1, 1, 1, 1};

  public Weeds(Stoner stoner) {
    this.stoner = stoner;
  }

  public void updateWeedsStates() {
    int[] configValues = new int[weedStages.length];

    int configValue;
    for (int i = 0; i < weedStages.length; i++) {
      configValues[i] = getConfigValue(weedStages[i], weedSeeds[i], weedState[i], i);
    }

    configValue =
        (configValues[0] << 16)
            + (configValues[1] << 8 << 16)
            + configValues[2]
            + (configValues[3] << 8);
    stoner.send(new SendConfig(MAIN_WEED_LOCATION_CONFIG, configValue));
  }

  public int getConfigValue(int weedStage, int seedId, int plantState, int index) {
    WeedData weedData = WeedData.forId(seedId);
    switch (weedStage) {
      case 0:
        return 0;
      case 1:
        return 0x01;
      case 2:
        return 0x02;
      case 3:
        return 0x03;
    }
    if (weedData == null) {
      return -1;
    }
    if (weedSeeds[index] == 6311) {
      if (plantState == 1) {
        return weedStages[index] + 0xc1;
      } else if (plantState == 2) {
        return weedStages[index] + 0xc3;
      }
    }
    return (plantState == 2
            ? weedStages[index] + 0x9e
            : plantState == 1 ? weedStages[index] + 0x9a : getPlantState(plantState) << 6)
        + weedData.getStartingState()
        + weedStage
        - 4;
  }

  public int getPlantState(int plantState) {
    switch (plantState) {
      case 0:
        return GROWING;
    }
    return -1;
  }

  public void doCalculations() {
    displayAll();
    for (int i = 0; i < weedSeeds.length; i++) {
      if (weedStages[i] > 0
          && weedStages[i] <= 3
          && (Cultivation.getMinutesCounter(stoner) - weedTimer[i]) >= 5) {
        weedStages[i] -= 2;
        weedTimer[i] = Cultivation.getMinutesCounter(stoner);
        updateWeedsStates();
      }
      WeedData weedData = WeedData.forId(weedSeeds[i]);
      if (weedData == null) {
        continue;
      }

      long difference = Cultivation.getMinutesCounter(stoner) - weedTimer[i];
      long growth = weedData.getGrowthTime();
      int nbStates = weedData.getEndingState() - weedData.getStartingState();
      int state = (int) (difference * nbStates / growth);
      if (weedTimer[i] == 0 || weedState[i] == 2 || state > nbStates) {
        continue;
      }
      if (4 + state != weedStages[i]) {
        weedStages[i] = 4 + state;
        doStateCalculation(i);
        updateWeedsStates();
      }
    }
  }

  public void doStateCalculation(int index) {
    if (weedState[index] == 2) {
      return;
    }
    if (weedState[index] == 1) {
      weedState[index] = 2;
    }

    if (weedState[index] == 4 && weedStages[index] != 3) {
      weedState[index] = 0;
    }

    if (weedState[index] == 0 && weedStages[index] >= 4 && weedStages[index] <= 7) {
      WeedData weedData = WeedData.forId(weedSeeds[index]);
      if (weedData == null) {
        return;
      }
      double chance = diseaseChance[index] * weedData.getDiseaseChance();
      int maxChance = (int) chance * 100;
      if (Utility.random(100) <= maxChance
          && !stoner.isCreditUnlocked(CreditPurchase.DISEASE_IMUNITY)) {
        weedState[index] = 1;
      }
    }
  }

  public boolean clearPatch(int objectX, int objectY, int itemId) {
    if (stoner.getProfession().locked()) {
      return false;
    }
    final WeedFieldsData weedFieldsData = WeedFieldsData.forIdPosition(objectX, objectY);
    int finalAnimation;
    int finalDelay;
    if (weedFieldsData == null
        || (itemId != CultivationConstants.RAKE && itemId != CultivationConstants.SPADE)) {
      return false;
    }
    if (weedStages[weedFieldsData.getWeedIndex()] == 3) {
      return true;
    }
    if (weedStages[weedFieldsData.getWeedIndex()] <= 3) {
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
    stoner.getUpdateFlags().sendAnimation(new Animation((animation)));

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
            stoner.getUpdateFlags().sendAnimation(new Animation((animation)));
            if (weedStages[weedFieldsData.getWeedIndex()] <= 2) {
              weedStages[weedFieldsData.getWeedIndex()]++;
              stoner.getBox().add(6055, 1);
            } else {
              weedStages[weedFieldsData.getWeedIndex()] = 3;
              stop();
            }
            stoner.getProfession().addExperience(Professions.CULTIVATION, CLEARING_EXPERIENCE);
            weedTimer[weedFieldsData.getWeedIndex()] = Cultivation.getMinutesCounter(stoner);
            updateWeedsStates();
            if (weedStages[weedFieldsData.getWeedIndex()] == 3) {
              stop();
            }
          }

          @Override
          public void onStop() {
            resetWeeds(weedFieldsData.getWeedIndex());
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
    final WeedFieldsData weedFieldsData = WeedFieldsData.forIdPosition(objectX, objectY);
    final WeedData weedData = WeedData.forId(seedId);
    if (weedFieldsData == null || weedData == null) {
      return false;
    }
    if (weedStages[weedFieldsData.getWeedIndex()] != 3) {
      stoner.send(new SendMessage("You can't plant a seed here."));
      return false;
    }
    if (weedData.getGradeRequired() > stoner.getGrades()[Professions.CULTIVATION]) {
      DialogueManager.sendStatement(
          stoner,
          "You need a cultivation grade of "
              + weedData.getGradeRequired()
              + " to plant this seed.");
      return true;
    }
    if (!stoner.getEquipment().isWearingItem(CultivationConstants.SEED_DIBBER)) {
      DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to plant seed here.");
      return true;
    }
    stoner.getUpdateFlags().sendAnimation(new Animation((CultivationConstants.SEED_DIBBING)));
    stoner.getBox().remove(seedId, 1);

    stoner.getProfession().lock(3);
    Controller controller = stoner.getController();
    stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
    TaskQueue.queue(
        new Task(
            stoner, 3, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
          @Override
          public void execute() {
            weedState[weedFieldsData.getWeedIndex()] = 0;
            weedStages[weedFieldsData.getWeedIndex()] = 4;
            weedSeeds[weedFieldsData.getWeedIndex()] = seedId;
            weedTimer[weedFieldsData.getWeedIndex()] = Cultivation.getMinutesCounter(stoner);
            stoner.getProfession().addExperience(Professions.CULTIVATION, weedData.getPlantingXp());
            stop();
          }

          @Override
          public void onStop() {
            updateWeedsStates();
            stoner.getUpdateFlags().sendAnimation(new Animation(65535));
            stoner.setController(controller);
          }
        });
    return true;
  }

  public void displayAll() {
    for (int i = 0; i < weedStages.length; i++) {
      if (weedSeeds[i] == 0) {
        continue;
      }
      System.out.println("minutes : " + Cultivation.getMinutesCounter(stoner));
      System.out.println("index : " + i);
      System.out.println("state : " + weedState[i]);
      System.out.println("harvest : " + weedHarvest[i]);
      System.out.println("seeds : " + weedSeeds[i]);
      System.out.println("stage : " + weedStages[i]);
      System.out.println("timer : " + weedTimer[i]);
      System.out.println("disease chance : " + diseaseChance[i]);
      System.out.println("-----------------------------------------------------------------");
    }
  }

  public boolean harvest(int objectX, int objectY) {
    if (stoner.getProfession().locked()) {
      return false;
    }
    final WeedFieldsData weedFieldsData = WeedFieldsData.forIdPosition(objectX, objectY);
    if (weedFieldsData == null) {
      return false;
    }
    final WeedData weedData = WeedData.forId(weedSeeds[weedFieldsData.getWeedIndex()]);
    if (weedData == null) {
      return false;
    }
    if (!stoner.getEquipment().isWearingItem(CultivationConstants.SPADE)) {
      DialogueManager.sendStatement(stoner, "You must be wearing a tool ring to harvest here.");
      return true;
    }

    stoner.getProfession().lock(3);

    stoner
        .getUpdateFlags()
        .sendAnimation(new Animation((CultivationConstants.PICKING_VEGETABLE_ANIM)));
    Controller controller = stoner.getController();
    stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
    TaskQueue.queue(
        new Task(
            stoner, 3, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
          @Override
          public void execute() {
            if (weedHarvest[weedFieldsData.getWeedIndex()] == 0) {
              weedHarvest[weedFieldsData.getWeedIndex()] =
                  1
                      + (START_HARVEST_AMOUNT
                          + Utility.random(
                              (END_HARVEST_AMOUNT
                                      + (stoner.getEquipment().isWearingItem(7409) ? 5 : 0))
                                  - START_HARVEST_AMOUNT));
            }

            if (weedHarvest[weedFieldsData.getWeedIndex()] == 1) {
              resetWeeds(weedFieldsData.getWeedIndex());
              weedStages[weedFieldsData.getWeedIndex()] = 3;
              weedTimer[weedFieldsData.getWeedIndex()] = Cultivation.getMinutesCounter(stoner);
              stop();
              return;
            }
            if (stoner.getBox().getFreeSlots() <= 0) {
              stop();
              return;
            }
            weedHarvest[weedFieldsData.getWeedIndex()]--;
            stoner
                .getUpdateFlags()
                .sendAnimation(new Animation((CultivationConstants.PICKING_WEED_ANIM)));
            stoner.send(new SendMessage("You harvest miss maryjane, and get some untrimmed buds."));
            stoner.getBox().add(weedData.getHarvestId(), 1);
            stoner.getProfession().addExperience(Professions.CULTIVATION, weedData.getHarvestXp());
          }

          @Override
          public void onStop() {
            updateWeedsStates();
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
    final WeedFieldsData weedFieldsData = WeedFieldsData.forIdPosition(objectX, objectY);
    if (weedFieldsData == null) {
      return false;
    }
    if (weedStages[weedFieldsData.getWeedIndex()] != 3
        || weedState[weedFieldsData.getWeedIndex()] == 4) {
      stoner.send(new SendMessage("This patch doesn't need compost."));
      return true;
    }
    stoner.getBox().remove(itemId, 1);
    stoner.getBox().add(1925, 1);

    stoner.send(
        new SendMessage(
            "You pour some " + (itemId == 6034 ? "super" : "") + "compost on the patch."));
    stoner.getUpdateFlags().sendAnimation(new Animation((CultivationConstants.PUTTING_COMPOST)));
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
            diseaseChance[weedFieldsData.getWeedIndex()] *=
                itemId == 6032 ? COMPOST_CHANCE : SUPERCOMPOST_CHANCE;
            weedState[weedFieldsData.getWeedIndex()] = 4;
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
    final WeedFieldsData weedFieldsData = WeedFieldsData.forIdPosition(objectX, objectY);
    if (weedFieldsData == null) {
      return false;
    }
    final InspectData inspectData = InspectData.forId(weedSeeds[weedFieldsData.getWeedIndex()]);
    final WeedData weedData = WeedData.forId(weedSeeds[weedFieldsData.getWeedIndex()]);
    System.out.println(
        weedStages[weedFieldsData.getWeedIndex()]
            + " 0x"
            + Integer.toHexString(weedStages[weedFieldsData.getWeedIndex()]));
    if (weedState[weedFieldsData.getWeedIndex()] == 1) {
      DialogueManager.sendStatement(
          stoner,
          "This plant is ready to flower. Use PK 13-14 to put it in flowering mode. ",
          "or clear the patch.");
      return true;
    } else if (weedState[weedFieldsData.getWeedIndex()] == 2) {
      DialogueManager.sendStatement(
          stoner,
          "This plant is out of flowering time. You did not add PK 13-14 in time.",
          "Clear the patch.");
      return true;
    }
    if (weedStages[weedFieldsData.getWeedIndex()] == 0) {
      DialogueManager.sendStatement(
          stoner, "This is an cannabis patch.", "The patch needs weeding.");
    } else if (weedStages[weedFieldsData.getWeedIndex()] == 3) {
      DialogueManager.sendStatement(
          stoner, "This is an cannabis patch.", "The patch is empty and weeded.");
    } else if (inspectData != null && weedData != null) {
      stoner.send(new SendMessage("You check the grow medium to see if it is suitable."));

      stoner.getUpdateFlags().sendAnimation(new Animation((1331)));
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
              if (weedStages[weedFieldsData.getWeedIndex()] - 4
                  < inspectData.getMessages().length - 2) {
                DialogueManager.sendStatement(
                    stoner,
                    inspectData.getMessages()[weedStages[weedFieldsData.getWeedIndex()] - 4]);
              } else if (weedStages[weedFieldsData.getWeedIndex()]
                  < weedData.getEndingState() - weedData.getStartingState() + 2) {
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
              stoner.getUpdateFlags().sendAnimation(new Animation((1332)));
              stoner.getUpdateFlags().sendAnimation(new Animation(65535));
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
    final WeedFieldsData weedFieldsData = WeedFieldsData.forIdPosition(objectX, objectY);
    if (weedFieldsData == null || itemId != 6036) {
      return false;
    }
    final WeedData weedData = WeedData.forId(weedSeeds[weedFieldsData.getWeedIndex()]);
    if (weedData == null) {
      return false;
    }
    if (weedState[weedFieldsData.getWeedIndex()] != 1) {
      stoner.send(
          new SendMessage(
              "This plant doesn't need to transition to flowering stage at this point."));
      return true;
    }
    stoner.getBox().remove(itemId, 1);
    stoner.getBox().add(229, 1);
    stoner.getUpdateFlags().sendAnimation(new Animation((CultivationConstants.CURING_ANIM)));
    stoner.getProfession().lock(7);

    Controller controller = stoner.getController();
    stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
    TaskQueue.queue(
        new Task(
            stoner, 7, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
          @Override
          public void execute() {
            stoner.send(
                new SendMessage("Your plant reached flowering stage after giving, PK 13-14."));
            weedState[weedFieldsData.getWeedIndex()] = 0;
            stop();
          }

          @Override
          public void onStop() {
            updateWeedsStates();
            stoner.getUpdateFlags().sendAnimation(new Animation(65535));
            stoner.setController(controller);
          }
        });
    return true;
  }

  @SuppressWarnings("unused")
  private void resetWeeds() {
    for (int i = 0; i < weedStages.length; i++) {
      weedSeeds[i] = 0;
      weedState[i] = 0;
      diseaseChance[i] = 0;
      weedHarvest[i] = 0;
    }
  }

  private void resetWeeds(int index) {
    weedSeeds[index] = 0;
    weedState[index] = 0;
    diseaseChance[index] = 1;
    weedHarvest[index] = 0;
  }

  public boolean checkIfRaked(int objectX, int objectY) {
    final WeedFieldsData weedFieldsData = WeedFieldsData.forIdPosition(objectX, objectY);
    if (weedFieldsData == null) return false;
    return weedStages[weedFieldsData.getWeedIndex()] == 3;
  }

  public enum WeedData {
    KUSH(5291, 199, 1, 60, 0.25, 11, 12.5, 0x04, 0x08),
    HAZE(5292, 201, 1, 60, 0.25, 13.5, 15, 0x0b, 0x0f),
    OG_KUSH(5293, 203, 1, 60, 0.25, 16, 18, 0x12, 0x16),
    POWERPLANT(5294, 205, 1, 60, 0.25, 21.5, 24, 0x19, 0x1d),
    GOUT_TUBER(6311, 3261, 1, 60, 0.25, 105, 45, 0xc0, 0xc4),
    CHEESE_HAZE(5295, 207, 1, 60, 0.20, 27, 30.5, 0x20, 0x24),
    BUBBA_KUSH(5296, 3049, 1, 60, 0.20, 34, 38.5, 0x27, 0x2b),
    CHOCOLOPE(5297, 209, 1, 60, 0.20, 43, 48.5, 0x2e, 0x32),
    GORILLA_GLUE(5298, 211, 1, 60, 0.20, 54.5, 61.5, 0x35, 0x39),
    JACK_HERER(5299, 213, 1, 60, 0.20, 69, 78, 0x44, 0x48),
    DURBAN_POISON(5300, 3051, 1, 60, 0.15, 87.5, 98.5, 0x4b, 0x4f),
    AMNESIA(5301, 215, 1, 60, 0.15, 106.5, 120, 0x52, 0x56),
    SUPER_SILVER_HAZE(5302, 2485, 1, 60, 0.15, 134.5, 151.5, 0x59, 0x5d),
    GIRL_SCOUT_COOKIES(5303, 217, 1, 60, 0.15, 170.5, 192, 0x60, 0x64),
    KHALIFA_KUSH(5304, 219, 1, 60, 0.15, 199.5, 224.5, 0x67, 0x6b);

    private static final Map<Integer, WeedData> seeds = new HashMap<Integer, WeedData>();

    static {
      for (WeedData data : WeedData.values()) {
        seeds.put(data.seedId, data);
      }
    }

    private final int seedId;
    private final int harvestId;
    private final int gradeRequired;
    private final int growthTime;
    private final double diseaseChance;
    private final double plantingXp;
    private final double harvestXp;
    private final int startingState;
    private final int endingState;

    WeedData(
        int seedId,
        int harvestId,
        int gradeRequired,
        int growthTime,
        double diseaseChance,
        double plantingXp,
        double harvestXp,
        int startingState,
        int endingState) {
      this.seedId = seedId;
      this.harvestId = harvestId;
      this.gradeRequired = gradeRequired;
      this.growthTime = 10;
      this.diseaseChance = diseaseChance;
      this.plantingXp = plantingXp;
      this.harvestXp = harvestXp;
      this.startingState = startingState;
      this.endingState = endingState;
    }

    public static WeedData forId(int seedId) {
      return seeds.get(seedId);
    }

    public int getSeedId() {
      return seedId;
    }

    public int getHarvestId() {
      return harvestId;
    }

    public int getGradeRequired() {
      return gradeRequired;
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

  public enum WeedFieldsData {
    ARDOUGNE(0, new Point[] {new Point(2670, 3374), new Point(2671, 3375)}),
    PHASMATYS(1, new Point[] {new Point(3605, 3529), new Point(3606, 3530)}),
    FALADOR(2, new Point[] {new Point(3058, 3311), new Point(3059, 3312)}),
    CATWEEDY(3, new Point[] {new Point(2813, 3463), new Point(2814, 3464)});

    private final int weedIndex;
    private final Point[] weedPosition;

    WeedFieldsData(int weedIndex, Point[] weedPosition) {
      this.weedIndex = weedIndex;
      this.weedPosition = weedPosition;
    }

    public static WeedFieldsData forIdPosition(int x, int y) {
      for (WeedFieldsData weedFieldsData : WeedFieldsData.values()) {
        if (CultivationConstants.inRangeArea(
            weedFieldsData.getWeedPosition()[0], weedFieldsData.getWeedPosition()[1], x, y)) {
          return weedFieldsData;
        }
      }
      return null;
    }

    public int getWeedIndex() {
      return weedIndex;
    }

    public Point[] getWeedPosition() {
      return weedPosition;
    }
  }

  public enum InspectData {
    KUSH(
        5291,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
        }),
    HAZE(
        5292,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
        }),
    OG_KUSH(
        5293,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
        }),
    POWERPLANT(
        5294,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
        }),
    GOUT_TUBER(
        6311,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
        }),
    CHEESE_HAZE(
        5295,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
        }),
    BUBBA_KUSH(
        5296,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
        }),
    CHOCOLOPE(
        5297,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
        }),
    GORILLA_GLUE(
        5298,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
        }),
    KUARM(
        5299,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
        }),
    DURBAN_POISON(
        5300,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
        }),
    AMNESIA(
        5301,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
        }),
    SUPER_SILVER_HAZE(
        5302,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
        }),
    DWARF(
        5303,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
        }),
    KHALIFA_KUSH(
        5304,
        new String[][] {
          {"The seed has only just been planted."},
          {"The weed is now ankle height."},
          {"The weed is now knee height."},
          {"The weed is now mid-thigh height."},
          {"The weed is fully grown and ready to harvest."}
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
