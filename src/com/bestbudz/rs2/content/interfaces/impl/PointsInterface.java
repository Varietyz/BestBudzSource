package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class PointsInterface extends InterfaceHandler {

  private final String[] text = {
    "CannaCredits:  @yel@" + Utility.format(stoner.getCredits()),
    "Achievement:  @yel@" + Utility.format(stoner.getAchievementsPoints()),
    "Chill:  @yel@" + Utility.format(stoner.getChillPoints()),
    "Bounty:  @yel@" + Utility.format(stoner.getBountyPoints()),
    "Mercenary:  @yel@" + Utility.format(stoner.getMercenaryPoints()),
    "Advance:  @yel@" + Utility.format(stoner.getAdvancePoints()),
    "Pest Control:  @yel@" + Utility.format(stoner.getPestPoints()),
    "Mage Arena:  @yel@" + Utility.format(stoner.getArenaPoints()),
    "Weapon Game:  @yel@" + Utility.format(stoner.getWeaponPoints()),
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

  public PointsInterface(Stoner stoner) {
    super(stoner);
  }

  @Override
  protected String[] text() {
    return text;
  }

  @Override
  protected int startingLine() {
    return 8145;
  }
}
