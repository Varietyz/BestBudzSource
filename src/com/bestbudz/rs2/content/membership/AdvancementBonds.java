package com.bestbudz.rs2.content.membership;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.HashMap;

public class AdvancementBonds
{

	public static BondData rewardIfThresholdReached(Stoner s) {

		int total = s.getTotalAdvances();

		for (BondData b : BondData.values()) {
        /* already given? we piggy-back on rights, because
           each bond-threshold matches one rights value        */
			if (s.getRights() == (4 + b.ordinal()))   // 5,6,7,8,9,10
				continue;

			if (total == b.advancementGrades) {
				/* inventory space check */
				if (s.getBox().getFreeSlots() == 0) {
					s.send(new SendMessage("Leave one free box slot to receive your "
						+ b.getName() + "!"));
					return null;                      // block calling code
				}

				/* award bond + credits */
				s.getBox().add(new Item(b.getItem(), 1));
				s.setCredits(s.getCredits() + b.getCredits() + b.getComplimentary());
				s.send(new SendMessage("@dre@Milestone reached – you received "
					+ b.getName() + "!"));

				return b;                            // tell caller we paid
			}
		}
		return null;                                 // nothing to give
	}

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
	stoner.send(new SendMessage("@dre@Jah bless!"));
	RankHandler.upgrade(stoner);
	if (data.getComplimentary() != 0) {
		stoner.setCredits(stoner.getCredits() + data.getComplimentary());
		stoner.send(new SendMessage("@dre@You have been complimentated " + Utility.format(data.getComplimentary()) + " CannaCredits!"));
	}
	World.sendGlobalMessage("</col>[ @dre@BestBudz </col>] @dre@" + stoner.deterquarryIcon(stoner) + " " + Utility.formatStonerName(stoner.getUsername()) + "</col> redeemed an advance voucher of @dre@" + Utility.format(data.getCredits()) + "</col> CannaCredits!");
	InterfaceHandler.writeText(new QuestTab(stoner));
	return true;
	}


	public enum BondData {

		ONE("1,000 credit", 13190, 5, 1000, 0),
		TWO("3,000 credit", 13191, 10, 3000, 0),
		THREE("5,000 credit", 13192, 20, 5000, 0),
		FOUR("8,000 credit", 13193, 35, 8000, 0),
		FIVE("10,000 credit", 13194, 55, 10000, 0),
		SIX("20,000 credit", 13195, 80, 20000, 250),
		SEVEN("50,000 credit", 13196, 105, 50000, 500);
		private static final HashMap<Integer, BondData> bonds = new HashMap<Integer, BondData>();

		static {
			for (final BondData item : BondData.values()) {
				BondData.bonds.put(item.item, item);
			}
		}

		public final int advancementGrades;
		private final String name;
		private final int item;
		private final int cannacredits;
		private final int complimentary;

		BondData(String name, int item, int advancementGrades, int cannacredits, int complimentary) {
		this.name = name;
		this.item = item;
		this.advancementGrades = advancementGrades;
		this.cannacredits = cannacredits;
		this.complimentary = complimentary;
		}

		public String getName() {
		return name;
		}

		public int getItem() {
		return item;
		}

		public int getCredits() {
		return cannacredits;
		}

		public int getComplimentary() {
		return complimentary;
		}
	}

}
