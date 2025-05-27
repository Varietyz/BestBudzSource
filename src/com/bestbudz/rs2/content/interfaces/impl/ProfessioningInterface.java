package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Handles the professioning teleport interface
 * 
 * @author Jaybane
 *
 */
public class ProfessioningInterface extends InterfaceHandler {

	public ProfessioningInterface(Stoner stoner) {
	super(stoner);
	}

	private final String[] text = { "Wilderness Resource", "Accomplisher", "Handiness", "Weedsmoking", "Quarrying", "Forging", "Fisher", "Lumbering", "Cultivation", "", "", "", "",

	};

	@Override
	protected String[] text() {
	return text;
	}

	@Override
	protected int startingLine() {
	return 62051;
	}

}
