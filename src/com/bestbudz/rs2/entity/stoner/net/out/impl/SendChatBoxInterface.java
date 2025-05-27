package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendChatBoxInterface extends OutgoingPacket {

  private final int id;

  public SendChatBoxInterface(int id) {
    super();
    this.id = id;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(3);
    out.writeHeader(client.getEncryptor(), 164);
    out.writeShort(id, StreamBuffer.ByteOrder.LITTLE);
    client.send(out.getBuffer());
    client.getStoner().getInterfaceManager().setChat(id);
  }

  @Override
  public int getOpcode() {
    return 164;
  }
}
