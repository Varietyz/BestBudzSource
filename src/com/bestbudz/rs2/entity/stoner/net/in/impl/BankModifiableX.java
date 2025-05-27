package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.network.StreamBuffer.ByteOrder;
import com.bestbudz.core.network.StreamBuffer.ValueType;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;

public class BankModifiableX extends IncomingPacket {

  @Override
  public int getMaxDuplicates() {
    return 1;
  }

  @Override
  public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
    in.readShort(ValueType.A, ByteOrder.BIG);
    in.readShort();
    int item = in.readShort(ValueType.A, ByteOrder.BIG);
    int amount = in.readInt();
    stoner.getBank().withdraw(item, amount);
  }
}
