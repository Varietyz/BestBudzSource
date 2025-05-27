package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendQuickSong extends OutgoingPacket {

	private final int id;

	private final int delay;

	public SendQuickSong(int id, int delay) {
	super();
	this.id = id;
	this.delay = delay;
	}

	@Override
	public void execute(Client client) {
	StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(5);
	out.writeHeader(client.getEncryptor(), 121);
	out.writeShort(id, StreamBuffer.ByteOrder.LITTLE);
	out.writeShort(delay, StreamBuffer.ByteOrder.LITTLE);
	client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
	return 121;
	}

}
