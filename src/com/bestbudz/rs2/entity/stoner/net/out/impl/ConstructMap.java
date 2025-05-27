package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.rs2.entity.Palette;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public final class ConstructMap extends OutgoingPacket {

  private final Palette palette;

  public ConstructMap(final Palette palette) {
    this.palette = palette;
  }

  @Override
  public void execute(Client client) {}

  @Override
  public int getOpcode() {
    return 241;
  }

  public Palette getPalette() {
    return palette;
  }
}
