package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendStonerProfilerIndex extends OutgoingPacket {

	private final int id;

	public SendStonerProfilerIndex(int id) {
	this.id = id;
	}

	@Override
	public void execute(Client client) {
	StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(3);
	out.writeHeader(client.getEncryptor(), 201);
	out.writeShort(id, StreamBuffer.ByteOrder.BIG);
	client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
	return 201;
	}
}
