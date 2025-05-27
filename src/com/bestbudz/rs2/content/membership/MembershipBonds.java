package com.bestbudz.rs2.content.membership;

import java.util.HashMap;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

/**
 * Handles Membership Bonds
 * 
 * @author Jaybane
 *
 */
public class MembershipBonds {

	/**
	 * Bond Data
	 * 
	 * @author Jaybane
	 *
	 */
	public enum BondData {

		ONE("1,000 credit", 13190, 5, 1000, 0),
		TWO("3,000 credit", 13191, 7, 3000, 0),
		THREE("5,000 credit", 13192, 10, 5000, 0),
		FOUR("8,000 credit", 13193, 12, 8000, 0),
		FIVE("10,000 credit", 13194, 15, 10000, 0),
		SIX("20,000 credit", 13195, 20, 20000, 250),
		SEVEN("50,000 credit", 13196, 25, 50000, 500),
		EIGHT("100,000 credit", 13197, 35, 100000, 1500),
		NINE("200,000 credit", 13198, 50, 200000, 5000);

		private final String name;
		private final int item;
		private final int moneySpent;
		private final int cannacredits;
		private final int complimentary;

		private BondData(String name, int item, int moneySpent, int cannacredits, int complimentary) {
		this.name = name;
		this.item = item;
		this.moneySpent = moneySpent;
		this.cannacredits = cannacredits;
		this.complimentary = complimentary;
		}

		public String getName() {
		return name;
		}

		public int getItem() {
		return item;
		}

		public int getSpent() {
		return moneySpent;
		}

		public int getCredits() {
		return cannacredits;
		}

		public int getComplimentary() {
		return complimentary;
		}

		private static HashMap<Integer, BondData> bonds = new HashMap<Integer, BondData>();

		static {
			for (final BondData item : BondData.values()) {
				BondData.bonds.put(item.item, item);
			}
		}
	}

	/**
	 * Handles opening bond
	 * 
	 * @param stoner
	 * @param itemId
	 * @return
	 */
	public static boolean handle(Stoner stoner, int itemId) {

	BondData data = BondData.bonds.get(itemId);

	if (data == null) {
		return false;
	}

	if (stoner.getBox().getFreeSlots() == 0) {
		stoner.send(new SendMessage("Please clear some space in your box!"));
		return false;
	}

	stoner.setMember(true);
	stoner.getBox().remove(data.getItem(), 1);
	stoner.setCredits(stoner.getCredits() + data.getCredits());
	stoner.setMoneySpent(stoner.getMoneySpent() + data.getSpent());
	stoner.send(new SendMessage("@dre@Jah bless!"));
	RankHandler.upgrade(stoner);
	if (data.getComplimentary() != 0) {
		stoner.setCredits(stoner.getCredits() + data.getComplimentary());
		stoner.send(new SendMessage("@dre@You have been complimentated " + Utility.format(data.getComplimentary()) + " CannaCredits!"));
	}
	World.sendGlobalMessage("</col>[ @dre@BestBudz </col>] @dre@" + stoner.deterquarryIcon(stoner) + " " + Utility.formatStonerName(stoner.getUsername()) + "</col> redeemed a voucher of @dre@" + Utility.format(data.getCredits()) + "</col> CannaCredits!");
	InterfaceHandler.writeText(new QuestTab(stoner));
	return true;
	}

}
