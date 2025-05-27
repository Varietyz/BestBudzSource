package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendSong extends OutgoingPacket {

  private final int id;

  public SendSong(int id) {
    super();
    this.id = id;
  }

  @Override
  public void execute(Client client) {
    if (id != -1) {
      StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(3);
      out.writeHeader(client.getEncryptor(), 74);
      out.writeShort(id, StreamBuffer.ByteOrder.LITTLE);
      client.send(out.getBuffer());
    }
  }

  @Override
  public int getOpcode() {
    return 74;
  }
}
