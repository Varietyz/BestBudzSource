package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public final class SendProfession extends OutgoingPacket {

  private final int id;
  private final long grade;
  private final long exp;

  public SendProfession(int id, long grade, long exp) {
    this.id = id;
    this.grade = grade;
    this.exp = exp;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(11);
    out.writeHeader(client.getEncryptor(), 134); // hdr
    out.writeByte(id); // id
    out.writeByte(client.getStoner().getProfessionAdvances()[id]); // adv
    out.writeInt(exp, StreamBuffer.ByteOrder.MIDDLE); // exp
    out.writeInt(grade, StreamBuffer.ByteOrder.MIDDLE); // grade
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 134;
  }
}
