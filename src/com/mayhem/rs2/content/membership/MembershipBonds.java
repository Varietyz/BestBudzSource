package com.mayhem.rs2.content.membership;

import java.util.HashMap;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.content.interfaces.impl.oldQuestTab;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * Handles Membership Bonds
 * @author Daniel
 *
 */
public class MembershipBonds {
	
	/**
	 * Bond Data
	 * @author Daniel
	 *
	 */
	public enum BondData {
		
		VOTE("20 FalconExpress Bucks", 7478, 0, 20, 0),
		ONE("100 FalconExpress Bucks", 13190, 1, 100, 10),
		TWO("300 FalconExpress Bucks", 13191, 3, 300, 30),
		THREE("500 FalconExpress Bucks", 13192, 5, 500, 50),
		FOUR("7500 FalconExpress Bucks", 13193, 8, 7500, 750),
		FIVE("1,000 FalconExpress Bucks", 14000, 10, 1000, 100),
		SIX("2,500 FalconExpress Bucks", 13195, 25, 2500, 250),
		SEVEN("5,000 FalconExpress Bucks", 13196, 50, 5000, 500),
		EIGHT("10,000 FalconExpress Bucks", 5444, 100, 10000, 1000),
		NINE("20,000 FalconExpress Bucks", 13198, 200, 20000, 2000);
		
		private final String name;
		private final int item;
		private final int moneySpent;
		private final int credits;
		private final int complimentary;
		
		private BondData(String name, int item, int moneySpent, int credits, int complimentary) {
			this.name = name;
			this.item = item;
			this.moneySpent = moneySpent;
			this.credits = credits;
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
			return credits;
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
	 * @param player
	 * @param itemId
	 * @return
	 */
	public static boolean handle(Player player, int itemId) {
		
		BondData data = BondData.bonds.get(itemId);

		if (data == null) {
			return false;
		}
		
		if (player.getInventory().getFreeSlots() == 0) {
			player.send(new SendMessage("Please clear up some inventory spaces before doing this!"));
			return false;
		}//say you spend $5, your rank automatically updates to donator. or if you open the credits bonds. and if you were t
		
		player.setMember(true);
		player.getInventory().remove(data.getItem(), 1);
		player.setCredits(player.getCredits() + data.getCredits());
		player.setMoneySpent(player.getMoneySpent() + data.getSpent());
		player.send(new SendMessage("@dre@Thank you for your purchase!"));
		RankHandler.upgrade(player);		
		if (data.getComplimentary() != 0) {
			player.setCredits(player.getCredits() + data.getComplimentary());
			player.send(new SendMessage("@dre@You have been complimentated " + Utility.format(data.getComplimentary()) + " FalconExpress Bucks!"));
		}
		World.sendGlobalMessage("</col>[ @dre@FalconExpress </col>] @dre@" + player.determineIcon(player) + " " + Utility.formatPlayerName(player.getUsername()) + "</col> has just reedemed @dre@" + Utility.format(data.getCredits()) + "</col> FalconExpress Bucks!");
		InterfaceHandler.writeText(new oldQuestTab(player));
		return true;
	}

}
