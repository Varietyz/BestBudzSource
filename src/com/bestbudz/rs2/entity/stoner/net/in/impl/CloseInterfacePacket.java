package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;

public class CloseInterfacePacket extends IncomingPacket {
  @Override
  public int getMaxDuplicates() {
    return 1;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {

    if (stoner.getInterfaceManager().getMain() == 48500) {
      stoner.getPriceChecker().withdrawAll();
    }

    stoner.getInterfaceManager().reset();

    if (stoner.getTrade().trading()) {
      stoner.getTrade().end(false);
    }

    if (stoner.getDueling().isStaking()) {
      stoner.getDueling().decline();
    }

    stoner.getShopping().reset();
  }
}
