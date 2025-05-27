package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendMoveCamera extends OutgoingPacket {

  private final int x, y, z, constantSpeed, variableSpeed;

  public SendMoveCamera(int x, int y, int z) {
    this.x = x / 128 - 64;
    this.y = y / 128 - 64;
    this.z = z;
    this.constantSpeed = 0;
    this.variableSpeed = 100;
  }

  public SendMoveCamera(int x, int y, int z, int constantSpeed, int variableSpeed) {
    Location l = new Location(x, y);
    this.x = l.getLocalX();
    this.y = l.getLocalY();
    this.z = z;
    this.constantSpeed = constantSpeed;
    this.variableSpeed = variableSpeed;
  }

  @Override
  public void execute(Client client) {
    StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(7);
    out.writeHeader(client.getEncryptor(), 166);
    out.writeByte(x);
    out.writeByte(y);
    out.writeShort(z);
    out.writeByte(constantSpeed);
    out.writeByte(variableSpeed);
    client.send(out.getBuffer());
  }

  @Override
  public int getOpcode() {
    return 166;
  }
}
