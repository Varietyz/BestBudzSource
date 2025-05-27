package com.bestbudz.rs2.content;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

/**
 * Handles Money Pouch
 * 
 * @author Jaybane
 *
 */
public class MoneyPouch {

	/**
	 * Stoner
	 */
	private final Stoner stoner;

	/**
	 * Money Pouch
	 * 
	 * @param stoner
	 */
	public MoneyPouch(Stoner stoner) {
	this.stoner = stoner;
	}

	/**
	 * Format bestbucks
	 * 
	 * @param amount
	 * @return
	 */
	public String formatBestBucks(long amount) {
	if (amount >= 1_000 && amount < 1_000_000) {
		return "" + (amount / 1_000) + "K";
	}

	if (amount >= 1_000_000 && amount < 1_000_000_000) {
		return "" + (amount / 1_000_000) + "M";
	}

	if (amount >= 1_000_000_000) {
		return "" + (amount / 1_000_000_000) + "B";
	}
	return "" + amount;
	}

	/**
	 * Adds bestbucks to Money Pouch
	 */
	public void addPouch() {

	// Checks for Pin
	if (stoner.getPin() != null && stoner.enteredPin == false) {
		stoner.send(new SendInterface(48750));
		return;
	}

	// Check if stoner is in a position to add bestbucks
	if (stoner.inWGLobby() || stoner.inWGGame() || stoner.getMage().isTeleporting() || stoner.isDead() || stoner.inWilderness() || stoner.getCombat().inCombat() || stoner.getDueling().isDueling() || stoner.getInterfaceManager().hasInterfaceOpen()) {
		stoner.send(new SendMessage("You can't do this right now!"));
		return;
	}

	// Check if money pouch is filled
	if (stoner.getMoneyPouch() == Long.MAX_VALUE) {
		stoner.send(new SendMessage("Your debit card is at maximum capacity!"));
		return;
	}

	// Grabs amount of bestbucks to store
	int amount = stoner.getBox().getItemAmount(995);

	// Checks if current stored bestbucks + new bestbucks to store exceed the max value
	if (stoner.getMoneyPouch() + amount >= Long.MAX_VALUE) {
		stoner.send(new SendMessage("Unable to exceed debit card upload limit!"));
		return;
	}

	// Removes bestbucks from box
	stoner.getBox().remove(995, amount);

	// Adds bestbucks to Money Pouch
	stoner.setMoneyPouch(stoner.getMoneyPouch() + amount);

	// Sends confirmation message
	stoner.send(new SendMessage("@dre@You have added " + Utility.format(amount) + " BestBucks to your debit card. Total: " + formatBestBucks(stoner.getMoneyPouch()) + "."));

	// Updates string
	stoner.send(new SendString(stoner.getMoneyPouch() + "", 8135));
	}

	/**
	 * Withdraw bestbucks from Money Pouch
	 * 
	 * @param amount
	 */
	public void withdrawPouch(long amount) {

	// Checks for Pin
	if (stoner.getPin() != null && stoner.enteredPin == false) {
		stoner.send(new SendInterface(48750));
		return;
	}

	// Check if stoner is in a position to withdraw bestbucks
	if (stoner.inWGLobby() || stoner.inWGGame() || stoner.getMage().isTeleporting() || stoner.isDead() || stoner.inWilderness() || stoner.getCombat().inCombat() || stoner.getDueling().isDueling() || stoner.getInterfaceManager().hasInterfaceOpen()) {
		stoner.send(new SendMessage("You can't do this right now!"));
		return;
	}

	// Checks if stoner is withdrawing a negative amount
	if (amount <= 0) {
		stoner.send(new SendMessage("You can't withdraw a negative amount!"));
		return;
	}

	// Checks if stoner has the amount to withdraw stored
	if (stoner.getMoneyPouch() < amount) {
		amount = stoner.getMoneyPouch();
	}

	// Checks if bestbucks in box + amount to withdraw passes max value
	if ((long) (stoner.getBox().getItemAmount(995) + amount) > Integer.MAX_VALUE) {
		stoner.send(new SendMessage("You don't have enough space to withdraw that many bestbucks"));
		amount = Integer.MAX_VALUE - stoner.getBox().getItemAmount(995);
	}

	// Check to see if stoner is withdrawing more than max value
	if (amount > Integer.MAX_VALUE) {
		stoner.send(new SendMessage("You can't withdraw more than 2B BestBucks at a time!"));
		return;
	}

	// Checks if stoner has max value of bestbucks in box
	if (stoner.getBox().getItemAmount(995) == Integer.MAX_VALUE) {
		stoner.send(new SendMessage("You can't withdraw any more BestBucks!"));
		return;
	}

	// Checks if stoner has space to withdraw the bestbucks
	if (!stoner.getBox().hasItemId(995) && stoner.getBox().getFreeSlots() == 0) {
		stoner.send(new SendMessage("You do not have enough box spaces to withdraw BestBucks."));
		return;
	}

	// Removes bestbucks from pouch
	stoner.setMoneyPouch(stoner.getMoneyPouch() - amount);

	// Adds bestbucks to box
	stoner.getBox().add(995, (int) amount);

	// Sends confirmation dialogue
	DialogueManager.sendItem1(stoner, "You have withdrawn <col=255>" + Utility.format(amount) + " </col>BestBucks.", 995);

	// Updates string
	stoner.send(new SendString(stoner.getMoneyPouch() + "", 8135));
	}

	public void clear() {
	}

}
