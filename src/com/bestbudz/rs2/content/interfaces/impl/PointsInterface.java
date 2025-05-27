package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class PointsInterface extends InterfaceHandler {

	public PointsInterface(Stoner stoner) {
	super(stoner);
	}

	private final String[] text = { "@dre@CannaCredits:  @yel@" + Utility.format(stoner.getCredits()), "@dre@Achievement:  @yel@" + Utility.format(stoner.getAchievementsPoints()), "@dre@Chill:  @yel@" + Utility.format(stoner.getChillPoints()), "@dre@Bounty:  @yel@" + Utility.format(stoner.getBountyPoints()), "@dre@Mercenary:  @yel@" + Utility.format(stoner.getMercenaryPoints()), "@dre@Advance:  @yel@" + Utility.format(stoner.getAdvancePoints()), "@dre@Pest Control:  @yel@" + Utility.format(stoner.getPestPoints()), "@dre@Mage Arena:  @yel@" + Utility.format(stoner.getArenaPoints()), "@dre@Weapon Game:  @yel@" + Utility.format(stoner.getWeaponPoints()), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", };

	@Override
	protected String[] text() {
	return text;
	}

	@Override
	protected int startingLine() {
	return 8145;
	}

}
