package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendPrivateMessage extends OutgoingPacket {

  private final long from;

  private final int rights;

  private final byte[] message;

  private final int id;

  public SendPrivateMessage(long from, int rights, byte[] message, int id) {
    super();
    this.from = from;
    this.rights = rights;
    this.message = message;
    this.id = id;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(2048);
    out.writeVariablePacketHeader(client.getEncryptor(), 196);
    out.writeLong(from);
    out.writeInt(id);
    out.writeByte(rights);
    out.writeBytes(message);
    out.finishVariablePacketHeader();
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 196;
  }
}
