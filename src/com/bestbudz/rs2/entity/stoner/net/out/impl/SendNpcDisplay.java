package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public final class SendNpcDisplay extends OutgoingPacket {

	private int npc;
	private int size;

	public SendNpcDisplay(int npc, int size) {
	this.npc = npc;
	this.size = size;
	}

	@Override
	public void execute(Client client) {
	StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(3);
	out.writeHeader(client.getEncryptor(), 124);
	out.writeByte(npc);
	out.writeByte(size);
	client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
	return 124;
	}
}
