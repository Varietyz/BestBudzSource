package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendExpCounter;

public class ResetCounter extends IncomingPacket {
  @Override
  public int getMaxDuplicates() {
    return 1;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    stoner.addCounterExp(-stoner.getCounterExp());
    stoner.send(new SendExpCounter(0, 0));
  }
}
