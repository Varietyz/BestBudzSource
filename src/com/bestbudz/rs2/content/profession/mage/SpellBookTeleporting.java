package com.bestbudz.rs2.content.profession.mage;

import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.profession.mage.MageProfession.TeleportTypes;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public final class SpellBookTeleporting {

	private final static int LAW_RUNE = 563;
	private final static int AIR_RUNE = 556;
	private final static int FIRE_RUNE = 554;
	private final static int EARTH_RUNE = 557;
	private final static int WATER_RUNE = 555;
	private final static int SOUL_RUNE = 566;
	private final static int BLOOD_RUNE = 565;

	public static void teleport(final Stoner stoner, final int button) {
	final TeleportationData data = TeleportationData.forId(button);
	if (data == null) {
		return;
	}
	if (button == 75010 || button == 84237 || button == 117048) {
		stoner.getMage().teleport(data.getX(), data.getY(), 0, TeleportTypes.SPELL_BOOK);
		return;
	}
	if (StonerConstants.isOwner(stoner)) {
		stoner.getMage().teleport(data.getX(), data.getY(), 0, TeleportTypes.SPELL_BOOK);
		return;
	}
	if (stoner.getBox().hasItemId(new Item(data.getRunes()[0], data.getRunes()[1])) && stoner.getBox().hasItemId(new Item(data.getRunes()[2], data.getRunes()[3])) && stoner.getBox().hasItemId(new Item(data.getRunes()[4], data.getRunes()[5]))) {
		if (stoner.getProfession().getGrades()[Professions.MAGE] >= data.getReq()) {
			stoner.getMage().teleport(data.getX(), data.getY(), 0, TeleportTypes.SPELL_BOOK);
			stoner.getBox().remove(new Item(data.getRunes()[0], data.getRunes()[1]));
			stoner.getBox().remove(new Item(data.getRunes()[2], data.getRunes()[3]));
			if (data.getRunes()[2] == data.getRunes()[4] && data.getRunes()[3] == data.getRunes()[5]) {
			} else {
				stoner.getBox().remove(new Item(data.getRunes()[4], data.getRunes()[5]));
			}
		} else {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You don't have a high enough mage grade to cast this spell."));
		}
	} else {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You don't have the required runes to cast this spell."));
	}
	}

	enum TeleportationData {

		HOME_TELEPORT(3434, 2890, 75010, 1, new int[] { AIR_RUNE, 3, FIRE_RUNE, 1, LAW_RUNE, 1 }),

		VARROCK(3210, 3424, 4140, 25, new int[] { AIR_RUNE, 3, FIRE_RUNE, 1, LAW_RUNE, 1 }),

		LUMBRIDGE(3222, 3218, 4143, 31, new int[] { AIR_RUNE, 3, EARTH_RUNE, 1, LAW_RUNE, 1 }),

		FALADOR(2964, 3378, 4146, 37, new int[] { AIR_RUNE, 3, WATER_RUNE, 1, LAW_RUNE, 1 }),

		CAMELOT(2757, 3477, 4150, 45, new int[] { AIR_RUNE, 3, LAW_RUNE, 1, LAW_RUNE, 1 }),

		ARDOUGNE(2662, 3305, 6004, 51, new int[] { WATER_RUNE, 2, LAW_RUNE, 2, LAW_RUNE, 2 }),

		WATCH_TOWER(3087, 3500, 6005, 58, new int[] { EARTH_RUNE, 2, LAW_RUNE, 2, LAW_RUNE, 2 }),

		TROLLHEIM(3243, 3513, 29031, 61, new int[] { FIRE_RUNE, 2, LAW_RUNE, 2, LAW_RUNE, 2 }),

		HOME_TELEPORT_ANCIENT(3434, 2890, 84237, 1, new int[] { AIR_RUNE, 3, FIRE_RUNE, 1, LAW_RUNE, 1 }),

		PADDEWWA(3097, 9882, 50235, 54, new int[] { AIR_RUNE, 1, FIRE_RUNE, 1, LAW_RUNE, 2 }),

		SENNTISTEN(3322, 3336, 50245, 60, new int[] { LAW_RUNE, 2, SOUL_RUNE, 1, SOUL_RUNE, 1 }),

		KHARYLL(3492, 3471, 50253, 66, new int[] { LAW_RUNE, 2, BLOOD_RUNE, 1, BLOOD_RUNE, 1 });

		private final int x, y, req;

		private final int[] runes;

		private final int button;

		TeleportationData(final int x, final int y, final int button, final int req, final int[] runes) {
		this.x = x;
		this.y = y;
		this.button = button;
		this.req = req;
		this.runes = runes;
		}

		private static final TeleportationData forId(int button) {
		for (TeleportationData data : TeleportationData.values()) {
			if (button == data.getButton()) {
				return data;
			}
		}
		return null;
		}

		private int[] getRunes() {
		return runes;
		}

		private int getX() {
		return x;
		}

		private int getY() {
		return y;
		}

		private int getReq() {
		return req;
		}

		private int getButton() {
		return button;
		}
	}
}
