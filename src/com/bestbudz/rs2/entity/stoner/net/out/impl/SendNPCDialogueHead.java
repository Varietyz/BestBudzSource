package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendNPCDialogueHead extends OutgoingPacket {

	private final int npc;

	private final int id;

	public SendNPCDialogueHead(int npc, int id) {
	super();
	this.npc = npc;
	this.id = id;
	}

	@Override
	public void execute(Client client) {
	StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(5);
	out.writeHeader(client.getEncryptor(), 75);
	out.writeShort(npc, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
	out.writeShort(id, StreamBuffer.ValueType.A, StreamBuffer.ByteOrder.LITTLE);
	client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
	return 75;
	}

}
