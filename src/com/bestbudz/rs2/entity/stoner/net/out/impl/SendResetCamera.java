package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendResetCamera extends OutgoingPacket {

	@Override
	public void execute(Client client) {
	StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(1);
	out.writeHeader(client.getEncryptor(), 107);
	client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
	return 107;
	}

}
