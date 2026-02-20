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
	  "FOCUSED MAGE",
	  "AoE MAGE",
	  "View Drop Table",
	  "Advance Professions",
	  "",
	  "",
	  "-SHOPS-",
	  "",
	  "General Store",
	  "Pack store",
	  "Professioning shop",
	  "BankStanding shop",
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
	  "Recharge Resonance",
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
	  "-TELEPORTS-",
	  "HOME",
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
	  "ROCK CRABS",
	  "HILL GIANTS",
	  "AL KAHID",
	  "COWS",
	  "YAKS",
	  "BRIMHAVEN DUNG",
	  "TAVERLY DUNG",
	  "MERCENARY TOWER",
	  "LAVA DRAGONS",
	  "MITHRIL DRAGONS",
	  "WILD RESOURCE",
	  "PET_MASTER",
	  "HANDINESS",
	  "QUARRYING",
	  "FORGING",
	  "FISHER",
	  "LUMBERING",
	  "BANKSTANDING",
	  "EDGEVILLE",
	  "VARROCK",
	  "EAST DRAGONS",
	  "CASTLE",
	  "MAGE BANK",
	  "KING BLACK DRAGON (Grade 276)",
	  "SEA TROLL QUEEN (Grade 170)",
	  "BARRELCHEST (Grade 190)",
	  "CORPOREAL BEAST (Grade 785)",
	  "DAGGANNOTH KINGS (Grade 303)",
	  "GOD WARS",
	  "ZULRAH (Grade 725)",
	  "KRAKEN (Grade 291)",
	  "GIANT MOLE (Grade 230)",
	  "CHAOS ELEMENTAL (Grade 305)",
	  "CALLISTO (Grade 470)",
	  "SCORPIA (Grade 225)",
	  "VETION (Grade 454)",
	  "CHAOS FANATIC (Grade 202)",
	  "CRAZY ARCHAEOLOGIST (Grade 204)",
	  "BARROWS",
	  "WARRIORS GUILD",
	  "DUEL ARENA",
	  "PEST CONTROL",
	  "BLOOD TRIAL",
	  "WEAPON GAME",
	  "CLAN WARS",
	  "MEMBERSHIP",
	  "STAFFZONE",
	  "ABYSS",
	  "ESSENCE MINE",

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
