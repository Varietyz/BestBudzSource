package com.bestbudz.rs2.content.minigames.bloodtrial.finish;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.minigames.bloodtrial.BloodTrial;
import com.bestbudz.rs2.content.minigames.bloodtrial.data.BloodTrialConfig;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class BloodTrialFinish
{
	private static final String COMPLETION_MESSAGE = "FUCKING SICK, YOU DID IT! YOU BEAST!";
	private static final String FAILURE_MESSAGE = "You suck!";
	private static final StringBuilder globalMessageBuilder = new StringBuilder(128);

	public static void finishedBloodTrial(Stoner stoner, boolean reward) {
		if (stoner == null) {
			return;
		}

		try {
			if (reward) {
				giveRewards(stoner);
				sendCompletionMessages(stoner);
				updateAchievements(stoner);
				rollForPet(stoner);
			} else {
				stoner.getClient().queueOutgoingPacket(new SendMessage(FAILURE_MESSAGE));
			}
		} finally {
			cleanupAndExit(stoner);
		}
	}

	private static void giveRewards(Stoner stoner) {
		stoner.getBox().addOrCreateGroundItem(BloodTrialConfig.FIRE_CAPE_ID, 1, true);
		stoner.getBox().addOrCreateGroundItem(BloodTrialConfig.TOKKUL_ID,
			BloodTrialConfig.TOKKUL_AMOUNT, true);
	}

	private static void sendCompletionMessages(Stoner stoner) {
		stoner.getClient().queueOutgoingPacket(new SendMessage(COMPLETION_MESSAGE));
		DialogueManager.sendStatement(stoner, COMPLETION_MESSAGE);

		globalMessageBuilder.setLength(0);
		globalMessageBuilder.append("<img=8> <col=C42BAD>")
			.append(stoner.getUsername())
			.append(" has just completed the Blood Trial!");
		World.sendGlobalMessage(globalMessageBuilder.toString());
	}

	private static void updateAchievements(Stoner stoner) {
		AchievementHandler.activateAchievement(stoner, AchievementList.OBTAIN_10_FIRECAPES, 1);
		AchievementHandler.activateAchievement(stoner, AchievementList.OBTAIN_50_FIRECAPES, 1);
	}

	private static void rollForPet(Stoner stoner) {
		if (Utility.random(BloodTrialConfig.PET_CHANCE) == 0) {
			BloodTrialPetRolls.handlePet(stoner);
		}
	}

	private static void cleanupAndExit(Stoner stoner) {
		stoner.teleport(BloodTrial.LEAVE);
		BloodTrial.onLeaveGame(stoner);
		stoner.getBloodTrialDetails().reset();
	}
}