package com.bestbudz.rs2.content.membership;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.CreditTab;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerConstants;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendBanner;

/**
 * Handles upgrading rank
 * 
 * @author Jaybane
 *
 */
public class RankHandler {

	/**
	 * Upgrades rank
	 * 
	 * @param stoner
	 */
	public static void upgrade(Stoner stoner) {

	if (StonerConstants.isStaff(stoner)) {
		DialogueManager.sendStatement(stoner, "You are worth more then a donor.");
		return;
	}

	int rights = 0;
	if (stoner.getMoneySpent() < 5)
		rights = 0;
	if (stoner.getMoneySpent() >= 5)
		rights = 5;
	if (stoner.getMoneySpent() >= 15)
		rights = 6;
	if (stoner.getMoneySpent() >= 25)
		rights = 7;
	if (stoner.getMoneySpent() >= 50)
		rights = 8;

	if (rights != 0 && stoner.getRights() != rights) {
		stoner.setRights(rights);
		stoner.getUpdateFlags().setUpdateRequired(true);
		InterfaceHandler.writeText(new QuestTab(stoner));
		InterfaceHandler.writeText(new CreditTab(stoner));
		stoner.send(new SendBanner("You are now " + Utility.getAOrAn(stoner.deterquarryRank(stoner)) + " " + stoner.deterquarryIcon(stoner) + " " + stoner.deterquarryRank(stoner) + "!", 0x1C889E));
		DialogueManager.sendStatement(stoner, "You are now " + Utility.getAOrAn(stoner.deterquarryRank(stoner)) + " " + stoner.deterquarryIcon(stoner) + " " + stoner.deterquarryRank(stoner) + "!");
	}
	}

}
