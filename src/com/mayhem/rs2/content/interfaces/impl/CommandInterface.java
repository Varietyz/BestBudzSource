package com.mayhem.rs2.content.interfaces.impl;

import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.entity.player.Player;

public class CommandInterface extends InterfaceHandler {

	public CommandInterface(Player player) {
		super(player);
	}

	private final String[] text = {
			"::players - shows amount of active players",
			"::vote - opens the voting link",
			"::reward - reward for votes", // needs to be fixed
			"::store - opens the Donate Store",
			"::answer - answers the TriviaBot",
			"::changepassword - changes password",
			"::empty - deletes inventory",
			"::home - teleports home",
			"::barrows - Barrows Teleport",
			"::duel - Duel Arena Teleport",
			"::bandits - Bandits Teleport",
			"::dzone - Donator Teleport",
			"::shops - teleports you to the shop area",	
			"::home - teleports you to home",
			"::oldhome - teleports you edgeville home",
			"::Vespula - Raid Boss for high level armor", 
			"::vrb - Raid Boss for high level armor"
			

	};

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int startingLine() {
		return 8145;
	}
	
}
