package com.mayhem.rs2.entity.player.net.out.impl;

import com.mayhem.core.network.StreamBuffer;
import com.mayhem.rs2.entity.player.net.Client;
import com.mayhem.rs2.entity.player.net.out.OutgoingPacket;

public class SendEntityFeed extends OutgoingPacket {
	String entityName;
	int HP;
	int maxHP;

	public SendEntityFeed(String entityName, int HP, int maxHP) {
		this.entityName = entityName;
		this.HP = HP;
		this.maxHP = maxHP;
	}

	@Override
	public void execute(Client client) {
		if (client.getPlayer() == null) {
			return;
		}

		if (entityName.length() == 0) {
			return;
		}
		
		
		
		StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(entityName.length() + 7);
		out.writeVariablePacketHeader(client.getEncryptor(), getOpcode());
		out.writeString(entityName);
		out.writeShort(HP);
		out.writeShort(maxHP);
		out.finishVariablePacketHeader();
		client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
		return 175;
	}

}