package com.mayhem.rs2.content.interfaces.impl;

import com.mayhem.core.util.Utility;
import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.entity.player.Player;

public class PointsInterface extends InterfaceHandler {
	
	public PointsInterface(Player player) {
		super(player);
	}

	private final String[] text = {
			"@dre@-----Skill Points-----",
			"@dre@Prayer Points: @blu@" +Utility.format(player.getprayerPoints()),
			"@dre@Runecrafting Points: @blu@" +Utility.format(player.getrunecraftingPoints()),
			//"@dre@Hunting Points: @blu@" +Utility.format(player.gethunterPoints()),
			"@dre@Herblore Points: @blu@" +Utility.format(player.getherblorePoints()),
			"@dre@Thieving Points: @blu@" +Utility.format(player.getthievePoints()),
			"@dre@Crafting Points: @blu@" +Utility.format(player.getcraftingPoints()),
			"@dre@Fletching Points: @blu@" +Utility.format(player.getfletchingPoints()),
			"@dre@Mining Points: @blu@" +Utility.format(player.getminingPoints()),
			"@dre@Smithing Points: @blu@" +Utility.format(player.getSmithingPoints()),
			"@dre@Fishing Points: @blu@" +Utility.format(player.getfishingPoints()),
			"@dre@Cooking Points: @blu@" +Utility.format(player.getcookingPoints()),
			"@dre@Firemaking Points: @blu@" +Utility.format(player.getfiremakingPoints()),
			"@dre@Woodcutting Points: @blu@" +Utility.format(player.getwoodcuttingPoints()),
			"@dre@Farming Points: @blu@" +Utility.format(player.getfarmingPoints()),
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
		"",
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

