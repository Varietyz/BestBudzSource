package com.mayhem.rs2.content.interfaces.impl;

import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.entity.player.Player;

public class SupportCommandsInterface extends InterfaceHandler {

	public SupportCommandsInterface(Player player) {
		super(player);
	}

	private final String[] text = {
			"::checkbank checks player bank",
			"::staffzone - teleports to staffzone",
			"::ecosearch - searches for amount of itemId in eco",
			"::teletome (name) - teleports a player to you",
			"::teleto (name) - teleports you to a player",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",			

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
