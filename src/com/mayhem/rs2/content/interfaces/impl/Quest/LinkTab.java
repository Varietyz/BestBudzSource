package com.mayhem.rs2.content.interfaces.impl.Quest;

import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.entity.player.Player;

/**
 * Handles the quest tab text
 * 
 * @author Daniel
 *
 */
public class LinkTab extends InterfaceHandler {

	private final String[] text = {
		"<col=ff9040>Vote", 
		"<col=ff9040>Store", 
		"<col=ff9040>Forums", 
		"<col=ff9040>Highscores", 
		"<col=ff9040>Updates",
		"<col=ff9040>Rune-Server Thread",
		"",
		"",
		"",
		"",
		"",
		"",

	};

	public LinkTab(Player player) {
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
