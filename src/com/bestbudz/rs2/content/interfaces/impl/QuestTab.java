package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.Server;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendColor;

public class QuestTab extends InterfaceHandler {

  private final String[] text = {
	  Utility.getCurrentServerTime(), // 115061
	  Server.bestbudzDate(), // 115062
	  "", // 115063
	  "Employees Online: " + World.getStaff(), // 115064
	  "Stoners Online: " + Utility.format(World.getActiveStoners()), // 115065
	  "", // 115066
	  Utility.capitalizeFirstLetter(stoner.getUsername()), // 115067
	  "Rank: " + stoner.deterquarryRank(stoner), // 115068
	  "", // 115069
	  "Check My Points", // 115070
	  "Check Tracked PvM Kills", // 115071
	  "", // 115072
	  "-FREQUENTLY USED-", // 115073
	  "FOCUSED MAGE", // 115074
	  "AoE MAGE", // 115075
	  "View Drop Table", // 115076
	  "Advance Professions", // 115077
	  "", // 115078
	  "", // 115079
	  "-SHOPS-", // 115080
	  "", // 115081
	  "General Store", // 115082
	  "Pack store", // 115083
	  "Professioning shop", // 115084
	  "BankStanding shop", // 115085
	  "THC-hempistry shop", // 115086
	  "Close-combat shop", // 115087
	  "Sagittarius shop", // 115088
	  "Mages shop", // 115089
	  "Pure shop", // 115090
	  "Fashion shop", // 115091
	  "Profession capes", // 115092
	  "Advancement capes", // 115093
	  "", // 115094
	  "-SHOPS2-", // 115095
	  "", // 115096
	  "Chill points", // 115097
	  "Weed protect points", // 115098
	  "Graceful marks", // 115099
	  "Achievement points", // 115100
	  "Advance points", // 115101
	  "Mercenary points", // 115102
	  "Bounty points", // 115103
	  "", // 115104
	  "-CRAFTER-", // 115105
	  "", // 115106
	  "Create Dragonfire Shield", // 115107
	  "Create Jewelery", // 115108
	  "Tan Hides", // 115109
	  "Decant Potions", // 115110
	  "", // 115111
	  "-MANAGEMENT-", // 115112
	  "", // 115113
	  "Deluxe title", // 115114
	  "Reset Combat Stats", // 115115
	  "Plastic Surgery", // 115116
	  "Recharge Resonance", // 115117
	  "Skull Self", // 115118
	  "", // 115119
	  "-TASKS-", // 115120
	  "", // 115121
	  "Mercenary (Monsters)", // 115122
	  "Mercenary (Bosses)", // 115123
	  "", // 115124
	  "-EXTRAS-", // 115125
	  "", // 115126
	  "Traveling Agency", // 115127
	  "Play Misery Box", // 115128
	  "Collect Achievement cape", // 115129
	  "", // 115130
	  "-TELEPORTS-", // 115131
	  "HOME", // 115132
	  "AIR Altar", // 115133
	  "MIND Altar", // 115134
	  "WATER Altar", // 115135
	  "EARTH Altar", // 115136
	  "FIRE Altar", // 115137
	  "BODY Altar", // 115138
	  "COSMIC Altar", // 115139
	  "CHAOS Altar", // 115140
	  "NATURE Altar", // 115141
	  "LAW Altar", // 115142
	  "DEATH Altar", // 115143
	  "ROCK CRABS", // 115144
	  "HILL GIANTS", // 115145
	  "AL KAHID", // 115146
	  "COWS", // 115147
	  "YAKS", // 115148
	  "BRIMHAVEN DUNG", // 115149
	  "TAVERLY DUNG", // 115150
	  "MERCENARY TOWER", // 115151
	  "LAVA DRAGONS", // 115152
	  "MITHRIL DRAGONS", // 115153
	  "WILD RESOURCE", // 115154
	  "PET_MASTER", // 115155
	  "HANDINESS", // 115156
	  "QUARRYING", // 115157
	  "FORGING", // 115158
	  "FISHER", // 115159
	  "LUMBERING", // 115160
	  "BANKSTANDING", // 115161
	  "EDGEVILLE", // 115162
	  "VARROCK", // 115163
	  "EAST DRAGONS", // 115164
	  "CASTLE", // 115165
	  "MAGE BANK", // 115166
	  "KING BLACK DRAGON (Grade 276)", // 115167
	  "SEA TROLL QUEEN (Grade 170)", // 115168
	  "BARRELCHEST (Grade 190)", // 115169
	  "CORPOREAL BEAST (Grade 785)", // 115170
	  "DAGGANNOTH KINGS (Grade 303)", // 115171
	  "GOD WARS", // 115172
	  "ZULRAH (Grade 725)", // 115173
	  "KRAKEN (Grade 291)", // 115174
	  "GIANT MOLE (Grade 230)", // 115175
	  "CHAOS ELEMENTAL (Grade 305)", // 115176
	  "CALLISTO (Grade 470)", // 115177
	  "SCORPIA (Grade 225)", // 115178
	  "VETION (Grade 454)", // 115179
	  "CHAOS FANATIC (Grade 202)", // 115180
	  "CRAZY ARCHAEOLOGIST (Grade 204)", // 115181
	  "BARROWS", // 115182
	  "WARRIORS GUILD", // 115183
	  "DUEL ARENA", // 115184
	  "PEST CONTROL", // 115185
	  "BLOOD TRIAL", // 115186
	  "WEAPON GAME", // 115187
	  "CLAN WARS", // 115188
	  "MEMBERSHIP", // 115189
	  "STAFFZONE", // 115190
	  "ABYSS", // 115191
	  "ESSENCE MINE", // 115192


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
