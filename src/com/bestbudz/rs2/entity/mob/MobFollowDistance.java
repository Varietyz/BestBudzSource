package com.bestbudz.rs2.entity.mob;

import java.util.HashMap;
import java.util.Map;

public enum MobFollowDistance {
  BOSSES(
      new int[] {
        8133, 6247, 6248, 6252, 6250, 6204, 6203, 6208, 6265, 6263, 6261, 6260, 6225, 6227, 6223,
        6222
      },
      40);

  private static final Map<Integer, MobFollowDistance> mobs;

  static {
    mobs = new HashMap<Integer, MobFollowDistance>();

    for (MobFollowDistance def : values()) {
      for (int k = 0; k < def.mobId.length; k++) {
        mobs.put(def.mobId[k], def);
      }
    }
  }

  private final int[] mobId;
  private final int distance;

  MobFollowDistance(int[] mobId, int distance) {
    this.mobId = mobId;
    this.distance = distance;
  }

  private static MobFollowDistance forId(int id) {
    return mobs.get(Integer.valueOf(id));
  }

  public static int getDistance(int mobId) {
    MobFollowDistance def = forId(mobId);
    return def == null ? 8 : def.getDistance();
  }

  public int getDistance() {
    return distance;
  }

  public int[] getMobId() {
    return mobId;
  }
}
