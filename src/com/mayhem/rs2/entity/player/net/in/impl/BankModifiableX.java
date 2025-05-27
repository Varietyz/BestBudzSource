package com.mayhem.rs2.entity.player.net.in.impl;

import com.mayhem.core.network.StreamBuffer;
import com.mayhem.core.network.StreamBuffer.ByteOrder;
import com.mayhem.core.network.StreamBuffer.ValueType;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.in.IncomingPacket;

public class BankModifiableX extends IncomingPacket {

	@Override
	public void handle(Player player, StreamBuffer.InBuffer in, int opcode, int length) {
		in.readShort(ValueType.A, ByteOrder.BIG);
		in.readShort();
		int item = in.readShort(ValueType.A, ByteOrder.BIG);
		int amount = in.readInt();
		player.getBank().withdraw(item, amount);
	}

	@Override
	public int getMaxDuplicates() {
		return 1;
	}
}