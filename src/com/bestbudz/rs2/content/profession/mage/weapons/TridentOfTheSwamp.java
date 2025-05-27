package com.bestbudz.rs2.content.profession.mage.weapons;

import java.math.RoundingMode;
import java.text.DecimalFormat;

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

public class TridentOfTheSwamp {

	private static final DecimalFormat FORMATTER = new DecimalFormat("#.#");

	private static final int FULL = 2_500;

	private int tridentCharge;

	public int getCharges() {
	return tridentCharge;
	}

	public TridentOfTheSwamp(int tridentCharge) {
	this.tridentCharge = tridentCharge;
	FORMATTER.setRoundingMode(RoundingMode.FLOOR);
	}

	public static boolean itemOnItem(Stoner stoner, Item itemUsed, Item usedWith) {
	if (itemUsed.getId() == 12899) {
		switch (usedWith.getId()) {
		case 554:
		case 560:
		case 562:
		case 12934:
			if (stoner.getBox().hasAllItems(new Item(554, 5), new Item(560, 1), new Item(562, 1), new Item(12934, 1))) {
				int min = Integer.MAX_VALUE;
				for (Item item : stoner.getBox().getItems()) {
					if (item != null) {
						switch (item.getId()) {
						case 554:
						case 560:
						case 562:
						case 12934:
							if (item.getAmount() / (item.getId() == 12934 ? 1 : item.getId() == 554 ? 5 : 1) < min) {
								min = item.getAmount() / (item.getId() == 12934 ? 1 : item.getId() == 554 ? 5 : 1);
							}
							break;
						}
					}
				}
				if (min + stoner.getSwampTrident().tridentCharge > FULL) {
					min = FULL - stoner.getSwampTrident().tridentCharge;
				}
				stoner.getBox().remove(554, 5 * min);
				stoner.getBox().remove(560, min);
				stoner.getBox().remove(562, min);
				stoner.getBox().remove(12934, 1 * min);
				stoner.getSwampTrident().tridentCharge = min + stoner.getSwampTrident().tridentCharge;
				stoner.getUpdateFlags().sendAnimation(new Animation(1979));
				stoner.getUpdateFlags().sendGraphic(new Graphic(1250, 40, false));
				DialogueManager.sendItem1(stoner, "You infuse the trident with @dre@" + stoner.getSwampTrident().tridentCharge + "</col> charge" + (stoner.getSwampTrident().tridentCharge > 1 ? "s" : "") + ".", 12899);
				check(stoner);
			}
			break;
		}
	} else if (usedWith.getId() == 12899) {
		switch (itemUsed.getId()) {
		case 554:
		case 560:
		case 562:
		case 12934:
			if (stoner.getBox().hasAllItems(new Item(554, 5), new Item(560, 1), new Item(562, 1), new Item(12934, 1))) {
				int min = Integer.MAX_VALUE;
				for (Item item : stoner.getBox().getItems()) {
					if (item != null) {
						switch (item.getId()) {
						case 554:
						case 560:
						case 562:
						case 12934:
							if (item.getAmount() / (item.getId() == 12934 ? 1 : item.getId() == 554 ? 5 : 1) < min) {
								min = item.getAmount() / (item.getId() == 12934 ? 1 : item.getId() == 554 ? 5 : 1);
							}
							break;
						}
					}
				}
				if (min + stoner.getSwampTrident().tridentCharge > FULL) {
					min = FULL - stoner.getSwampTrident().tridentCharge;
				}
				stoner.getBox().remove(554, 5 * min);
				stoner.getBox().remove(560, min);
				stoner.getBox().remove(562, min);
				stoner.getBox().remove(12934, 1 * min);
				stoner.getSwampTrident().tridentCharge = min + stoner.getSwampTrident().tridentCharge;
				stoner.getUpdateFlags().sendAnimation(new Animation(1979));
				stoner.getUpdateFlags().sendGraphic(new Graphic(1250, 40, false));
				DialogueManager.sendItem1(stoner, "You infuse the trident with @dre@" + stoner.getSwampTrident().tridentCharge + "</col> charge" + (stoner.getSwampTrident().tridentCharge > 1 ? "s" : "") + ".", 12899);
				check(stoner);
			}
			break;
		}
	} else {
		return false;
	}
	return false;
	}

	public static boolean itemOption(Stoner stoner, int i, int itemId) {
	if (itemId != 12899) {
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

	public static void check(Stoner stoner) {
	TridentOfTheSwamp trident = stoner.getSwampTrident();

	if (trident.tridentCharge > 0) {
		stoner.send(new SendMessage("Your trident has " + trident.tridentCharge + " charge" + (trident.tridentCharge > 1 ? "s" : "") + "."));
	} else {
		stoner.send(new SendMessage("Your trident has no charges."));
	}
	}

	public static boolean hasTrident(Stoner stoner) {
	return stoner.getEquipment().isWearingItem(12899, EquipmentConstants.WEAPON_SLOT);
	}

	public static void unload(Stoner stoner) {
	stoner.getBox().addOrCreateGroundItem(554, stoner.getSwampTrident().tridentCharge * 5, true);
	stoner.getBox().addOrCreateGroundItem(560, stoner.getSwampTrident().tridentCharge, true);
	stoner.getBox().addOrCreateGroundItem(562, stoner.getSwampTrident().tridentCharge, true);
	stoner.getSwampTrident().tridentCharge = 0;
	stoner.send(new SendMessage("You have unloaded your trident."));
	}

	public static void degrade(Stoner stoner) {
	stoner.getSwampTrident().tridentCharge--;

	if (stoner.getSwampTrident().tridentCharge == 0) {
		stoner.send(new SendMessage("You do not have any charges left to use this."));
	}
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

}
