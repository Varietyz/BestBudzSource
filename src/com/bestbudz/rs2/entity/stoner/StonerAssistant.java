package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.content.achievements.AchievementList;

/**
 * Handles miscellaneous methods for stoner
 * 
 * @author Jaybane
 *
 */
public class StonerAssistant {

	private Stoner stoner;

	public StonerAssistant(Stoner stoner) {
	this.stoner = stoner;
	}

	/**
	 * Gets the total amount of achievements completed
	 * 
	 * @return
	 */
	public int achievementCompleted() {
	int completed = 0;
	for (AchievementList achievement : stoner.getStonerAchievements().keySet()) {
		if (achievement != null && stoner.getStonerAchievements().get(achievement) == achievement.getCompleteAmount()) {
			completed++;
		}
	}
	return completed;
	}

}
