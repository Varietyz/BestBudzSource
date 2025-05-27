package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.network.StreamBuffer.ByteOrder;
import com.bestbudz.core.network.StreamBuffer.ValueType;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;

public class BankAllButOne extends IncomingPacket {

	@Override
	public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
	in.readShort(ValueType.A, ByteOrder.BIG);
	in.readShort();
	int item = in.readShort(ValueType.A, ByteOrder.BIG);
	if (stoner.getBank().hasItemId(item) && stoner.getBank().getItemAmount(item) > 1) {
		stoner.getBank().withdraw(item, stoner.getBank().getItemAmount(item) - 1);
	}
	}

	@Override
	public int getMaxDuplicates() {
	return 1;
	}
}