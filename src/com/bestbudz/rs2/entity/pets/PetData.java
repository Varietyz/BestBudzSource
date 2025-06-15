package com.bestbudz.rs2.entity.pets;

/**
 * Pet data enumeration - maintains original structure and naming
 */
public enum PetData {
	KALPHITE_PRINCESS_FLY(12654, 6637),
	KALPHITE_PRINCESS_BUG(12647, 6638),
	SMOKE_DEVIL(12648, 6655),
	DARK_CORE(12816, 318),
	PRINCE_BLACK_DRAGON(12653, 4000),
	GREEN_SNAKELING(12921, 2130),
	RED_SNAKELING(12939, 2131),
	BLUE_SNAKELING(12940, 2132),
	CHAOS_ELEMENT(11995, 5907),
	KREE_ARRA(12649, 4003),
	CALLISTO(13178, 497),
	SCORPIAS_OFFSPRING(13181, 5547),
	VENENATIS(13177, 495),
	VETION_PURPLE(13179, 5559),
	VETION_ORANGE(13180, 5560),
	BABY_MOLE(12646, 6635),
	KRAKEN(12655, 6640),
	DAGANNOTH_SUPRIME(12643, 4006),
	DAGANNOTH_RIME(12644, 4007),
	DAGANNOTH_REX(12645, 4008),
	GENERAL_GRAARDOR(12650, 4001),
	COMMANDER_ZILYANA(12651, 4009),
	KRIL_TSUTSAROTH(12652, 4004),
	IMP(9952, 5008),
	KEBBIT(9953, 1347),
	BUTTERFLY(9970, 1854),
	GIANT_EAGLE(9974, 5317),
	BLACK_CHINCHOMPA(11959, 2912),
	GNOME(3257, 4233),
	CHICKEN(5609, 6367),
	HELLHOUND(8137, 3133),
	BABY_DRAGON(8134, 137),
	DEMON(8138, 142),
	ROCNAR(8305, 143),
	FLAMBEED(8304, 4881),
	TENTACLE(8303, 5535),
	DEATH(5567, 12840);

	private final int itemID;
	public final int npcID;

	PetData(int itemID, int npcID) {
		this.itemID = itemID;
		this.npcID = npcID;
	}

	public static PetData forItem(int id) {
		for (PetData data : PetData.values()) {
			if (data.itemID == id) return data;
		}
		return null;
	}

	public static PetData forNPC(int id) {
		for (PetData data : PetData.values()) {
			if (data.npcID == id) return data;
		}
		return null;
	}

	public int getItem() {
		return itemID;
	}

	public int getNPC() {
		return npcID;
	}

	public String getName() {
		return name().toLowerCase().replace("_", " ");
	}
}