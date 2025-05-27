package com.mayhem.rs2.content.interfaces.impl;

import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.entity.player.Player;

/**
 * Handles the other teleport interface
 * @author Daniel
 *
 */
public class OtherInterface extends InterfaceHandler {
	
	public OtherInterface(Player player) {
		super(player);
	}

	private final String[] text = {
			"Members Zone",
			"Shopping Area",
			"Varrock",
			"Falador",
			"Camelot",
			"Zeah",
			"Lumbridge",
			"Draynor",
			"Remmington",
			"Yanille",
			"Taverly",
			"Ardounge",
			"Karamja",

			
	};

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int startingLine() {
		return 61551;
	}

}

