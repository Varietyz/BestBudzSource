package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendProfessionGoal extends OutgoingPacket {

	private final int profession;
	private final int init;
	private final int goal;
	private final int type;

	public SendProfessionGoal(int profession, int init, int goal, int type) {
	this.profession = profession;
	this.init = init;
	this.goal = goal;
	this.type = type;
	}

	@Override
	public void execute(Client client) {
	client.getStoner().getProfessionGoals()[profession][0] = init;
	client.getStoner().getProfessionGoals()[profession][1] = goal;
	client.getStoner().getProfessionGoals()[profession][2] = type;
	StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(11);
	out.writeHeader(client.getEncryptor(), 125);
	out.writeByte(profession);
	out.writeInt(init);
	out.writeInt(goal);
	out.writeByte(type);
	client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
	return 125;
	}
}