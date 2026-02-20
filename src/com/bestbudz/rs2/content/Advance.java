package com.bestbudz.rs2.content;

import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.io.sqlite.SaveCache;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class Advance {

	private static final String ADVANCE_COLOR = "<col=CC0066>";
	private static final int MAX_ADVANCES = 420;
	private static final int AUTO_ADVANCE_XP = 500_000_000;
	private static final int NOTIFICATION_START_XP = 420_000_000;
	private static final int NOTIFICATION_INTERVAL_XP = 20_000_000;

	public static String[] professionName = {
		"Assault",
		"Aegis",
		"Vigour",
		"Life",
		"Sagittarius",
		"Resonance",
		"Mage",
		"Foodie",
		"Lumbering",
		"Woodcarving",
		"Fisher",
		"Pyromaniac",
		"Handiness",
		"Forging",
		"Quarrying",
		"THC-hempistry",
		"Weedsmoking",
		"Pet Master",
		"Mercenary",
		"BankStanding",
		"Consumer"
	};

	public static void checkAutoAdvancement(Stoner stoner, int professionId) {
		double currentXP = stoner.getProfession().getExperience()[professionId];

		if (currentXP >= NOTIFICATION_START_XP && currentXP < AUTO_ADVANCE_XP) {
			checkAdvancementNotification(stoner, professionId, currentXP);
		}

		if (currentXP >= AUTO_ADVANCE_XP && canAdvance(stoner, professionId)) {
			performAutoAdvancement(stoner, professionId);
		}
	}

	private static void checkAdvancementNotification(Stoner stoner, int professionId, double currentXP) {

		double progressXP = currentXP - NOTIFICATION_START_XP;
		int notificationIndex = (int) (progressXP / NOTIFICATION_INTERVAL_XP);

		double thresholdXP = NOTIFICATION_START_XP + (notificationIndex * NOTIFICATION_INTERVAL_XP);
		double remainingXP = AUTO_ADVANCE_XP - currentXP;

		if (Math.abs(currentXP - thresholdXP) < 1000 && remainingXP > 0) {
			String professionName = getProfessionName(professionId);
			String message = String.format(
				"@ora@%s advancement approaching! %.1fM XP remaining until auto-advance.",
				professionName,
				remainingXP / 1_000_000.0
			);
			stoner.send(new SendMessage(message));
		}
	}

	private static void performAutoAdvancement(Stoner stoner, int professionId) {
		AdvanceData data = AdvanceData.forProfession(professionId);
		if (data == null) {
			return;
		}

		if (professionId == 3) {
			stoner.getGrades()[professionId] = 3;
			stoner.getMaxGrades()[professionId] = 3;
			stoner.getProfession().getExperience()[professionId] =
				stoner.getProfession().getXPForGrade(professionId, 3);
		} else {
			stoner.getGrades()[professionId] = 1;
			stoner.getMaxGrades()[professionId] = 1;
			stoner.getProfession().getExperience()[professionId] =
				stoner.getProfession().getXPForGrade(professionId, 1);
		}

		stoner.getProfessionAdvances()[professionId] += 1;
		stoner.setTotalAdvances(stoner.getTotalAdvances() + 1);
		stoner.setAdvancePoints(stoner.getAdvancePoints() + 1);

		if (stoner.getBank().getItemAmount(995) + data.getMoney() <= 2147000000){
			stoner.getBank().add(new Item(995, data.getMoney()));
			stoner.send(new SendMessage("Awarded to bank: " + data.getMoney() + " BestBucks"));
		} else if (stoner.getBox().getItemAmount(995) + data.getMoney() <= 2147000000){
			stoner.getBox().add(new Item(995, data.getMoney()));
			stoner.send(new SendMessage("Awarded to box: " + data.getMoney() + " BestBucks"));
		} else {
			stoner.send(new SendMessage("Could not store: " + data.getMoney() + " BestBucks (Too much money on bank and in box, go buy a gf)"));
			stoner.setCredits(stoner.getCredits() + 250);
			stoner.send(new SendMessage("you've received +250 CannaCredits as Substitution"));
		}

		if (stoner.getBank().getFreeSlots() >= 4) {
			stoner.getBank().add(new Item(data.getMastercape(), 1));
			stoner.getBank().add(new Item(data.getSkillcape(), 1));
			stoner.getBank().add(new Item(data.getSkillcapeT(), 1));
			stoner.getBank().add(new Item(data.getHood(), 1));
			stoner.setCredits(stoner.getCredits() + data.getCredits());
			stoner.send(new SendMessage("Awarded to bank: 1x " + getProfessionName(professionId) + " Cape, Cape(t), Hood & Master Cape."));
			stoner.send(new SendMessage("you've also received: " + data.getCredits() + " CannaCredits for advancing!"));
		} else if (stoner.getBox().getFreeSlots() >= 4) {

			stoner.getBox().add(new Item(data.getMastercape(), 1));
			stoner.getBox().add(new Item(data.getSkillcape(), 1));
			stoner.getBox().add(new Item(data.getSkillcapeT(), 1));
			stoner.getBox().add(new Item(data.getHood(), 1));
			stoner.setCredits(stoner.getCredits() + data.getCredits());
			stoner.send(new SendMessage("*BANK FULL* Awarded to box: 1x " + getProfessionName(professionId) + " Cape, Cape(t), Hood & Master Cape."));
			stoner.send(new SendMessage("you've also received: " + data.getCredits() + " CannaCredits for advancing!"));
		} else {
			stoner.setCredits(stoner.getCredits() + data.getCredits() + 250);
			stoner.send(new SendMessage("*BANK & BOX  FULL* Could not award: 1x " + getProfessionName(professionId) + " Cape, Cape(t), Hood & Master Cape."));
			stoner.send(new SendMessage("you've received: " + data.getCredits() + " CannaCredits for advancing! (+250 Credits Substitution"));
		}

		stoner.send(new SendMessage(
			"Auto-advanced " + ADVANCE_COLOR + getProfessionName(professionId) +
				" to " + stoner.getProfessionAdvances()[professionId] + "</col>!"
		));

		World.sendGlobalMessage(
			"<img=8> " + ADVANCE_COLOR + stoner.getUsername() +
				" </col>auto-advanced " + ADVANCE_COLOR + getProfessionName(professionId) +
				",</col>new total advances: " + ADVANCE_COLOR + stoner.getProfessionAdvances()[professionId] +
				"</col>, SMOKE SESH!"
		);

		AchievementHandler.activateAchievement(stoner, AchievementList.ADVANCE_105_TIMES, 1);
		stoner.getProfession().restore();
		stoner.getProfession().update(professionId);
		SaveCache.markDirty(stoner);
	}

	public static boolean canAdvance(Stoner stoner, int professionId) {
		if (stoner.getMaxGrades()[professionId] < 420) {
			return false;
		}
		if (stoner.getProfessionAdvances()[professionId] >= MAX_ADVANCES) {
			return false;
		}
		return true;
	}

	public static String getProfessionName(int i) {
		return professionName[i];
	}

	public static int professionTierColor(Stoner stoner, int professionId) {
		switch (stoner.getProfessionAdvances()[professionId]) {
			case 1: return 0xE100FF;
			case 2: return 0xFF6A00;
			case 3: return 0x11BF0B;
			case 4: return 0x0D96D1;
			case 5: return 0xED0909;
			default: return 0x070707;
		}
	}

	public enum AdvanceData {
		ASSAULT(199088, " ", 0, 51010, 6_000_000, 100, 13200, 9747, 9748, 9749),
		AEGIS(199094, " ", 1, 51012, 6_000_000, 100, 13201, 9753, 9754, 9755),
		VIGOUR(199091, " ", 2, 51011, 6_000_000, 100, 13202, 9750, 9751, 9752),
		LIFE(199109, " ", 3, 51017, 6_000_000, 100, 13203, 9768, 9769, 9770),
		SAGITTARIUS(199097, " ", 4, 51013, 6_000_000, 100, 13204, 9756, 9757, 9758),
		RESONANCE(199100, " ", 5, 51014, 6_000_000, 100, 13205, 9759, 9760, 9761),
		MAGE(199103, " ", 6, 51015, 6_000_000, 100, 13206, 9762, 9763, 9764),
		FOODIE(199139, " ", 7, 51027, 10_500_000, 800, 13207, 9801, 9802, 9803),
		LUMBERING(199145, " ", 8, 51029, 10_500_000, 800, 13208, 9807, 9808, 9809),
		WOODCARVING(199124, " ", 9, 51022, 10_500_000, 800, 13209, 9783, 9784, 9785),
		FISHER(199136, " ", 10, 51026, 10_500_000, 800, 13210, 9798, 9799, 9800),
		PYROMANIAC(199142, " ", 11, 51028, 10_500_000, 800, 13211, 9804, 9805, 9806),
		HANDINESS(199121, " ", 12, 51021, 10_500_000, 800, 13212, 9780, 9781, 9782),
		FORGING(199133, " ", 13, 51025, 10_500_000, 800, 13213, 9795, 9796, 9797),
		QUARRYING(199130, " ", 14, 51024, 10_500_000, 800, 13214, 9792, 9793, 9794),
		THCHEMPISTRY(199115, " ", 15, 51019, 20_500_000, 1400, 13215, 9774, 9775, 9776),
		WEEDSMOKING(199112, " ", 16, 51018, 50_000_000, 2500, 13216, 9771, 9772, 9773),
		PET_MASTER(199118, " ", 17, 51020, 20_500_000, 1600, 13223, 9948, 9949, 9950),
		MERCENARY(199127, " ", 18, 51023, 10_500_000, 800, 13218, 9786, 9787, 9788),
		BANKSTANDING(199148, " ", 19, 51030, 20_500_000, 1000, 13219, 9810, 9811, 9812),
		CONSUMER(199106, " ", 20, 51016, 10_500_000, 800, 13220, 9765, 9766, 9767);

		String name;
		int buttonId, profession, frame, money, credits, mastercape, skillcape, skillcapeT, hood;

		AdvanceData(int buttonId, String name, int profession, int frame, int money, int credits, int mastercape, int skillcape, int skillcapeT, int hood) {
			this.buttonId = buttonId;
			this.name = name;
			this.profession = profession;
			this.frame = frame;
			this.money = money;
			this.credits = credits;
			this.mastercape = mastercape;
			this.skillcape = skillcape;
			this.skillcapeT = skillcapeT;
			this.hood = hood;
		}

		public static AdvanceData forProfession(int id) {
			for (AdvanceData data : AdvanceData.values()) {
				if (data.profession == id) return data;
			}
			return null;
		}

		public String getName() { return name; }
		public int getButton() { return buttonId; }
		public int getProfession() { return profession; }
		public int getFrame() { return frame; }
		public int getMoney() { return money; }
		private int getCredits() { return credits; }
		private int getMastercape() { return mastercape; }
		private int getSkillcape() { return skillcape; }
		private int getSkillcapeT() { return skillcapeT; }
		private int getHood() { return hood; }
	}

}
