package com.bestbudz.rs2.content.gambling;

import java.util.ArrayList;
import java.util.List;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.io.StonerSaveUtil;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

/**
 * Handles the Lottery
 * 
 * @author Jaybane
 *
 */
public class Lottery {

	/**
	 * Stoners entered in the lottery
	 */
	private static final List<Stoner> entries = new ArrayList<Stoner>();

	/**
	 * The lottery limit
	 */
	private static final int LOTTERY_LIMIT = 100_000_000;

	/**
	 * The entry price of lottery
	 */
	private static final int ENTRY_PRICE = 1_000_000;

	/**
	 * Current threshold of lottery
	 */
	private static int CURRENT_POT = 0;

	/**
	 * The winner of lottery
	 */
	private static Stoner winner = null;

	/**
	 * Handles stoner entering the lottery
	 * 
	 * @param stoner
	 */
	public static void enterLotter(Stoner stoner) {

	if (hasEntered(stoner)) {
		DialogueManager.sendStatement(stoner, "You are already entered in the lottery!");
		return;
	}

	if (CURRENT_POT >= LOTTERY_LIMIT) {
		DialogueManager.sendStatement(stoner, "The lottery is currently full.");
		return;
	}

	if (!stoner.getBox().hasItemAmount(995, ENTRY_PRICE)) {
		DialogueManager.sendStatement(stoner, "You need " + Utility.format(ENTRY_PRICE) + " BestBucks to enter the lottery!");
		return;
	}

	stoner.getBox().remove(995, ENTRY_PRICE);
	CURRENT_POT += ENTRY_PRICE;
	entries.add(stoner);
	World.sendGlobalMessage("[ <col=C46423>Lottery </col>] <col=C46423>" + stoner.deterquarryIcon(stoner) + " " + stoner.getUsername() + "</col> has just entered the lottery! Pot: <col=C46423>" + Utility.format(CURRENT_POT) + " </col>/ <col=C46423>" + Utility.format(LOTTERY_LIMIT) + "</col>.");


	if (CURRENT_POT == LOTTERY_LIMIT) {
		draw();
	}

	}

	/**
	 * Draws the lottery
	 */
	public static void draw() {
	if (entries.isEmpty()) {
		return;
	}
	winner = Utility.randomElement(entries);

	if (winner == null) {
		StonerSaveUtil.addToOfflineContainer(winner.getUsername(), new Item(995, CURRENT_POT));
	} else {
		winner.send(new SendMessage("Congratulations! You have won the lottery. Money has been sent to your Debit Card."));

		winner.setMoneyPouch(winner.getMoneyPouch() + CURRENT_POT);
		winner.send(new SendString(winner.getMoneyPouch() + "", 8135));
	}

	World.sendGlobalMessage("[ <col=C46423>Lottery </col>] <col=C46423>" + winner.getUsername() + "</col> has just won the lottery of <col=C46423>" + Utility.format(CURRENT_POT) + "</col>!");
	reset();
	}

	/**
	 * Resets the lottery
	 */
	public static void reset() {
	winner = null;
	CURRENT_POT = 0;
	entries.clear();
	}

	/**
	 * Does an announcement for lottery
	 */
	public static void announce() {
	World.sendGlobalMessage("[ <col=C46423>Lottery </col>] The current pot is at <col=C46423>" + Utility.format(CURRENT_POT) + " </col>/ <col=C46423>" + Utility.format(LOTTERY_LIMIT) + "</col>.");
	}

	/**
	 * Gets the current lottery pot
	 * 
	 * @return
	 */
	public static int getPot() {
	return CURRENT_POT;
	}

	/**
	 * Gets the current lottery limit
	 * 
	 * @return
	 */
	public static int getLimit() {
	return LOTTERY_LIMIT;
	}

	/**
	 * Gets the amount of stoners involved in lottery
	 * 
	 * @return
	 */
	public static int getStoners() {
	return entries.size();
	}

	/**
	 * Checks if stoner has entered the lotter
	 * 
	 * @param stoner
	 * @return
	 */
	public static boolean hasEntered(Stoner stoner) {
	if (entries.contains(stoner)) {
		return true;
	}
	return false;
	}

}
