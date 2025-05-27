package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class TrainingInterface extends InterfaceHandler {

  private final String[] text = {
    "Rock Crabs",
    "Hill Giants",
    "Al-Kharid",
    "Cows",
    "Yaks",
    "Brimhaven Dung",
    "Taverly Dung",
    "Mercenary Tower",
    "Lava Dragons",
    "Mithril Dragons",
  };

  public TrainingInterface(Stoner stoner) {
    super(stoner);
  }

  @Override
  protected String[] text() {
    return text;
  }

  @Override
  protected int startingLine() {
    return 61051;
  }
}
