package com.mayhem.rs2.content.interfaces.impl;

import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.entity.player.Player;

/**
 * Handles the training teleport interface
 * @author Daniel
 *
 */
public class TrainingInterface extends InterfaceHandler {
	
	public TrainingInterface(Player player) {
		super(player);
	}

	private final String[] text = {
			"Rock Crabs",
			"Sand Crabs",
			"Al-Kharid",
			"Cows",
			"Yaks",
			"Brimhaven Dung",
			"Taverly Dung",
			"Slayer Tower",
			"Lava Dragons",
			"Mithril Dragons",
			"Hill Giants",
			"Crystal Monsters",
			"Cave Horrors",
			"Nieves Slayer Dungeon",
			"Superior Slayer Dungeon",
			"Revenant Dungeon"
	};

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int startingLine() {
		return 61051;
	}

}

