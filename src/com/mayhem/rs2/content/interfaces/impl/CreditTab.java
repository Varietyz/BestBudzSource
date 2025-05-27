package com.mayhem.rs2.content.interfaces.impl;

import com.mayhem.rs2.content.interfaces.InterfaceHandler;
import com.mayhem.rs2.content.membership.CreditPurchase;
import com.mayhem.rs2.entity.player.Player;

/**
 * Handles the credit tab interface
 * @author Daniel
 *
 */
public class CreditTab extends InterfaceHandler {
	
	public CreditTab(Player player) {
		super(player);
		if (player.isCreditUnlocked(CreditPurchase.FREE_TELEPORTS)) {
			text[4] = "<str>Unlock Free Teleports (@red@35</col>)";
		}
		if (player.isCreditUnlocked(CreditPurchase.DISEASE_IMUNITY)) {
			text[5] = "<str>Unlock Disease Immunity (@red@27</col>)";
		}
		if (player.isCreditUnlocked(CreditPurchase.DISEASE_IMUNITY)) {
			text[7] = "<str>Hide Wilderness Kills (@red@30</col>)";
		}
	}

	private final String[] text = {
			"Vote points shop",
			"General Store",
			"Skilling supply shops",
			"Food/Potion shops",
			"Weapons shop",
			"Armour shop",
			"Pure shop",
			"Magic shop",
			"Range shop",
			"Outfits shops",
			"Blood money shop",
			"Skill points shop",
			"PVM points shop",
			"Boss points shop",
			"Donator shops",
			"Titles shop",
			"Achievement shop",
			"Prestige shop",
			"Bounty shop",
			"Ironman shops",
			//"Shops",
			//"Restore Special Attack (@red@1</col>)",
			//"Restore Prayer (@red@1</col>)",
			//"Open Bank (@red@3</col>)",
			//"Unlock Free Teleports (@red@35</col>)",
			//"Unlock Disease Immunity (@red@27</col>)",
			//"Remove Teleblock (@red@2</col>)",
			//"Hide Wilderness Kills (@red@30</col>)",
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
		return 52531;
	}

}

