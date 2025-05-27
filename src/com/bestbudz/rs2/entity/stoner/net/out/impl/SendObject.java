package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.object.GameObject;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendObject extends OutgoingPacket {

	private final GameObject o;
	private final Location base;

	public SendObject(Stoner p, GameObject o) {
	super();
	this.o = o;
	this.base = new Location(p.getCurrentRegion());
	}

	@Override
	public void execute(Client client) {
	new SendCoordinates(o.getLocation(), base).execute(client);
	StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(5);
	out.writeHeader(client.getEncryptor(), getOpcode());
	out.writeByte(0, StreamBuffer.ValueType.S);
	out.writeShort(o.getId(), StreamBuffer.ByteOrder.LITTLE);
	out.writeByte(((o.getType() << 2) + (o.getFace() & 3)), StreamBuffer.ValueType.S);
	client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
	return 151;
	}

}
