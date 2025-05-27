package com.bestbudz.rs2.content;

import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

/**
 * This class handles the reseting of all combat professions
 * 
 * @author Jaybane
 *
 */
public class GenieReset {

	/**
	 * Genie data for profession reseting
	 * 
	 * @author Jaybane
	 *
	 */
	public enum GenieData {
		ASSAULT(232114, "Assault", 0),
		VIGOUR(232117, "Vigour", 2),
		AEGIS(232120, "Aegis", 1),
		RANGE(232123, "Sagittarius", 4),
		MAGE(232126, "Mage", 6),
		NECROMANCE(232129, "Necromance", 5),
		LIFE(232132, "Life", 3);

		private int buttonId, professionId;
		private String professionName;

		private GenieData(int buttonId, String professionName, int professionId) {
		this.buttonId = buttonId;
		this.professionName = professionName;
		this.professionId = professionId;
		}

		public int getButton() {
		return buttonId;
		}

		public String getProfessionName() {
		return professionName;
		}

		public int getProfessionId() {
		return professionId;
		}

		public static GenieData forId(int buttonId) {
		for (GenieData data : GenieData.values())
			if (data.buttonId == buttonId)
				return data;
		return null;
		}
	}

	/**
	 * Handles the profession reseting
	 * 
	 * @param stoner
	 * @param buttonId
	 * @return
	 */
	public static boolean handle(Stoner stoner, int buttonId) {
	// Grabs the genie data
	GenieData genie = GenieData.forId(buttonId);

	// Return if genie data is null
	if (genie == null) {
		return false;
	}

	// Closes the interface
	stoner.send(new SendRemoveInterfaces());

	// Resets the stoner's profession
	if (genie.getProfessionId() == 3) {
		stoner.getGrades()[genie.getProfessionId()] = ((byte) 3);
		stoner.getMaxGrades()[genie.getProfessionId()] = ((byte) 3);
		stoner.getProfession().getExperience()[genie.getProfessionId()] = stoner.getProfession().getXPForGrade(genie.getProfessionId(), 3);
		stoner.getProfession().update(genie.getProfessionId());
	} else {
		stoner.getGrades()[genie.getProfessionId()] = ((byte) 1);
		stoner.getMaxGrades()[genie.getProfessionId()] = ((byte) 1);
		stoner.getProfession().getExperience()[genie.getProfessionId()] = stoner.getProfession().getXPForGrade(genie.getProfessionId(), 1);
		stoner.getProfession().update(genie.getProfessionId());
	}
	// Refreshes the stoner's professions
	stoner.getProfession().update();

	// Sends Genie dialogue
	DialogueManager.sendNpcChat(stoner, 326, Emotion.HAPPY, "You have successfully reset your " + genie.getProfessionName() + " to " + stoner.getProfession().getGrades()[genie.getProfessionId()] + "!");

	// Reset dialogue
	stoner.getDialogue().setNext(-1);

	// Sends the Achievement
	AchievementHandler.activateAchievement(stoner, AchievementList.RESET_5_STATISTICS, 1);
	stoner.send(new SendMessage("Yes"));

	// Return true
	return true;
	}

}
