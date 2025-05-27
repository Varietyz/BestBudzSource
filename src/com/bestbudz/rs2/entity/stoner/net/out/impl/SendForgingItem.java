package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendForgingItem extends OutgoingPacket {

  private final int id;
  private final int slot;
  private final int column;

  public SendForgingItem(int id, int slot, int column) {
    this.id = id;
    this.slot = slot;
    this.column = column;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(32);
    out.writeVariableShortPacketHeader(client.getEncryptor(), 34);
    out.writeShort(column);
    out.writeByte(4);
    out.writeShort(slot);
    out.writeShort(id + 1);
    out.writeByte(1);
    out.finishVariableShortPacketHeader();
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 34;
  }
}
