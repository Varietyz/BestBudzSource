package com.mayhem.rs2.content.houses;

import java.util.Arrays;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.item.Item;



public enum HouseObject {
	/**
	 * TREES
	 */
	TREE(0, 1278, 10, ObjectType.TREES, new Item(8419, 1)),
	OAK_TREE(0, 11756, 10, ObjectType.TREES, new Item(8421, 1)),
	MAPLE_TREE(0, 11762, 10, ObjectType.TREES, new Item(8425, 1)),
	YEW_TREE(0, 11758, 10, ObjectType.TREES, new Item(8427, 1)),
	MAGIC_TREE(0, 11764, 10, ObjectType.TREES, new Item(8429, 1)),
	/**
	 * ORES
	 */
	COPPER_ORE(0, 13708, 10, ObjectType.ORES, new Item(436, 3)),
	TIN_ORE(0, 13712, 10, ObjectType.ORES, new Item(438, 3)),
	IRON_ORE(0, 13710, 10, ObjectType.ORES, new Item(440, 3)),
	SILVER_ORE(0, 13716, 10, ObjectType.ORES, new Item(442, 3)),
	COAL_ORE(0, 13706, 10, ObjectType.ORES, new Item(453, 3)),
	GOLD_ORE(0, 13707, 10, ObjectType.ORES, new Item(444, 3)),
	MITHRIL_ORE(0, 13718, 10, ObjectType.ORES, new Item(447, 3)),
	ADAMANT_ORE(0, 13720, 10, ObjectType.ORES, new Item(449, 3)),
	RUNITE_ORE(0, 14175, 10, ObjectType.ORES, new Item(451, 3)),
	/**
	 * ALTARS
	 */
	ALTAR(0, 409, 10, ObjectType.ALTARS, new Item(526, 10), new Item(4037, 1)),
	CHAOS_ALTAR(0, 412, 10, ObjectType.ALTARS, new Item(532, 10), new Item(4039, 1)),
	ANCIENT_ALTAR(0, 6552, 10, ObjectType.ALTARS, new Item(536, 10), new Item(7633, 1)),
	/**
	 * STALLS
	 */
	FOOD_STALL(0, 4875, 10, ObjectType.STALLS, new Item(3162, 20)),
	GENERAL_STALL(0, 4876, 10, ObjectType.STALLS, new Item(1887, 20)),
	CRAFTING_STALL(0, 4874, 10, ObjectType.STALLS, new Item(1635, 20)),
	MAGIC_STALL(0, 4877, 10, ObjectType.STALLS, new Item(8788, 20)),
	SCIMITAR_STALL(0, 4878, 10, ObjectType.STALLS, new Item(6721, 20)),

	/**
	 * MISC
	 */
	PILLAR(0, 6284, 10, ObjectType.MISC, new Item(995, 100000)),
	BANK(0, 11744, 10, ObjectType.MISC, new Item(995, 100000)),
	BENCH(0, 2323, 10, ObjectType.MISC, new Item(995, 100000)),
	FLOWERBED(0, 2136, 10, ObjectType.MISC, new Item(995, 100000)),
	ROSES(0, 23616, 10, ObjectType.MISC, new Item(995, 100000)),
	STOVE(0, 12269, 10, ObjectType.MISC, new Item(995, 100000)),
	FURNACE(0, 2030, 10, ObjectType.MISC, new Item(995, 100000)),
	ANVIL(0, 2097, 10, ObjectType.MISC, new Item(995, 100000)),
	BED(0, 424, 10, ObjectType.MISC, new Item(995, 100000)),
	FOUNTAIN(0, 2654, 10, ObjectType.MISC, new Item(995, 100000)),
	FIREPLACE(0, 24970, 10, ObjectType.MISC, new Item(995, 100000));

		

	private int buttonId, objectId, type;
	private ObjectType objectType;
	private Item[] materials;

	HouseObject(int buttonId, int objectId, int type, ObjectType objectType, Item... materials) {
		this.buttonId = buttonId;
		this.objectId = objectId;
		this.type = type;
		this.objectType = objectType;
		this.materials = materials;
	}


	public static Object[] getHouseObjects(ObjectType filter) {
		if (filter == null)
			return HouseObject.values();
		return Arrays.stream(HouseObject.values()).filter(o -> (o != null && o.getObjectType() == filter)).toArray();
	}

	public int getButtonId() {
		return buttonId;
	}

	public int getObjectId() {
		return objectId;
	}

	public int getType() {
		return type;
	}

	public ObjectType getObjectType() {
		return objectType;
	}

	public Item[] getMaterials() {
		return materials;
	}

	@Override
	public String toString() {
		return Utility.formatPlayerName(this.name().toLowerCase().replaceAll("_", " ").trim());
	}

	public static enum ObjectType {
		TREES,
		ORES,
		ALTARS,
		STALLS,
		FISHING,
		MISC;
	}
}
