package com.mayhem.rs2.content.achievements;

import java.util.HashMap;

import com.mayhem.rs2.content.achievements.AchievementHandler.AchievementDifficulty;
import com.mayhem.rs2.entity.player.Player;


/**
 * Handles the achievement buttons
 * 
 * @author Daniel
 * @author Michael
 */
public class AchievementButtons {

	private static final HashMap<Integer, AchievementList> BUTTONS_1 = new HashMap<Integer, AchievementList>();
	private static final HashMap<Integer, AchievementList> BUTTONS_2 = new HashMap<Integer, AchievementList>();
	private static final HashMap<Integer, AchievementList> BUTTONS_3 = new HashMap<Integer, AchievementList>();

	static {
		int button = 136215;
		button = 136215;
		for (final AchievementList achievement : AchievementList.asList(AchievementDifficulty.EASY)) {
			BUTTONS_1.put(button++, achievement);
		}
		button = 136215;
		for (final AchievementList achievement : AchievementList.asList(AchievementDifficulty.MEDIUM)) {
			BUTTONS_2.put(button++, achievement);
		}
		button = 136215;
		for (final AchievementList achievement : AchievementList.asList(AchievementDifficulty.HARD)) {
			BUTTONS_3.put(button++, achievement);
		}
	}

	public static boolean handleButtons(Player player, int buttonId) {
		if (player.getAchievement() == AchievementDifficulty.EASY && BUTTONS_1.containsKey(buttonId)) {
			AchievementInterface.sendInterfaceForAchievement(player, BUTTONS_1.get(buttonId));
			return true;
		}

		if (player.getAchievement() == AchievementDifficulty.MEDIUM && BUTTONS_2.containsKey(buttonId)) {
			AchievementInterface.sendInterfaceForAchievement(player, BUTTONS_2.get(buttonId));
			return true;
		}

		if (player.getAchievement() == AchievementDifficulty.HARD && BUTTONS_3.containsKey(buttonId)) {
			AchievementInterface.sendInterfaceForAchievement(player, BUTTONS_3.get(buttonId));
			return true;
		}
		return false;
	}

}