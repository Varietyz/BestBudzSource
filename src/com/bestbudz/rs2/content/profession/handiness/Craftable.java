package com.bestbudz.rs2.content.profession.handiness;

import java.util.HashMap;
import java.util.Map;

public enum Craftable {
  LEATHERGLOVES(1741, 1060, 1, 113.81D, 1),
  LEATWEEDOOTS(1741, 1062, 1, 116.25D, 1),
  LEATHERCOWL(1741, 1168, 1, 118.5D, 1),
  LEATHERVAMBS(1741, 1064, 1, 122.0D, 1),
  LEATWEEDODY(1741, 1130, 1, 125.0D, 1),
  LEATHERCHAPS(1741, 1096, 1, 127.0D, 1),
  COIF(1741, 1170, 1, 137.0D, 1);

  private static final Map<Integer, Craftable> craftableRewards = new HashMap<Integer, Craftable>();
  private final int itemId;
  private final int outcome;
  private final int requiredGrade;
  private final int requiredAmount;
  private final double experience;

  Craftable(int itemId, int outcome, int requiredGrade, double experience, int requiredAmount) {
    this.itemId = itemId;
    this.outcome = outcome;
    this.requiredGrade = requiredGrade;
    this.experience = experience;
    this.requiredAmount = requiredAmount;
  }

  public static final void declare() {
    for (Craftable craftable : values())
      craftableRewards.put(Integer.valueOf(craftable.getOutcome()), craftable);
  }

  public static Craftable forReward(int id) {
    return craftableRewards.get(Integer.valueOf(id));
  }

  public double getExperience() {
    return experience;
  }

  public int getItemId() {
    return itemId;
  }

  public int getOutcome() {
    return outcome;
  }

  public int getRequiredAmount() {
    return requiredAmount;
  }

  public int getRequiredGrade() {
    return requiredGrade;
  }
}
