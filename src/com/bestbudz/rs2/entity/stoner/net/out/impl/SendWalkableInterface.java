package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendWalkableInterface extends OutgoingPacket {

  private final int id;

  public SendWalkableInterface(int id) {
    this.id = id;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(3);
    out.writeHeader(client.getEncryptor(), 208);
    out.writeShort(id, StreamBuffer.ByteOrder.LITTLE);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 208;
  }
}
