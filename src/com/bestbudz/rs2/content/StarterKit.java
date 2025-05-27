package com.bestbudz.rs2.content;

import java.util.HashMap;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.io.StonerSaveUtil;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.controllers.ControllerManager;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendColor;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendSidebarInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendUpdateItemsAlt;

public class StarterKit {

	private static int selected = 202051;

	public enum StarterData {

		STONER(202051, 51766, 0, new Item[] { // Professioning
				new Item(995, 5000000), // Cash
				new Item(7945, 100), // Raw Monkfish Noted
				new Item(11879, 1), // Watervial pack
				new Item(11881, 1), // Feather pack
				new Item(11883, 1), // Bait pack
				new Item(12009, 1), // Softclay pack
				new Item(11885, 1), // Broadarrow pack
				new Item(12859, 1), // eye of newt pack
				new Item(11738, 1), // Weed BOX
				new Item(1511, 100), // Logs
				new Item(1521, 100), // Oak logs
				new Item(1519, 100), // willow logs
				new Item(1517, 100), // Maple logs
				new Item(1515, 100), // Yew logs
				new Item(1513, 100), // Mage logs
				new Item(6199, 1), // Misery Box
				new Item(6857, 1), // Bobble scarf
				new Item(775, 1), // foodie gauntlet
				new Item(1837, 1), // Desert boots
				new Item(13223, 1), // Summ mastercape
				new Item(6575, 1), // Toolring
				new Item(6577, 1), // Fisher necklace
				new Item(12647, 1) // Kalph princess pet
		}, "", "", ""),

		DEALER(202052, 51767, 11, new Item[] { // melee set
				new Item(995, 5000000), // Cash
				new Item(7947, 100), // Monkfish Noted
				new Item(2437, 10), // Att. Pot
				new Item(2441, 10), // Str. Pot
				new Item(2443, 10), // Def. Pot
				new Item(3025, 10), // Restore pot
				new Item(12414, 1), // Dragon G Chainbody
				new Item(12415, 1), // Dragon G Platelegs
				new Item(12416, 1), // Dragon G Plateskirt
				new Item(12417, 1), // Dragon G Full helm
				new Item(12418, 1), // Dragon G SQ
				new Item(4587, 1), // Dragon scimi
				new Item(6585, 1), // Fury amulet
				new Item(7461, 1), // Dragon gloves
				new Item(11840, 1), // Dragon boots
				new Item(2675, 1), // Guthix Kiteshield
				new Item(2673, 1), // Guthix Full Helm
				new Item(2671, 1), // Guthix Platelegs
				new Item(2669, 1), // Guthix Platebody
				new Item(13223, 1), // Summ mastercape
				new Item(12654, 1) // Kalph princess pet
		}, "", "", ""),

		GROWER(202053, 51768, 12, new Item[] { // mage/range set
				new Item(995, 5000000), // Cash
				new Item(7947, 100), // Monkfish Noted
				new Item(2445, 10), // Range pot
				new Item(3041, 10), // Mage pot
				new Item(3025, 10), // Restore pot
				new Item(892, 500), // Rune arrow
				new Item(4675, 1), // Ancient staff
				new Item(2579, 1), // Wizard boots
				new Item(2577, 1), // Sagittarius boots
				new Item(4214, 1), // Crystal bow
				new Item(861, 1), // Mage bow
				new Item(10376, 1), // Guthix Dhide braces
				new Item(10380, 1), // Guthix Dhide chaps
				new Item(6585, 1), // Fury amulet
				new Item(13223, 1), // Summ mastercape
				new Item(12648, 1) // Pet devil
		}, "", "", "");

		private final int button;
		private final int stringId;
		private final int rights;
		private final Item items[];
		private final String[] descriptions;

		private StarterData(int button, int stringId, int rights, Item[] items, String... descriptions) {
		this.button = button;
		this.stringId = stringId;
		this.rights = rights;
		this.items = items;
		this.descriptions = descriptions;
		}

		public int getButton() {
		return button;
		}

		public int getString() {
		return stringId;
		}

		public int getRights() {
		return rights;
		}

		public Item[] getItems() {
		return items;
		}

		public String[] getDescription() {
		return descriptions;
		}

		private static HashMap<Integer, StarterData> starterKits = new HashMap<Integer, StarterData>();

		static {
			for (final StarterData starters : StarterData.values()) {
				StarterData.starterKits.put(starters.button, starters);
			}
		}
	}

	public static boolean handle(Stoner stoner, int buttonId) {

	StarterData data = StarterData.starterKits.get(buttonId);

	if (data == null) {
		if (extraButton(stoner, buttonId)) {
			return false;
		}
		return false;
	}

	int color = 0xBA640D;

	if (buttonId == 202051) {
		stoner.send(new SendColor(51766, color));
		stoner.send(new SendColor(51767, 0xF7AA25));
		stoner.send(new SendColor(51768, 0xF7AA25));
	} else if (buttonId == 202052) {
		stoner.send(new SendColor(51766, 0xF7AA25));
		stoner.send(new SendColor(51767, color));
		stoner.send(new SendColor(51768, 0xF7AA25));
	} else if (buttonId == 202053) {
		stoner.send(new SendColor(51766, 0xF7AA25));
		stoner.send(new SendColor(51767, 0xF7AA25));
		stoner.send(new SendColor(51768, color));
	}

	selected = buttonId;
	stoner.send(new SendColor(data.getString(), 0xC71C1C));
	String name = Utility.capitalize(data.name().toLowerCase().replaceAll("_", " "));
	stoner.send(new SendString("Stash (@red@" + name + "</col>):", 51757));

	for (int i = 0; i < 30; i++) {
		stoner.getClient().queueOutgoingPacket(new SendUpdateItemsAlt(51758, 0, 0, i));
	}

	for (int i = 0; i < data.getItems().length; i++) {
		stoner.getClient().queueOutgoingPacket(new SendUpdateItemsAlt(51758, data.getItems()[i].getId(), data.getItems()[i].getAmount(), i));
	}

	for (int i = 0; i < data.getDescription().length; i++) {
		stoner.send(new SendString(data.getDescription()[i], 51760 + i));
	}

	return true;

	}

	public static boolean extraButton(Stoner stoner, int button) {
	if (stoner.getInterfaceManager().main != 51750) {
		return false;
	}
	switch (button) {
	case 202054:
		stoner.send(new SendConfig(1085, 0));
		StarterKit.handle(stoner, 202051);
		return true;
	case 202055:
		stoner.send(new SendConfig(1085, 1));
		StarterKit.handle(stoner, 202052);
		return true;
	case 202056:
		stoner.send(new SendConfig(1085, 2));
		StarterKit.handle(stoner, 202053);
		return true;
	case 202057:
		StarterKit.confirm(stoner);
		return true;
	}
	return false;

	}

	public static void confirm(Stoner stoner) {

	StarterData data = StarterData.starterKits.get(selected);

	if (data == null || stoner.getDelay().elapsed() < 1000) {
		return;
	}

	if (stoner.getInterfaceManager().main != 51750) {
		stoner.send(new SendRemoveInterfaces());
		return;
	}

	stoner.getDelay().reset();
	String name = Utility.capitalize(data.name().toLowerCase().replaceAll("_", " "));
	stoner.send(new SendRemoveInterfaces());
	stoner.send(new SendMessage("@red@You will be playing as " + Utility.getAOrAn(name) + " " + name + "."));
	stoner.setController(ControllerManager.DEFAULT_CONTROLLER);
	stoner.setStarter(false);
	stoner.send(new SendInterface(3559));

	for (int i = 0; i < StonerConstants.SIDEBAR_INTERFACE_IDS.length; i++) {
		stoner.send(new SendSidebarInterface(i, StonerConstants.SIDEBAR_INTERFACE_IDS[i]));
	}

	stoner.send(new SendSidebarInterface(5, 5608));
	stoner.send(new SendSidebarInterface(6, 1151));
	stoner.setRights(data.getRights());
	stoner.getUpdateFlags().setUpdateRequired(true);

	switch (selected) {

	case 202052:
	case 202053:
	case 202051:
		if (!StonerSaveUtil.hasReceived2Starters(stoner) || stoner.getLastLoginYear() != 0) {
			stoner.getBox().addItems(data.getItems());
		}
		stoner.setRights(0);
		break;

	}
	}

}
