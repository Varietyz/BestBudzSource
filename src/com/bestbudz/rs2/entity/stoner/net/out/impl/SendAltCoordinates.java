package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendAltCoordinates extends OutgoingPacket {

  private final Location p;
  private final Location base;

  public SendAltCoordinates(Location p, Stoner stoner) {
    super();
    this.p = p;
    base = stoner.getCurrentRegion();
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(3);
    out.writeHeader(client.getEncryptor(), 85);
    int y = p.getY() - base.getRegionY() * 8 - 2;
    int x = p.getX() - base.getRegionX() * 8 - 3;
    out.writeByte(y, StreamBuffer.ValueType.C);
    out.writeByte(x, StreamBuffer.ValueType.C);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 85;
  }
}
