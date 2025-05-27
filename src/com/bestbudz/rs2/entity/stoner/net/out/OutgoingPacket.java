package com.bestbudz.rs2.entity.stoner.net.out;

import com.bestbudz.rs2.entity.stoner.net.Client;

public abstract class OutgoingPacket {

  @Override
  public boolean equals(Object o) {
    if ((o instanceof OutgoingPacket)) {
      return ((OutgoingPacket) o).getOpcode() == getOpcode();
    }

    return false;
  }

  public abstract void execute(Client paramClient);

  public abstract int getOpcode();
}
