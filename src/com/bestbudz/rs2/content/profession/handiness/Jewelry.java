package com.bestbudz.rs2.content.profession.handiness;

import com.bestbudz.rs2.entity.item.Item;
import java.util.HashMap;
import java.util.Map;

public enum Jewelry {
  GOLD_RING(1636, 1, 15, 1592, 2357),
  SAPPHIRE_RING(1638, 1, 40, 1592, 2357, 1607),
  EMERALD_RING(1640, 1, 55, 1592, 2357, 1605),
  RUBY_RING(1642, 1, 70, 1592, 2357, 1603),
  DIAMOND_RING(1644, 1, 85, 1592, 2357, 1601),
  DRAGONSTONE_RING(1646, 1, 100, 1592, 2357, 1615),
  ONYX_RING(6576, 1, 85, 1592, 2357, 6573),

  GOLD_NECKLACE(1655, 6, 1, 1597, 2357),
  SAPPHIRE_NECKLACE(1657, 1, 55, 1597, 2357, 1607),
  EMERALD_NECKLACE(1659, 1, 60, 1597, 2357, 1605),
  RUBY_NECKLACE(1661, 1, 75, 1597, 2357, 1603),
  DIAMOND_NECKLACE(1663, 1, 90, 1597, 2357, 1601),
  DRAGONSTONE_NECKLACE(1665, 1, 105, 1597, 2357, 1615),
  ONYX_NECKLACE(6578, 1, 120, 1597, 2357, 6573),

  GOLD_AMULET(1674, 8, 1, 1595, 2357),
  SAPPHIRE_AMULET(1676, 1, 65, 1595, 2357, 1607),
  EMERALD_AMULET(1678, 1, 61, 1595, 2357, 1605),
  RUBY_AMULET(1680, 50, 1, 1595, 2357, 1603),
  DIAMOND_AMULET(1682, 1, 100, 1595, 2357, 1601),
  DRAGONSTONE_AMULET(1684, 1, 125, 1595, 2357, 1615),
  ONYX_AMULET(6580, 1, 150, 1595, 2357, 6573);

  private static final Map<Integer, Jewelry> jewelry = new HashMap<Integer, Jewelry>();
  private final Item reward;
  private final short gradeRequired;
  private final double experienceGain;
  private final int[] materialsRequired;

  Jewelry(int rewardId, int gradeRequired, double experienceGain, int... materialsRequired) {
    this.reward = new Item(rewardId);
    this.gradeRequired = ((short) gradeRequired);
    this.experienceGain = experienceGain;
    this.materialsRequired = materialsRequired;
  }

  public static final void declare() {
    for (Jewelry jewel : values()) {
      jewelry.put(Integer.valueOf(jewel.getReward().getId()), jewel);
    }
  }

  public static Jewelry forReward(int id) {
    return jewelry.get(Integer.valueOf(id));
  }

  public double getExperience() {
    return experienceGain;
  }

  public int[] getMaterialsRequired() {
    return materialsRequired;
  }

  public short getRequiredGrade() {
    return gradeRequired;
  }

  public Item getReward() {
    return reward;
  }
}
