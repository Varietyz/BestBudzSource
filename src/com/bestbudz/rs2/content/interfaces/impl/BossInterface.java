package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Handles the boss teleport interface
 * 
 * @author Jaybane
 *
 */
public class BossInterface extends InterfaceHandler {

	public BossInterface(Stoner stoner) {
	super(stoner);
	}

	private final String[] text = { "King Black Dragon", "Sea Troll Queen", "Barrelchest", "Corporeal Beast", "Daggonoths Kings", "Godwars", "Zulrah", "Kraken", "Giant Mole", "Chaos Element", "Callisto", "Scorpia", "Vet'ion", "Venenatis (N/A)", "Chaos Fanatic", "Crazy archaeologist", "Kalphite Queen (N/A)",

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
