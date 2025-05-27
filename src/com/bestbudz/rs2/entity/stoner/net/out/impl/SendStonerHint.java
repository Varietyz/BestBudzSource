package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendStonerHint extends OutgoingPacket {

  private final int type;

  private final int id;

  public SendStonerHint(boolean stoner, int id) {
    super();
    type = (stoner ? 10 : 1);
    this.id = id;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(7);
    out.writeHeader(client.getEncryptor(), 254);
    out.writeByte(type);
    out.writeShort(id);
    out.writeByte(0 >> 16);
    out.writeByte(0 >> 8);
    out.writeByte(0);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 254;
  }
}
