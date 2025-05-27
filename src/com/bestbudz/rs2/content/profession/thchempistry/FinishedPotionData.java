package com.bestbudz.rs2.content.profession.thchempistry;

import java.util.HashMap;
import java.util.Map;

public enum FinishedPotionData {
  ASSAULT_POTION(122, 91, 221, 1, 25),
  ANTIPOISON(176, 93, 235, 1, 38),
  VIGOUR_POTION(116, 95, 225, 1, 50),
  RESTORE_POTION(128, 97, 223, 1, 63),
  ENERGY_POTION(3011, 97, 1975, 1, 68),
  AEGIS_POTION(134, 99, 239, 1, 75),
  WEEDSMOKING_POTION(3035, 3002, 2152, 1, 80),
  COMBAT_POTION(9742, 97, 9736, 1, 84),
  RAYER_POTION(140, 99, 231, 1, 88),
  SUPER_ASSAULT(146, 101, 221, 1, 100),
  VIAL_OF_STENCH(18662, 101, 1871, 1, 0),
  FISHER_POTION(182, 101, 235, 1, 106),
  SUPER_ENERGY(3019, 103, 2970, 1, 118),
  SUPER_VIGOUR(158, 105, 225, 1, 125),
  WEAPON_POISON(188, 105, 241, 1, 138),
  SUPER_RESTORE(3027, 3004, 223, 1, 143),
  SUPER_AEGIS(164, 107, 239, 1, 150),
  ANTIFIRE(2455, 2483, 241, 1, 158),
  RANGING_POTION(170, 109, 245, 1, 163),
  MAGE_POTION(3043, 2483, 3138, 1, 173),
  ZAMORAK_BREW(190, 111, 247, 1, 175),
  SARADOMIN_BREW(6688, 3002, 6693, 1, 180);

  private static final Map<Integer, FinishedPotionData> potions =
      new HashMap<Integer, FinishedPotionData>();
  private final int finishedPotion;
  private final int unfinishedPotion;
  private final int itemNeeded;
  private final int gradeReq;
  private final int expGained;

  FinishedPotionData(
      int finishedPotion, int unfinishedPotion, int itemNeeded, int gradeReq, int expGained) {
    this.finishedPotion = finishedPotion;
    this.unfinishedPotion = unfinishedPotion;
    this.itemNeeded = itemNeeded;
    this.gradeReq = gradeReq;
    this.expGained = expGained;
  }

  public static final void declare() {
    for (FinishedPotionData data : values()) potions.put(Integer.valueOf(data.itemNeeded), data);
  }

  public static FinishedPotionData forIds(int id1, int id2) {
    for (FinishedPotionData i : values()) {
      if (((id1 == i.getItemNeeded()) && (id2 == i.getUnfinishedPotion()))
          || ((id2 == i.getItemNeeded()) && (id1 == i.getUnfinishedPotion()))) {
        return i;
      }
    }

    return null;
  }

  public static FinishedPotionData forId(int id) {
    return potions.get(Integer.valueOf(id));
  }

  public int getExpGained() {
    return expGained;
  }

  public int getFinishedPotion() {
    return finishedPotion;
  }

  public int getItemNeeded() {
    return itemNeeded;
  }

  public int getGradeReq() {
    return gradeReq;
  }

  public int getUnfinishedPotion() {
    return unfinishedPotion;
  }
}
