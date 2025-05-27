package com.bestbudz.rs2.content.profession.cultivation;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.Task.BreakType;
import com.bestbudz.core.task.Task.StackType;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.core.task.impl.TaskIdentifier;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.object.GameObject;
import com.bestbudz.rs2.entity.object.ObjectManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.controllers.Controller;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.HashMap;
import java.util.Map;

public class Compost {

  public static final int BUCKET = 1925;
  public static final double COMPOST_EXP_RETRIEVE = 4.5;
  public static final double SUPER_COMPOST_EXP_RETRIEVE = 8.5;
  public static final double COMPOST_EXP_USE = 18;
  public static final double SUPER_COMPOST_EXP_USE = 26;
  public static final double ROTTEN_TOMATOES_EXP_RETRIEVE = 8.5;
  public static final int COMPOST = 6032;
  public static final int SUPER_COMPOST = 6034;
  public static final int ROTTE_TOMATO = 2518;
  public static final int TOMATO = 1982;
  public static final int FIRST_TYPE_COMPOST_BIN = 7808;
  public static final int SECOND_TYPE_COMPOST_BIN = 7818;
  public static final int[] COMPOST_ORGANIC = {
    6055, 1942, 1957, 1965, 5986, 5504, 5982, 249, 251, 253, 255, 257, 2998, 259, 261, 263, 3000,
    265, 2481, 267, 269, 1951, 753, 2126, 247, 239, 6018
  };
  public static final int[] SUPER_COMPOST_ORGANIC = {2114, 5978, 5980, 5982, 6004, 247, 6469};
  private final Stoner stoner;
  public int[] compostBins = new int[4];
  public long[] compostBinsTimer = new long[4];
  public int[] organicItemAdded = new int[4];
  public int tempCompostState;

  public Compost(Stoner stoner) {
    this.stoner = stoner;
  }

  public void updateCompostBin(int index) {
    CompostBinStages compostBinStages =
        CompostBinStages.forId(CompostBinLocations.forId(index).getBinObjectId());

    if (compostBinStages == null) {
      return;
    }
    int x = CompostBinLocations.forId(index).x;
    int y = CompostBinLocations.forId(index).y;
    int finalObject;
    if (compostBins[index] > 0) {
      if (compostBins[index] % 17 == 0) {
        finalObject = compostBinStages.getBinWithSuperCompostable();
      } else if (compostBins[index] % 77 == 0) {
        finalObject = compostBinStages.getBinWithTomatoes();
      } else {
        finalObject = compostBinStages.getBinWithCompostable();
      }
    } else {
      finalObject = compostBinStages.getBinEmpty();
    }
    if (compostBins[index] == 255) {
      finalObject = compostBinStages.getBinFullOFSuperCompostable();
      tempCompostState = 2;
    } else if (compostBins[index] == 1155) {
      finalObject = compostBinStages.getBinFullOfTomatoes();
      tempCompostState = 3;
    } else if (organicItemAdded[index] == 15) {
      finalObject = compostBinStages.getBinFullOfCompostable();
      tempCompostState = 1;
    }
    switch (compostBins[index]) {
      case 100:
      case 200:
      case 300:
        finalObject = compostBinStages.getClosedBin();
        break;
      case 150:
        finalObject = compostBinStages.getBinFullOfCompost();
        break;
      case 250:
        finalObject = compostBinStages.getBinFullOfSuperCompost();
        break;
      case 350:
        finalObject = compostBinStages.getBinFullOfRottenTomatoes();
        break;
    }
    if (compostBins[index] == 150 && organicItemAdded[index] < 15) {
      finalObject = compostBinStages.getBinWithCompost();
    } else if (compostBins[index] == 250 && organicItemAdded[index] < 15) {
      finalObject = compostBinStages.getBinWithSuperCompost();
    }
    if (compostBins[index] == 350 && organicItemAdded[index] < 15) {
      finalObject = compostBinStages.getBinWithRottenTomatoes();
    }

    ObjectManager.send(
        new GameObject(
            finalObject,
            x,
            y,
            stoner.getZ(),
            10,
            CompostBinLocations.forId(index).getObjectFace()));
  }

  public void closeCompostBin(final int index) {
    if (stoner.getProfession().locked()) {
      return;
    }
    compostBins[index] = tempCompostState * 100;
    compostBinsTimer[index] = Cultivation.getMinutesCounter(stoner);

    stoner.getUpdateFlags().sendAnimation(new Animation(835));
    stoner.getProfession().lock(2);
    Controller controller = stoner.getController();
    stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
    TaskQueue.queue(
        new Task(
            stoner, 2, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
          @Override
          public void execute() {
            stoner.send(
                new SendMessage("You close the compost bin, and its content start to rot."));
            updateCompostBin(index);
            stop();
          }

          @Override
          public void onStop() {
            stoner.setController(controller);
          }
        });
  }

  public void openCompostBin(final int index) {
    int timerRequired;
    timerRequired = compostBins[index] == 200 ? 90 : 45;
    if (Cultivation.getMinutesCounter(stoner) - compostBinsTimer[index] >= timerRequired) {
      compostBins[index] += 50;
      stoner.getUpdateFlags().sendAnimation(new Animation(834));

      Controller controller = stoner.getController();
      stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
      TaskQueue.queue(
          new Task(
              stoner,
              2,
              false,
              StackType.NEVER_STACK,
              BreakType.NEVER,
              TaskIdentifier.CULTIVATION) {
            @Override
            public void execute() {
              updateCompostBin(index);
              stop();
            }

            @Override
            public void onStop() {
              stoner.setController(controller);
            }
          });
    } else {
      stoner.send(
          new SendMessage("The compost bin is still rotting. I should wait until it is complete."));
    }
  }

  public void fillCompostBin(int x, int y, final int organicItemUsed) {
    if (stoner.getProfession().locked()) {
      return;
    }
    final CompostBinLocations compostBinLocations = CompostBinLocations.forPosition(x, y);
    if (compostBinLocations == null) {
      return;
    }
    final int index = compostBinLocations.getCompostIndex();

    int incrementFactor = 0;
    for (int normalCompost : COMPOST_ORGANIC) {
      if (organicItemUsed == normalCompost) {
        incrementFactor = 2;
        break;
      }
    }

    for (int superCompost : SUPER_COMPOST_ORGANIC) {
      if (organicItemUsed == superCompost) {
        incrementFactor = 17;
        break;
      }
    }

    if (organicItemUsed == TOMATO) {
      if (compostBins[index] % 77 == 0) {
        incrementFactor = 77;
      } else {
        incrementFactor = 2;
      }
    }
    if (incrementFactor == 0) {
      stoner.send(
          new SendMessage(
              "You need to put organic items into the compost bin in order to make compost."));
      return;
    }
    final int factor = incrementFactor;
    stoner.getProfession().lock(2);
    Controller controller = stoner.getController();
    stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
    TaskQueue.queue(
        new Task(
            stoner, 2, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
          @Override
          public void execute() {
            if (!stoner.getBox().hasItemId(organicItemUsed) || organicItemAdded[index] == 15) {
              stop();
              return;
            }
            organicItemAdded[index]++;
            stoner.getUpdateFlags().sendAnimation(new Animation(832));
            stoner.getBox().remove(organicItemUsed, 1);
            compostBins[index] += factor;
            updateCompostBin(index);
          }

          @Override
          public void onStop() {
            stoner.getUpdateFlags().sendAnimation(new Animation(65535));
            stoner.setController(controller);
          }
        });
  }

  public void retrieveCompost(final int index) {

    final int finalItem =
        compostBins[index] == 150
            ? COMPOST
            : compostBins[index] == 250 ? SUPER_COMPOST : ROTTE_TOMATO;

    stoner.getUpdateFlags().sendAnimation(new Animation(832));
    Controller controller = stoner.getController();
    stoner.setController(ControllerManager.FORCE_MOVEMENT_CONTROLLER);
    TaskQueue.queue(
        new Task(
            stoner, 2, false, StackType.NEVER_STACK, BreakType.NEVER, TaskIdentifier.CULTIVATION) {
          @Override
          public void execute() {
            if (!stoner.getBox().hasItemId(BUCKET) && compostBins[index] != 350
                || organicItemAdded[index] == 0) {
              stop();
              return;
            }
            stoner
                .getProfession()
                .addExperience(
                    Professions.CULTIVATION,
                    finalItem == COMPOST
                        ? COMPOST_EXP_RETRIEVE
                        : finalItem == SUPER_COMPOST
                            ? SUPER_COMPOST_EXP_RETRIEVE
                            : ROTTEN_TOMATOES_EXP_RETRIEVE);
            if (compostBins[index] != 350) {
              stoner.getBox().remove(BUCKET, 1);
            }
            stoner.getBox().add(finalItem, 1);
            stoner.getUpdateFlags().sendAnimation(new Animation(832));
            organicItemAdded[index]--;
            if (organicItemAdded[index] == 0) {
              resetVariables(index);
            }
            updateCompostBin(index);
          }

          @Override
          public void onStop() {
            stoner.getUpdateFlags().sendAnimation(new Animation(65535));
            stoner.setController(controller);
          }
        });
  }

  public boolean handleItemOnObject(int itemUsed, int objectId, int objectX, int objectY) {
    switch (objectId) {
      case 7814:
      case 7815:
      case 7816:
      case 7817:
      case 7824:
      case 7825:
      case 7826:
      case 7827:
        if (itemUsed == BUCKET) {
          retrieveCompost(CompostBinLocations.forPosition(objectX, objectY).getCompostIndex());
        } else {
          stoner.send(new SendMessage("You might need some buckets to gather the compost."));
        }
        return true;

      case 7839:
      case 7838:
      case 7837:
      case 7836:
      case 7808:
      case 7809:
      case 7811:
      case 7819:
      case 7821:
      case 7828:
      case 7832:
        fillCompostBin(objectX, objectY, itemUsed);
        return true;
    }
    return false;
  }

  public boolean handleObjectClick(int objectId, int objectX, int objectY) {

    switch (objectId) {
      case 7810:
      case 7812:
      case 7820:
      case 7822:
      case 7829:
      case 7833:
        closeCompostBin(CompostBinLocations.forPosition(objectX, objectY).getCompostIndex());
        return true;

      case 7813:
      case 7823:
        openCompostBin(CompostBinLocations.forPosition(objectX, objectY).getCompostIndex());
        return true;

      case 7830:
      case 7831:
      case 7834:
      case 7835:
        retrieveCompost(CompostBinLocations.forPosition(objectX, objectY).getCompostIndex());
        return true;
    }
    return false;
  }

  public void resetVariables(int index) {
    compostBins[index] = 0;
    compostBinsTimer[index] = 0;
    organicItemAdded[index] = 0;
  }

  public enum CompostBinLocations {
    NORTH_ARDOUGNE(0, FIRST_TYPE_COMPOST_BIN, 3, 2661, 3375),
    PHASMATYS(1, SECOND_TYPE_COMPOST_BIN, 1, 3610, 3522),
    FALADOR(2, FIRST_TYPE_COMPOST_BIN, 4, 3056, 3312),
    CATWEEDY(3, FIRST_TYPE_COMPOST_BIN, 3, 2804, 3464);

    private static final Map<Integer, CompostBinLocations> bins =
        new HashMap<Integer, CompostBinLocations>();

    static {
      for (CompostBinLocations data : CompostBinLocations.values()) {
        bins.put(data.compostIndex, data);
      }
    }

    private final int compostIndex;
    private final int binObjectId;
    private final int objectFace;
    private final int x;
    private final int y;

    CompostBinLocations(int compostIndex, int binObjectId, int objectFace, int x, int y) {
      this.compostIndex = compostIndex;
      this.binObjectId = binObjectId;
      this.objectFace = objectFace;
      this.x = x;
      this.y = y;
    }

    public static CompostBinLocations forId(int index) {
      return bins.get(index);
    }

    public static CompostBinLocations forPosition(int x, int y) {
      for (CompostBinLocations compostBinLocations : CompostBinLocations.values()) {
        if (compostBinLocations.x == x && compostBinLocations.y == y) {
          return compostBinLocations;
        }
      }
      return null;
    }

    public int getCompostIndex() {
      return compostIndex;
    }

    public int getBinObjectId() {
      return binObjectId;
    }

    public int getObjectFace() {
      return objectFace;
    }
  }

  public enum CompostBinStages {
    FIRST_TYPE(7808, 7813, 7809, 7810, 7811, 7812, 7814, 7815, 7816, 7817, 7828, 7829, 7830, 7831),
    SECOND_TYPE(7818, 7823, 7819, 7820, 7821, 7822, 7824, 7825, 7826, 7827, 7832, 7833, 7834, 7835);

    private static final Map<Integer, CompostBinStages> bins =
        new HashMap<Integer, CompostBinStages>();

    static {
      for (CompostBinStages data : CompostBinStages.values()) {
        bins.put(data.binEmpty, data);
      }
    }

    private final int binEmpty;
    private final int closedBin;
    private final int binWithCompostable;
    private final int binFullOfCompostable;
    private final int binWithSuperCompostable;
    private final int binFullOFSuperCompostable;
    private final int binWithCompost;
    private final int binFullOfCompost;
    private final int binWithSuperCompost;
    private final int binFullOfSuperCompost;
    private final int binWithTomatoes;
    private final int binFullOfTomatoes;
    private final int binWithRottenTomatoes;
    private final int binFullOfRottenTomatoes;

    CompostBinStages(
        int binEmpty,
        int closedBin,
        int binWithCompostable,
        int binFullOfCompostable,
        int binWithSuperCompostable,
        int binFullOFSuperCompostable,
        int binWithCompost,
        int binFullOfCompost,
        int binWithSuperCompost,
        int binFullOfSuperCompost,
        int binWithTomatoes,
        int binFullOfTomatoes,
        int binWithRottenTomatoes,
        int binFullOfRottenTomatoes) {
      this.binEmpty = binEmpty;
      this.closedBin = closedBin;
      this.binWithCompostable = binWithCompostable;
      this.binFullOfCompostable = binFullOfCompostable;
      this.binWithSuperCompostable = binWithSuperCompostable;
      this.binFullOFSuperCompostable = binFullOFSuperCompostable;
      this.binWithCompost = binWithCompost;
      this.binFullOfCompost = binFullOfCompost;
      this.binWithSuperCompost = binWithSuperCompost;
      this.binFullOfSuperCompost = binFullOfSuperCompost;
      this.binWithTomatoes = binWithTomatoes;
      this.binFullOfTomatoes = binFullOfTomatoes;
      this.binWithRottenTomatoes = binWithRottenTomatoes;
      this.binFullOfRottenTomatoes = binFullOfRottenTomatoes;
    }

    public static CompostBinStages forId(int binId) {
      return bins.get(binId);
    }

    public int getBinEmpty() {
      return binEmpty;
    }

    public int getClosedBin() {
      return closedBin;
    }

    public int getBinWithCompostable() {
      return binWithCompostable;
    }

    public int getBinFullOfCompostable() {
      return binFullOfCompostable;
    }

    public int getBinWithSuperCompostable() {
      return binWithSuperCompostable;
    }

    public int getBinFullOFSuperCompostable() {
      return binFullOFSuperCompostable;
    }

    public int getBinWithCompost() {
      return binWithCompost;
    }

    public int getBinFullOfCompost() {
      return binFullOfCompost;
    }

    public int getBinWithSuperCompost() {
      return binWithSuperCompost;
    }

    public int getBinFullOfSuperCompost() {
      return binFullOfSuperCompost;
    }

    public int getBinWithTomatoes() {
      return binWithTomatoes;
    }

    public int getBinFullOfTomatoes() {
      return binFullOfTomatoes;
    }

    public int getBinWithRottenTomatoes() {
      return binWithRottenTomatoes;
    }

    public int getBinFullOfRottenTomatoes() {
      return binFullOfRottenTomatoes;
    }
  }
}
