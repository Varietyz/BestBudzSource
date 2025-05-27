package com.bestbudz.rs2.content.interfaces.impl;

import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class OtherInterface extends InterfaceHandler {

  private final String[] text = {
    "Membership Zone", "Staff Zone", "Relaxation Zone",
  };

  public OtherInterface(Stoner stoner) {
    super(stoner);
  }

  @Override
  protected String[] text() {
    return text;
  }

  @Override
  protected int startingLine() {
    return 61551;
  }
}
