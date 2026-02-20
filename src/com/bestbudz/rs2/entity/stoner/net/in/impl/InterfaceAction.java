package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;

public class InterfaceAction extends IncomingPacket {

  @Override
  public int getMaxDuplicates() {
    return 1;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    int id = in.readShort(false);
    int action = in.readShort(false);
    switch (id) {
      case 43704:
        break;
      case 43707:
      case 43710:
      case 43713:
      case 43716:
        break;

      default:
        break;
    }

  }
}
