package com.bestbudz.rs2.content.profession.mage.weapons;

import com.bestbudz.core.definitions.ItemDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.Animation;
import com.bestbudz.rs2.entity.Graphic;
import com.bestbudz.rs2.entity.item.EquipmentConstants;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendChatBoxInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItemsAlt;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class TridentOfTheSeas {

	private static final DecimalFormat FORMATTER = new DecimalFormat("#.#");

	private static final int FULL = 2_500;

	private int tridentCharge;

	public TridentOfTheSeas(int tridentCharge) {
	this.tridentCharge = tridentCharge;
	FORMATTER.setRoundingMode(RoundingMode.FLOOR);
	}

	public static boolean itemOnItem(Stoner stoner, Item itemUsed, Item usedWith) {
	Item used = null;
	Item with = null;

	if (itemUsed.getId() == 11907 || itemUsed.getId() == 11908) {
		used = itemUsed.getSingle();
		with = usedWith.getSingle();
	} else if (usedWith.getId() == 11907 || usedWith.getId() == 11908) {
		used = usedWith.getSingle();
		with = itemUsed.getSingle();
	}

	if (used == null || with == null) {
		return false;
	}

	switch (with.getId()) {
	case 554:
	case 560:
	case 562:
	case 995:
		if (stoner.getBox().hasAllItems(new Item(554, 5), new Item(560), new Item(562), new Item(995, 500))) {
			int min = Integer.MAX_VALUE;
			for (Item item : stoner.getBox().getItems()) {
				if (item != null) {
					switch (item.getId()) {
					case 554:
					case 560:
					case 562:
					case 995:
						if (item.getAmount() / (item.getId() == 995 ? 500 : item.getId() == 554 ? 5 : 1) < min) {
							min = item.getAmount() / (item.getId() == 995 ? 500 : item.getId() == 554 ? 5 : 1);
						}
						break;
					}
				}
			}

			if (min + stoner.getSeasTrident().tridentCharge > FULL) {
				min = FULL - stoner.getSeasTrident().tridentCharge;
			}

			if (min > 0) {
				if (usedWith.getId() == 11908) {
					int slot = stoner.getBox().getItemSlot(usedWith.getId());
					stoner.getBox().get(slot).setId(11907);
				} else if (itemUsed.getId() == 11908) {
					int slot = stoner.getBox().getItemSlot(itemUsed.getId());
					stoner.getBox().get(slot).setId(11907);
				}

				stoner.getBox().remove(554, 5 * min);
				stoner.getBox().remove(560, min);
				stoner.getBox().remove(562, min);
				stoner.getBox().remove(995, 500 * min);
				stoner.getSeasTrident().tridentCharge = min + stoner.getSeasTrident().tridentCharge;
				stoner.getUpdateFlags().sendAnimation(new Animation(1979));
				stoner.getUpdateFlags().sendGraphic(new Graphic(1250, 40, false));
				DialogueManager.sendItem1(stoner, "You infuse the trident with @dre@" + stoner.getSeasTrident().tridentCharge + "</col> charge" + (stoner.getSeasTrident().tridentCharge > 1 ? "s" : "") + ".", 11907);
				check(stoner);
			}
		}
		return true;
	}

	return false;
	}

	public static void check(Stoner stoner) {
	TridentOfTheSeas trident = stoner.getSeasTrident();

	if (trident.tridentCharge > 0) {
		stoner.send(new SendMessage("Your trident has " + trident.tridentCharge + " charge" + (trident.tridentCharge > 1 ? "s" : "") + "."));
	} else {
		stoner.send(new SendMessage("Your trident has no charges."));
	}
	}

	public static boolean hasTrident(Stoner stoner) {
	return stoner.getEquipment().isWearingItem(11907, EquipmentConstants.WEAPON_SLOT);
	}

	public static void unload(Stoner stoner) {
	stoner.getBox().addOrCreateGroundItem(554, stoner.getSeasTrident().tridentCharge * 5, true);
	stoner.getBox().addOrCreateGroundItem(560, stoner.getSeasTrident().tridentCharge, true);
	stoner.getBox().addOrCreateGroundItem(562, stoner.getSeasTrident().tridentCharge, true);
	stoner.getSeasTrident().tridentCharge = 0;
	int slot = stoner.getBox().getItemSlot(11907);
	stoner.getBox().get(slot).setId(11908);
	}

	public static boolean itemOption(Stoner stoner, int i, int itemId) {
	if (itemId != 11907 && itemId != 11908) {
		return false;
	}
	switch (i) {
	case 1:
	case 2:
		check(stoner);
		return true;
	case 3:
		unload(stoner);
		return true;
	case 4:
		ask(stoner, itemId);
		stoner.getAttributes().set("ASK_KEY", 1);
		return true;
	}
	return false;
	}

	public static void ask(Stoner stoner, int itemId) {
	ItemDefinition itemDef = GameDefinitionLoader.getItemDef(itemId);
	String[][] info = { { "Are you sure you want to destroy this object?", "14174" }, { "Yes.", "14175" }, { "No.", "14176" }, { "", "14177" }, { "", "14182" }, { "If you uncharge the trident, all runes will fall out.", "14183" }, { itemDef.getName(), "14184" } };
	stoner.send(new SendUpdateItemsAlt(14171, itemId, 1, 0));
	for (int i = 0; i < info.length; i++) {
		stoner.send(new SendString(info[i][0], Integer.parseInt(info[i][1])));
	}
	stoner.send(new SendChatBoxInterface(14170));
	}

	public static void degrade(Stoner stoner) {
	stoner.getSeasTrident().tridentCharge--;

	if (stoner.getSeasTrident().tridentCharge == 0) {
		stoner.send(new SendMessage("The trident needs to be charged with 500 bestbucks, 1 death, 1 chaos, and 5 fire runes."));
		stoner.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].setId(11908);
	}
	}

	public int getCharges() {
	return tridentCharge;
	}
}