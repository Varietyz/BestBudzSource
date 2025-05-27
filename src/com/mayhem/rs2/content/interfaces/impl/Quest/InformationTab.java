package com.mayhem.rs2.content.interfaces.impl.Quest;

import com.mayhem.Server;
import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.player.Player;

/**
 * Handles the quest tab text
 * 
 * @author Daniel
 *
 */
public class InformationTab extends InterfaceHandler {

	private final String[] text = {
		"<col=ff9040>View Quest<col=75C934>",
		"<col=ff9040>Online Players: <col=75C934>" + World.getActivePlayers(),
		"<col=ff9040>Online Staff: <col=75C934>" + World.getStaff(),
		"", 
		"",
		"<col=ff9040>Time: <col=75C934>" + Utility.getCurrentServerTime(),
		"<col=ff9040>Date: <col=75C934>" + Server.MayhemDate(),
		"<col=ff9040>Uptime: <col=75C934>" + Server.MayhemTime(),
		"",
		"",
		"",
		"",
		"",
		"",
		"",
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

	public InformationTab(Player player) {
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
