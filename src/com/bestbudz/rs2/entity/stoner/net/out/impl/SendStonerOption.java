package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendStonerOption extends OutgoingPacket {

	private final String option;

	private final int id;

	public SendStonerOption(String option, int id) {
	super();
	this.option = option;
	this.id = id;
	}

	@Override
	public void execute(Client client) {
	if (!client.getLastStonerOption().equals(option)) {
		client.setLastStonerOption(option);
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(option.length() + 6);
		out.writeVariablePacketHeader(client.getEncryptor(), 104);
		out.writeByte(id, StreamBuffer.ValueType.C);
		out.writeByte(0, StreamBuffer.ValueType.A);
		out.writeString(option);
		out.finishVariablePacketHeader();
		client.send(out.getBuffer());
	}
	}

	@Override
	public int getOpcode() {
	return 104;
	}

}
