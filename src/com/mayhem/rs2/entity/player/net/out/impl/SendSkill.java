package com.mayhem.rs2.entity.player.net.out.impl;

import com.mayhem.core.network.StreamBuffer;
import com.mayhem.rs2.entity.player.net.Client;
import com.mayhem.rs2.entity.player.net.out.OutgoingPacket;

public class SendSkill extends OutgoingPacket {

	private final int id;

	private final int level;

	private final int exp;

	public SendSkill(int id, int level, int exp) {
		this.id = id;
		this.level = level;
		this.exp = exp;
	}

	@Override
	public void execute(Client client) {
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(8);
		out.writeHeader(client.getEncryptor(), 134);
		out.writeByte(id);
		out.writeByte(client.getPlayer().getSkillPrestiges()[id]);
		out.writeInt(exp, StreamBuffer.ByteOrder.MIDDLE);
		out.writeByte(level);
		client.send(out.getBuffer());
		if(id==22) {
			client.getPlayer().send(new SendString(Integer.toString(level), 24134));
			client.getPlayer().send(new SendString(Integer.toString(level), 24135));
		}
	}

	@Override
	public int getOpcode() {
		return 134;
	}

}
