package com.bestbudz.rs2.content.profession.handiness;

import java.util.HashMap;
import java.util.Map;

public enum Glass {
  MOLTEN_GLASS(1775, 1, 120.0D, 1781),
  BEER_GLASS(1920, 1, 117.5D, 1775),
  CANDLE_LANTERN(4528, 1, 119.0D, 1775),
  OIL_LAMP(4523, 1, 125.0D, 1775),
  VIAL(230, 1, 135.0D, 1775),
  FISHBOWL(6668, 1, 142.5D, 1775),
  UNPOWDERED_ORD(568, 1, 152.5D, 1775),
  LANTERN_LENS(4543, 1, 155.0D, 1775),
  LIGHT_ORB(10974, 1, 170.0D, 1775);

  public static Map<Integer, Glass> glass = new HashMap<Integer, Glass>();
  short rewardId;
  short gradeRequired;
  double experience;
  int materialId;

  Glass(int rewardId, int gradeRequired, double experience, int materialId) {
    this.rewardId = ((short) rewardId);
    this.gradeRequired = ((short) gradeRequired);
    this.experience = experience;
    this.materialId = materialId;
  }

  public static final void declare() {
    for (Glass glassType : values()) glass.put(Integer.valueOf(glassType.getRewardId()), glassType);
  }

  public static Glass forReward(int id) {
    return glass.get(Integer.valueOf(id));
  }

  public double getExperience() {
    return experience;
  }

  public int getMaterialId() {
    return materialId;
  }

  public int getRequiredGrade() {
    return gradeRequired;
  }

  public int getRewardId() {
    return rewardId;
  }
}
