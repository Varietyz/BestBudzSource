package com.bestbudz.rs2.content.gambling;

import com.bestbudz.core.util.Utility;
import com.bestbudz.core.util.FileHandler;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class Gambling {

	private static final int CHANCE_OF_WINNING = 55;

	private static final int MAXIMUM_AMOUNT = 15000000;

	private static final int MINIMUM_AMOUNT = 500000;

	public static long MONEY_TRACKER;

	public static boolean calculateWin() {
	return Utility.random(100) >= CHANCE_OF_WINNING;
	}

	public static boolean canPlay(Stoner stoner, int amount) {
	if (StonerConstants.isStaff(stoner)) {
		DialogueManager.sendNpcChat(stoner, 1011, Emotion.DEFAULT, "Sorry, but Jaybane has forbidden you from gambling.");
		return false;
	}
	if (amount > MAXIMUM_AMOUNT) {
		DialogueManager.sendNpcChat(stoner, 1011, Emotion.DEFAULT, "Woah there fella!", "The maximum bet allowed is " + Utility.format(MAXIMUM_AMOUNT) + "!");
		return false;
	}
	if (amount < MINIMUM_AMOUNT) {
		DialogueManager.sendNpcChat(stoner, 1011, Emotion.DEFAULT, "Sorry buddy, bets have to be more than " + Utility.format(MINIMUM_AMOUNT) + ".");
		return false;
	}
	if (stoner.isPouchPayment()) {
		if (stoner.getMoneyPouch() < amount) {
			DialogueManager.sendNpcChat(stoner, 1011, Emotion.DEFAULT, "You don't have that much money to bet!");
			return false;
		}
	} else {
		if (!stoner.getBox().hasItemAmount(995, amount)) {
			DialogueManager.sendNpcChat(stoner, 1011, Emotion.DEFAULT, "You don't have that much money to bet!");
			return false;
		}
	}
	return true;
	}

	public static void play(Stoner stoner, int amount) {
	if (!canPlay(stoner, amount)) {
		return;
	}
	if (calculateWin()) {
		results(stoner, amount, true);
	} else {
		results(stoner, amount, false);
	}
	}

	public static void results(Stoner stoner, int amount, boolean win) {
	String bet = Utility.format(amount);
	if (win) {
		if (stoner.isPouchPayment()) {
			stoner.setMoneyPouch(stoner.getMoneyPouch() + amount);
			stoner.send(new SendString(stoner.getMoneyPouch() + "", 8135));
		} else {
			stoner.getBox().add(995, amount);
		}
		if (amount >= 10_000_000) {
			World.sendGlobalMessage("<img=8> <col=C42BAD>" + stoner.getUsername() + " has just won " + Utility.format(amount) + " from the Gambler!");
		}
		save(+amount);
		DialogueManager.sendNpcChat(stoner, 1011, Emotion.DEFAULT, "Congratulations! You have won " + bet + ".");
		return;
	}
	if (stoner.isPouchPayment()) {
		stoner.setMoneyPouch(stoner.getMoneyPouch() - amount);
		stoner.send(new SendString(stoner.getMoneyPouch() + "", 8135));
	} else {
		stoner.getBox().remove(995, amount);
	}
	DialogueManager.sendNpcChat(stoner, 1011, Emotion.DEFAULT, "Sorry! You have lost " + bet + "!");
	save(-amount);
	}

	public static void save(long amount) {
	MONEY_TRACKER += amount;
	FileHandler.saveGambling();
	}

}
