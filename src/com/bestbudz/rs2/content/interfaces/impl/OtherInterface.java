package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Handles the other teleport interface
 * 
 * @author Jaybane
 *
 */
public class OtherInterface extends InterfaceHandler {

	public OtherInterface(Stoner stoner) {
	super(stoner);
	}

	private final String[] text = { "Membership Zone", "Staff Zone", "Relaxation Zone",

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
