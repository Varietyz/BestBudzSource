package com.bestbudz.rs2.content.clanchat;

import com.bestbudz.GameDataLoader;
import com.bestbudz.Server;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import org.javacord.api.entity.channel.TextChannel;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Clan {

	public String title;
	public String founder;
	public LinkedList<String> activeMembers = new LinkedList();
	public LinkedList<String> bannedMembers = new LinkedList();
	public LinkedList<String> rankedMembers = new LinkedList();
	public LinkedList<Integer> ranks = new LinkedList();
	public int whoCanJoin = -1;
	public int whoCanTalk = -1;
	public int whoCanKick = 6;
	public int whoCanBan = 7;

	public Clan(Stoner paramStoner) {
	setTitle(paramStoner.getUsername() + "'s Cult");
	setFounder(paramStoner.getUsername().toLowerCase());
	}

	public Clan(String paramString1, String paramString2) {
	setTitle(paramString1);
	setFounder(paramString2);
	}

	public void addMember(Stoner paramStoner) {
	if (isBanned(paramStoner.getUsername())) {
		paramStoner.getClient().queueOutgoingPacket(new SendMessage("You are currently banned from this Cult."));
		return;
	}
	if ((this.whoCanJoin > -1) && (!isFounder(paramStoner.getUsername())) && (getRank(paramStoner.getUsername()) < this.whoCanJoin)) {
		paramStoner.getClient().queueOutgoingPacket(new SendMessage("Only " + getRankTitle(this.whoCanJoin) + "s+ may join this Cult."));
		return;
	}

	paramStoner.clan = this;
	paramStoner.lastClanChat = getFounder();
	this.activeMembers.add(paramStoner.getUsername());
	paramStoner.getClient().queueOutgoingPacket(new SendString("Excommunicate", 18135));
	paramStoner.getClient().queueOutgoingPacket(new SendString("</col>Cult: <col=FFFF64><shad=0>" + getTitle(), 18139));
	paramStoner.getClient().queueOutgoingPacket(new SendString("</col>High Priest: <col=#64d542><shad=0>" + Utility.formatStonerName(getFounder()), 18140));
	paramStoner.getClient().queueOutgoingPacket(new SendMessage("Attempting to join Cult..."));
	paramStoner.getClient().queueOutgoingPacket(new SendMessage("Joined Cult <col=FFFF64><shad=0>" + getTitle() + "</shad></col>."));
	updateMembers();
	if (this.founder.equalsIgnoreCase("bestbudz")) {
		Stoner stoner = paramStoner;
		String ts = "";
		ts = ts + stoner.getUsername() + " is active.";
	}
	}

	public void banMember(String paramString) {
	paramString = Utility.formatStonerName(paramString);
	if (this.bannedMembers.contains(paramString)) {
		return;
	}
	if (paramString.equalsIgnoreCase(getFounder())) {
		return;
	}
	if (isRanked(paramString)) {
		return;
	}
	removeMember(paramString);
	this.bannedMembers.add(paramString);
	save();
	Stoner localStoner = World.getStonerByName(paramString);
	if ((localStoner != null)) {
		localStoner.getClient().queueOutgoingPacket(new SendMessage("You have been banned from the Cult."));
	}
	sendMessage("Attempting to excommunicate the user '" + Utility.formatStonerName(paramString) + "' from the Cult.");
	}

	public boolean canBan(String paramString) {
	if (isFounder(paramString)) {
		return true;
	}
		return getRank(paramString) >= this.whoCanBan;
	}

	public boolean canKick(String paramString) {
	if (isFounder(paramString)) {
		return true;
	}
		return getRank(paramString) >= this.whoCanKick;
	}

	public void delete() {
	for (String str : this.activeMembers) {
		removeMember(str);
		Stoner localStoner = World.getStonerByName(str);
		localStoner.getClient().queueOutgoingPacket(new SendMessage("The Cult you were in has vanished."));
	}
	Server.clanManager.delete(this);
	}

	public void demote(String paramString) {
	if (!this.rankedMembers.contains(paramString)) {
		return;
	}
	int i = this.rankedMembers.indexOf(paramString);
	this.rankedMembers.remove(i);
	this.ranks.remove(i);
	save();
	}

	public String getFounder() {
	return this.founder;
	}

	public void setFounder(String paramString) {
	this.founder = paramString;
	}

	public int getRank(String paramString) {
	paramString = Utility.formatStonerName(paramString);
	if (this.rankedMembers.contains(paramString)) {
		return this.ranks.get(this.rankedMembers.indexOf(paramString)).intValue();
	}
	if (isAdmin(paramString)) {
		return 8;
	}
	if (isFounder(paramString)) {
		return 7;
	}
	return -1;
	}

	public String getRankTitle(int paramInt) {
	switch (paramInt) {
	case -1:
		return "Initiate";
	case 0:
		return "Stoner";
	case 1:
		return "Disciple";
	case 2:
		return "Mendicant";
	case 3:
		return "Priest";
	case 4:
		return "Lector";
	case 5:
		return "Arch Lector";
	case 6:
		return "High Priest";
	case 7:
		return "Only Me";
	}
	return "";
	}

	public String getTitle() {
	return this.title;
	}

	public void setTitle(String paramString) {
	this.title = paramString;
	}

	public boolean isAdmin(String paramString) {
		return paramString.equalsIgnoreCase("jaybane") || paramString.equalsIgnoreCase("ikushz") || paramString.equalsIgnoreCase("bestbudz") || paramString.equalsIgnoreCase("");
	}

	public boolean isBanned(String paramString) {
	paramString = Utility.formatStonerName(paramString);
		return this.bannedMembers.contains(paramString);
	}

	public boolean isFounder(String paramString) {
		return getFounder().equalsIgnoreCase(paramString);
	}

	public boolean isRanked(String paramString) {
	paramString = Utility.formatStonerName(paramString);
		return this.rankedMembers.contains(paramString);
	}

	public void kickMember(String paramString) {
	if (!this.activeMembers.contains(paramString)) {
		return;
	}
	if (paramString.equalsIgnoreCase(getFounder())) {
		return;
	}
	removeMember(paramString);
	Stoner localStoner = World.getStonerByName(paramString);
	if (localStoner != null) {
		localStoner.getClient().queueOutgoingPacket(new SendMessage("You have been excommunicated from the Cult."));
	}
	sendMessage("Attempting to excommunicate the user '" + Utility.formatStonerName(paramString) + "' from this Cult.");
	}

	public void removeMember(Stoner paramStoner) {
	for (int i = 0; i < this.activeMembers.size(); i++) {
		if (this.activeMembers.get(i).equalsIgnoreCase(paramStoner.getUsername())) {
			if (this.founder.equalsIgnoreCase("bestbudz")) {
				Stoner stoner = paramStoner;
				TextChannel channel = (TextChannel) GameDataLoader.discord.getChannelById("947616122964934686").get();
				String ts = "";
				ts = ts + stoner.getUsername() + " is present.";
				channel.sendMessage("**" + ts + "**");
			}
			paramStoner.clan = null;
			resetInterface(paramStoner);
			this.activeMembers.remove(i);
		}
	}
	updateMembers();
	}

	public void removeMember(String paramString) {
	for (int i = 0; i < this.activeMembers.size(); i++) {
		if (this.activeMembers.get(i).equalsIgnoreCase(paramString)) {
			Stoner localStoner = World.getStonerByName(paramString);
			if (localStoner != null) {
				if (this.founder.equalsIgnoreCase("bestbudz")) {
					Stoner stoner = localStoner;
					String ts = "";
					ts = ts + stoner.getUsername() + " is absent.";
				}
				localStoner.clan = null;
				resetInterface(localStoner);
				this.activeMembers.remove(i);
			}
		}
	}
	updateMembers();
	}

	public void resetInterface(Stoner paramStoner) {
	paramStoner.getClient().queueOutgoingPacket(new SendString("Join Cult", 18135));
	paramStoner.getClient().queueOutgoingPacket(new SendString("", 18139));
	paramStoner.getClient().queueOutgoingPacket(new SendString("", 18140));
	paramStoner.getClient().queueOutgoingPacket(new SendString("", 18252));
	for (int i = 0; i < 100; i++) {
		paramStoner.getClient().queueOutgoingPacket(new SendString("", 18144 + i));
	}
	}

	public void save() {
	Server.clanManager.save(this);
	updateMembers();
	}

	public void sendChat(Stoner paramStoner, String paramString) {
	if (getRank(paramStoner.getUsername()) < this.whoCanTalk) {
		paramStoner.getClient().queueOutgoingPacket(new SendMessage("Only " + getRankTitle(this.whoCanTalk) + "s+ may talk in this Cult."));
		return;
	}
	if (paramStoner.isMuted()) {
		if (paramStoner.getMuteLength() == -1) {
			paramStoner.send(new SendMessage("You are permanently silenced on this account."));
			return;
		} else {
			long muteHours = TimeUnit.MILLISECONDS.toMinutes(paramStoner.getMuteLength() - System.currentTimeMillis());
			String timeUnit = "hour" + (muteHours > 1 ? "s" : "");
			if (muteHours < 60) {
				if (muteHours <= 0) {
					paramStoner.send(new SendMessage("Your silence has been lifted!"));
					paramStoner.setMuted(false);
				}
				timeUnit = "minute" + (muteHours > 1 ? "s" : "");
			} else {
				muteHours = TimeUnit.MINUTES.toHours(muteHours);
			}
			if (paramStoner.isMuted()) {
				paramStoner.send(new SendMessage("You are silenced, you will receive back speech in " + muteHours + " " + timeUnit + "."));
				return;
			}
		}
	}
	if (this.founder.equalsIgnoreCase("bestbudz")) {
		Stoner stoner = paramStoner;
		String ts = "";
		ts = ts + stoner.getUsername() + ": ";
	}
	for (int j = 0; j < World.getStoners().length; j++) {
		if (World.getStoners()[j] != null) {
			Stoner c = World.getStoners()[j];
			if ((c != null) && (this.activeMembers.contains(c.getUsername()) && paramStoner.getRights() == 2)) {
				c.getClient().queueOutgoingPacket(new SendMessage("</col>[@blu@" + getTitle() + "</col>] " + "<cult=" + getRank(paramStoner.getUsername()) + ">" + paramStoner.getUsername() + ":@dre@ " + Utility.capitalizeFirstLetter(paramString)));
			} else if ((c != null) && (this.activeMembers.contains(c.getUsername()))) {
				c.getClient().queueOutgoingPacket(new SendMessage("</col>[@blu@" + getTitle() + "</col>] <cult=" + getRank(paramStoner.getUsername()) + ">" + paramStoner.getUsername() + ":@dre@ " + Utility.capitalizeFirstLetter(paramString)));
			}
		}
	}
	}

	public void sendMessage(String paramString) {
	for (int j = 0; j < World.getStoners().length; j++) {
		if (World.getStoners()[j] != null) {
			Stoner c = World.getStoners()[j];
			if ((c != null) && (this.activeMembers.contains(c.getUsername())))
				c.getClient().queueOutgoingPacket(new SendMessage(paramString));
		}
	}
	}

	public void setRank(String paramString, int paramInt) {
	if (this.rankedMembers.contains(paramString)) {
		this.ranks.set(this.rankedMembers.indexOf(paramString), Integer.valueOf(paramInt));
	} else {
		this.rankedMembers.add(paramString);
		this.ranks.add(Integer.valueOf(paramInt));
	}
	save();
	}

	public void setRankCanBan(int paramInt) {
	this.whoCanBan = paramInt;
	}

	public void setRankCanJoin(int paramInt) {
	this.whoCanJoin = paramInt;
	}

	public void setRankCanKick(int paramInt) {
	this.whoCanKick = paramInt;
	}

	public void setRankCanTalk(int paramInt) {
	this.whoCanTalk = paramInt;
	}

	public void unbanMember(String paramString) {
	paramString = Utility.formatStonerName(paramString);
	if (this.bannedMembers.contains(paramString)) {
		this.bannedMembers.remove(paramString);
		save();
	}
	}

	public void updateInterface(Stoner paramStoner) {
	paramStoner.getClient().queueOutgoingPacket(new SendString("Cult: " + getTitle(), 18139));
	paramStoner.getClient().queueOutgoingPacket(new SendString("High Priest: " + (Utility.formatStonerName(getFounder())), 18140));
	Collections.sort(this.activeMembers);
	for (int i = 0; i < 100; i++)
		if (i < this.activeMembers.size()) {
			paramStoner.getClient().queueOutgoingPacket(new SendString("<cult=" + getRank(this.activeMembers.get(i)) + ">" + this.activeMembers.get(i), 18144 + i));
		} else {
			paramStoner.getClient().queueOutgoingPacket(new SendString(" ", 18144 + i));
		}
	paramStoner.getClient().queueOutgoingPacket(new SendString("(" + this.activeMembers.size() + "/100)", 18252));
	}

	public void updateMembers() {
	for (int j = 0; j < World.getStoners().length; j++) {
		if (World.getStoners()[j] != null) {
			Stoner stoner = World.getStoners()[j];
			if ((stoner != null) && (this.activeMembers != null) && (this.activeMembers.contains(stoner.getUsername())))
				updateInterface(stoner);
		}
	}
	}

	public static class Rank {

		public static final int ANYONE = -1;
		public static final int FRIEND = 0;
		public static final int RECRUIT = 1;
		public static final int CORPORAL = 2;
		public static final int SERGEANT = 3;
		public static final int LIEUTENANT = 4;
		public static final int CAPTAIN = 5;
		public static final int GENERAL = 6;
		public static final int OWNER = 7;
	}
}