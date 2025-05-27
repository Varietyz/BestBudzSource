package com.bestbudz.rs2.content.dialogue.impl;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.Dialogue;
import com.bestbudz.rs2.content.dialogue.DialogueConstants;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.dialogue.Emotion;
import com.bestbudz.rs2.content.shopping.impl.BountyShop;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

/**
 * Dialogue that handles Emblem Trader
 * 
 * @author Jaybane
 *
 */
public class EmblemDialogue extends Dialogue {

	/**
	 * Emblem Trader
	 * 
	 * @param stoner
	 */
	public EmblemDialogue(Stoner stoner) {
	this.stoner = stoner;
	}

	/**
	 * The bounty total
	 */
	int bountyTotal;

	/**
	 * The tier data
	 */
	public int[][] TIER_DATA = { { 12746, 50_000 }, { 12748, 100_000 }, { 12749, 200_000 }, { 12750, 400_000 }, { 12751, 750_000 }, { 12752, 1_200_000 }, { 12753, 1_750_000 }, { 12754, 2_500_000 }, { 12755, 3_500_000 }, { 12756, 5_000_000 } };

	/**
	 * Calculation of tier
	 */
	public void calculateTotal() {
	bountyTotal = 0;
	for (int[] emblem : TIER_DATA) {
		bountyTotal += stoner.getBox().getItemAmount(emblem[0]) * emblem[1];
	}
	}

	/**
	 * Clicking button
	 * 
	 * @param id
	 */
	@Override
	public boolean clickButton(int id) {
	switch (id) {
	// Trading tiers
	case DialogueConstants.OPTIONS_5_1:
		for (int i = 0; i < TIER_DATA.length; i++) {
			if (stoner.getBox().hasItemId(new Item(TIER_DATA[i][0]))) {
				stoner.send(new SendRemoveInterfaces());
				calculateTotal();
				stoner.setBountyPoints(stoner.getBountyPoints() + bountyTotal);
				DialogueManager.sendStatement(stoner, "You traded your emblems for " + Utility.format(bountyTotal) + " points.");
				bountyTotal = 0;
				stoner.getBox().remove(new Item(TIER_DATA[i][0]));
				break;
			} else {
				DialogueManager.sendNpcChat(stoner, 315, Emotion.ANGRY_1, "You do not have any tiers on you!");
			}
		}
		break;

	// Statistics
	case DialogueConstants.OPTIONS_5_2:
		double kdr = ((double) stoner.getKills()) / ((double) stoner.getDeaths());
		DialogueManager.sendInformationBox(stoner, "PvP Statistics:", "Points: @red@" + stoner.getBountyPoints(), "Kills: @red@" + stoner.getKills(), "Deaths: @red@" + stoner.getDeaths(), "KDR: @red@" + kdr);
		break;

	// Trading
	case DialogueConstants.OPTIONS_5_3:
		stoner.getShopping().open(BountyShop.SHOP_ID);
		break;

	// Skulling
	case DialogueConstants.OPTIONS_5_4:
		if (stoner.getSkulling().isSkulled()) {
			DialogueManager.sendNpcChat(stoner, 315, Emotion.DEFAULT, "You already have a wilderness skull!");
			return false;
		}
		stoner.getSkulling().skull(stoner, stoner);
		DialogueManager.sendNpcChat(stoner, 315, Emotion.DEFAULT, "You have been skulled.");
		break;

	// Close dialogue
	case DialogueConstants.OPTIONS_5_5:
		if (stoner.getCredits() < 3) {
			DialogueManager.sendStatement(stoner, "You need 3 cannacredits to do this!");
			return false;
		}
		stoner.setCredits(stoner.getCredits() - 3);
		stoner.setDeaths(0);
		DialogueManager.sendStatement(stoner, "Your deaths have been reset!");
		break;
	}
	return false;
	}

	/**
	 * Execute dialogue
	 */
	@Override
	public void execute() {
	switch (next) {
	case 0:
		DialogueManager.sendNpcChat(stoner, 315, Emotion.CALM, "Hello stoner.", "How may I help you today?");
		next++;
		break;
	case 1:
		DialogueManager.sendOption(stoner, "Sell tiers", "Show me my PvP statistics", "I would like to trade", "Give me a wilderness skull", "Reset KDR");
		break;
	}
	}

}
