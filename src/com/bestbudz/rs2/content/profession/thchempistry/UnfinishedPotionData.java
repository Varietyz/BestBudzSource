package com.bestbudz.rs2.content.profession.thchempistry;

import java.util.HashMap;
import java.util.Map;

public enum UnfinishedPotionData {
  KUSH_POTION(91, 249, 1),
  HAZE_POTION(93, 251, 1),
  OG_KUSH_POTION(95, 253, 1),
  POWERPLANT_POTION(97, 255, 1),
  CHEESE_HAZE_POTION(99, 257, 1),
  BUBBA_KUSH_POTION(3002, 2998, 1),
  SPIRIT_WEED_POTION(12181, 12172, 1),
  CHOCOLOPE_POTION(101, 259, 1),
  WERGALI_POTION(14856, 14854, 1),
  GORILLA_GLUE_POTION(103, 261, 1),
  JACK_HERER_POTION(105, 263, 1),
  DURBAN_POISON_POTION(3004, 3000, 1),
  AMNESIA_POTION(107, 265, 1),
  SUPER_SILVER_HAZE(2483, 2481, 1),
  GIRL_SCOUT_COOKIES_POTION(109, 267, 1),
  KHALIFA_KUSH_POTION(111, 269, 1);

  private static final Map<Integer, UnfinishedPotionData> potions =
      new HashMap<Integer, UnfinishedPotionData>();
  private final int unfinishedPotion;
  private final int weedNeeded;
  private final int gradeReq;

  UnfinishedPotionData(int unfinishedPotion, int weedNeeded, int gradeReq) {
    this.unfinishedPotion = unfinishedPotion;
    this.weedNeeded = weedNeeded;
    this.gradeReq = gradeReq;
  }

  public static final void declare() {
    for (UnfinishedPotionData data : values())
      potions.put(Integer.valueOf(data.getWeedNeeded()), data);
  }

  public static UnfinishedPotionData forId(int weedId) {
    return potions.get(Integer.valueOf(weedId));
  }

  public int getWeedNeeded() {
    return weedNeeded;
  }

  public int getGradeReq() {
    return gradeReq;
  }

  public int getUnfPotion() {
    return unfinishedPotion;
  }
}
