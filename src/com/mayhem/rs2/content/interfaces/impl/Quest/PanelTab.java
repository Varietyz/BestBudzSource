package com.mayhem.rs2.content.interfaces.impl.Quest;

import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.entity.player.Player;

/**
 * Handles the quest tab text
 * 
 * @author Daniel
 *
 */
public class PanelTab extends InterfaceHandler {

	private final String[] text = {
		"<col=ff9040>My Profile",
		"<col=ff9040>Monster Drops",
		"<col=ff9040>Achievements",
		"<col=ff9040>Command List",
		"<col=ff9040>Kill Tracker", 
		"<col=ff9040>Donator Info", 
		"<col=ff9040>Chat Color Manager",
		"<col=ff9040>Title Manager", 
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

	public PanelTab(Player player) {
		super(player);

	}

	@Override
	protected int startingLine() {
		return 29451;
	}

	@Override
	protected String[] text() {
		return text;
	}

}
