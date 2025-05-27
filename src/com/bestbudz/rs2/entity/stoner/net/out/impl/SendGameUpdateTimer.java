package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendGameUpdateTimer extends OutgoingPacket {

  private final int time;

  public SendGameUpdateTimer(int time) {
    this.time = time;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(3);
    out.writeHeader(client.getEncryptor(), 114);
    int toSend = time * 50 / 30;
    out.writeShort(toSend, StreamBuffer.ByteOrder.LITTLE);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 114;
  }
}
