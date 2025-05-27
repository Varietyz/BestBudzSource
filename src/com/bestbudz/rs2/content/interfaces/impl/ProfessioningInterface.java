package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class ProfessioningInterface extends InterfaceHandler {

  private final String[] text = {
    "Wilderness Resource",
    "Accomplisher",
    "Handiness",
    "Weedsmoking",
    "Quarrying",
    "Forging",
    "Fisher",
    "Lumbering",
    "Cultivation",
    "",
    "",
    "",
    "",
  };

  public ProfessioningInterface(Stoner stoner) {
    super(stoner);
  }

  @Override
  protected String[] text() {
    return text;
  }

  @Override
  protected int startingLine() {
    return 62051;
  }
}
