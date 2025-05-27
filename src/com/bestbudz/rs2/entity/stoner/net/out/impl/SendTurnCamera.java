package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendTurnCamera extends OutgoingPacket {

  private final int x;
  private final int y;
  private final int z;
  private final int constantSpeed;
  private final int variableSpeed;

  public SendTurnCamera(int x, int y, int z, int constantSpeed, int variableSpeed) {
    super();
    this.x = x / 64;
    this.y = y / 64;
    this.z = z;
    this.constantSpeed = constantSpeed;
    this.variableSpeed = variableSpeed;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(7);
    out.writeHeader(client.getEncryptor(), 177);
    out.writeByte(x);
    out.writeByte(y);
    out.writeShort(z);
    out.writeByte(constantSpeed);
    out.writeByte(variableSpeed);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 177;
  }
}
