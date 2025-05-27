package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.BestbudzConstants;
import com.bestbudz.Server;
import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.DropTable;
import com.bestbudz.rs2.content.StonerTitle;
import com.bestbudz.rs2.content.clanchat.Clan;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.content.gambling.Gambling;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendRemoveInterfaces;

public class StringInputPacket extends IncomingPacket {

	@Override
	public int getMaxDuplicates() {
	return 1;
	}

	@Override
	public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
	String input = Utility.longToStonerName2(in.readLong());
	input = input.replaceAll("_", " ");

	if (stoner.getInterfaceManager().getMain() == 41750) {
		stoner.reportName = Utility.capitalize(input);
		return;
	}

	if (stoner.getInterfaceManager().getMain() == 59800) {
		DropTable.searchItem(stoner, input);
		return;
	}

	if (stoner.getEnterXInterfaceId() == 56000) {
		Gambling.play(stoner, Integer.parseInt(input));
		return;
	}

	if (stoner.getEnterXInterfaceId() == 56002) {
		for (int i = 0; i < BestbudzConstants.BAD_STRINGS.length; i++) {
			if (input.equalsIgnoreCase(BestbudzConstants.BAD_STRINGS[i])) {
				DialogueManager.sendStatement(stoner, "Grow up! That title can not be used.");
				return;
			}
		}
		if (input.length() >= 15) {
			DialogueManager.sendStatement(stoner, "Titles can not exceed 15 characters!");
			return;
		}
		stoner.setStonerTitle(StonerTitle.create(input, stoner.getStonerTitle().getColor(), false));
		stoner.setAppearanceUpdateRequired(true);
		stoner.send(new SendRemoveInterfaces());
		return;
	}

	if (stoner.getEnterXInterfaceId() == 55776) {
		stoner.setCredits(stoner.getCredits() - 10);
		stoner.setShopMotto(Utility.capitalize(input));
		DialogueManager.sendInformationBox(stoner, "Stoner Owned Shops Exchange", "You have successfully changed your shop motto.", "Motto:", "@red@" + Utility.capitalize(input), "");
		return;
	}

	if (stoner.getEnterXInterfaceId() == 100) {
		stoner.getMercenary().setSocialMercenaryPartner(input);
		return;
	}

	if (stoner.getEnterXInterfaceId() == 55777) {
		stoner.getShopping().open(World.getStonerByName(input));
		return;
	}

	if (stoner.getEnterXInterfaceId() == 55778) {
		stoner.getStonerShop().setSearch(input);
		return;
	}

	if (stoner.getEnterXInterfaceId() == 6969) {
		if ((input != null) && (input.length() > 0) && (stoner.clan == null)) {
			Clan localClan = Server.clanManager.getClan(input);
			if (localClan != null)
				localClan.addMember(stoner);
			else if (input.equalsIgnoreCase(stoner.getUsername()))
				Server.clanManager.create(stoner);
			else {
				stoner.getClient().queueOutgoingPacket(new SendMessage(Utility.formatStonerName(input) + " has not created a clan yet."));
			}
		}
	} else {
		return;
	}
	}
}
