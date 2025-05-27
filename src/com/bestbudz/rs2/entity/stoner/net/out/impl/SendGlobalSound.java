package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendGlobalSound extends OutgoingPacket {

	private final int id;

	private final int type;

	private final int delay;

	public SendGlobalSound(int id, int type, int delay) {
	super();
	this.id = id;
	this.type = type;
	this.delay = delay;
	}

	@Override
	public void execute(Client client) {
	StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(18);
	out.writeHeader(client.getEncryptor(), getOpcode());
	out.writeShort(id);
	out.writeByte(type);
	out.writeShort(delay);
	for (Stoner stoner : World.getStoners()) {
		if (stoner != null) {
			if (Utility.getExactDistance(client.getStoner().getLocation(), stoner.getLocation()) < 10) {
				stoner.getClient().send(out.getBuffer());
			}
		}
	}
	}

	@Override
	public int getOpcode() {
	return 174;
	}

}
