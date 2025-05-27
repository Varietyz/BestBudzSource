package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendExpCounter extends OutgoingPacket {

  private final int profession;

  private final int exp;

  public SendExpCounter(int profession, int exp) {
    this.profession = profession;
    this.exp = exp;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(10);
    client.getStoner().addCounterExp(exp);
    out.writeHeader(client.getEncryptor(), 127);
    out.writeByte(profession);
    out.writeInt(exp);
    out.writeInt((int) client.getStoner().getCounterExp());
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 127;
  }
}
