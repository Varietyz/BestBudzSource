package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendBoxInterface extends OutgoingPacket {

  private final int invId;

  private final int id;

  public SendBoxInterface(int id, int invId) {
    super();
    this.invId = invId;
    this.id = id;
  }

  @Override
  public void execute(Client client) {
    if (client.getStoner().getMovementHandler().moving()) {
      client.getStoner().getMovementHandler().reset();
    }
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(5);
    out.writeHeader(client.getEncryptor(), 248);
    out.writeShort(id, StreamBuffer.ValueType.A);
    out.writeShort(invId);
    client.send(out.getBuffer());
    client.getStoner().getInterfaceManager().setActive(id, invId);
  }

  @Override
  public int getOpcode() {
    return 248;
  }
}
