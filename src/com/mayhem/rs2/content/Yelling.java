package com.mayhem.rs2.content;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

public class Yelling {

	public static final String YELL_COOLDOWN_KEY = "yellcooldown";

	public static String send;

	public static void yell(Player player, String message) {
		
		message = Utility.capitalizeFirstLetter(message);

		int rights = player.getRights();
		
		if (rights == 1) {
			send = "[@blu@Moderator</col>] <img=0>@blu@" + player.getUsername() + "</col>: " + message;
		} else if (rights == 2) {
			send = "[<col=D17417>@Administrator</col>]  <img=1><col=D17417>" + player.getUsername() + "</col>: " + message;
		} else if (rights == 3) {
			send = "[@red@Community Manager</col>] <img=2>@red@" + player.getUsername() + "</col>: " + message;
		} else if (rights == 4) {
			send = "[@dre@Developer</col>] <img=3>@dre@" + player.getUsername() + "</col>: " + message;
		} else if (player.getMoneySpent() >= 5 && player.getMoneySpent() <= 24) {
			send = "[<col=D11717>Donator</col>] <img=4><col=D11717>" + player.getUsername() + "</col>: " + message;
		} else if (player.getMoneySpent() >= 25 && player.getMoneySpent() <= 74) {
			send = "[<col=0956AD>Super Donator</col>] <img=5><col=0956AD>" + player.getUsername() + "</col>: " + message;
		} else if (player.getMoneySpent() >= 75 && player.getMoneySpent() <= 199) {
			send = "[<col=4D8528>Uber Donator</col>] <img=6><col=4D8528>" + player.getUsername() + "</col>: " + message;
		} else if (player.getMoneySpent() >= 200) {
			send = "[<col=971FF2>Elite Donator</col>] <img=7><col=971FF2>" + player.getUsername() + "</col>: " + message;
		} else if (rights == 13) {
			send = "[@blu@Support</col>]@blu@ <img=11>" + player.getUsername() + "</col>: " + message;
		} else if (rights == 15) {
			send = "[@red@Youtuber</col>]@red@ <img=15>" + player.getUsername() + "</col>: " + message;
		}
		
		
		else {

			if (player.getRights() == 0) {
				if (player.getAttributes().get("yellcooldown") == null) {
					player.getAttributes().set("yellcooldown", Long.valueOf(System.currentTimeMillis()));
				} else if (System.currentTimeMillis() - ((Long) player.getAttributes().get("yellcooldown")).longValue() < 3000L) {
					player.getClient().queueOutgoingPacket(new SendMessage("You must wait a few seconds before yelling again."));
					return;
				}
				
				player.getAttributes().set("yellcooldown", Long.valueOf(System.currentTimeMillis()));
			}
			return;
		}

		if (player.isMuted()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You are muted and cannot yell."));
			return;
		}

		if (player.isYellMuted()) {
			player.getClient().queueOutgoingPacket(new SendMessage("You are muted are not allowed to yell."));
			return;
		}

		if (message.contains("<")) {
			player.getClient().queueOutgoingPacket(new SendMessage("You cannot use text arguments when yelling."));
			return;
		}


		for (Player i : World.getPlayers())
			if (i != null && send != null)
				i.getClient().queueOutgoingPacket(new SendMessage(send));
	}
}
