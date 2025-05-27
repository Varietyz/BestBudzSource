package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendStonerDialogueHead extends OutgoingPacket {

  private final int id;

  public SendStonerDialogueHead(int id) {
    super();
    this.id = id;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(3);
    out.writeHeader(client.getEncryptor(), 185);
    out.writeShort(id, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 185;
  }
}
