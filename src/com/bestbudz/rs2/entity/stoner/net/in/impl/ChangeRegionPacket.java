package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendDetails;

public class ChangeRegionPacket extends IncomingPacket {

  @Override
  public int getMaxDuplicates() {
    return 1;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    stoner.getClient().queueOutgoingPacket(new SendDetails(stoner.getIndex()));
    stoner.getGroundItems().onRegionChange();
    stoner.getObjects().onRegionChange();

    if (stoner.getDueling().isStaking()) {
      stoner.getDueling().decline();
    }

    if (stoner.getTrade().trading()) {
      stoner.getTrade().end(false);
    }

    stoner.resetAggression();
  }
}
