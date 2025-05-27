package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.Server;
import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.clanchat.Clan;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class ReceiveString extends IncomingPacket {

	@Override
	public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
	String text = in.readString();
	int index = text.indexOf(",");
	int id = Integer.parseInt(text.substring(0, index));
	String string = text.substring(index + 1);
	switch (id) {
	case 0:
		if (stoner.clan != null) {
			stoner.clan.removeMember(stoner);
			stoner.lastClanChat = "";
		} else {
			stoner.setEnterXInterfaceId(551);
		}
		break;
	case 1:
		if (string.length() == 0) {
			break;
		} else if (string.length() > 15) {
			string = string.substring(0, 15);
		}
		Clan clan = stoner.getClan();
		if (clan == null) {
			Server.clanManager.create(stoner);
			clan = stoner.getClan();
		}
		if (clan != null) {
			clan.setTitle(string);
			stoner.getClient().queueOutgoingPacket(new SendString(clan.getTitle(), 43706));
			clan.save();
		}
		break;
	case 2:
		if (string.length() == 0) {
			break;
		} else if (string.length() > 12) {
			string = string.substring(0, 12);
		}
		if (string.equalsIgnoreCase(stoner.getUsername())) {
			break;
		}
		clan = stoner.getClan();
		if (clan.isBanned(string)) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot promote a banned member."));
			break;
		}
		if (clan != null) {
			clan.setRank(Utility.formatStonerName(string), 1);
			stoner.setClanData();
			clan.save();
		}
		break;
	case 3:
		if (string.length() == 0) {
			break;
		} else if (string.length() > 12) {
			string = string.substring(0, 12);
		}
		if (string.equalsIgnoreCase(stoner.getUsername())) {
			break;
		}
		clan = stoner.getClan();
		if (clan.isRanked(string)) {
			stoner.getClient().queueOutgoingPacket(new SendMessage("You can't ban a ranked member of this clan chat channel."));
			break;
		}
		if (clan != null) {
			clan.banMember(Utility.formatStonerName(string));
			stoner.setClanData();
			clan.save();
		}
		break;
	default:
		System.out.println("Received string: identifier=" + id + ", string=" + string);
		break;
	}
	}

	@Override
	public int getMaxDuplicates() {
	return 1;
	}
}