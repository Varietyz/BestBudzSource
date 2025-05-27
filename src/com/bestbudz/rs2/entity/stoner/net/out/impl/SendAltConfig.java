package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendAltConfig extends OutgoingPacket {

  private final int id;

  private final int state;

  public SendAltConfig(int id, int state) {
    this.id = id;
    this.state = state;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(7);
    out.writeHeader(client.getEncryptor(), 87);
    out.writeShort(id, StreamBuffer.ByteOrder.LITTLE);
    out.writeInt(state, StreamBuffer.ByteOrder.MIDDLE);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 87;
  }
}
