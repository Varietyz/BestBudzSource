package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.Server;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendColor;

public class QuestTab extends InterfaceHandler {

  private final String[] text = {
    Utility.getCurrentServerTime(),
    Server.bestbudzDate(),
    "",
    "Employees Online: " + World.getStaff(),
    "Stoners Online: " + Utility.format(World.getActiveStoners()),
    "",
	  Utility.capitalizeFirstLetter(stoner.getUsername()),
    "Rank: " + stoner.deterquarryRank(stoner),
    "",
    "Check My Points",
    "Check Tracked PvM Kills",
    "",
    "-FREQUENTLY USED-",
    "",
    "Change Type of Mage",
    "View Drop Table",
    "Advance Professions",
    "Sell Junk",
    "",
    "-SHOPS-",
    "",
    "General Store",
    "Pack store",
    "Professioning shop",
    "Cultivation shop",
    "THC-hempistry shop",
    "Close-combat shop",
    "Sagittarius shop",
    "Mages shop",
    "Pure shop",
    "Fashion shop",
    "Profession capes",
    "Advancement capes",
    "",
    "-SHOPS2-",
    "",
    "Chill points",
    "Weed protect points",
    "Graceful marks",
    "Achievement points",
    "Advance points",
    "Mercenary points",
    "Bounty points",
    "",
    "-CRAFTER-",
    "",
    "Create Dragonfire Shield",
    "Create Jewelery",
    "Tan Hides",
    "Decant Potions",
    "",
    "-MANAGEMENT-",
    "",
    "Deluxe title",
    "Reset Combat Stats",
    "Plastic Surgery",
    "Recharge Necromance",
    "Skull Self",
    "",
    "-TASKS-",
    "",
    "Mercenary (Monsters)",
    "Mercenary (Bosses)",
    "",
    "-EXTRAS-",
    "",
    "Traveling Agency",
    "Play Misery Box",
    "Collect Achievement cape",
    "",
    "-ALTAR LOCATIONS-",
    "",
    "AIR Altar",
    "MIND Altar",
    "WATER Altar",
    "EARTH Altar",
    "FIRE Altar",
    "BODY Altar",
    "COSMIC Altar",
    "CHAOS Altar",
    "NATURE Altar",
    "LAW Altar",
    "DEATH Altar",
    "",
  };

  public QuestTab(Stoner stoner) {
    super(stoner);
    color(16, 0xC71C1C);
    color(17, 0xC71C1C);
  }

  public void color(int id, int color) {
    stoner.send(new SendColor(startingLine() + id, color));
  }

  @Override
  protected String[] text() {
    return text;
  }

  @Override
  protected int startingLine() {
    return 29501;
  }
}
