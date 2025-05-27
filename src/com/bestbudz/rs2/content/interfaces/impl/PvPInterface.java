package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class PvPInterface extends InterfaceHandler {

  private final String[] text = {
    "Edgeville", "Varrock", "East Dragons", "Castle", "Mage Bank", "", "", "", "", "", "", "",
  };

  public PvPInterface(Stoner stoner) {
    super(stoner);
  }

  @Override
  protected String[] text() {
    return text;
  }

  @Override
  protected int startingLine() {
    return 63051;
  }
}
