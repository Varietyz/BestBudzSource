package com.bestbudz.rs2.content.achievements;

import com.bestbudz.rs2.content.achievements.AchievementHandler.AchievementDifficulty;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.HashMap;

public class AchievementButtons {

	private static final HashMap<Integer, AchievementList> BUTTONS = new HashMap<Integer, AchievementList>();

	static {
		int button = 121035;
		AchievementDifficulty last = null;
		for (AchievementList achievement : AchievementList.values()) {
			if (last != achievement.getDifficulty()) {
				button++;
				last = achievement.getDifficulty();
			}
			BUTTONS.put(button++, achievement);
		}
	}

	public static boolean handleButtons(Stoner stoner, int buttonId) {
	if (BUTTONS.containsKey(buttonId)) {
		AchievementInterface.sendInterfaceForAchievement(stoner, BUTTONS.get(buttonId));
		return true;
	}
	return false;
	}

}