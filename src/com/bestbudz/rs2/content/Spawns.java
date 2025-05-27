package com.bestbudz.rs2.content;

import java.util.HashMap;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class Spawns {

	public enum SpawnData {
		POTIONS(115069, new Item(13066)),
		FOODS(115070, new Item(391, 28)),
		BARRAGE_RUNES(115070, new Item(560, 500), new Item(555, 500), new Item(565, 500)),
		VENGEANCE_RUNES(115071, new Item(560, 500), new Item(557, 500), new Item(9075, 500));

		private final int buttonID;
		private final Item items[];

		private SpawnData(int buttonID, Item... items) {
		this.buttonID = buttonID;
		this.items = items;
		}

		private static final HashMap<Integer, SpawnData> SPAWNS = new HashMap<Integer, SpawnData>();

		static {
			for (final SpawnData packs : SpawnData.values()) {
				SpawnData.SPAWNS.put(packs.buttonID, packs);
			}
		}
	}

	public static boolean handle(Stoner stoner, int buttonID) {
	// Get the item
	SpawnData data = SpawnData.SPAWNS.get(buttonID);

	// If item is nulled; return
	if (data == null) {
		return false;
	}

	// Return is stoner does not have enough box space
	if (stoner.getBox().getFreeSlots() <= data.items.length) {
		stoner.send(new SendMessage("You need at least " + data.items.length + " available box spaces to do this!"));
		return false;
	}

	// Add all the item pieces to box
	stoner.getBox().addItems(data.items);

	// Send message of successful opening
	stoner.send(new SendMessage("You have spawned some " + Utility.capitalize(data.name().toLowerCase().replaceAll("_", " ")) + "."));
	return true;
	}

}
