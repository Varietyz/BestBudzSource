package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendInterface extends OutgoingPacket {

  private final int id;

  public SendInterface(int id) {
    super();
    this.id = id;
  }

  @Override
  public void execute(Client client) {
    if (client.getStoner().getMovementHandler().moving()) {
      client.getStoner().getMovementHandler().reset();
    }
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(10);
    out.writeHeader(client.getEncryptor(), 97);
    out.writeShort(id);
    client.send(out.getBuffer());
    client.getStoner().getInterfaceManager().setActive(id, -1);
  }

  @Override
  public int getOpcode() {
    return 97;
  }
}
