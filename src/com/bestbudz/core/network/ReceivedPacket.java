package com.bestbudz.core.network;

import io.netty.buffer.ByteBuf;

public class ReceivedPacket {

  private final int opcode;
  private final int size;
  private final ByteBuf payload;

  public ReceivedPacket(int opcode, int size, ByteBuf payload) {
    this.opcode = opcode;
    this.size = size;
    this.payload = payload;
  }

  public int getOpcode() {
    return opcode;
  }

  public ByteBuf getPayload() {
    return payload;
  }

  public int getSize() {
    return size;
  }
}
