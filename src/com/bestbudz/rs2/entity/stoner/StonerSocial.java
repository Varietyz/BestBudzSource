package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.Server;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.PrivateMessaging;
import com.bestbudz.rs2.content.StonerTitle;
import com.bestbudz.rs2.content.clanchat.Clan;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all social functionality including clan chat, private messaging, titles, and player reports
 */
public class StonerSocial {
	private final Stoner stoner;
	private final PrivateMessaging privateMessaging;

	// Player titles
	private StonerTitle stonerTitle;
	public List<StonerTitle> unlockedTitles = new ArrayList<>();
	private String yellTitle = "Stoner";

	// Reporting system
	public long lastReport = 0;
	public String lastReported = "";
	public String reportName = "";
	public int reportClicked = 0;

	// Targeting system
	public String targetName = "";
	public int targetIndex;
	public String viewing;

	public StonerSocial(Stoner stoner) {
		this.stoner = stoner;
		this.privateMessaging = new PrivateMessaging(stoner);
	}

	public void process() {
		if (stoner.isPetStoner()) {
			return; // Pets don't need social processing
		}
		// Any periodic social processing can go here
	}

	/**
	 * Gets the clan this player owns
	 */
	public Clan getClan() {
		if (Server.clanManager.clanExists(stoner.getUsername())) {
			return Server.clanManager.getClan(stoner.getUsername());
		}
		return null;
	}

	/**
	 * Clears the clan chat interface
	 */
	public void clearClanChat() {
		stoner.send(new SendString("Chilling in: ", 18139));
		stoner.send(new SendString("Grower: ", 18140));
		for (int j = 18144; j < 18244; j++) {
			stoner.send(new SendString("", j));
		}
	}

	/**
	 * Sets up clan data on the interface
	 */
	public void setClanData() {
		boolean exists = Server.clanManager.clanExists(stoner.getUsername());
		if (!exists || stoner.clan == null) {
			stoner.send(new SendString("Join Cult", 18135));
			stoner.send(new SendString("", 18139));
			stoner.send(new SendString("", 18140));
		}
		if (!exists) {
			stoner.send(new SendString("You been excommunicated", 53706));
			String title = "";
			for (int id = 53707; id < 53717; id += 3) {
				if (id == 53707) {
					title = "Stoners";
				} else if (id == 53710) {
					title = "Stoners";
				} else if (id == 53713) {
					title = "Stoner+";
				} else if (id == 53716) {
					title = "Only stoner";
				}
				stoner.send(new SendString(title, id + 2));
			}
			for (int index = 0; index < 100; index++) {
				stoner.send(new SendString("", 53723 + index));
			}
			for (int index = 0; index < 100; index++) {
				stoner.send(new SendString("", 18424 + index));
			}
			return;
		}
		Clan clan = Server.clanManager.getClan(stoner.getUsername());
		stoner.send(new SendString(clan.getTitle(), 53706));
		String title = "";
		for (int id = 53707; id < 53717; id += 3) {
			if (id == 53707) {
				title =
					clan.getRankTitle(clan.whoCanJoin)
						+ (clan.whoCanJoin > Clan.Rank.ANYONE && clan.whoCanJoin < Clan.Rank.OWNER
						? "+"
						: "");
			} else if (id == 53710) {
				title =
					clan.getRankTitle(clan.whoCanTalk)
						+ (clan.whoCanTalk > Clan.Rank.ANYONE && clan.whoCanTalk < Clan.Rank.OWNER
						? "+"
						: "");
			} else if (id == 53713) {
				title =
					clan.getRankTitle(clan.whoCanKick)
						+ (clan.whoCanKick > Clan.Rank.ANYONE && clan.whoCanKick < Clan.Rank.OWNER
						? "+"
						: "");
			} else if (id == 53716) {
				title =
					clan.getRankTitle(clan.whoCanBan)
						+ (clan.whoCanBan > Clan.Rank.ANYONE && clan.whoCanBan < Clan.Rank.OWNER
						? "+"
						: "");
			}
			stoner.send(new SendString(title, id + 2));
		}
		if (clan.rankedMembers != null) {
			for (int index = 0; index < 100; index++) {
				if (index < clan.rankedMembers.size()) {
					stoner.send(
						new SendString(
							"<clan=" + clan.ranks.get(index) + ">" + clan.rankedMembers.get(index),
							43723 + index));
				} else {
					stoner.send(new SendString("", 43723 + index));
				}
			}
		}
		if (clan.bannedMembers != null) {
			for (int index = 0; index < 100; index++) {
				if (index < clan.bannedMembers.size()) {
					stoner.send(new SendString(clan.bannedMembers.get(index), 43824 + index));
				} else {
					stoner.send(new SendString("", 43824 + index));
				}
			}
		}
	}

	/**
	 * Adds the player to the default channel
	 */
	public void addDefaultChannel() {
		if (stoner.clan == null) {
			Clan localClan = Server.clanManager.getClan("bestbudz");
			if (localClan != null) {
				localClan.addMember(stoner);
			} else {
				stoner.send(new SendMessage(Utility.formatStonerName("") + " has not created a Cult yet."));
			}
		}
	}

	/**
	 * Determines the player's rank string for display
	 */
	public String deterquarryRank(Stoner stoner) {
		switch (stoner.getRights()) {
			case 0:
				return "Stoner";
			case 1:
				return "<col=006699>Moderator</col>";
			case 2:
				return "<col=E6E600>Adminstrator</col>";
			case 3:
				return "<col=AB1818>Owner</col>";
			case 4:
				return "<col=CF1DCF>Developer</col>";
			case 5:
				return "<col=B20000>Babylonian</col>";
			case 6:
				return "<col=223ca9>Ganja Man</col>";
			case 7:
				return "<col=2EB8E6>Rasta</col>";
			case 8:
				return "<col=971FF2>Waldo</col>";
			case 9:
				return "<col=971FF2>Best Bud</col>";
			case 10:
				return "<col=971FF2>No-Life</col>";
			case 11:
				return "@gry@Dealer</col>";
			case 12:
				return "@gre@Grower</col>";
		}
		return "Unknown!";
	}

	/**
	 * Determines the player's rank icon for display
	 */
	public String deterquarryIcon(Stoner stoner) {
		switch (stoner.getRights()) {
			case 0:
				return "<img=11>";
			case 1:
				return "<img=0>";
			case 2:
				return "<img=1>";
			case 3:
				return "<img=2>";
			case 4:
				return "<img=3>";
			case 5:
				return "<img=4>";
			case 6:
				return "<img=5>";
			case 7:
				return "<img=6>";
			case 8:
				return "<img=7>";
			case 9:
				return "<img=8>";
			case 10:
				return "<img=10>";
			case 11:
				return "<img=11>";
			case 12:
				return "<img=11>";
		}
		return "";
	}

	// Getters and setters
	public PrivateMessaging getPrivateMessaging() { return privateMessaging; }

	public StonerTitle getStonerTitle() { return stonerTitle; }
	public void setStonerTitle(StonerTitle stonerTitle) { this.stonerTitle = stonerTitle; }

	public List<StonerTitle> getUnlockedTitles() { return unlockedTitles; }
	public void setUnlockedTitles(List<StonerTitle> unlockedTitles) { this.unlockedTitles = unlockedTitles; }

	public String getYellTitle() { return yellTitle; }
	public void setYellTitle(String yellTitle) { this.yellTitle = yellTitle; }

	public String getTargetName() { return targetName; }
	public void setTargetName(String targetName) { this.targetName = targetName; }

	public int getTargetIndex() { return targetIndex; }
	public void setTargetIndex(int targetIndex) { this.targetIndex = targetIndex; }

	public String getViewing() { return viewing; }
	public void setViewing(String viewing) { this.viewing = viewing; }

	// Access to lastClanChat field
	public String getLastClanChat() { return stoner.lastClanChat; }
	public void setLastClanChat(String lastClanChat) { stoner.lastClanChat = lastClanChat; }
}