package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendProfession extends OutgoingPacket {

	private final int id;

	private final int grade;

	private final int exp;

	public SendProfession(int id, int grade, int exp) {
	this.id = id;
	this.grade = grade;
	this.exp = exp;
	}

	@Override
	public void execute(Client client) {
	StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(8);
	out.writeHeader(client.getEncryptor(), 134);
	out.writeByte(id);
	out.writeByte(client.getStoner().getProfessionAdvances()[id]);
	out.writeInt(exp, StreamBuffer.ByteOrder.MIDDLE);
	out.writeByte(grade);
	client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
	return 134;
	}

}
