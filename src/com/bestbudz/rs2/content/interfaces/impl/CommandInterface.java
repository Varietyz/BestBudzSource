package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class CommandInterface extends InterfaceHandler {

	public CommandInterface(Stoner stoner) {
	super(stoner);
	}

	private final String[] text = { "::stoners - shows amount of active stoners", "::changepassword - changes password", "::yell - does a global yell", "::yelltitle - changes yell title", "::empty - deletes box", "::home - teleports home", "::teleport - opens the teleporting menu", "::devilspact - ITS A QUICK WAY TO MAX, BUT BEWARE!", "::smokeweed - Lets u smoke a big pipe.", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",

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
