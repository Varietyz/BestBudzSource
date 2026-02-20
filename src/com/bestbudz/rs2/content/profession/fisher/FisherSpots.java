package com.bestbudz.rs2.content.profession.fisher;

import static com.bestbudz.rs2.content.profession.fisher.Fisher.combined;
import java.util.HashMap;
import java.util.Map;

public enum FisherSpots {
	SMALL_NET_OR_BAIT(1518, combined()),
	LURE_OR_BAIT(1526, combined()),
	CAGE_OR_HARPOON(1519, combined()),
	LARGE_NET_OR_HARPOON(1520, combined()),
	HARPOON_OR_SMALL_NET(1534, combined()),
	MANTA_RAY(3019, combined()),
	DARK_CRAB(1536, combined());

	private static final Map<Integer, FisherSpots> fisherSpots =
		new HashMap<Integer, FisherSpots>();
	private final int id;
	public final Fishable[] option_1;

	FisherSpots(int id, Fishable[] option_1) {
		this.id = id;
		this.option_1 = option_1;
	}

	public static final void declare() {
		for (FisherSpots spots : values()) fisherSpots.put(Integer.valueOf(spots.getId()), spots);
	}

	public static FisherSpots forId(int id) {
		return fisherSpots.get(Integer.valueOf(id));
	}

	public int getId() {
		return id;
	}

	public Fishable[] getOption_1() {
		return option_1;
	}
}