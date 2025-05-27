package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendUpdateFlashingSidebarIcon extends OutgoingPacket {

  private final int id;

  public SendUpdateFlashingSidebarIcon(int id) {
    super();
    this.id = id;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(7);
    out.writeHeader(client.getEncryptor(), 152);
    out.writeByte(id);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 152;
  }
}
