package com.mayhem.rs2.content.clanchat;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import com.mayhem.Server;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendString;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Clan {

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

	public Clan(Player paramPlayer) {
		setTitle(paramPlayer.getUsername() + " ");
		setFounder(paramPlayer.getUsername().toLowerCase());
	}

	public Clan(String paramString1, String paramString2) {
		setTitle(paramString1);
		setFounder(paramString2);
	}

	public void addMember(Player paramPlayer) {
		if (isBanned(paramPlayer.getUsername())) {
			paramPlayer.getClient().queueOutgoingPacket(new SendMessage("</col>You are currently banned from this clan chat.<col=b30000><shad=0>"));
			return;
		}
		if ((this.whoCanJoin > -1) && (!isFounder(paramPlayer.getUsername())) && (getRank(paramPlayer.getUsername()) < this.whoCanJoin)) {
			paramPlayer.getClient().queueOutgoingPacket(new SendMessage("Only " + getRankTitle(this.whoCanJoin) + "s+ may join this chat."));
			return;
		}

		paramPlayer.clan = this;
		paramPlayer.lastClanChat = getFounder();
		this.activeMembers.add(paramPlayer.getUsername());
		paramPlayer.getClient().queueOutgoingPacket(new SendString("</col>Leave chat <col=ff6564><shad=0>", 18135));
		paramPlayer.getClient().queueOutgoingPacket(new SendString("</col>Talking in: <col=00b3b3><shad=0>" + getTitle(), 18139));
		paramPlayer.getClient().queueOutgoingPacket(new SendString("</col>Owner: <col=b264ff><shad=0>" + Utility.formatPlayerName(getFounder()), 18140));
		paramPlayer.getClient().queueOutgoingPacket(new SendMessage("</col>Attempting to join clan channel...<col=beff7e><shad=0>"));
		paramPlayer.getClient().queueOutgoingPacket(new SendMessage("Now talking in clan chat <col=00b3b3><shad=0>" + getTitle() + "</shad></col>."));
		paramPlayer.getClient().queueOutgoingPacket(new SendMessage("To talk, start each line of chat with the / symbol."));
		updateMembers();
	}

	public void banMember(String paramString) {
		paramString = Utility.formatPlayerName(paramString);
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
		Player localPlayer = World.getPlayerByName(paramString);
		if ((localPlayer != null)) {
			localPlayer.getClient().queueOutgoingPacket(new SendMessage("</col>You have been banned from the channel.<col=b30000><shad=0>"));
		}
		sendMessage("Attempting to kick/ban the user '" + Utility.formatPlayerName(paramString) + "' from this clan chat channel.");
	}

	public boolean canBan(String paramString) {
		if (isFounder(paramString)) {
			return true;
		}
		if (getRank(paramString) >= this.whoCanBan) {
			return true;
		}
		return false;
	}

	public boolean canKick(String paramString) {
		if (isFounder(paramString)) {
			return true;
		}
		if (getRank(paramString) >= this.whoCanKick) {
			return true;
		}
		return false;
	}

	public void delete() {
		for (String str : this.activeMembers) {
			removeMember(str);
			Player localPlayer = World.getPlayerByName(str);
			localPlayer.getClient().queueOutgoingPacket(new SendMessage("</col>The clan you were in has been deleted.<col=ffb364><shad=0>")); 
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

	public int getRank(String paramString) {
		paramString = Utility.formatPlayerName(paramString);
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
			return "Anyone";
		case 0:
			return "Friend";
		case 1:
			return "Recruit";
		case 2:
			return "Corporal";
		case 3:
			return "Sergeant";
		case 4:
			return "Lieutenant";
		case 5:
			return "Captain";
		case 6:
			return "General";
		case 7:
			return "Only Me";
		}
		return "";
	}

	public String getTitle() {
		return this.title;
	}

	public boolean isAdmin(String paramString) {
		if (paramString.equalsIgnoreCase("</col>Help<col=b364ff><shad=0>")) {
			return true;
		}
			
		return false;
	}

	public boolean isBanned(String paramString) {
		paramString = Utility.formatPlayerName(paramString);
		if (this.bannedMembers.contains(paramString)) {
			return true;
		}
		return false;
	}

	public boolean isFounder(String paramString) {
		if (getFounder().equalsIgnoreCase(paramString)) {
			return true;
		}
		return false;
	}

	public boolean isRanked(String paramString) {
		paramString = Utility.formatPlayerName(paramString);
		if (this.rankedMembers.contains(paramString)) {
			return true;
		}
		return false;
	}

	public void kickMember(String paramString) {
		if (!this.activeMembers.contains(paramString)) {
			return;
		}
		if (paramString.equalsIgnoreCase(getFounder())) {
			return;
		}
		removeMember(paramString);
		Player localPlayer = World.getPlayerByName(paramString);
		if (localPlayer != null) {
			localPlayer.getClient().queueOutgoingPacket(new SendMessage("</col>You have been kicked from the channel.<col=ff6664><shad=0>"));
		}
		sendMessage("Attempting to kick/ban the user '" + Utility.formatPlayerName(paramString) + "' from this clan chat channel.");
	}

	public void removeMember(Player paramPlayer) {
		for (int i = 0; i < this.activeMembers.size(); i++) {
			if (this.activeMembers.get(i).equalsIgnoreCase(paramPlayer.getUsername())) {
				paramPlayer.clan = null;
				resetInterface(paramPlayer);
				this.activeMembers.remove(i);
			}
		}
		updateMembers();
	}

	public void removeMember(String paramString) {
		for (int i = 0; i < this.activeMembers.size(); i++) {
			if (this.activeMembers.get(i).equalsIgnoreCase(paramString)) {
				Player localPlayer = World.getPlayerByName(paramString);
				if (localPlayer != null) {
					localPlayer.clan = null;
					resetInterface(localPlayer);
					this.activeMembers.remove(i);
				}
			}
		}
		updateMembers();
	}

	public void resetInterface(Player paramPlayer) {
		paramPlayer.getClient().queueOutgoingPacket(new SendString("</col>Join Chat<col=beff7e><shad=0>", 18135));
		paramPlayer.getClient().queueOutgoingPacket(new SendString("</col>Talking in: None<col=00b3b3><shad=0>", 18139));
		paramPlayer.getClient().queueOutgoingPacket(new SendString("</col>Owner: None<col=b264ff><shad=0>", 18140));
		paramPlayer.getClient().queueOutgoingPacket(new SendString("", 18252));
		for (int i = 0; i < 100; i++) {
			paramPlayer.getClient().queueOutgoingPacket(new SendString("", 18144 + i));
		}
	}

	public void save() {
		Server.clanManager.save(this);
		updateMembers();
	}

	public void sendChat(Player paramPlayer, String paramString) {
		if (getRank(paramPlayer.getUsername()) < this.whoCanTalk) {
			paramPlayer.getClient().queueOutgoingPacket(new SendMessage("</col>Only<col=56e4d5><shad=0> " + getRankTitle(this.whoCanTalk) + "s+ may talk in this chat."));
			return;
		}
		if (paramPlayer.isMuted()) {
			if (paramPlayer.getMuteLength() == -1) {
				paramPlayer.send(new SendMessage("</col>You are permanently muted on this account.<col=b30000><shad=0>"));
				return;
			} else {
				long muteHours = TimeUnit.MILLISECONDS.toMinutes(paramPlayer.getMuteLength() - System.currentTimeMillis());
				String timeUnit = "hour" + (muteHours > 1 ? "s" : "");
				if (muteHours < 60) {
					if (muteHours <= 0) {
						paramPlayer.send(new SendMessage("</col>Your mute has been lifted!<col=64ffb3><shad=0>"));
						paramPlayer.setMuted(false);
					}
					timeUnit = "minute" + (muteHours > 1 ? "s" : "");
				} else {
					muteHours = TimeUnit.MINUTES.toHours(muteHours);
				}
				if (paramPlayer.isMuted()) {
					paramPlayer.send(new SendMessage("</col>You are muted, you will be unmuted in <col=ff64b0><shad=0>" + muteHours + " " + timeUnit + "."));
					return;
				}
			}
		}
		for (int j = 0; j < World.getPlayers().length; j++) {
			if (World.getPlayers()[j] != null) {
				Player c = World.getPlayers()[j];
				if ((c != null) && (this.activeMembers.contains(c.getUsername()) && paramPlayer.getRights() == 2)) {
					c.getClient().queueOutgoingPacket(new SendMessage("</col>[@blu@" + getTitle() + "</col>] " + "<clan=" + getRank(paramPlayer.getUsername()) + ">" + paramPlayer.getUsername() + ":@dre@ " + Utility.capitalizeFirstLetter(paramString) + ""));
				} else if ((c != null) && (this.activeMembers.contains(c.getUsername()))) {
					c.getClient().queueOutgoingPacket(new SendMessage("</col>[@blu@" + getTitle() + "</col>] <clan=" + getRank(paramPlayer.getUsername()) + ">" + paramPlayer.getUsername() + ":@dre@ " + Utility.capitalizeFirstLetter(paramString) + ""));
				}
			}
		}
	}

	public void sendMessage(String paramString) {
		for (int j = 0; j < World.getPlayers().length; j++) {
			if (World.getPlayers()[j] != null) {
				Player c = World.getPlayers()[j];
				if ((c != null) && (this.activeMembers.contains(c.getUsername())))
					c.getClient().queueOutgoingPacket(new SendMessage(paramString));
			}
		}
	}

	public void setFounder(String paramString) {
		this.founder = paramString;
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

	public void setTitle(String paramString) {
		this.title = paramString;
	}

	public void unbanMember(String paramString) {
		paramString = Utility.formatPlayerName(paramString);
		if (this.bannedMembers.contains(paramString)) {
			this.bannedMembers.remove(paramString);
			save();
		}
	}

	public void updateInterface(Player paramPlayer) {
		paramPlayer.getClient().queueOutgoingPacket(new SendString("</col>Talking in: <col=00b3b3><shad=0>" + getTitle(), 18139));
		paramPlayer.getClient().queueOutgoingPacket(new SendString("</col>Owner: <col=b264ff><shad=0>" + (Utility.formatPlayerName(getFounder())), 18140));
		Collections.sort(this.activeMembers);
		for (int i = 0; i < 100; i++)
			if (i < this.activeMembers.size()) {
				paramPlayer.getClient().queueOutgoingPacket(new SendString("<clan=" + getRank(this.activeMembers.get(i)) + ">" + this.activeMembers.get(i), 18144 + i));
			} else {
				paramPlayer.getClient().queueOutgoingPacket(new SendString(" ", 18144 + i));
			}
		paramPlayer.getClient().queueOutgoingPacket(new SendString("("+this.activeMembers.size()+"/100)", 18252));
	}

	public void updateMembers() {
		for (int j = 0; j < World.getPlayers().length; j++) {
			if (World.getPlayers()[j] != null) {
				Player player = World.getPlayers()[j];
				if ((player != null) && (this.activeMembers != null) && (this.activeMembers.contains(player.getUsername())))
					updateInterface(player);
			}
		}
	}
}