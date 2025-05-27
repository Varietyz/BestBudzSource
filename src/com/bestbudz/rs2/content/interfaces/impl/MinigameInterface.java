package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class MinigameInterface extends InterfaceHandler {

  private final String[] text = {
    "Old school Barrows",
    "Warriors Guild",
    "Duel Arena",
    "Pest Control",
    "Fight Caves",
    "Weapon Game",
    "Cult Wars",
  };

  public MinigameInterface(Stoner stoner) {
    super(stoner);
  }

  @Override
  protected String[] text() {
    return text;
  }

  @Override
  protected int startingLine() {
    return 65051;
  }
}
