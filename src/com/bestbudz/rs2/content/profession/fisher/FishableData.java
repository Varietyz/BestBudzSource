package com.bestbudz.rs2.content.profession.fisher;

import java.util.HashMap;
import java.util.Map;

public class FishableData {

	private static final int FISHER_NECKLACE = 6577;

	public static enum Fishable {
		SHRIMP(317, FISHER_NECKLACE, 1, 110.0D, -1),
		CRAYFISH(13435, 13431, 1, 110.0D, -1),
		KARAMBWANJI(3150, FISHER_NECKLACE, 1, 15.0D, -1),
		SARDINE(327, FISHER_NECKLACE, 1, 120.0D, FISHER_NECKLACE),

		FISH_BONES(6904, FISHER_NECKLACE, 91, 200.0D, -1),
		DRAGON_ARROWS(11212, FISHER_NECKLACE, 94, 240.0D, -1),
		FLAX(1779, FISHER_NECKLACE, 1, 100.0D, -1),

		HERRING(345, FISHER_NECKLACE, 1, 130.0D, FISHER_NECKLACE),
		ANCHOVIES(321, FISHER_NECKLACE, 1, 140.0D, -1),
		MACKEREL(353, FISHER_NECKLACE, 1, 120.0D, -1),
		TROUT(335, FISHER_NECKLACE, 1, 150.0D, -1),
		COD(341, FISHER_NECKLACE, 1, 145.0D, -1),
		PIKE(349, FISHER_NECKLACE, 1, 160.0D, FISHER_NECKLACE),
		SLIMY_EEL(3379, FISHER_NECKLACE, 1, 165.0D, FISHER_NECKLACE),
		SALMON(331, FISHER_NECKLACE, 1, 170.0D, 314),
		FROG_SPAWN(5004, FISHER_NECKLACE, 1, 175.0D, -1),
		TUNA(359, FISHER_NECKLACE, 1, 180.0D, -1),
		CAVE_EEL(5001, FISHER_NECKLACE, 1, 180.0D, FISHER_NECKLACE),
		LOBSTER(377, FISHER_NECKLACE, 1, 190.0D, -1),
		BASS(363, FISHER_NECKLACE, 1, 200.0D, -1),
		SWORD_FISH(371, FISHER_NECKLACE, 1, 200.0D, -1),
		LAVA_EEL(2148, FISHER_NECKLACE, 1, 230.0D, FISHER_NECKLACE),
		MONK_FISH(7944, FISHER_NECKLACE, 1, 110.0D, -1),
		KARAMBWAN(3142, 3157, 1, 100.0D, -1),
		SHARK(383, FISHER_NECKLACE, 1, 125.0D, -1),
		SEA_TURTLE(395, -1, 1, 138.0D, -1),
		MANTA_RAY(389, FISHER_NECKLACE, 1, 155.0D, -1),
		DARK_CRAB(11934, 301, 80, 240.0D, FISHER_NECKLACE);

		public static final void declare() {
		for (Fishable fishes : values())
			fish.put(Integer.valueOf(fishes.getRawFishId()), fishes);
		}

		private short rawFishId;
		private short toolId;
		private short gradeRequired;
		private short baitRequired;

		private double experienceGain;

		private static Map<Integer, Fishable> fish = new HashMap<Integer, Fishable>();

		public static Fishable forId(int rawFishId) {
		return fish.get(Integer.valueOf(rawFishId));
		}

		private Fishable(int rawFishId, int toolId, int gradeRequired, double experienceGain, int baitRequired) {
		this.rawFishId = ((short) rawFishId);
		this.toolId = ((short) toolId);
		this.gradeRequired = ((short) gradeRequired);
		this.experienceGain = experienceGain;
		this.baitRequired = ((short) baitRequired);
		}

		public short getBaitRequired() {
		return baitRequired;
		}

		public double getExperience() {
		return experienceGain;
		}

		public short getRawFishId() {
		return rawFishId;
		}

		public short getRequiredGrade() {
		return gradeRequired;
		}

		public short getToolId() {
		return toolId;
		}
	}
}
