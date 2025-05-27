package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendPMServer extends OutgoingPacket {

  private final int state;

  public SendPMServer(int state) {
    super();
    this.state = state;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(2);
    out.writeHeader(client.getEncryptor(), 221);
    out.writeByte(state);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 221;
  }
}
