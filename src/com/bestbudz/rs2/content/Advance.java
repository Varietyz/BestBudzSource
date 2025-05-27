package com.bestbudz.rs2.content;

import java.util.HashMap;

import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

/**
 * Handles the advance system
 * 
 * @author Jaybane
 *
 */
public class Advance {

	/**
	 * Advance sendMessage color
	 */
	private static final String ADVANCE_COLOR = "<col=CC0066>";

	/**
	 * Holds the max number of advance tiers
	 */
	private static final int MAX_ADVANCES = 5;

	/**
	 * Advance Data
	 * 
	 * @author Jaybane
	 *
	 */
	public enum AdvanceData {
		ASSAULT(199088, " ", 0, 51010, 600_000),
		AEGIS(199094, " ", 1, 51012, 600_000),
		VIGOUR(199091, " ", 2, 51011, 600_000),
		LIFE(199109, " ", 3, 51017, 600_000),
		RANGE(199097, " ", 4, 51013, 600_000),
		NECROMANCE(199100, " ", 5, 51014, 600_000),
		MAGE(199103, " ", 6, 51015, 600_000),
		FOODIE(199139, " ", 7, 51027, 1_500_000),
		LUMBERING(199145, " ", 8, 51029, 1_500_000),
		WOODCARVING(199124, " ", 9, 51022, 1_500_000),
		FISHER(199136, " ", 10, 51026, 1_500_000),
		PYROMANIAC(199142, " ", 11, 51028, 1_500_000),
		HANDINESS(199121, " ", 12, 51021, 1_500_000),
		FORGING(199133, " ", 13, 51025, 1_500_000),
		QUARRYING(199130, " ", 14, 51024, 1_500_000),
		THCHEMPISTRY(199115, " ", 15, 51019, 2_500_000),
		WEEDSMOKING(199112, " ", 16, 51018, 5_000_000),
		ACCOMPLISHER(199118, " ", 17, 51020, 1_500_000),
		MERCENARY(199127, " ", 18, 51023, 1_500_000),
		CULTIVATION(199148, " ", 19, 51030, 2_500_000),
		CONSUMER(199106, " ", 20, 51016, 1_500_000);
		// HUNTER(199151, " ", 21, 51031, 1_500_000);

		String name;
		int buttonId, profession, frame, money;

		private AdvanceData(int buttonId, String name, int profession, int frame, int money) {
		this.buttonId = buttonId;
		this.name = name;
		this.profession = profession;
		this.frame = frame;
		this.money = money;
		}

		public String getName() {
		return name;
		}

		public int getButton() {
		return buttonId;
		}

		public int getProfession() {
		return profession;
		}

		public int getFrame() {
		return frame;
		}

		public int getMoney() {
		return money;
		}

		public static AdvanceData forProfession(int id) {
		for (AdvanceData data : AdvanceData.values())
			if (data.profession == id)
				return data;
		return null;
		}

		public static HashMap<Integer, AdvanceData> advance = new HashMap<Integer, AdvanceData>();

		static {
			for (final AdvanceData advance : AdvanceData.values()) {
				AdvanceData.advance.put(advance.buttonId, advance);
			}
		}
	}

	/**
	 * Handles the clicking buttons for interface
	 * 
	 * @param stoner
	 * @param actionButtonId
	 */
	public static boolean handleActionButtons(Stoner stoner, int buttonId) {
	AdvanceData data = AdvanceData.advance.get(buttonId);

	if (data == null) {
		return false;
	}

	if (stoner.getInterfaceManager().main != 51000) {
		stoner.send(new SendRemoveInterfaces());
		stoner.send(new SendMessage("That interface does not exist!"));
		return false;
	}

	if (advanceProfession(stoner, data.getProfession())) {
		stoner.getBox().add(new Item(995, data.getMoney()));
	}
	return true;
	}

	/**
	 * Checks if stoner can advance
	 * 
	 * @param stoner
	 * @param professionId
	 * @return
	 */
	public static boolean canAdvance(Stoner stoner, int professionId) {
	if (stoner.getMaxGrades()[professionId] < 99) {
		stoner.send(new SendMessage("" + getProfessionName(professionId) + " is not grade 99 yet!"));
		return false;
	}
	if (stoner.getProfessionAdvances()[professionId] >= MAX_ADVANCES) {
		stoner.send(new SendMessage("You reached advance " + MAX_ADVANCES + " already, grind to 420M exp."));
		return false;
	}
	if (stoner.getBox().getFreeSlots() < 1) {
		stoner.send(new SendMessage("Leave one open space in ur box!"));
		return false;
	}
	return true;
	}

	/**
	 * Advances the profession if all requirements are met
	 * 
	 * @param stoner
	 * @param professionId
	 */
	public static boolean advanceProfession(Stoner stoner, int professionId) {
	if (!canAdvance(stoner, professionId)) {
		return false;
	}

	if (professionId == 3) {
		stoner.getGrades()[professionId] = ((byte) 3);
		stoner.getMaxGrades()[professionId] = ((byte) 3);
		stoner.getProfession().getExperience()[professionId] = stoner.getProfession().getXPForGrade(professionId, 3);
		stoner.getProfession().update(professionId);
	} else {
		stoner.getGrades()[professionId] = ((byte) 1);
		stoner.getMaxGrades()[professionId] = ((byte) 1);
		stoner.getProfession().getExperience()[professionId] = stoner.getProfession().getXPForGrade(professionId, 1);
		stoner.getProfession().update(professionId);
	}

	stoner.getProfessionAdvances()[professionId] += 1;
	stoner.setTotalAdvances(stoner.getTotalAdvances() + 1);
	stoner.setAdvancePoints(stoner.getAdvancePoints() + 1);
	stoner.send(new SendMessage("Advanced " + ADVANCE_COLOR + "" + getProfessionName(professionId) + " to " + stoner.getProfessionAdvances()[professionId] + "</col>!"));
	World.sendGlobalMessage("<img=8> " + ADVANCE_COLOR + stoner.getUsername() + " </col>advanced " + ADVANCE_COLOR + "" + getProfessionName(professionId) + "</col>  to " + ADVANCE_COLOR + "" + stoner.getProfessionAdvances()[professionId] + "</col>, SMOKE SESH!");
	AchievementHandler.activateAchievement(stoner, AchievementList.ADVANCE_105_TIMES, 1);
	stoner.getProfession().restore();
	update(stoner);
	return true;
	}

	/**
	 * Updates the interface
	 * 
	 * @param stoner
	 */
	public static void update(Stoner stoner) {
	stoner.send(new SendString("@gre@" + stoner.deterquarryIcon(stoner) + "  " + stoner.getUsername(), 51007));
	stoner.send(new SendString("</col>Total Advances: @gre@" + stoner.getTotalAdvances(), 51008));
	stoner.send(new SendString("</col>Advance Points: @gre@" + stoner.getAdvancePoints(), 51009));
	for (int i = 0; i < stoner.getProfessionAdvances().length; i++) {
		AdvanceData data = AdvanceData.forProfession(i);
		if (data == null) {
			continue;
		}

	}
	}

	/**
	 * Profession names
	 */
	public static String[] professionName = { "Assault", "Aegis", "Vigour", "Life", "Sagittarius", "Necromance", "Mage", "Foodie", "Lumbering", "Woodcarving", "Fisher", "Pyromaniac", "Handiness", "Forging", "Quarrying", "THC-hempistry", "Weedsmoking", "Accomplisher", "Mercenary", "Cultivation", "Consumer"/* , "Hunter" */ };

	/**
	 * Gets the professionname
	 * 
	 * @param i
	 * @return
	 */
	public static String getProfessionName(int i) {
	return professionName[i];
	}

	/**
	 * The profession colors
	 * 
	 * @param stoner
	 * @param professionId
	 * @return
	 */
	public static int professionTierColor(Stoner stoner, int professionId) {
	switch (stoner.getProfessionAdvances()[professionId]) {
	case 1:
		return 0xE100FF;
	case 2:
		return 0xFF6A00;
	case 3:
		return 0x11BF0B;
	case 4:
		return 0x0D96D1;
	case 5:
		return 0xED0909;
	default:
		return 0x070707;
	}
	}
}