package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendScrollInterface extends OutgoingPacket {

  private final int id;

  private final int pos;

  public SendScrollInterface(int id, int pos) {
    super();
    this.id = id;
    this.pos = pos;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(6);
    out.writeHeader(client.getEncryptor(), 79);
    out.writeShort(id, StreamBuffer.ByteOrder.LITTLE);
    out.writeShort(pos, StreamBuffer.ValueType.A);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 79;
  }
}
