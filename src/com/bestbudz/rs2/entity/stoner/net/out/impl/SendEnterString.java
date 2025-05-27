package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.network.StreamBuffer.OutBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendEnterString extends OutgoingPacket {

  @Override
  public void execute(Client client) {
    OutBuffer outBuffer = StreamBuffer.newOutBuffer(5);
    outBuffer.writeHeader(client.getEncryptor(), getOpcode());
    client.send(outBuffer.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 187;
  }
}
