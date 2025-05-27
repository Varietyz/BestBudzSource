package com.bestbudz.rs2.content.profession.cultivation;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class ChopTree {

  public static final int[] COMMON_SEEDS = {5312, 5283, 5284, 5285, 5286, 5313};
  public static final int[] UNCOMMON_SEEDS = {5314, 5288, 5287, 5315, 5289};
  public static final int[] RARE_SEEDS = {5316, 5290};
  public static final int[] VERY_RARE_SEEDS = {5317};
  public static final int[] COMMON_RING = {1635, 1637};
  public static final int[] UNCOMMON_RING = {1639};
  public static final int[] RARE_RING = {1641};
  public static final int[] VERY_RARE_RING = {1643};
  private static final Axe[] axes = new Axe[1];
  private static final Tree[] trees = new Tree[11];

  static {
    axes[0] = new Axe(6575, 1, -1, 2846, 0);
    trees[0] = new Tree(new int[] {2023}, 1, 200, 2862, 3371, -1, -1);
    trees[1] =
        new Tree(
            new int[] {
              1276, 1277, 1278, 1279, 1280, 1282, 1283, 1284, 1285, 1286, 1289, 1290, 1291, 1315,
              1316, 1318, 1319, 1330, 1331, 1332, 1333, 1365, 1383, 1384, 2409, 3033, 3034, 3035,
              3036, 3881, 3882, 3883, 5902, 5903, 5904
            },
            1,
            200,
            1511,
            1342,
            -1,
            -1);
    trees[2] = new Tree(new int[] {1281, 2037}, 1, 200, 1521, 1356, -1, -1);
    trees[3] = new Tree(new int[] {1308, 5551, 5552, 5553}, 1, 200, 1519, 7399, -1, -1);
    trees[4] = new Tree(new int[] {9036}, 1, 200, 6333, 9037, -1, -1);
    trees[5] = new Tree(new int[] {1307, 4677}, 1, 200, 1517, 1343, -1, -1);
    trees[6] = new Tree(new int[] {2289, 4060}, 1, 200, 3239, 2310, -1, -1);
    trees[7] = new Tree(new int[] {9034}, 1, 300, 6332, 9035, -1, -1);
    trees[8] = new Tree(new int[] {1309}, 1, 400, 1515, 7402, -1, -1);
    trees[9] = new Tree(new int[] {1306}, 1, 500, 1513, 7401, -1, -1);
    trees[10] = new Tree(new int[] {1292}, 1, 250, 771, 1513, -1, -1);
  }

  public static boolean handleNest(Stoner stoner, int itemId) {
    int[] commonItems, uncommonItems, rareItems, veryRareItems;
    switch (itemId) {
      case 5070:
        stoner.getBox().remove(itemId, 1);
        stoner.getBox().add(5075, 1);
        stoner.getBox().add(5076, 1);
        return true;
      case 5071:
        stoner.getBox().remove(itemId, 1);
        stoner.getBox().add(5075, 1);
        stoner.getBox().add(5078, 1);
        return true;
      case 5072:
        stoner.getBox().remove(itemId, 1);
        stoner.getBox().add(5075, 1);
        stoner.getBox().add(5077, 1);
        return true;
      case 5073:
        commonItems = COMMON_SEEDS;
        uncommonItems = UNCOMMON_SEEDS;
        rareItems = RARE_SEEDS;
        veryRareItems = VERY_RARE_SEEDS;
        break;
      case 5074:
        commonItems = COMMON_RING;
        uncommonItems = UNCOMMON_RING;
        rareItems = RARE_RING;
        veryRareItems = VERY_RARE_RING;
        break;
      default:
        return false;
    }
    int randomNumber = Utility.random(100), finalItem;
    if (randomNumber <= 60) finalItem = commonItems[Utility.random(commonItems.length - 1)];
    else if (randomNumber <= 80)
      finalItem = uncommonItems[Utility.random(uncommonItems.length - 1)];
    else if (randomNumber <= 95) finalItem = rareItems[Utility.random(rareItems.length - 1)];
    else finalItem = veryRareItems[Utility.random(veryRareItems.length - 1)];

    stoner.send(new SendMessage("You search the nest, hoping you will find weed."));
    stoner.getBox().remove(itemId, 1);
    stoner.getBox().add(5075, 1);
    stoner.getBox().add(finalItem, 1);
    return true;
  }

  public static Axe getAxe(Stoner stoner) {
    final int axeIndex = getAxeIndex(stoner);
    if (axeIndex == -1) {
      return null;
    }
    return axes[axeIndex];
  }

  public static Tree getTree(int objectId) {
    final int treeIndex = getTreeIndex(objectId);
    if (treeIndex == -1) {
      return null;
    }
    return trees[treeIndex];
  }

  public static int getAxeIndex(Stoner stoner) {
    for (int i = 0; i < axes.length; i++) {
      if (stoner.getEquipment().getItems()[12].getId() == (axes[i].getId())) {
        return i;
      }
    }
    for (int i = axes.length - 1; i >= 0; i--) {
      if (stoner.getEquipment().getItems()[12].getId() == (axes[i].getId())) {
        if (stoner.getGrades()[Professions.LUMBERING] >= axes[i].getGrade()) {
          return i;
        }
      }
    }
    return -1;
  }

  public static int getTreeIndex(final int objectId) {
    for (int i = 0; i < trees.length; i++) {
      int[] ids = trees[i].getId();
      for (int id : ids) {
        if (objectId == id) {
          return i;
        }
      }
    }
    return -1;
  }

  public static class Axe {

    private final int id;
    private final int grade;
    private final int head;
    private final int animation;
    private final int bonus;

    public Axe(int id, int grade, int head, int animation, int bonus) {
      this.id = id;
      this.grade = grade;
      this.head = head;
      this.animation = animation;
      this.bonus = bonus;
    }

    public int getId() {
      return id;
    }

    public int getGrade() {
      return grade;
    }

    public int getHead() {
      return head;
    }

    public int getAnimation() {
      return animation;
    }

    public int getBonus() {
      return bonus;
    }
  }

  public static class Tree {
    private final int[] id;
    private final int grade;
    private final double xp;
    private final int log;
    private final int stump;
    private final int respawnTime;
    private final int decayChance;

    public Tree(
        int[] id, int grade, double xp, int log, int stump, int respawnTime, int decayChance) {
      this.id = id;
      this.grade = grade;
      this.xp = xp;
      this.log = log;
      this.stump = stump;
      this.respawnTime = respawnTime;
      this.decayChance = decayChance;
    }

    public int[] getId() {
      return id;
    }

    public int getGrade() {
      return grade;
    }

    public double getXP() {
      return xp;
    }

    public int getLog() {
      return log;
    }

    public int getStump() {
      return stump;
    }

    public int getRespawnTime() {
      return respawnTime;
    }

    public int getDecayChance() {
      return decayChance;
    }
  }
}
