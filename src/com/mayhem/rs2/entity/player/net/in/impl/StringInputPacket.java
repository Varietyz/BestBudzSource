package com.mayhem.rs2.entity.player.net.in.impl;

import com.mayhem.Server;
import com.mayhem.Constants;
import com.mayhem.core.network.StreamBuffer;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.DropTable;
import com.mayhem.rs2.content.PlayerTitle;
import com.mayhem.rs2.content.clanchat.Clan;
import com.mayhem.rs2.content.dialogue.DialogueManager;
import com.mayhem.rs2.content.gambling.Gambling;
import com.mayhem.rs2.content.houses.HouseManager;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.in.IncomingPacket;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import com.mayhem.rs2.entity.player.net.out.impl.SendRemoveInterfaces;

public class StringInputPacket extends IncomingPacket {

	@Override
	public int getMaxDuplicates() {
		return 1;
	}

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		String input = Utility.longToPlayerName2(in.readLong());
		input = input.replaceAll("_", " ");
		
		if (player.getAttributes().get("house-kick-name") != null) {
			player.getAttributes().remove("house-kick-name");
			HouseManager.kickPlayer(player, input);
			return;
		}
		if (player.getAttributes().get("house-ban-name") != null) {
			player.getAttributes().remove("house-ban-name");
			HouseManager.punishPlayer(player, input, false);
			return;
		}
		if (player.getAttributes().get("house-unban-name") != null) {
			player.getAttributes().remove("house-unban-name");
			HouseManager.punishPlayer(player, input, true);
			return;
		}
		if (player.getAttributes().get("house-friend-name") != null) {
			player.getAttributes().remove("house-friend-name");
			HouseManager.enterHouse(player, input);
			return;
		}
		
		if (player.getInterfaceManager().getMain() == 41750) {
			player.reportName = Utility.capitalize(input);
			return;
		}
		
		if (player.getInterfaceManager().getMain() == 59800) {
			DropTable.searchItem(player, input);
			return;
		}
		
		if (player.getEnterXInterfaceId() == 56000) {
			Gambling.play(player, Integer.parseInt(input));
			return;
		}
		
		if (player.getEnterXInterfaceId() == 56002) {
			for (int i = 0; i < Constants.BAD_STRINGS.length; i++) {
				if (input.equalsIgnoreCase(Constants.BAD_STRINGS[i])) {
					DialogueManager.sendStatement(player, "Grow up! That title can not be used.");
					return;
				}
			}
			if (input.length() >= 15) {
				DialogueManager.sendStatement(player, "Titles can not exceed 15 characters!");
				return;
			}
			player.setPlayerTitle(PlayerTitle.create(input, player.getPlayerTitle().getColor(), false));
			player.setAppearanceUpdateRequired(true);
			player.send(new SendRemoveInterfaces());
			return;
		}

		if (player.getEnterXInterfaceId() == 55776) {
			player.setCredits(player.getCredits() - 10);
			player.setShopMotto(Utility.capitalize(input));
			DialogueManager.sendInformationBox(player, "Player Owned Shops Exchange", "You have successfully changed your shop motto.", "Motto:", "@red@" + Utility.capitalize(input), "");
			return;
		}

		if (player.getEnterXInterfaceId() == 100) {
			player.getSlayer().setSocialSlayerPartner(input);
			return;
		}

		if (player.getEnterXInterfaceId() == 55777) {
			player.getShopping().open(World.getPlayerByName(input));
			return;
		}

		if (player.getEnterXInterfaceId() == 55778) {
			player.getPlayerShop().setSearch(input);
			return;
		}

		if (player.getEnterXInterfaceId() == 6969) {
			if ((input != null) && (input.length() > 0) && (player.clan == null)) {
				Clan localClan = Server.clanManager.getClan(input);
				if (localClan != null)
					localClan.addMember(player);
				else if (input.equalsIgnoreCase(player.getUsername()))
					Server.clanManager.create(player);
				else {
					player.getClient().queueOutgoingPacket(new SendMessage(Utility.formatPlayerName(input) + " has not created a clan yet."));
				}
			}
		} else {
			return;
		}
	}
}
