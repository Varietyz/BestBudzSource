package com.bestbudz.rs2.content.achievements;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.AchievementTab;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Handles the achievements
 * 
 * @author Jaybane
 * 
 */
public class AchievementHandler {

	/**
	 * Holds the types of achievements
	 * 
	 */
	public enum AchievementDifficulty {
		EASY,
		MEDIUM,
		HARD
	}

	/**
	 * Activates the achievement for the individual stoner. Increments the completed
	 * amount for the stoner. If the stoner has completed the achievement, they will
	 * receive their reward.
	 * 
	 * @param stoner
	 *                        The stoner activating the achievement.
	 * @param achievement
	 *                        The achievement for activation.
	 */
	public static void activateAchievement(Stoner stoner, AchievementList achievement, int increase) {
	if (stoner.getStonerAchievements().get(achievement) == achievement.getCompleteAmount()) {
		return;
	}

	int current = stoner.getStonerAchievements().get(achievement);

	stoner.getStonerAchievements().put(achievement, current + increase);

	InterfaceHandler.writeText(new AchievementTab(stoner));

	if (stoner.getStonerAchievements().put(achievement, current + increase) == achievement.getCompleteAmount()) {
		AchievementInterface.sendCompleteInterface(stoner, achievement);
		stoner.addAchievementPoints(stoner.getAchievementsPoints() + achievement.getReward());
	stoner.send(new SendMessage("@or2@Achievement completed: @gre@'" + achievement.getName() + "'"));
	if (achievement.getDifficulty() == AchievementDifficulty.HARD) 
		stoner.getProfession().addExperience(17, 1830000);
	 else if (achievement.getDifficulty() == AchievementDifficulty.MEDIUM) 
		stoner.getProfession().addExperience(17, 1375000);
	 else if (achievement.getDifficulty() == AchievementDifficulty.EASY) 
		stoner.getProfession().addExperience(17, 1000000);
	
		InterfaceHandler.writeText(new AchievementTab(stoner));
	}
	}

}