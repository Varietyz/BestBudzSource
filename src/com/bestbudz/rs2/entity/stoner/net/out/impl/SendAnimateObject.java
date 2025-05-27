package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendAnimateObject extends OutgoingPacket {

  private final RSObject object;
  private final int animation;

  public SendAnimateObject(RSObject object, int animation) {
    this.object = object;
    this.animation = animation;
  }

  @Override
  public void execute(Client client) {
    if (object == null) {
      return;
    }

    new SendCoordinates(
            new Location(object.getX(), object.getY(), object.getZ()), client.getStoner())
        .execute(client);
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(5);
    out.writeHeader(client.getEncryptor(), 160);
    out.writeByte(0, StreamBuffer.ValueType.S);
    out.writeByte(
        ((long) object.getType() << 2) + (object.getFace() & 3), StreamBuffer.ValueType.S);
    out.writeShort(animation, StreamBuffer.ValueType.A);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 160;
  }
}
