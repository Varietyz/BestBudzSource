package com.bestbudz.rs2.content;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class Yelling {

	public static final String YELL_COOLDOWN_KEY = "yellcooldown";

	public static String send;

	public static void yell(Stoner stoner, String message) {

	message = Utility.capitalizeFirstLetter(message);

	int rights = stoner.getRights();

	if (rights == 0) {
		send = "[@whi@" + Utility.capitalize(stoner.getYellTitle()) + "</col>] <img=11>@whi@" + stoner.getUsername() + "</col>: " + message;
	} else if (rights == 1) {
		send = "[@gry@M.Bestbud</col>] <img=0>@gry@" + stoner.getUsername() + "</col>: " + message;
	} else if (rights == 2) {
		send = "[<col=D17417>A.BestBud</col>]  <img=1><col=D17417>" + stoner.getUsername() + "</col>: " + message;
	} else if (rights == 3) {
		send = "[@gre@BestBud</col>] <img=2>@gre@" + stoner.getUsername() + "</col>: " + message;
	} else if (rights == 4) {
		send = "[@dre@Developer</col>] <img=3>@dre@" + stoner.getUsername() + "</col>: " + message;
	} else if (rights == 5) {
		send = "[<col=B20000>" + Utility.capitalize(stoner.getYellTitle()) + "</col>] <img=4><col=D11717>" + stoner.getUsername() + "</col>: " + message;
	} else if (rights == 6) {
		send = "[<col=223ca9>" + Utility.capitalize(stoner.getYellTitle()) + "</col>] <img=5><col=0956AD>" + stoner.getUsername() + "</col>: " + message;
	} else if (rights == 7) {
		send = "[<col=2EB8E6>" + Utility.capitalize(stoner.getYellTitle()) + "</col>] <img=6><col=4D8528>" + stoner.getUsername() + "</col>: " + message;
	} else if (rights == 8) {
		send = "[<col=971FF2>" + Utility.capitalize(stoner.getYellTitle()) + "</col>] <img=7><col=971FF2>" + stoner.getUsername() + "</col>: " + message;

	} else {

		// if (stoner.getRights() == 0) {
		// if (stoner.getAttributes().get("yellcooldown") == null) {
		// stoner.getAttributes().set("yellcooldown",
		// Long.valueOf(System.currentTimeMillis()));
		// } else if (System.currentTimeMillis() - ((Long)
		// stoner.getAttributes().get("yellcooldown")).longValue() < 3000L) {
		// stoner.getClient().queueOutgoingPacket(new SendMessage("You must wait a few
		// seconds before yelling again."));
		// return;
		// }

		// stoner.getAttributes().set("yellcooldown",
		// Long.valueOf(System.currentTimeMillis()));
		// }
		return;
	}

	if (stoner.isMuted()) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You are muted and cannot yell."));
		return;
	}

	if (stoner.isYellMuted()) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You are muted are not allowed to yell."));
		return;
	}

	if (message.contains("<")) {
		stoner.getClient().queueOutgoingPacket(new SendMessage("You cannot use text arguments when yelling."));
		return;
	}

	for (Stoner i : World.getStoners())
		if (i != null && send != null)
			i.getClient().queueOutgoingPacket(new SendMessage(send));
	}
}
