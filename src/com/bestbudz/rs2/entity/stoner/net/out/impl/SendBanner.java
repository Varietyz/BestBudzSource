package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendBanner extends OutgoingPacket {

	private final String message;

	private final int color;

	public SendBanner(Object message, int color) {
	this.message = String.valueOf(message);
	this.color = color;
	}

	@Override
	public void execute(Client client) {
	if (message == null || message.length() == 0) {
		return;
	}

	StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(message.length() + 7);
	out.writeVariablePacketHeader(client.getEncryptor(), getOpcode());
	out.writeString(message);
	out.writeInt(color);
	out.finishVariablePacketHeader();
	client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
	return 202;
	}

}
