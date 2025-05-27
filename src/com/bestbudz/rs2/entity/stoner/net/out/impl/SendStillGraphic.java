package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendStillGraphic extends OutgoingPacket {

  private final int id;

  private final Location p;

  private final int delay;

  public SendStillGraphic(int id, Location p, int delay) {
    this.id = id;
    this.p = p;
    this.delay = delay;
  }

  @Override
  public void execute(Client client) {
    new SendCoordinates(p, client.getStoner()).execute(client);
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(7);
    out.writeHeader(client.getEncryptor(), 4);
    out.writeByte(0);
    out.writeShort(id);
    out.writeByte(p.getZ());
    out.writeShort(delay);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 4;
  }
}
