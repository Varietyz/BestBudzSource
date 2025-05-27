package com.bestbudz.rs2.entity.stoner.net.out.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.OutgoingPacket;

public class SendCoordinates extends OutgoingPacket {

	private final Location p;
	private final Location base;

	public SendCoordinates(Location p, Location base) {
	this.p = p;
	this.base = base;
	}

	public SendCoordinates(Location p, Stoner stoner) {
	this.p = p;
	base = stoner.getCurrentRegion();
	}

	@Override
	public void execute(Client client) {
	StreamBuffer.OutBuffer out = StreamBuffer.newOutBuffer(3);
	out.writeHeader(client.getEncryptor(), 85);
	int y = p.getY() - 8 * base.getRegionY();
	int x = p.getX() - 8 * base.getRegionX();
	out.writeByte(y, StreamBuffer.ValueType.C);
	out.writeByte(x, StreamBuffer.ValueType.C);
	client.send(out.getBuffer());
	}

	@Override
	public int getOpcode() {
	return 85;
	}

}
