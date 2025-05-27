package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Handles the pvp teleport interface
 * 
 * @author Jaybane
 *
 */
public class PvPInterface extends InterfaceHandler {

	public PvPInterface(Stoner stoner) {
	super(stoner);
	}

	private final String[] text = { "Edgeville", "Varrock", "East Dragons", "Castle", "Mage Bank", "", "", "", "", "", "", "",

	};

	@Override
	protected String[] text() {
	return text;
	}

	@Override
	protected int startingLine() {
	return 63051;
	}

}
