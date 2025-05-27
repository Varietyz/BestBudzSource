package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class BossInterface extends InterfaceHandler {

  private final String[] text = {
    "King Black Dragon",
    "Sea Troll Queen",
    "Barrelchest",
    "Corporeal Beast",
    "Daggonoths Kings",
    "Godwars",
    "Zulrah",
    "Kraken",
    "Giant Mole",
    "Chaos Element",
    "Callisto",
    "Scorpia",
    "Vet'ion",
    "Venenatis (N/A)",
    "Chaos Fanatic",
    "Crazy archaeologist",
    "Kalphite Queen (N/A)",
  };

  public BossInterface(Stoner stoner) {
    super(stoner);
  }

  @Override
  protected String[] text() {
    return text;
  }

  @Override
  protected int startingLine() {
    return 64051;
  }
}
