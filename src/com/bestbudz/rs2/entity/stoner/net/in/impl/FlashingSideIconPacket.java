package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;

public class FlashingSideIconPacket extends IncomingPacket {

	@Override
	public int getMaxDuplicates() {
	return 1;
	}

	@Override
	public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
	}
}
