package com.bestbudz.rs2.content.profession.mage.spells;

import java.util.HashMap;

import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItemsAlt;

/**
 * Handles Bolt Enchanting
 * 
 * @author Jaybane
 *
 */
public class BoltEnchanting {

	public enum BoltData {
		OPAL(879, 9236, 4, new Item[] { new Item(564, 1), new Item(556, 2) }),
		SAPPHIRE(9337, 9240, 7, new Item[] { new Item(564, 1), new Item(555, 1), new Item(558, 1) }),
		JADE(9335, 9237, 14, new Item[] { new Item(564, 1), new Item(557, 2) }),
		PEARL(880, 9238, 24, new Item[] { new Item(564, 1), new Item(555, 2) }),
		EMERALD(9338, 9241, 27, new Item[] { new Item(564, 1), new Item(556, 3), new Item(561, 1) }),
		RED_TOPAZ(9336, 9239, 29, new Item[] { new Item(564, 1), new Item(554, 2) }),
		RUBY(9339, 9242, 49, new Item[] { new Item(564, 1), new Item(554, 5), new Item(565, 1) }),
		DIAMOND(9340, 9243, 57, new Item[] { new Item(564, 1), new Item(557, 10), new Item(563, 2) }),
		DRAGONSTONE(9341, 9244, 68, new Item[] { new Item(564, 1), new Item(557, 15), new Item(566, 1) }),
		ONYX(9342, 9245, 87, new Item[] { new Item(564, 1), new Item(554, 20), new Item(560, 1) });

		private final int bolt;
		private final int enchantedBolt;
		private final int gradeRequired;
		private final Item[] runesRequired;

		private BoltData(int bolt, int enchantedBolt, int gradeRequired, Item... runesRequired) {
		this.bolt = bolt;
		this.enchantedBolt = enchantedBolt;
		this.gradeRequired = gradeRequired;
		this.runesRequired = runesRequired;
		}

		public int getBolt() {
		return bolt;
		}

		public int getEnchanted() {
		return enchantedBolt;
		}

		public int getGrade() {
		return gradeRequired;
		}

		public Item[] getRunes() {
		return runesRequired;
		}

		private static final HashMap<Integer, BoltData> boltMap = new HashMap<Integer, BoltData>();

		static {
			for (final BoltData bolts : BoltData.values()) {
				BoltData.boltMap.put(bolts.enchantedBolt, bolts);
			}
		}
	}

	public static void handle(Stoner stoner, int itemId) {
	BoltData data = BoltData.boltMap.get(itemId);

	if (data == null) {
		return;
	}

	if (stoner.getInterfaceManager().main != 42750) {
		stoner.send(new SendRemoveInterfaces());
		return;
	}

	String boltName = GameDefinitionLoader.getItemDef(data.getBolt()).getName();

	if (stoner.getProfession().getGrades()[Professions.MAGE] < data.getGrade()) {
		stoner.send(new SendMessage("@red@You need a Mage grade of " + data.getGrade() + " to enchant " + boltName + " bolts."));
		return;
	}

	if (!stoner.getBox().hasItemAmount(data.getBolt(), 10)) {
		stoner.send(new SendMessage("@red@You need 10 " + boltName + " to do this!"));
		return;
	}

	if (!stoner.getBox().hasAllItems(data.getRunes())) {
		stoner.send(new SendMessage("@red@You do not have the required runes to do this!"));
		return;
	}

	for (int index = 0; index < data.getRunes().length; index++) {
		stoner.getBox().remove(data.getRunes()[index]);
	}

	stoner.getBox().remove(data.getBolt(), 10);
	stoner.getBox().add(data.getEnchanted(), 10);
	stoner.send(new SendMessage("@red@You have enchanted 10 " + boltName + (boltName.endsWith("bolts") ? "." : " bolts.")));

	String color = "@red@";

	for (int i = 0; i < runes.length; i++) {
		if (!stoner.getBox().hasItemAmount(runes[i][0], runes[i][1])) {
			color = "@red@";
		} else {
			color = "@gre@";
		}
		int amount = stoner.getBox().getItemAmount(runes[i][0]);

		stoner.send(new SendString(color + (amount >= runes[i][1] ? runes[i][1] + "" : amount) + "/" + runes[i][1], 42766 + i));
	}

	stoner.getProfession().addExperience(Professions.MAGE, 250);

	}

	private static int[] grades = { 4, 7, 14, 24, 27, 29, 49, 57, 68, 87 };

	private static int[][] runes = { { 564, 1 }, { 556, 2 }, { 564, 1 }, { 555, 1 }, { 558, 1 }, { 564, 1 }, { 557, 2 }, { 564, 1 }, { 555, 2 }, { 564, 1 }, { 556, 3 }, { 561, 1 }, { 564, 1 }, { 554, 2 }, { 564, 1 }, { 554, 5 }, { 565, 1 }, { 564, 1 }, { 557, 10 }, { 563, 2 }, { 564, 1 }, { 557, 15 }, { 566, 1 }, { 564, 1 }, { 554, 20 }, { 560, 1 } };

	private static Item[] item = { new Item(9236), new Item(9240), new Item(9237), new Item(9238), new Item(9241), new Item(9239), new Item(9242), new Item(9243), new Item(9244), new Item(9245) };

	public static void open(Stoner stoner) {
	String color = "@red@";
	for (int i = 0; i < item.length; i++) {
		stoner.getClient().queueOutgoingPacket(new SendUpdateItemsAlt(42752, item[i].getId(), 10, i));
	}
	for (int i = 0; i < grades.length; i++) {
		if (stoner.getProfession().getGrades()[Professions.MAGE] < grades[i]) {
			color = "@red@";
		} else {
			color = "@gr3@";
		}
		stoner.send(new SendString(color + "Mage " + grades[i], 42756 + i));
	}
	for (int i = 0; i < runes.length; i++) {
		if (!stoner.getBox().hasItemAmount(runes[i][0], runes[i][1])) {
			color = "@red@";
		} else {
			color = "@gre@";
		}
		int amount = stoner.getBox().getItemAmount(runes[i][0]);

		stoner.send(new SendString(color + (amount >= runes[i][1] ? runes[i][1] + "" : amount) + "/" + runes[i][1], 42766 + i));
	}
	stoner.send(new SendInterface(42750));
	}

}
