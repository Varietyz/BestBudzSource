package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendSpecialBar extends OutgoingPacket {

  private final int main;

  private final int sub;

  public SendSpecialBar(int main, int sub) {
    super();
    this.main = main;
    this.sub = sub;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(4);
    out.writeHeader(client.getEncryptor(), 171);
    out.writeByte(main);
    out.writeShort(sub);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 171;
  }
}
