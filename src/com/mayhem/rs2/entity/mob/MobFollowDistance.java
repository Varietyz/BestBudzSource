package com.mayhem.rs2.entity.mob;

import java.util.HashMap;
import java.util.Map;

public enum MobFollowDistance {
	
	BOSSES(new int[] { 7605, 7606, 1233, 1498, 1833, 3474, 8133, 6247, 3129, 84, 3358, 1118, 6766, 1540, 6619, 5862, 6248, 6252, 6250, 6204, 6203, 6208, 6265,
			6263, 6261, 6260, 6225, 6227, 6223, 6222, 7530, 7542, 7528, 7538 }, 100);

	private int[] mobId;
	private int distance;
	private static Map<Integer, MobFollowDistance> mobs;

	static {
		mobs = new HashMap<Integer, MobFollowDistance>();

		for (MobFollowDistance def : values()) {
			for (int k = 0; k < def.mobId.length; k++) {
				mobs.put(def.mobId[k], def);
			}
		}
	}

	private static MobFollowDistance forId(int id) {
		return mobs.get(Integer.valueOf(id));
	}

	public static int getDistance(int mobId) {
		MobFollowDistance def = forId(mobId);
		return def == null ? 100 : def.getDistance();
	}

	private MobFollowDistance(int[] mobId, int distance) {
		this.mobId = mobId;
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}

	public int[] getMobId() {
		return mobId;
	}
}
