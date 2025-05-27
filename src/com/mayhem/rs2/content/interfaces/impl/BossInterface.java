package com.mayhem.rs2.content.interfaces.impl;

import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.entity.player.Player;

/**
 * Handles the boss teleport interface
 * @author Daniel
 *
 */
public class BossInterface extends InterfaceHandler {
	
	public BossInterface(Player player) {
		super(player);
	}

	private final String[] text = {
			"King Black Dragon",
			"Sea Troll Queen",
			"Barrelchest",
			"Corporeal Beast",
			"Daggonoths Kings",
			"Godwars",
			"Zulrah",
			"Kraken",
			"Giant Mole",
			"Chaos Element",
			"Callisto",
			"Scorpia",
			"Vet'ion",
			"Venenatis",
			"Chaos Fanatic",
			"Crazy archaeologist",
			"Cerberus",
			"Thermonuclear Smoke Devil",
			"Lizard Shaman",
			"Demonic Gorillas",
			"Gelatinnoth Mother",
			"RFD Bosses",
			"Ancient Warriors",
			"Kalphite Queen",
			"The Everlasting (Magic Only)"
			
			
	};

	@Override
	protected String[] text() {
		return text;
	}

	@Override
	protected int startingLine() {
		return 64051;
	}

}

